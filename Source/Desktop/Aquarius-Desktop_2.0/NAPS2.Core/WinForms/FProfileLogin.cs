using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Windows.Forms;
using NAPS2.Config;
using NAPS2.Entity;
using NAPS2.ImportExport;
using NAPS2.Lang.Resources;
using NAPS2.Scan;
using NAPS2.Scan.Images;
using NAPS2.Util;
using Newtonsoft.Json.Linq;
using treeView_Data;

namespace NAPS2.WinForms
{
    public partial class FProfileLogin : FormBase
    {
        private const int DEFAULT_PROFILE_ICON_ID = 3;
        private const int LOCK_PROFILE_ICON_ID = 4;
        private const int DEFAULT_LOCK_PROFILE_ICON_ID = 5;

        private readonly IProfileManager profileManager;
        private readonly AppConfigManager appConfigManager;
        private readonly IconButtonSizer iconButtonSizer;
        private readonly IScanPerformer scanPerformer;
        private readonly ProfileNameTracker profileNameTracker;

        public FProfileLogin(IProfileManager profileManager, AppConfigManager appConfigManager, IconButtonSizer iconButtonSizer, IScanPerformer scanPerformer, ProfileNameTracker profileNameTracker)
        {
            this.profileManager = profileManager;
            this.appConfigManager = appConfigManager;
            this.iconButtonSizer = iconButtonSizer;
            this.scanPerformer = scanPerformer;
            this.profileNameTracker = profileNameTracker;
            InitializeComponent();
            
        }

        public Action<ScannedImage> ImageCallback { get; set; }

        private ScanProfile SelectedProfile
        {
            get
            {
                if (lvProfiles2.SelectedIndices.Count == 1)
                {
                    return profileManager.Profiles[lvProfiles2.SelectedIndices[0]];
                }
                return null;
            }
        }

        private bool SelectionLocked
        {
            get
            {
                return profileManager.Profiles.ElementsAt(lvProfiles2.SelectedIndices.OfType<int>()).Any(x => x.IsLocked);
            }
        }

        protected override void OnLoad(object sender, EventArgs e)
        {
            lvProfiles2.LargeImageList = ilProfileIcons.IconsList;
            /*btnAdd.Enabled = !(appConfigManager.Config.NoUserProfiles && profileManager.Profiles.Any(x => x.IsLocked));
            btnEdit.Enabled = false;
            btnDelete.Enabled = false;*/
            UpdateProfiles();
            SelectProfile(x => x.IsDefault);

            if (appConfigManager.Config.NoUserProfiles && profileManager.Profiles.Any(x => x.IsLocked))
            {
                ctxScan.Visible = false;
                toolStripSeparator1.Visible = false;
                ctxEdit.Visible = false;
                ctxDelete.Visible = false;
                ctxSetDefault.Visible = true;
                toolStripSeparator2.Visible = false;
                ctxCopy.Visible = false;
                ctxPaste.Visible = false;
                contextMenuStrip.Items.Remove(ctxCopy);
                contextMenuStrip.Items.Remove(ctxPaste);
                contextMenuStrip.Items.Remove(toolStripSeparator2);
            }

            /* var lm = new LayoutManager(this)
                 .Bind(lvProfiles)
                     .WidthToForm()
                     .HeightToForm()
                 .Bind(btnAdd, btnEdit, btnDelete, btnDone)
                     .BottomToForm()
                 .Bind(btnDone, btnScan)
                     .RightToForm()
                 .Bind(btnEdit)
                     .LeftTo(() => btnAdd.Right)
                 .Bind(btnDelete)
                     .LeftTo(() => btnEdit.Right)
                 .Activate();

             iconButtonSizer.WidthOffset = 20;
             iconButtonSizer.PaddingRight = 4;
             iconButtonSizer.MaxWidth = 100;
             iconButtonSizer.ResizeButtons(btnAdd, btnEdit, btnDelete);

             lm.UpdateLayout();*/
        }

        private void UpdateProfiles()
        {
            lvProfiles2.Items.Clear();
            foreach (var profile in profileManager.Profiles)
            {
                lvProfiles2.Items.Add(profile.DisplayName,
                    profile.IsDefault
                        ? profile.IsLocked ? DEFAULT_LOCK_PROFILE_ICON_ID : DEFAULT_PROFILE_ICON_ID
                        : profile.IsLocked ? LOCK_PROFILE_ICON_ID : profile.IconID);
            }
        }

        private void SelectProfile(Func<ScanProfile, bool> pred)
        {
            int i = 0;
            foreach (var profile in profileManager.Profiles)
            {
                if (pred(profile))
                {
                    lvProfiles2.Items[i].Selected = true;
                }
                i++;
            }
            if (profileManager.Profiles.Count == 1)
            {
                lvProfiles2.Items[0].Selected = true;
            }
        }

        private void btnAdd_Click(object sender, EventArgs e)
        {
            var fedit = FormFactory.Create<FEditProfile>();
            fedit.ScanProfile = appConfigManager.Config.DefaultProfileSettings ?? new ScanProfile { Version = ScanProfile.CURRENT_VERSION };
            fedit.ShowDialog();
            if (fedit.Result)
            {
                AddProfile(fedit.ScanProfile);
            }
        }

        private void btnEdit_Click(object sender, EventArgs e)
        {
            if (lvProfiles2.SelectedItems.Count > 0)
            {
                int profileIndex = lvProfiles2.SelectedItems[0].Index;
                var fedit = FormFactory.Create<FEditProfile>();
                fedit.ScanProfile = profileManager.Profiles[profileIndex];
                fedit.ShowDialog();
                if (fedit.Result)
                {
                    profileManager.Profiles[profileIndex] = fedit.ScanProfile;
                    profileManager.Save();
                    UpdateProfiles();
                    SelectProfile(x => x == fedit.ScanProfile);
                    lvProfiles2.SelectedIndices.Add(profileIndex);
                }
                else
                {
                    // Rollback
                    profileManager.Load();
                }
            }
        }

        private void lvProfiles_SelectedIndexChanged(object sender, EventArgs e)
        {
            /*btnEdit.Enabled = lvProfiles.SelectedItems.Count == 1;
            btnDelete.Enabled = lvProfiles.SelectedItems.Count > 0 && !SelectionLocked;*/
        }

        private void btnDelete_Click(object sender, EventArgs e)
        {
            if (lvProfiles2.SelectedItems.Count > 0 && !SelectionLocked)
            {
                string message = lvProfiles2.SelectedIndices.Count == 1
                    ? string.Format(MiscResources.ConfirmDeleteSingleProfile, profileManager.Profiles[lvProfiles2.SelectedIndices[0]].DisplayName)
                    : string.Format(MiscResources.ConfirmDeleteMultipleProfiles, lvProfiles2.SelectedIndices.Count);
                if (MessageBox.Show(message, MiscResources.Delete, MessageBoxButtons.YesNo, MessageBoxIcon.Exclamation) == DialogResult.Yes)
                {
                    foreach (var profile in profileManager.Profiles.ElementsAt(lvProfiles2.SelectedIndices.OfType<int>()))
                    {
                        profileNameTracker.DeletingProfile(profile.DisplayName);
                    }
                    profileManager.Profiles.RemoveAll(lvProfiles2.SelectedIndices.OfType<int>());
                    if (profileManager.Profiles.Count == 1)
                    {
                        profileManager.DefaultProfile = profileManager.Profiles.First();
                    }
                    profileManager.Save();
                    UpdateProfiles();
                    lvProfiles_SelectedIndexChanged(null, null);
                }
            }
        }

        private void lvProfiles_ItemActivate(object sender, EventArgs e)
        {
            if (SelectedProfile != null)
            {
                profileManager.DefaultProfile = SelectedProfile;
                profileManager.Save();

                UpdateProfiles();
                SelectProfile(x => x.IsDefault);

                //PerformScan();
            }
        }

        private void lvProfiles_KeyDown(object sender, KeyEventArgs e)
        {
           /* if (e.KeyData == Keys.Delete && btnDelete.Enabled)
            {
                btnDelete_Click(null, null);
            }*/
        }

        private void btnDone_Click(object sender, EventArgs e)
        {
            
            
             this.Hide();
            
                //Abrir Desktop
                var form = FormFactory.Create<FDesktop>();
                form.ShowDialog();
           
         

        }
       /*/GET PROFILE
        private void LoadScanPerfilGet() {
            String token = ResultadoLogin.exito;
            String profile = ResultadoLogin.exito;
            String json = ScanPerfilGet(token);
            JObject jsonResultado = JObject.Parse(json);
            JArray jsonExito = JArray.FromObject(jsonResultado.GetValue("exito"));
            JObject exito = JObject.Parse(jsonExito[0].ToString());
            JObject profileObj = JObject.Parse(exito.GetValue("profile").ToString());
            //MessageBox.Show(profileObj.ToString());
            EntityNodo.ScanPerfil = profileObj.ToString();
        }*/
       

        

        private void btnScan_Click(object sender, EventArgs e)
        {
            PerformScan();
        }

        private void PerformScan()
        {
            if (profileManager.Profiles.Count == 0)
            {
                var editSettingsForm = FormFactory.Create<FEditProfile>();
                editSettingsForm.ScanProfile = new ScanProfile
                {
                    Version = ScanProfile.CURRENT_VERSION
                };
                editSettingsForm.ShowDialog();
                if (!editSettingsForm.Result)
                {
                    return;
                }
                profileManager.Profiles.Add(editSettingsForm.ScanProfile);
                profileManager.DefaultProfile = editSettingsForm.ScanProfile;
                profileManager.Save();
                UpdateProfiles();
                lvProfiles2.SelectedIndices.Add(0);
            }
            if (SelectedProfile == null)
            {
                MessageBox.Show(MiscResources.SelectProfileBeforeScan, MiscResources.ChooseProfile, MessageBoxButtons.OK,
                    MessageBoxIcon.Warning);
                return;
            }
            if (profileManager.DefaultProfile == null)
            {
                var profile = SelectedProfile;
                profileManager.DefaultProfile = profile;
                UpdateProfiles();
                SelectProfile(x => x == profile);
            }
            profileManager.Save();
            scanPerformer.PerformScan(SelectedProfile, new ScanParams(), this, null, ImageCallback);
            Activate();
        }

        private void contextMenuStrip_Opening(object sender, System.ComponentModel.CancelEventArgs e)
        {
            var ido = Clipboard.GetDataObject();
            var canPaste = ido != null && ido.GetDataPresent(typeof (DirectProfileTransfer).FullName);
            if (SelectedProfile == null)
            {
                if (canPaste)
                {
                    /*foreach (ToolStripItem item in contextMenuStrip.Items)
                    {
                        if (item != ctxPaste)
                        {
                            item.Visible = false;
                        }
                    }
                   ctxPaste.Enabled = true;*/
                }
                else
                {
                    e.Cancel = true;
                }
            }
            else
            {
                foreach (ToolStripItem item in contextMenuStrip.Items)
                {
                    item.Visible = true;
                }
                ctxSetDefault.Enabled = !SelectedProfile.IsDefault;
                ctxScan.Visible = false;
                toolStripSeparator1.Visible = false;
                ctxEdit.Visible = false;
                ctxDelete.Visible = false;
                toolStripSeparator2.Visible = false;
                ctxCopy.Visible = false;
                ctxPaste.Visible = false;
            }
        }

        private void ctxScan_Click(object sender, EventArgs e)
        {
            PerformScan();
        }

        private void ctxEdit_Click(object sender, EventArgs e)
        {
            btnEdit_Click(null, null);
        }

        private void ctxDelete_Click(object sender, EventArgs e)
        {
            btnDelete_Click(null, null);
        }

        private void ctxSetDefault_Click(object sender, EventArgs e)
        {
            if (SelectedProfile != null)
            {
                profileManager.DefaultProfile = SelectedProfile;
                profileManager.Save();

                UpdateProfiles();
                SelectProfile(x => x.IsDefault);
            }
        }

        private void AddProfile(ScanProfile profile)
        {
            profileManager.Profiles.Add(profile);
            if (profileManager.Profiles.Count == 1)
            {
                profileManager.DefaultProfile = profile;
            }
            UpdateProfiles();
            SelectProfile(x => x == profile);
            profileManager.Save();
        }

        private IDataObject GetSelectedProfileDataObject()
        {
            IDataObject ido = new DataObject();
            int profileIndex = lvProfiles2.SelectedItems[0].Index;
            var profile = profileManager.Profiles[profileIndex];
            ido.SetData(typeof(DirectProfileTransfer), new DirectProfileTransfer(profile));
            return ido;
        }

        private void ctxCopy_Click(object sender, EventArgs e)
        {
            Clipboard.SetDataObject(GetSelectedProfileDataObject());
        }

        private void ctxPaste_Click(object sender, EventArgs e)
        {
            if (appConfigManager.Config.NoUserProfiles && profileManager.Profiles.Any(x => x.IsLocked))
            {
                return;
            }
            var ido = Clipboard.GetDataObject();
            if (ido == null)
            {
                return;
            }
            if (ido.GetDataPresent(typeof(DirectProfileTransfer).FullName))
            {
                var data = (DirectProfileTransfer)ido.GetData(typeof(DirectProfileTransfer).FullName);
                AddProfile(data.ScanProfile);
            }
        }

        #region Drag/Drop

        private void lvProfiles_ItemDrag(object sender, ItemDragEventArgs e)
        {
            // Provide drag data
            if (lvProfiles2.SelectedItems.Count > 0)
            {
                var ido = GetSelectedProfileDataObject();
                DoDragDrop(ido, DragDropEffects.Move | DragDropEffects.Copy);
            }
        }

        private void lvProfiles_DragEnter(object sender, DragEventArgs e)
        {
            // Determine if drop data is compatible
            if (appConfigManager.Config.NoUserProfiles && profileManager.Profiles.Any(x => x.IsLocked))
            {
                return;
            }
            try
            {
                if (e.Data.GetDataPresent(typeof(DirectProfileTransfer).FullName))
                {
                    var data = (DirectProfileTransfer)e.Data.GetData(typeof(DirectProfileTransfer).FullName);
                    e.Effect = data.ProcessID == Process.GetCurrentProcess().Id
                        ? data.Locked
                            ? DragDropEffects.None
                            : DragDropEffects.Move
                        : DragDropEffects.Copy;
                }
            }
            catch (Exception ex)
            {
                Log.ErrorException("Error receiving drag/drop", ex);
            }
        }

        private void lvProfiles_DragDrop(object sender, DragEventArgs e)
        {
            // Receive drop data
            if (e.Data.GetDataPresent(typeof(DirectProfileTransfer).FullName))
            {
                var data = (DirectProfileTransfer)e.Data.GetData(typeof(DirectProfileTransfer).FullName);
                if (data.ProcessID == Process.GetCurrentProcess().Id)
                {
                    DragMoveProfile(e);
                }
                else
                {
                    if (!(appConfigManager.Config.NoUserProfiles && profileManager.Profiles.Any(x => x.IsLocked)))
                    {
                        AddProfile(data.ScanProfile);
                    }
                }
            }
            lvProfiles2.InsertionMark.Index = -1;
        }

        private void lvProfiles_DragLeave(object sender, EventArgs e)
        {
            lvProfiles2.InsertionMark.Index = -1;
        }

        private void DragMoveProfile(DragEventArgs e)
        {
            if (lvProfiles2.SelectedItems.Count == 0)
            {
                return;
            }
            int index = GetDragIndex(e);
            if (index != -1)
            {
                var selectedIndex = lvProfiles2.SelectedItems[0].Index;
                var selectedProfile = profileManager.Profiles[selectedIndex];
                if (selectedProfile.IsLocked)
                {
                    return;
                }
                while (index < profileManager.Profiles.Count && profileManager.Profiles[index].IsLocked)
                {
                    index++;
                }
                profileManager.Profiles.RemoveAt(selectedIndex);
                if (index > selectedIndex)
                {
                    index--;
                }
                profileManager.Profiles.Insert(index, selectedProfile);
                UpdateProfiles();
                SelectProfile(x => x == selectedProfile);
                profileManager.Save();
            }
        }

        private void lvProfiles_DragOver(object sender, DragEventArgs e)
        {
            if (e.Effect == DragDropEffects.Move)
            {
                var index = GetDragIndex(e);
                if (index == profileManager.Profiles.Count)
                {
                    lvProfiles2.InsertionMark.Index = index - 1;
                    lvProfiles2.InsertionMark.AppearsAfterItem = true;
                }
                else
                {
                    lvProfiles2.InsertionMark.Index = index;
                    lvProfiles2.InsertionMark.AppearsAfterItem = false;
                }
            }
        }

        private int GetDragIndex(DragEventArgs e)
        {
            Point cp = lvProfiles2.PointToClient(new Point(e.X, e.Y));
            ListViewItem dragToItem = lvProfiles2.GetItemAt(cp.X, cp.Y);
            if (dragToItem == null)
            {
                var items = lvProfiles2.Items.Cast<ListViewItem>().ToList();
                var minY = items.Select(x => x.Bounds.Top).Min();
                var maxY = items.Select(x => x.Bounds.Bottom).Max();
                if (cp.Y < minY)
                {
                    cp.Y = minY;
                }
                if (cp.Y > maxY)
                {
                    cp.Y = maxY;
                }
                var row = items.Where(x => x.Bounds.Top <= cp.Y && x.Bounds.Bottom >= cp.Y).OrderBy(x => x.Bounds.X).ToList();
                dragToItem = row.FirstOrDefault(x => x.Bounds.Right >= cp.X) ?? row.LastOrDefault();
            }
            if (dragToItem == null)
            {
                return -1;
            }
            int dragToIndex = dragToItem.Index;
            if (cp.X > (dragToItem.Bounds.X + dragToItem.Bounds.Width / 2))
            {
                dragToIndex++;
            }
            return dragToIndex;
        }

        #endregion

        private void FProfileLogin_Load(object sender, EventArgs e)
        {

        }

        private void lvProfiles2_DoubleClick(object sender, EventArgs e)
        {
            Console.Write("Doble Click");
        }
    }
}
