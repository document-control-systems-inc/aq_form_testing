using System;
using System.Collections.Generic;
using System.Linq;

namespace NAPS2.WinForms
{
    partial class FProfileLogin
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FProfileLogin));
            this.lvProfiles2 = new System.Windows.Forms.ListView();
            this.contextMenuStrip = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.ctxScan = new System.Windows.Forms.ToolStripMenuItem();
            this.ctxEdit = new System.Windows.Forms.ToolStripMenuItem();
            this.ctxSetDefault = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
            this.ctxCopy = new System.Windows.Forms.ToolStripMenuItem();
            this.ctxPaste = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.ctxDelete = new System.Windows.Forms.ToolStripMenuItem();
            this.btnDone2 = new System.Windows.Forms.Button();
            this.ilProfileIcons = new NAPS2.WinForms.ILProfileIcons(this.components);
            this.contextMenuStrip.SuspendLayout();
            this.SuspendLayout();
            // 
            // lvProfiles2
            // 
            this.lvProfiles2.AllowDrop = true;
            resources.ApplyResources(this.lvProfiles2, "lvProfiles2");
            this.lvProfiles2.ContextMenuStrip = this.contextMenuStrip;
            this.lvProfiles2.HideSelection = false;
            this.lvProfiles2.MultiSelect = false;
            this.lvProfiles2.Name = "lvProfiles2";
            this.lvProfiles2.UseCompatibleStateImageBehavior = false;
            this.lvProfiles2.ItemActivate += new System.EventHandler(this.lvProfiles_ItemActivate);
            this.lvProfiles2.ItemDrag += new System.Windows.Forms.ItemDragEventHandler(this.lvProfiles_ItemDrag);
            this.lvProfiles2.SelectedIndexChanged += new System.EventHandler(this.lvProfiles_SelectedIndexChanged);
            this.lvProfiles2.DragDrop += new System.Windows.Forms.DragEventHandler(this.lvProfiles_DragDrop);
            this.lvProfiles2.DragEnter += new System.Windows.Forms.DragEventHandler(this.lvProfiles_DragEnter);
            this.lvProfiles2.DragOver += new System.Windows.Forms.DragEventHandler(this.lvProfiles_DragOver);
            this.lvProfiles2.DragLeave += new System.EventHandler(this.lvProfiles_DragLeave);
            this.lvProfiles2.DoubleClick += new System.EventHandler(this.lvProfiles2_DoubleClick);
            this.lvProfiles2.KeyDown += new System.Windows.Forms.KeyEventHandler(this.lvProfiles_KeyDown);
            // 
            // contextMenuStrip
            // 
            this.contextMenuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.ctxScan,
            this.ctxEdit,
            this.ctxSetDefault,
            this.toolStripSeparator2,
            this.ctxCopy,
            this.ctxPaste,
            this.toolStripSeparator1,
            this.ctxDelete});
            this.contextMenuStrip.Name = "contextMenuStrip";
            resources.ApplyResources(this.contextMenuStrip, "contextMenuStrip");
            this.contextMenuStrip.Opening += new System.ComponentModel.CancelEventHandler(this.contextMenuStrip_Opening);
            // 
            // ctxScan
            // 
            resources.ApplyResources(this.ctxScan, "ctxScan");
            this.ctxScan.Image = global::NAPS2.Icons.control_play_blue_small;
            this.ctxScan.Name = "ctxScan";
            this.ctxScan.Click += new System.EventHandler(this.ctxScan_Click);
            // 
            // ctxEdit
            // 
            this.ctxEdit.Image = global::NAPS2.Icons.pencil_small;
            this.ctxEdit.Name = "ctxEdit";
            resources.ApplyResources(this.ctxEdit, "ctxEdit");
            this.ctxEdit.Click += new System.EventHandler(this.ctxEdit_Click);
            // 
            // ctxSetDefault
            // 
            this.ctxSetDefault.Image = global::NAPS2.Icons.accept_small;
            this.ctxSetDefault.Name = "ctxSetDefault";
            resources.ApplyResources(this.ctxSetDefault, "ctxSetDefault");
            this.ctxSetDefault.Click += new System.EventHandler(this.ctxSetDefault_Click);
            // 
            // toolStripSeparator2
            // 
            this.toolStripSeparator2.Name = "toolStripSeparator2";
            resources.ApplyResources(this.toolStripSeparator2, "toolStripSeparator2");
            // 
            // ctxCopy
            // 
            this.ctxCopy.Name = "ctxCopy";
            resources.ApplyResources(this.ctxCopy, "ctxCopy");
            this.ctxCopy.Click += new System.EventHandler(this.ctxCopy_Click);
            // 
            // ctxPaste
            // 
            this.ctxPaste.Name = "ctxPaste";
            resources.ApplyResources(this.ctxPaste, "ctxPaste");
            this.ctxPaste.Click += new System.EventHandler(this.ctxPaste_Click);
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            resources.ApplyResources(this.toolStripSeparator1, "toolStripSeparator1");
            // 
            // ctxDelete
            // 
            this.ctxDelete.Image = global::NAPS2.Icons.cross_small;
            this.ctxDelete.Name = "ctxDelete";
            resources.ApplyResources(this.ctxDelete, "ctxDelete");
            this.ctxDelete.Click += new System.EventHandler(this.ctxDelete_Click);
            // 
            // btnDone2
            // 
            resources.ApplyResources(this.btnDone2, "btnDone2");
            this.btnDone2.Name = "btnDone2";
            this.btnDone2.UseVisualStyleBackColor = true;
            this.btnDone2.Click += new System.EventHandler(this.btnDone_Click);
            // 
            // FProfileLogin
            // 
            resources.ApplyResources(this, "$this");
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ControlBox = false;
            this.Controls.Add(this.btnDone2);
            this.Controls.Add(this.lvProfiles2);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "FProfileLogin";
            this.Load += new System.EventHandler(this.FProfileLogin_Load);
            this.contextMenuStrip.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.ListView lvProfiles2;
        private System.Windows.Forms.Button btnDone2;
        private ILProfileIcons ilProfileIcons;
        private System.Windows.Forms.ContextMenuStrip contextMenuStrip;
        private System.Windows.Forms.ToolStripMenuItem ctxScan;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.ToolStripMenuItem ctxEdit;
        private System.Windows.Forms.ToolStripMenuItem ctxDelete;
        private System.Windows.Forms.ToolStripMenuItem ctxSetDefault;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
        private System.Windows.Forms.ToolStripMenuItem ctxCopy;
        private System.Windows.Forms.ToolStripMenuItem ctxPaste;
    }
}
