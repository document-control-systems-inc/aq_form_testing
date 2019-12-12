using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using NAPS2.Config;
using NAPS2.Lang.Resources;
using NAPS2.Scan;
using NAPS2.Scan.Exceptions;
using NAPS2.Scan.Twain;
using NAPS2.Scan.Wia;
using NAPS2.Util;
using Newtonsoft.Json.Linq;
using treeView_Data;
using System.Net;
using System.IO;
using NAPS2.Entity;

namespace NAPS2.WinForms
{
    public partial class FEditProfile : FormBase
    {
        private readonly IScanDriverFactory driverFactory;
        private readonly IErrorOutput errorOutput;
        private readonly ProfileNameTracker profileNameTracker;
        private readonly AppConfigManager appConfigManager;

        private ScanProfile scanProfile;
        private ScanDevice currentDevice;
        private bool isDefault;

        private int iconID;
        private bool result;

        private bool suppressChangeEvent;

        public FEditProfile(IScanDriverFactory driverFactory, IErrorOutput errorOutput, ProfileNameTracker profileNameTracker, AppConfigManager appConfigManager)
        {
            this.driverFactory = driverFactory;
            this.errorOutput = errorOutput;
            this.profileNameTracker = profileNameTracker;
            this.appConfigManager = appConfigManager;
            InitializeComponent();
            AddEnumItems<ScanHorizontalAlign>(cmbAlign);
            AddEnumItems<ScanBitDepth>(cmbDepth);
            AddEnumItems<ScanDpi>(cmbResolution);
            AddEnumItems<ScanScale>(cmbScale);
            AddEnumItems<ScanSource>(cmbSource);
            cmbPage.Format += (sender, e) =>
            {
                var item = (PageSizeListItem)e.ListItem;
                e.Value = item.Label;
            };
        }

        protected override void OnLoad(object sender, EventArgs e)
        {
            // Don't trigger any onChange events
            suppressChangeEvent = true;

            pctIcon.Image = ilProfileIcons.IconsList.Images[ScanProfile.IconID];
            txtName.Text = ScanProfile.DisplayName;
            if (CurrentDevice == null)
            {
                CurrentDevice = ScanProfile.Device;
            }
            isDefault = ScanProfile.IsDefault;
            iconID = ScanProfile.IconID;

            cmbSource.SelectedIndex = (int)ScanProfile.PaperSource;
            cmbDepth.SelectedIndex = (int)ScanProfile.BitDepth;
            cmbResolution.SelectedIndex = (int)ScanProfile.Resolution;
            txtContrast.Text = ScanProfile.Contrast.ToString("G");
            txtBrightness.Text = ScanProfile.Brightness.ToString("G");
            UpdatePageSizeList();
            SelectPageSize();
            cmbScale.SelectedIndex = (int)ScanProfile.AfterScanScale;
            cmbAlign.SelectedIndex = (int)ScanProfile.PageAlign;

            cbAutoSave.Checked = ScanProfile.EnableAutoSave;

            // The setter updates the driver selection checkboxes
            DeviceDriverName = ScanProfile.DriverName;

            rdbNative.Checked = ScanProfile.UseNativeUI;
            rdbConfig.Checked = !ScanProfile.UseNativeUI;

            // Start triggering onChange events again
            suppressChangeEvent = false;

            UpdateEnabledControls();

            linkAutoSaveSettings.Location = new Point(cbAutoSave.Right, linkAutoSaveSettings.Location.Y);
            new LayoutManager(this)
                .Bind(txtName, txtDevice, panel1, panel2)
                    .WidthToForm()
                .Bind(pctIcon, btnChooseDevice, btnOK, btnCancel)
                    .RightToForm()
                .Bind(cmbAlign, cmbDepth, cmbPage, cmbResolution, cmbScale, cmbSource, trBrightness, trContrast, rdbConfig, rdbNative)
                    .WidthTo(() => Width / 2)
                .Bind(rdTWAIN, rdbNative, label3, cmbDepth, label9, cmbAlign, label10, cmbScale, label7, trContrast)
                    .LeftTo(() => Width / 2)
                .Bind(txtBrightness)
                    .LeftTo(() => trBrightness.Right)
                .Bind(txtContrast)
                    .LeftTo(() => trContrast.Right)
                .Activate();
        }

        private void UpdatePageSizeList()
        {
            cmbPage.Items.Clear();

            // Defaults
            foreach (ScanPageSize item in Enum.GetValues(typeof(ScanPageSize)))
            {
                cmbPage.Items.Add(new PageSizeListItem
                {
                    Type = item,
                    Label = item.Description()
                });
            }

            // Custom Presets
            foreach (var preset in UserConfigManager.Config.CustomPageSizePresets.OrderBy(x => x.Name))
            {
                cmbPage.Items.Insert(cmbPage.Items.Count - 1, new PageSizeListItem
                {
                    Type = ScanPageSize.Custom,
                    Label = string.Format(MiscResources.NamedPageSizeFormat, preset.Name, preset.Dimens.Width, preset.Dimens.Height, preset.Dimens.Unit.Description()),
                    CustomName = preset.Name,
                    CustomDimens = preset.Dimens
                });
            }
        }

        private void SelectPageSize()
        {
            if (ScanProfile.PageSize == ScanPageSize.Custom)
            {
                SelectCustomPageSize(ScanProfile.CustomPageSizeName, ScanProfile.CustomPageSize);
            }
            else
            {
                cmbPage.SelectedIndex = (int) ScanProfile.PageSize;
            }
        }

        private void SelectCustomPageSize(string name, PageDimensions dimens)
        {
            for (int i = 0; i < cmbPage.Items.Count; i++)
            {
                var item = (PageSizeListItem) cmbPage.Items[i];
                if (item.Type == ScanPageSize.Custom && item.CustomName == name && item.CustomDimens == dimens)
                {
                    cmbPage.SelectedIndex = i;
                    return;
                }
            }

            // Not found, so insert a new item
            cmbPage.Items.Insert(cmbPage.Items.Count - 1, new PageSizeListItem
            {
                Type = ScanPageSize.Custom,
                Label = string.IsNullOrEmpty(name)
                    ? string.Format(MiscResources.CustomPageSizeFormat, dimens.Width, dimens.Height, dimens.Unit.Description())
                    : string.Format(MiscResources.NamedPageSizeFormat, name, dimens.Width, dimens.Height, dimens.Unit.Description()),
                CustomName = name,
                CustomDimens = dimens
            });
            cmbPage.SelectedIndex = cmbPage.Items.Count - 2;
        }

        public bool Result => result;

        public ScanProfile ScanProfile
        {
            get => scanProfile;
            set => scanProfile = value.Clone();
        }

        private string DeviceDriverName
        {
            get => rdTWAIN.Checked ? TwainScanDriver.DRIVER_NAME : WiaScanDriver.DRIVER_NAME;
            set
            {
                if (value == TwainScanDriver.DRIVER_NAME)
                {
                    rdTWAIN.Checked = true;
                }
                else
                {
                    rdWIA.Checked = true;
                }
            }
        }

        public ScanDevice CurrentDevice
        {
            get => currentDevice;
            set
            {
                currentDevice = value;
                txtDevice.Text = (value == null ? "" : value.Name);
            }
        }

        private void ChooseDevice(string driverName)
        {
            var driver = driverFactory.Create(driverName);
            try
            {
                driver.DialogParent = this;
                driver.ScanProfile = ScanProfile;
                ScanDevice device = driver.PromptForDevice();
                if (device != null)
                {
                    if (string.IsNullOrEmpty(txtName.Text) ||
                        CurrentDevice != null && CurrentDevice.Name == txtName.Text)
                    {
                        txtName.Text = device.Name;
                    }
                    CurrentDevice = device;
                }
            }
            catch (ScanDriverException e)
            {
                if (e is ScanDriverUnknownException)
                {
                    Log.ErrorException(e.Message, e.InnerException);
                    errorOutput.DisplayError(e.Message, e);
                }
                else
                {
                    errorOutput.DisplayError(e.Message);
                }
            }
        }

        private void btnChooseDevice_Click(object sender, EventArgs e)
        {
            ChooseDevice(DeviceDriverName);
        }

        private void SaveSettings()
        {
            if (ScanProfile.IsLocked)
            {
                if (!ScanProfile.IsDeviceLocked)
                {
                    ScanProfile.Device = CurrentDevice;
                }
                return;
            }
            var pageSize = (PageSizeListItem) cmbPage.SelectedItem;
            if (ScanProfile.DisplayName != null)
            {
                profileNameTracker.RenamingProfile(ScanProfile.DisplayName, txtName.Text);
            }
            scanProfile = new ScanProfile
            {
                Version = ScanProfile.CURRENT_VERSION,

                Device = CurrentDevice,
                IsDefault = isDefault,
                DriverName = DeviceDriverName,
                DisplayName = txtName.Text,
                IconID = iconID,
                MaxQuality = ScanProfile.MaxQuality,
                UseNativeUI = rdbNative.Checked,

                AfterScanScale = (ScanScale)cmbScale.SelectedIndex,
                BitDepth = (ScanBitDepth)cmbDepth.SelectedIndex,
                Brightness = trBrightness.Value,
                Contrast = trContrast.Value,
                PageAlign = (ScanHorizontalAlign)cmbAlign.SelectedIndex,
                PageSize = pageSize.Type,
                CustomPageSizeName = pageSize.CustomName,
                CustomPageSize = pageSize.CustomDimens,
                Resolution = (ScanDpi)cmbResolution.SelectedIndex,
                PaperSource = (ScanSource)cmbSource.SelectedIndex,

                EnableAutoSave = cbAutoSave.Checked,
                AutoSaveSettings = ScanProfile.AutoSaveSettings,
                Quality = ScanProfile.Quality,
                BrightnessContrastAfterScan = ScanProfile.BrightnessContrastAfterScan,
                AutoDeskew = ScanProfile.AutoDeskew,
                WiaOffsetWidth = ScanProfile.WiaOffsetWidth,
                WiaRetryOnFailure = ScanProfile.WiaRetryOnFailure,
                WiaDelayBetweenScans = ScanProfile.WiaDelayBetweenScans,
                WiaDelayBetweenScansSeconds = ScanProfile.WiaDelayBetweenScansSeconds,
                ForcePageSize = ScanProfile.ForcePageSize,
                ForcePageSizeCrop = ScanProfile.ForcePageSizeCrop,
                FlipDuplexedPages = ScanProfile.FlipDuplexedPages,
                TwainImpl = ScanProfile.TwainImpl,

                ExcludeBlankPages = ScanProfile.ExcludeBlankPages,
                BlankPageWhiteThreshold = ScanProfile.BlankPageWhiteThreshold,
                BlankPageCoverageThreshold = ScanProfile.BlankPageCoverageThreshold,

                // Se agregan los campos de Aquarius en la clase:
                AquariusProfile = AquariusUtils.validateIsNull(txtName.Text),
                AquariusIdParent = AquariusUtils.validateIsNull(EntityNodo.Id),
                AquariusNameParent = AquariusUtils.validateIsNull(EntityNodo.Nombre),
                AquariusDocumentClass = AquariusUtils.validateIsNull(EntityNodo.DocumentClass),
                AquariusDomain = AquariusUtils.validateIsNull(EntityNodo.Domain)

        };
            // Se agrega la creaci�n del Json para Aquarius:
            AquariusUtils.addProfile(scanProfile);
            
        }

        private void btnOK_Click(object sender, EventArgs e)
        {
            // Note: If CurrentDevice is null, that's fine. A prompt will be shown when scanning.

            if (txtName.Text == "")
            {
                errorOutput.DisplayError(MiscResources.NameMissing);
                return;
            }
            result = true;
            var formTree = FormFactory.Create<FTreeView>();
            formTree.ShowDialog();
            if (!EntityNodo.cancelUpdate)
            {
                SaveSettings();
                try
                {
                    String resultado = AquariusUtils.setProfile(ResultadoLogin.token, EntityNodo.ScanPerfil);
                    JObject jObject = JObject.Parse(resultado);
                    if (!jObject.GetValue("status").ToString().Equals("0"))
                    {
                        MessageBox.Show(AquariusUtils.mensajeError("es_mx", jObject.GetValue("status").ToString()));
                    }
                }
                catch (WebException ex)
                {
                    WebResponse errorResponse = ex.Response;
                    using (Stream responseStream = errorResponse.GetResponseStream())
                    {
                        StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.GetEncoding("utf-8"));
                        String errorText = reader.ReadToEnd();
                    }
                    throw;
                }//Fin
                Close();
            }
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            Close();
        }

        private void rdbConfig_CheckedChanged(object sender, EventArgs e)
        {
            UpdateEnabledControls();
        }

        private void rdbNativeWIA_CheckedChanged(object sender, EventArgs e)
        {
            UpdateEnabledControls();
        }

        private void UpdateEnabledControls()
        {
            if (!suppressChangeEvent)
            {
                suppressChangeEvent = true;
                
                bool locked = ScanProfile.IsLocked;
                bool deviceLocked = ScanProfile.IsDeviceLocked;
                bool settingsEnabled = !locked && rdbConfig.Checked;

                txtName.Enabled = !locked;
                rdWIA.Enabled = rdTWAIN.Enabled = !locked;
                txtDevice.Enabled = !deviceLocked;
                btnChooseDevice.Enabled = !deviceLocked;
                rdbConfig.Enabled = rdbNative.Enabled = !locked;

                cmbSource.Enabled = settingsEnabled;
                cmbResolution.Enabled = settingsEnabled;
                cmbPage.Enabled = settingsEnabled;
                cmbDepth.Enabled = settingsEnabled;
                cmbAlign.Enabled = settingsEnabled;
                cmbScale.Enabled = settingsEnabled;
                trBrightness.Enabled = settingsEnabled;
                trContrast.Enabled = settingsEnabled;
                txtBrightness.Enabled = settingsEnabled;
                txtContrast.Enabled = settingsEnabled;

                cbAutoSave.Enabled = !locked && !appConfigManager.Config.DisableAutoSave;
                linkAutoSaveSettings.Visible = !locked && !appConfigManager.Config.DisableAutoSave;

                btnAdvanced.Enabled = !locked;

                suppressChangeEvent = false;
            }
        }

        private void rdWIA_CheckedChanged(object sender, EventArgs e)
        {
            if (!suppressChangeEvent)
            {
                ScanProfile.Device = null;
                CurrentDevice = null;
                UpdateEnabledControls();
            }
        }

        private void txtBrightness_TextChanged(object sender, EventArgs e)
        {
            int value;
            if (int.TryParse(txtBrightness.Text, out value))
            {
                if (value >= trBrightness.Minimum && value <= trBrightness.Maximum)
                {
                    trBrightness.Value = value;
                }
            }
        }

        private void trBrightness_Scroll(object sender, EventArgs e)
        {
            txtBrightness.Text = trBrightness.Value.ToString("G");
        }

        private void txtContrast_TextChanged(object sender, EventArgs e)
        {
            int value;
            if (int.TryParse(txtContrast.Text, out value))
            {
                if (value >= trContrast.Minimum && value <= trContrast.Maximum)
                {
                    trContrast.Value = value;
                }
            }
        }

        private void trContrast_Scroll(object sender, EventArgs e)
        {
            txtContrast.Text = trContrast.Value.ToString("G");
        }

        private int lastPageSizeIndex = -1;
        private PageSizeListItem lastPageSizeItem = null;

        private void cmbPage_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (cmbPage.SelectedIndex == cmbPage.Items.Count - 1)
            {
                // "Custom..." selected
                var form = FormFactory.Create<FPageSize>();
                form.PageSizeDimens = lastPageSizeItem.Type == ScanPageSize.Custom
                    ? lastPageSizeItem.CustomDimens
                    : lastPageSizeItem.Type.PageDimensions();
                if (form.ShowDialog() == DialogResult.OK)
                {
                    UpdatePageSizeList();
                    SelectCustomPageSize(form.PageSizeName, form.PageSizeDimens);
                }
                else
                {
                    cmbPage.SelectedIndex = lastPageSizeIndex;
                }
            }
            lastPageSizeIndex = cmbPage.SelectedIndex;
            lastPageSizeItem = (PageSizeListItem)cmbPage.SelectedItem;
        }

        private void linkAutoSaveSettings_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            if (appConfigManager.Config.DisableAutoSave)
            {
                return;
            }
            var form = FormFactory.Create<FAutoSaveSettings>();
            ScanProfile.DriverName = DeviceDriverName;
            form.ScanProfile = ScanProfile;
            form.ShowDialog();
        }

        private void btnAdvanced_Click(object sender, EventArgs e)
        {
            var form = FormFactory.Create<FAdvancedScanSettings>();
            ScanProfile.DriverName = DeviceDriverName;
            ScanProfile.BitDepth = (ScanBitDepth)cmbDepth.SelectedIndex;
            form.ScanProfile = ScanProfile;
            form.ShowDialog();
        }

        private void cbAutoSave_CheckedChanged(object sender, EventArgs e)
        {
            if (!suppressChangeEvent)
            {
                if (cbAutoSave.Checked)
                {
                    linkAutoSaveSettings.Enabled = true;
                    var form = FormFactory.Create<FAutoSaveSettings>();
                    form.ScanProfile = ScanProfile;
                    form.ShowDialog();
                    if (!form.Result)
                    {
                        cbAutoSave.Checked = false;
                    }
                }
            }
            linkAutoSaveSettings.Enabled = cbAutoSave.Checked;
        }

        private void txtDevice_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Delete)
            {
                CurrentDevice = null;
            }
        }

        private class PageSizeListItem
        {
            public string Label { get; set; }

            public ScanPageSize Type { get; set; }

            public string CustomName { get; set; }

            public PageDimensions CustomDimens { get; set; }
        }
    }
}
