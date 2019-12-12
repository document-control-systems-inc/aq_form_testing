using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Windows.Forms;
using NAPS2.Config;
using NAPS2.Entity;
using NAPS2.Lang.Resources;
using NAPS2.Scan;
using NAPS2.Scan.Exceptions;
using NAPS2.Scan.Twain;
using NAPS2.Scan.Wia;
using NAPS2.Util;
using Newtonsoft.Json.Linq;
using treeView_Data;

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
                cmbPage.SelectedIndex = (int)ScanProfile.PageSize;
            }
        }

        private void SelectCustomPageSize(string name, PageDimensions dimens)
        {
            for (int i = 0; i < cmbPage.Items.Count; i++)
            {
                var item = (PageSizeListItem)cmbPage.Items[i];
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
            //Se guarda en entity el perfil de scaneo
            EntityNodo.ScanPerfil = scanProfile.ToString();
            String drivername = scanProfile.DriverName;
            String textname = txtName.Text;
            String textdevice = txtDevice.Text;
            String textpaper = cmbSource.Text;
            String textpage = cmbPage.Text;
            String textresolution = cmbResolution.Text;
            String textbit = cmbDepth.Text;
            String textalign = cmbAlign.Text;
            String textscale = cmbScale.Text;
            String textBrightness = txtBrightness.Text;
            String textContrast = txtContrast.Text;
            String textQuality = scanProfile.Quality.ToString();
            String textwhite = scanProfile.BlankPageWhiteThreshold.ToString();
            String textCoverage = scanProfile.BlankPageCoverageThreshold.ToString();
            String textDelay = scanProfile.WiaDelayBetweenScans.ToString();

            ScanRepository CurrentRepository = new ScanRepository();
            CurrentRepository.IdParent = EntityNodo.Id;
            CurrentRepository.NameParent = EntityNodo.Nombre;
            CurrentRepository.DocumentClass = EntityNodo.DocumentClass;
            CurrentRepository.Domain = EntityNodo.Domain;



            if (ScanProfile.IsLocked)
            {
                if (!ScanProfile.IsDeviceLocked)
                {
                    ScanProfile.Device = CurrentDevice;
                }
                return;
            }
            var pageSize = (PageSizeListItem)cmbPage.SelectedItem;
            if (ScanProfile.DisplayName != null)
            {
                profileNameTracker.RenamingProfile(ScanProfile.DisplayName, txtName.Text);
            }

            var afterScanScale = (ScanScale)cmbScale.SelectedIndex;
            var bitDepth = (ScanBitDepth)cmbDepth.SelectedIndex;
            var pageAlign = (ScanHorizontalAlign)cmbAlign.SelectedIndex;
            var resolution = (ScanDpi)cmbResolution.SelectedIndex;
            var paperSource = (ScanSource)cmbSource.SelectedIndex;

            JObject jsonScanData = new JObject();
            jsonScanData.Add("iD", CurrentDevice.ID);
            jsonScanData.Add("name", CurrentDevice.Name);
            jsonScanData.Add("driverName", DeviceDriverName);
            jsonScanData.Add("displayName", txtName.Text);
            jsonScanData.Add("perfil",txtName.Text) ;
            jsonScanData.Add("iconID", iconID);
            jsonScanData.Add("maxQuality", ScanProfile.MaxQuality);
            jsonScanData.Add("isDefault", false);
            jsonScanData.Add("version", ScanProfile.CURRENT_VERSION);
            jsonScanData.Add("useNativeUI", rdbNative.Checked);
            jsonScanData.Add("afterScanScale", afterScanScale.ToString());
            jsonScanData.Add("brightness", trBrightness.Value);
            jsonScanData.Add("contrast", trContrast.Value);
            jsonScanData.Add("bitDepth", bitDepth.ToString());
            jsonScanData.Add("pageAlign", pageAlign.ToString());
            jsonScanData.Add("pageSize", pageSize.Type.ToString());
            jsonScanData.Add("resolution", resolution.ToString());
            jsonScanData.Add("paperSource", paperSource.ToString());
            jsonScanData.Add("enableAutoSave", cbAutoSave.Checked);
            jsonScanData.Add("quality", ScanProfile.Quality);
            jsonScanData.Add("autoDeskew", ScanProfile.AutoDeskew);
            jsonScanData.Add("brightnessContrastAfterScan", ScanProfile.BrightnessContrastAfterScan);
            jsonScanData.Add("forcePageSize", ScanProfile.ForcePageSize.ToString());
            jsonScanData.Add("forcePageSizeCrop", ScanProfile.ForcePageSizeCrop.ToString());
            jsonScanData.Add("twainImpl", ScanProfile.TwainImpl.ToString());
            jsonScanData.Add("excludeBlankPages", ScanProfile.ExcludeBlankPages.ToString());
            jsonScanData.Add("blankPageWhiteThreshold", ScanProfile.BlankPageWhiteThreshold);
            jsonScanData.Add("blankPageCoverageThreshold", ScanProfile.BlankPageCoverageThreshold);
            jsonScanData.Add("wiaOffsetWidth", ScanProfile.WiaOffsetWidth);
            jsonScanData.Add("wiaRetryOnFailure", ScanProfile.WiaRetryOnFailure);
            jsonScanData.Add("wiaDelayBetweenScans", ScanProfile.WiaDelayBetweenScans);
            jsonScanData.Add("wiaDelayBetweenScansSeconds", ScanProfile.WiaDelayBetweenScansSeconds);
            jsonScanData.Add("flipDuplexedPages", ScanProfile.FlipDuplexedPages);
            // se aumentan los campos para guardar automaticamente:
            jsonScanData.Add("repositoryIdParent", EntityNodo.Id);
            jsonScanData.Add("repositoryNameParent", EntityNodo.Nombre);
            jsonScanData.Add("repositoryDocumentClass", EntityNodo.DocumentClass);
            jsonScanData.Add("repositoryDomain", EntityNodo.Domain);

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
                Repository = CurrentRepository
                
            };
            EntityNodo.ScanPerfil = jsonScanData.ToString().Replace("\r\n", "").Replace("\n", "").Replace("\r", "").Replace("\\", "");


    }
    //Guardar Perfil de Escaneo
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
                //Manejo de errores 
                String locale = "es_mx";
                String resultadoError = "";
                try
                {
                    String resultado = putMetodo(ResultadoLogin.token, EntityNodo.ScanPerfil);
                    JObject jObject = JObject.Parse(resultado);
                    resultadoError = MensajeError(locale, jObject.GetValue("status").ToString());
                    JObject jObjectexito = JObject.Parse(resultadoError);
                    JObject jObjectMsgError = JObject.Parse(jObjectexito.GetValue("exito").ToString());
                    if (jObjectMsgError.GetValue("message").ToString() != "Operación Exitosa")
                    {
                        MessageBox.Show(jObjectMsgError.GetValue("message").ToString());
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
        //Servicio de Manejo de Errores
        public String MensajeError(String locale, String id)
        {
            String url = AquariusUtils.getHostNamePath() + "error/";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            String resultado = "";
            try
            {
                request.Headers["locale"] = locale;
                request.Headers["id"] = id;
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.UTF8);
                    return reader.ReadToEnd();
                }
            }
            catch (WebException ex)
            {
                MessageBox.Show(ex.Message);

            }
            return resultado;
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
       
     
        //Metodo put
        public String putMetodo(String token, String profile)
        {
            String url = AquariusUtils.getHostNamePath() + "scan/profile/";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = "PUT";

            String resultado = "";
            try
            {
                request.Headers["token"] = token;
                request.Headers["profile"] = profile;
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.UTF8);
                    return reader.ReadToEnd();
                }
            }
            catch (WebException ex)
            {
                MessageBox.Show(ex.Message);

            }
            return resultado;
        }//Fin metodo put

        //=============================SERVICIO DE ESCANEO DE PERFILES==================================//
        //Servicio Scan Perfil ADD/UPDATE
        public static String ScanPerfilAdd(String token, String profile)
        {
            String url = AquariusUtils.getHostNamePath() + "scan/profile/";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);

            String resultado = "";
            try
            {
                request.Headers["token"] = token;
                request.Headers["profile"] = profile;
                request.Method = "PUT";
                //request.ContentType = "application/json";
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.UTF8);
                    return reader.ReadToEnd();
                }
            }
            catch (WebException ex)
            {
                MessageBox.Show(ex.Message);

            }
            return resultado;
        }
        //Fin ScanPerfilAdd

        //Servicio Scan Perfil GET
        public static String ScanPerfilGet(String token)
        {
            String url = AquariusUtils.getHostNamePath() + "scan/profile/";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);

            String resultado = "";
            try
            {
                request.Headers["token"] = token;
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.UTF8);
                    return reader.ReadToEnd();
                }
            }
            catch (WebException ex)
            {
                MessageBox.Show(ex.Message);

            }
            return resultado;
        }
        //Fin ScanPerfilGet

        //Servicio Scan Perfil DELETE
        public static String ScanPerfilDelete(String token, String perfil)
        {
            String url = AquariusUtils.getHostNamePath() + "scan/profile/";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);

            String resultado = "";
            try
            {
                request.Headers["token"] = token;
                request.Headers["perfil"] = perfil;
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.UTF8);
                    return reader.ReadToEnd();
                }
            }
            catch (WebException ex)
            {
                MessageBox.Show(ex.Message);

            }
            return resultado;
        }

        private void FEditProfile_Load(object sender, EventArgs e)
        {

            if (EntityNodo.displayName==null)
            {
                txtName.Enabled=true;
            }
            else
            {
                txtName.Enabled = false;
            }
        }
        //Fin ScanPerfilDELETE
        //=============================FIN SERVICIO DE ESCANEO DE PERFILES==================================//
    }//Fin SaveSettings

}

