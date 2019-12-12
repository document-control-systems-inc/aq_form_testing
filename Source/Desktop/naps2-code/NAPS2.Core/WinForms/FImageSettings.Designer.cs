using System;
using System.Collections.Generic;
using System.Linq;

namespace NAPS2.WinForms
{
    partial class FImageSettings
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FImageSettings));
            this.btnOK = new System.Windows.Forms.Button();
            this.btnCancel = new System.Windows.Forms.Button();
            this.ilProfileIcons = new NAPS2.WinForms.ILProfileIcons(this.components);
            this.cbRememberSettings = new System.Windows.Forms.CheckBox();
            this.btnRestoreDefaults = new System.Windows.Forms.Button();
            this.txtDefaultFilePath = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.linkPlaceholders = new System.Windows.Forms.LinkLabel();
            this.txtJpegQuality = new System.Windows.Forms.TextBox();
            this.tbJpegQuality = new System.Windows.Forms.TrackBar();
            this.lblWarning = new System.Windows.Forms.Label();
            this.btnChooseFolder = new System.Windows.Forms.Button();
            this.cbSkipSavePrompt = new System.Windows.Forms.CheckBox();
            this.cmbFormat = new System.Windows.Forms.ComboBox();
            ((System.ComponentModel.ISupportInitialize)(this.tbJpegQuality)).BeginInit();
            this.SuspendLayout();
            // 
            // btnOK
            // 
            resources.ApplyResources(this.btnOK, "btnOK");
            this.btnOK.Name = "btnOK";
            this.btnOK.UseVisualStyleBackColor = true;
            this.btnOK.Click += new System.EventHandler(this.btnOK_Click);
            // 
            // btnCancel
            // 
            resources.ApplyResources(this.btnCancel, "btnCancel");
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.UseVisualStyleBackColor = true;
            this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
            // 
            // cbRememberSettings
            // 
            resources.ApplyResources(this.cbRememberSettings, "cbRememberSettings");
            this.cbRememberSettings.Name = "cbRememberSettings";
            this.cbRememberSettings.UseVisualStyleBackColor = true;
            // 
            // btnRestoreDefaults
            // 
            resources.ApplyResources(this.btnRestoreDefaults, "btnRestoreDefaults");
            this.btnRestoreDefaults.Name = "btnRestoreDefaults";
            this.btnRestoreDefaults.UseVisualStyleBackColor = true;
            this.btnRestoreDefaults.Click += new System.EventHandler(this.btnRestoreDefaults_Click);
            // 
            // txtDefaultFilePath
            // 
            resources.ApplyResources(this.txtDefaultFilePath, "txtDefaultFilePath");
            this.txtDefaultFilePath.Name = "txtDefaultFilePath";
            this.txtDefaultFilePath.TextChanged += new System.EventHandler(this.txtDefaultFilePath_TextChanged);
            // 
            // label1
            // 
            resources.ApplyResources(this.label1, "label1");
            this.label1.Name = "label1";
            // 
            // label2
            // 
            resources.ApplyResources(this.label2, "label2");
            this.label2.Name = "label2";
            // 
            // linkPlaceholders
            // 
            resources.ApplyResources(this.linkPlaceholders, "linkPlaceholders");
            this.linkPlaceholders.Name = "linkPlaceholders";
            this.linkPlaceholders.TabStop = true;
            this.linkPlaceholders.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.linkPlaceholders_LinkClicked);
            // 
            // txtJpegQuality
            // 
            resources.ApplyResources(this.txtJpegQuality, "txtJpegQuality");
            this.txtJpegQuality.Name = "txtJpegQuality";
            this.txtJpegQuality.TextChanged += new System.EventHandler(this.txtJpegQuality_TextChanged);
            // 
            // tbJpegQuality
            // 
            resources.ApplyResources(this.tbJpegQuality, "tbJpegQuality");
            this.tbJpegQuality.Maximum = 100;
            this.tbJpegQuality.Name = "tbJpegQuality";
            this.tbJpegQuality.TickFrequency = 25;
            this.tbJpegQuality.Scroll += new System.EventHandler(this.tbJpegQuality_Scroll);
            // 
            // lblWarning
            // 
            resources.ApplyResources(this.lblWarning, "lblWarning");
            this.lblWarning.Name = "lblWarning";
            // 
            // btnChooseFolder
            // 
            resources.ApplyResources(this.btnChooseFolder, "btnChooseFolder");
            this.btnChooseFolder.Name = "btnChooseFolder";
            this.btnChooseFolder.UseVisualStyleBackColor = true;
            this.btnChooseFolder.Click += new System.EventHandler(this.btnChooseFolder_Click);
            // 
            // cbSkipSavePrompt
            // 
            resources.ApplyResources(this.cbSkipSavePrompt, "cbSkipSavePrompt");
            this.cbSkipSavePrompt.Name = "cbSkipSavePrompt";
            this.cbSkipSavePrompt.UseVisualStyleBackColor = true;
            // 
            // cmbFormat
            // 
            this.cmbFormat.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbFormat.FormattingEnabled = true;
            this.cmbFormat.Items.AddRange(new object[] {
            resources.GetString("cmbFormat.Items"),
            resources.GetString("cmbFormat.Items1"),
            resources.GetString("cmbFormat.Items2"),
            resources.GetString("cmbFormat.Items3"),
            resources.GetString("cmbFormat.Items4"),
            resources.GetString("cmbFormat.Items5"),
            resources.GetString("cmbFormat.Items6"),
            resources.GetString("cmbFormat.Items7")});
            resources.ApplyResources(this.cmbFormat, "cmbFormat");
            this.cmbFormat.Name = "cmbFormat";
            // 
            // FImageSettings
            // 
            resources.ApplyResources(this, "$this");
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.cmbFormat);
            this.Controls.Add(this.cbSkipSavePrompt);
            this.Controls.Add(this.btnChooseFolder);
            this.Controls.Add(this.lblWarning);
            this.Controls.Add(this.txtJpegQuality);
            this.Controls.Add(this.linkPlaceholders);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.txtDefaultFilePath);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.btnRestoreDefaults);
            this.Controls.Add(this.cbRememberSettings);
            this.Controls.Add(this.btnCancel);
            this.Controls.Add(this.btnOK);
            this.Controls.Add(this.tbJpegQuality);
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "FImageSettings";
            ((System.ComponentModel.ISupportInitialize)(this.tbJpegQuality)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button btnOK;
        private System.Windows.Forms.Button btnCancel;
        private ILProfileIcons ilProfileIcons;
        private System.Windows.Forms.CheckBox cbRememberSettings;
        private System.Windows.Forms.Button btnRestoreDefaults;
        private System.Windows.Forms.TextBox txtDefaultFilePath;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.LinkLabel linkPlaceholders;
        private System.Windows.Forms.TextBox txtJpegQuality;
        private System.Windows.Forms.TrackBar tbJpegQuality;
        private System.Windows.Forms.Label lblWarning;
        private System.Windows.Forms.Button btnChooseFolder;
        private System.Windows.Forms.CheckBox cbSkipSavePrompt;
        private System.Windows.Forms.ComboBox cmbFormat;
    }
}
