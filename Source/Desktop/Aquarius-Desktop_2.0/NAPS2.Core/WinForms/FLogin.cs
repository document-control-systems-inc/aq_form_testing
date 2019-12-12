#region Usings

using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Threading;
using System.Windows.Forms;
using System.Xml;
using NAPS2.Config;
using NAPS2.Entity;
using NAPS2.ImportExport;
using NAPS2.Ocr;
using NAPS2.Operation;
using NAPS2.Recovery;
using NAPS2.Scan;
using NAPS2.Scan.Images;
using NAPS2.Util;
using Newtonsoft.Json.Linq;

#endregion

namespace NAPS2.WinForms
{
    public partial class FLogin : FormBase
    {
        #region Dependencies

        private readonly StringWrapper stringWrapper;
        private readonly AppConfigManager appConfigManager;
        private readonly RecoveryManager recoveryManager;
        private readonly OcrDependencyManager ocrDependencyManager;
        private readonly IProfileManager profileManager;
        private readonly IScanPerformer scanPerformer;
        private readonly IScannedImagePrinter scannedImagePrinter;
        private readonly ChangeTracker changeTracker;
        private readonly StillImage stillImage;
        private readonly IOperationFactory operationFactory;
        private readonly IUserConfigManager userConfigManager;
        private readonly KeyboardShortcutManager ksm;
        private readonly WinFormsExportHelper exportHelper;
        private readonly ScannedImageRenderer scannedImageRenderer;

        #endregion

        #region State Fields

        private readonly ScannedImageList imageList = new ScannedImageList();
        private CancellationTokenSource renderThumbnailsCts;
        private LayoutManager layoutManager;
        private bool disableSelectedIndexChangedEvent;
        private readonly ThumbnailRenderer thumbnailRenderer;
        private NotificationManager notify;
        private static string locale;
        private static string id;

        #endregion

        #region Initialization and Culture

        public FLogin(StringWrapper stringWrapper, AppConfigManager appConfigManager, RecoveryManager recoveryManager, OcrDependencyManager ocrDependencyManager, IProfileManager profileManager, IScanPerformer scanPerformer, IScannedImagePrinter scannedImagePrinter, ChangeTracker changeTracker, StillImage stillImage, IOperationFactory operationFactory, IUserConfigManager userConfigManager, KeyboardShortcutManager ksm, ThumbnailRenderer thumbnailRenderer, WinFormsExportHelper exportHelper, ScannedImageRenderer scannedImageRenderer)
        {
            this.stringWrapper = stringWrapper;
            this.appConfigManager = appConfigManager;
            this.recoveryManager = recoveryManager;
            this.ocrDependencyManager = ocrDependencyManager;
            this.profileManager = profileManager;
            this.scanPerformer = scanPerformer;
            this.scannedImagePrinter = scannedImagePrinter;
            this.changeTracker = changeTracker;
            this.stillImage = stillImage;
            this.operationFactory = operationFactory;
            this.userConfigManager = userConfigManager;
            this.ksm = ksm;
            this.thumbnailRenderer = thumbnailRenderer;
            this.exportHelper = exportHelper;
            this.scannedImageRenderer = scannedImageRenderer;
            InitializeComponent();

            Shown += FLogin_Shown;
        }

        protected override void OnLoad(object sender, EventArgs eventArgs)
        {
            PostInitializeComponent();
        }

        /// <summary>
        /// Runs when the form is first loaded and every time the language is changed.
        /// </summary>
        private void PostInitializeComponent()
        {
            
        }

        private void InitLanguageDropdown()
        {
            
        }

        private void RelayoutToolbar()
        {
            
        }

        private void ResetToolbarMargin()
        {
            
        }

        private void ShrinkToolbarMargin()
        {
            
        }

        private void SetCulture(string cultureId)
        {
        }

        private void FLogin_Shown(object sender, EventArgs e)
        {
            
        }

        #endregion



        private void btnLogin_Click(object sender, EventArgs e)
        {
            
            if (AquariusUtils.login(txtUsuario.Text, txtPass.Text))
            {
                int numPerfiles = AquariusUtils.getScanProfile();
                //==================FIN========================//
                if (numPerfiles > -1)
                {
                    this.Hide();
                    if (numPerfiles > 0)
                    {
                        var form = FormFactory.Create<FProfileLogin>();
                        form.ShowDialog();
                    }
                    else
                    {
                        var form = FormFactory.Create<FDesktop>();
                        form.ShowDialog();
                    }
                    AquariusUtils.logout(ResultadoLogin.token);
                    Close();
                } else
                {
                    AquariusUtils.logout(ResultadoLogin.token);
                }
            }
        }
        


        private void FLogin_Load(object sender, EventArgs e)
        {
            AquariusUtils.setUserConfig(UserConfigManager);
        }

        private void txtPass_TextChanged(object sender, EventArgs e)
        {

        }

        private void txtUsuario_TextChanged(object sender, EventArgs e)
        {

        }

        private void label2_Click(object sender, EventArgs e)
        {

        }

        private void txtPass_KeyDown(object sender, KeyEventArgs e)
        {
            //Ejecuta el evento del btnLogin al dar Enter
            if (e.KeyCode == Keys.Enter) {
               btnLogin_Click(this, new EventArgs());
            }
        }
    }
}
