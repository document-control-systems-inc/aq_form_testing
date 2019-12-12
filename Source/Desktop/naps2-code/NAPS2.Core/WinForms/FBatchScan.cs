using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using NAPS2.Config;
using NAPS2.Entity;
using NAPS2.ImportExport;
using NAPS2.Lang.Resources;
using NAPS2.Scan;
using NAPS2.Scan.Batch;
using NAPS2.Scan.Exceptions;
using NAPS2.Scan.Images;
using NAPS2.Scan.Twain;
using NAPS2.Util;
using treeView_Data;

namespace NAPS2.WinForms
{
    public partial class FBatchScan : FormBase
    {
        public const string PATCH_CODE_INFO_URL = "http://www.naps2.com/doc-batch-scan.html#patch-t";

        private readonly IProfileManager profileManager;
        private readonly AppConfigManager appConfigManager;
        private readonly IconButtonSizer iconButtonSizer;
        private readonly IScanPerformer scanPerformer;
        private readonly IUserConfigManager userConfigManager;
        private readonly BatchScanPerformer batchScanPerformer;
        private readonly IErrorOutput errorOutput;
        private readonly ThreadFactory threadFactory;
        private readonly DialogHelper dialogHelper;

        private bool batchRunning = false;
        private bool cancelBatch = false;
        private Thread batchThread;
        private string resultadoCombo;

        public FBatchScan(IProfileManager profileManager, AppConfigManager appConfigManager, IconButtonSizer iconButtonSizer, IScanPerformer scanPerformer, IUserConfigManager userConfigManager, BatchScanPerformer batchScanPerformer, IErrorOutput errorOutput, ThreadFactory threadFactory, DialogHelper dialogHelper)
        {
            this.profileManager = profileManager;
            this.appConfigManager = appConfigManager;
            this.iconButtonSizer = iconButtonSizer;
            this.scanPerformer = scanPerformer;
            this.userConfigManager = userConfigManager;
            this.batchScanPerformer = batchScanPerformer;
            this.errorOutput = errorOutput;
            this.threadFactory = threadFactory;
            this.dialogHelper = dialogHelper;
            InitializeComponent();

            RestoreFormState = false;
        }

        public Action<ScannedImage> ImageCallback { get; set; }

        private BatchSettings BatchSettings { get; set; }

        protected override void OnLoad(object sender, EventArgs eventArgs)
        {
            new LayoutManager(this)
                .Bind(groupboxScanConfig, groupboxOutput,
                      panelSaveSeparator, panelSaveTo, panelSaveType, panelScanDetails, panelScanType,
                      comboProfile, txtFilePath, lblStatus)
                    .WidthToForm()
                .Bind(btnEditProfile, btnAddProfile, btnStart, btnCancel, btnChooseFolder)
                    .RightToForm()
                .Activate();

            btnAddProfile.Enabled = !(appConfigManager.Config.NoUserProfiles && profileManager.Profiles.Any(x => x.IsLocked));

            ConditionalControls.LockHeight(this);

            BatchSettings = userConfigManager.Config.LastBatchSettings ?? new BatchSettings();
            UpdateUIFromSettings();
        }

        private void UpdateUIFromSettings()
        {
            UpdateProfiles();

            rdSingleScan.Checked = BatchSettings.ScanType == BatchScanType.Single;
            rdMultipleScansPrompt.Checked = BatchSettings.ScanType == BatchScanType.MultipleWithPrompt;
            rdMultipleScansDelay.Checked = BatchSettings.ScanType == BatchScanType.MultipleWithDelay;

            // TODO: Verify culture (+ vaildation ofc)
            txtNumberOfScans.Text = BatchSettings.ScanCount.ToString(CultureInfo.CurrentCulture);
            txtTimeBetweenScans.Text = BatchSettings.ScanIntervalSeconds.ToString(CultureInfo.CurrentCulture);

            rdLoadIntoNaps2.Checked = BatchSettings.OutputType == BatchOutputType.Load;
            rdSaveToSingleFile.Checked = BatchSettings.OutputType == BatchOutputType.SingleFile;
            rdSaveToMultipleFiles.Checked = BatchSettings.OutputType == BatchOutputType.MultipleFiles;

            rdFilePerScan.Checked = BatchSettings.SaveSeparator == SaveSeparator.FilePerScan;
            rdFilePerPage.Checked = BatchSettings.SaveSeparator == SaveSeparator.FilePerPage;
            rdSeparateByPatchT.Checked = BatchSettings.SaveSeparator == SaveSeparator.PatchT;

            txtFilePath.Text = BatchSettings.SavePath;
        }

        private bool ValidateSettings()
        {
            bool ok = true;

            BatchSettings.ProfileDisplayName = comboProfile.Text;
            if (comboProfile.SelectedIndex == -1)
            {
                ok = false;
                comboProfile.Focus();
            }

            BatchSettings.ScanType = rdMultipleScansPrompt.Checked ? BatchScanType.MultipleWithPrompt
                                   : rdMultipleScansDelay.Checked ? BatchScanType.MultipleWithDelay
                                   : BatchScanType.Single;

            if (rdMultipleScansDelay.Checked)
            {
                int scanCount;
                if (!int.TryParse(txtNumberOfScans.Text, out scanCount) || scanCount <= 0)
                {
                    ok = false;
                    scanCount = 0;
                    txtNumberOfScans.Focus();
                }
                BatchSettings.ScanCount = scanCount;

                double scanInterval;
                if (!double.TryParse(txtTimeBetweenScans.Text, out scanInterval) || scanInterval < 0)
                {
                    ok = false;
                    scanInterval = 0;
                    txtTimeBetweenScans.Focus();
                }
                BatchSettings.ScanIntervalSeconds = scanInterval;
            }

            BatchSettings.OutputType = rdSaveToSingleFile.Checked ? BatchOutputType.SingleFile
                                     : rdSaveToMultipleFiles.Checked ? BatchOutputType.MultipleFiles
                                     : BatchOutputType.Load;

            BatchSettings.SaveSeparator = rdFilePerScan.Checked ? SaveSeparator.FilePerScan
                                        : rdSeparateByPatchT.Checked ? SaveSeparator.PatchT
                                        : SaveSeparator.FilePerPage;

            BatchSettings.SavePath = txtFilePath.Text;
            if (BatchSettings.OutputType != BatchOutputType.Load && string.IsNullOrWhiteSpace(BatchSettings.SavePath))
            {
                ok = false;
                txtFilePath.Focus();
            }

            return ok;
        }

        private void UpdateProfiles()
        {
            comboProfile.Items.Clear();
            comboProfile.Items.AddRange(profileManager.Profiles.Cast<object>().ToArray());
            if (BatchSettings.ProfileDisplayName != null &&
                profileManager.Profiles.Any(x => x.DisplayName == BatchSettings.ProfileDisplayName))
            {
                comboProfile.Text = BatchSettings.ProfileDisplayName;
            }
            else if (profileManager.DefaultProfile != null)
            {
                comboProfile.Text = profileManager.DefaultProfile.DisplayName;
            }
            else
            {
                comboProfile.Text = "";
            }
        }

        private void rdSingleScan_CheckedChanged(object sender, EventArgs e)
        {
            ConditionalControls.UnlockHeight(this);
            ConditionalControls.SetVisible(rdFilePerScan, !rdSingleScan.Checked && rdSaveToMultipleFiles.Checked);
            ConditionalControls.LockHeight(this);
        }

        private void rdMultipleScansDelay_CheckedChanged(object sender, EventArgs e)
        {
            ConditionalControls.UnlockHeight(this);
            ConditionalControls.SetVisible(panelScanDetails, rdMultipleScansDelay.Checked);
            ConditionalControls.LockHeight(this);
        }

        private void rdLoadIntoNaps2_CheckedChanged(object sender, EventArgs e)
        {
            ConditionalControls.UnlockHeight(this);
            ConditionalControls.SetVisible(panelSaveTo, !rdLoadIntoNaps2.Checked);
            ConditionalControls.LockHeight(this);
        }

        private void rdSaveToMultipleFiles_CheckedChanged(object sender, EventArgs e)
        {
            ConditionalControls.UnlockHeight(this);
            ConditionalControls.SetVisible(panelSaveSeparator, rdSaveToMultipleFiles.Checked);
            ConditionalControls.SetVisible(rdFilePerScan, !rdSingleScan.Checked && rdSaveToMultipleFiles.Checked);
            ConditionalControls.LockHeight(this);
        }

        private void btnChooseFolder_Click(object sender, EventArgs e)
        {
            string savePath;
            if (dialogHelper.PromptToSavePdfOrImage(null, out savePath))
            {
                txtFilePath.Text = savePath;
            }
        }

        private void linkPatchCodeInfo_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            Process.Start(PATCH_CODE_INFO_URL);
        }

        private void comboProfile_Format(object sender, ListControlConvertEventArgs e)
        {
            e.Value = ((ScanProfile)e.ListItem).DisplayName;
        }

        private void btnEditProfile_Click(object sender, EventArgs e)
        {
            if (comboProfile.SelectedItem != null)
            {
                EntityNodo.displayName = "edit";
                var fedit = FormFactory.Create<FEditProfile>();
                fedit.ScanProfile = (ScanProfile)comboProfile.SelectedItem;
                fedit.ShowDialog();
                if (fedit.Result)
                {
                    profileManager.Profiles[comboProfile.SelectedIndex] = fedit.ScanProfile;
                    profileManager.Save();
                    BatchSettings.ProfileDisplayName = fedit.ScanProfile.DisplayName;
                    UpdateProfiles();
                }
            }
        }

        private void btnAddProfile_Click(object sender, EventArgs e)
        {
            EntityNodo.displayName = null;
            if (!(appConfigManager.Config.NoUserProfiles && profileManager.Profiles.Any(x => x.IsLocked)))
            {
                var fedit = FormFactory.Create<FEditProfile>();
                fedit.ScanProfile = appConfigManager.Config.DefaultProfileSettings ?? new ScanProfile {Version = ScanProfile.CURRENT_VERSION};
                fedit.ShowDialog();
                if (fedit.Result)
                {
                    profileManager.Profiles.Add(fedit.ScanProfile);
                    profileManager.Save();
                    BatchSettings.ProfileDisplayName = fedit.ScanProfile.DisplayName;
                    UpdateProfiles();
                }
            }
        }

        private void btnStart_Click(object sender, EventArgs e)
        {
            
            EntityNodo.LstrutaFiles = null;

            if (batchRunning)
            {
                return;
            }
            if (!ValidateSettings())
            {
                return;
            }

            // Update state
            batchRunning = true;
            cancelBatch = false;

            // Update UI
            btnStart.Enabled = false;
            btnCancel.Enabled = true;
            btnCancel.Text = MiscResources.Cancel;
            EnableDisableSettings(false);

            // Start the batch
            batchThread = threadFactory.CreateThread(DoBatchScan);
            batchThread.Start();

            // Save settings for next time (could also do on form close)
            userConfigManager.Config.LastBatchSettings = BatchSettings;
            userConfigManager.Save();

            //Abrir ventana Treeview
            var formTree = FormFactory.Create<FTreeView>();
            formTree.ShowDialog();

        }
        //Subida de Archivos en boton Comenzar
        private async Task btnClicklScanLotes()
        {
            String TokenString = ResultadoLogin.token;
            String idParent = EntityNodo.Id;
            String domain = EntityNodo.Domain;
            String documentClass = EntityNodo.DocumentClass;
            String nombre = "Documento nuevo ";

            String resultado = "";

            if (EntityNodo.LstrutaFiles != null)
            {
                    foreach (string ruta in EntityNodo.LstrutaFiles)
                    {
                        try
                        {
                            DateTime fecha = DateTime.Now;
                            nombre = nombre + fecha.ToString("dd-MM-yyyy HH:mm:ss.zzz");

                            resultado = await uploadFileAsync(ruta, TokenString, idParent, domain, documentClass, nombre);
                            MessageBox.Show(resultado);
                            //Eliminar archivo local
                            File.Delete(ruta);
                            this.Close();
                        }
                        catch (Exception error)
                        {
                            MessageBox.Show(error.Message);
                        }
                    }  
            }
            this.Close();
        }
        //Fin subida Archivos

        //Servicio Subida de Archivos
        public async Task<String> uploadFileAsync(String filePath, String TokenString, String idParent, String domain, String documentClass, String nombre)
        {
            byte[] fileBytes = GetBytesAsync(filePath);
            String resultado = "";
            try
            {
                String contentMIMEType = mimeType.GetMimeType(Path.GetExtension(filePath));

                HttpClient httpClient = new HttpClient();
                MultipartFormDataContent form = new MultipartFormDataContent();

                ByteArrayContent byteContent = new ByteArrayContent(File.ReadAllBytes(filePath));
                byteContent.Headers.Remove("Content-Type");
                byteContent.Headers.Add("Content-Type", contentMIMEType);

                form.Add(new StringContent(TokenString), "token");
                form.Add(new StringContent(idParent), "idParent");
                form.Add(new StringContent(domain), "domain");
                form.Add(new StringContent(documentClass), "documentclass");
                form.Add(new StringContent(Path.GetFileName(filePath)), "filename");
                form.Add(new StringContent("{ \"title\": \"" + nombre + "\" }"), "properties");
                form.Add(byteContent, "file", Path.GetFileName(filePath));

                HttpResponseMessage response = await httpClient.PostAsync(AquariusUtils.getHostNamePath() + "document", form);

                response.EnsureSuccessStatusCode();
                httpClient.Dispose();
                resultado = response.Content.ReadAsStringAsync().Result;

            }
            catch (Exception error)
            {
                resultado = error.Message.ToString();
            }
            return resultado;
        }

        public byte[] GetBytesAsync(String fileName)
        {
            byte[] buffer = null;
            using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
            {
                buffer = new byte[fs.Length];
                fs.Read(buffer, 0, (int)fs.Length);
            }
            return buffer;
        }
        //Fin Subida de Archivos

        private void EnableDisableSettings(bool enabled)
        {
            EnableDisable(groupboxScanConfig, enabled);
            EnableDisable(groupboxOutput, enabled);
        }

        private void EnableDisable(Control root, bool enabled)
        {
            foreach (Control control in root.Controls)
            {
                if (control.Controls.Count > 0)
                {
                    EnableDisable(control, enabled);
                }
                else
                {
                    control.Enabled = enabled;
                }
            }
        }

        private void DoBatchScan()
        {
            try
            {
                batchScanPerformer.PerformBatchScan(BatchSettings, this,
                    image => SafeInvoke(() => ImageCallback(image)), ProgressCallback());
                SafeInvoke(() =>
                {
                    lblStatus.Text = cancelBatch
                        ? MiscResources.BatchStatusCancelled
                        : MiscResources.BatchStatusComplete;
                });
            }
            catch (ScanDriverException ex)
            {
                if (ex is ScanDriverUnknownException)
                {
                    Log.ErrorException("Error in batch scan", ex);
                    errorOutput.DisplayError(ex.Message, ex);
                }
                else
                {
                    errorOutput.DisplayError(ex.Message);
                }
            }
            catch (Exception ex)
            {
                Log.ErrorException("Error in batch scan", ex);
                errorOutput.DisplayError(MiscResources.BatchError, ex);
                SafeInvoke(() =>
                {
                    lblStatus.Text = MiscResources.BatchStatusError;
                });
            }
            SafeInvoke(() =>
            {
                batchRunning = false;
                cancelBatch = false;
                btnStart.Enabled = true;
                btnCancel.Enabled = true;
                btnCancel.Text = MiscResources.Close;
                EnableDisableSettings(true);
                Activate();
            });
        }

        private Func<string, bool> ProgressCallback()
        {
            return status =>
            {
                if (!cancelBatch)
                {
                    SafeInvoke(() =>
                    {
                        lblStatus.Text = status;
                    });
                }
                return !cancelBatch;
            };
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            if (batchRunning)
            {
                cancelBatch = true;
                btnCancel.Enabled = false;
                lblStatus.Text = MiscResources.BatchStatusCancelling;
            }
            else
            {
                Close();
            }
        }

        private void FBatchScan_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (cancelBatch)
            {
                // Keep dialog open while cancel is in progress to avoid concurrency issues
                e.Cancel = true;
            }
        }

        private void linkPlaceholders_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            var form = FormFactory.Create<FPlaceholders>();
            form.FileName = txtFilePath.Text;
            if (form.ShowDialog() == DialogResult.OK)
            {
                txtFilePath.Text = form.FileName;
            }
        }

        private void FBatchScan_Load(object sender, EventArgs e)
        {
           
        }

        private void groupboxOutput_Enter(object sender, EventArgs e)
        {

        }
    }
}