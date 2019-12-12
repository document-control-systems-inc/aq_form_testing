using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;

namespace NAPS2.WinForms
{
    partial class FTreeView
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FTreeView));
            this.tvCarpeta = new System.Windows.Forms.TreeView();
            this.ddlDocumentClass = new System.Windows.Forms.ComboBox();
            this.checkBox1 = new System.Windows.Forms.CheckBox();
            this.button1 = new System.Windows.Forms.Button();
            this.ddlDomain = new System.Windows.Forms.ComboBox();
            this.btnClose = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // tvCarpeta
            // 
            this.tvCarpeta.BackColor = System.Drawing.Color.White;
            resources.ApplyResources(this.tvCarpeta, "tvCarpeta");
            this.tvCarpeta.Name = "tvCarpeta";
            this.tvCarpeta.AfterSelect += new System.Windows.Forms.TreeViewEventHandler(this.tvCarpeta_AfterSelect);
            this.tvCarpeta.ForeColorChanged += new System.EventHandler(this.tvCarpeta_ForeColorChanged);
            // 
            // ddlDocumentClass
            // 
            this.ddlDocumentClass.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.ddlDocumentClass.FormattingEnabled = true;
            resources.ApplyResources(this.ddlDocumentClass, "ddlDocumentClass");
            this.ddlDocumentClass.Name = "ddlDocumentClass";
            this.ddlDocumentClass.SelectedIndexChanged += new System.EventHandler(this.comboBox1_SelectedIndexChanged);
            // 
            // checkBox1
            // 
            resources.ApplyResources(this.checkBox1, "checkBox1");
            this.checkBox1.Name = "checkBox1";
            this.checkBox1.UseVisualStyleBackColor = true;
            this.checkBox1.CheckedChanged += new System.EventHandler(this.checkBox1_CheckedChanged);
            this.checkBox1.Click += new System.EventHandler(this.checkBox1_Click);
            // 
            // button1
            // 
            resources.ApplyResources(this.button1, "button1");
            this.button1.Name = "button1";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.AutoSizeChanged += new System.EventHandler(this.button1_AutoSizeChanged);
            this.button1.Click += new System.EventHandler(this.button1_Click_1);
            // 
            // ddlDomain
            // 
            this.ddlDomain.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.ddlDomain.FormattingEnabled = true;
            resources.ApplyResources(this.ddlDomain, "ddlDomain");
            this.ddlDomain.Name = "ddlDomain";
            this.ddlDomain.SelectedIndexChanged += new System.EventHandler(this.ddlDomain_SelectedIndexChanged);
            // 
            // btnClose
            // 
            resources.ApplyResources(this.btnClose, "btnClose");
            this.btnClose.Name = "btnClose";
            this.btnClose.UseVisualStyleBackColor = true;
            this.btnClose.Click += new System.EventHandler(this.btnClose_Click);
            // 
            // FTreeView
            // 
            resources.ApplyResources(this, "$this");
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.btnClose);
            this.Controls.Add(this.ddlDomain);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.checkBox1);
            this.Controls.Add(this.ddlDocumentClass);
            this.Controls.Add(this.tvCarpeta);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "FTreeView";
            this.Load += new System.EventHandler(this.FTreeView_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion
        private System.Windows.Forms.TreeView tvCarpeta;
        private System.Windows.Forms.ComboBox ddlDocumentClass;
        private System.Windows.Forms.CheckBox checkBox1;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.ComboBox ddlDomain;
        private System.Windows.Forms.Button btnClose;
    }
}
