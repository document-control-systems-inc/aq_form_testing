using System;
using System.Collections.Generic;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Reflection;
using System.Threading.Tasks;
using System.Windows.Forms;
using NAPS2.Entity;
using NAPS2.Lang.Resources;
using NAPS2.Util;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using treeView_Data;


namespace NAPS2.WinForms
{
    partial class FTreeView : FormBase
    {
        
        public FTreeView()
        {
            RestoreFormState = false;
            EntityNodo.Nombre = null;
            EntityNodo.Id = null;
            EntityNodo.DocumentClass = null;
            EntityNodo.Domain = null;
            EntityNodo.cancelUpdate = true;
            InitializeComponent();
            this.ActiveControl = tvCarpeta;

        }

        #region Assembly Attribute Accessors

        private static string GetAssemblyAttributeValue<T>(Func<T, string> selector)
        {
            object[] attributes = Assembly.GetEntryAssembly().GetCustomAttributes(typeof(T), false);
            if (attributes.Length == 0)
            {
                return "";
            }
            return selector((T)attributes[0]);
        }

        public string AssemblyTitle
        {
            get
            {
                string title = GetAssemblyAttributeValue<AssemblyTitleAttribute>(x => x.Title);
                if (string.IsNullOrEmpty(title))
                {
                    title = Path.GetFileNameWithoutExtension(Assembly.GetEntryAssembly().CodeBase);
                }
                return title;
            }
        }

        public string AssemblyVersion => Assembly.GetEntryAssembly().GetName().Version.ToString();

        public string AssemblyDescription => GetAssemblyAttributeValue<AssemblyDescriptionAttribute>(x => x.Description);

        public string AssemblyProduct => GetAssemblyAttributeValue<AssemblyProductAttribute>(x => x.Product);

        public string AssemblyCopyright => GetAssemblyAttributeValue<AssemblyCopyrightAttribute>(x => x.Copyright);

        public string AssemblyCompany => GetAssemblyAttributeValue<AssemblyCompanyAttribute>(x => x.Company);

        #endregion

        public TreeNode getNodeRecursiva(JObject item)
        {
            TreeNode nodo = new TreeNode();
            nodo.Name = item.GetValue("id").ToString();
            nodo.Text = item.GetValue("value").ToString();
            nodo.ImageIndex = 0;
            nodo.SelectedImageIndex = 0;
            
            JArray array = JArray.FromObject(item.GetValue("children"));
            if (array.Count > 0)
            {
                foreach (JObject data in array)
                {
                    nodo.Nodes.Add(getNodeRecursiva(data));
                }
            }

            return nodo;
        }



        private void okButton_Click(object sender, EventArgs e)
        {
            

        }

        //Servicio TreeView 
        public static String tree(String token)
        {
            String url = AquariusUtils.getHostNamePath() + "tree/root";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
  
    
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
                WebResponse errorResponse = ex.Response;
                using (Stream responseStream = errorResponse.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.GetEncoding("utf-8"));
                    String errorText = reader.ReadToEnd();
                }
                throw;
            }
        }

        private void button1_Click(object sender, EventArgs e)
        {
            
        }
        //Cargar el treeview en load
        private void LoadTreeview() {
            String json = tree(ResultadoLogin.token);
            JObject jsonResultadoTree = JObject.Parse(json);
            //String json = "{\"status\": 0, \"exito\": {\"nombre\": \"nombre\", \"id\": \"id\", \"children\": [{\"nombre\": \"nombre1\", \"id\": \"id1\", \"children\": [{\"nombre\": \"nombre2\", \"id\": \"id2\", \"children\": [{\"nombre\": \"nombre3\", \"id\": \"id3\", \"children\": [{\"nombre\": \"nombre4\", \"id\": \"id4\", \"children\": [{\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] } ] } ] } ] }, {\"nombre\": \"nombre2\", \"id\": \"id2\", \"children\": [{\"nombre\": \"nombre3\", \"id\": \"id3\", \"children\": [{\"nombre\": \"nombre4\", \"id\": \"id4\", \"children\": [{\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [{\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }] } ] } ] } ] } ] }, {\"nombre\": \"nombre2\", \"id\": \"id2\", \"children\": [{\"nombre\": \"nombre3\", \"id\": \"id3\", \"children\": [{\"nombre\": \"nombre4\", \"id\": \"id4\", \"children\": [{\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] } ] } ] } ] }, {\"nombre\": \"nombre2\", \"id\": \"id2\", \"children\": [{\"nombre\": \"nombre3\", \"id\": \"id3\", \"children\": [{\"nombre\": \"nombre4\", \"id\": \"id4\", \"children\": [{\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [{\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }] } ] } ] } ] } ] } ] }, {\"nombre\": \"nombre1\", \"id\": \"id1\", \"children\": [{\"nombre\": \"nombre2\", \"id\": \"id2\", \"children\": [{\"nombre\": \"nombre3\", \"id\": \"id3\", \"children\": [{\"nombre\": \"nombre4\", \"id\": \"id4\", \"children\": [{\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] } ] } ] } ] } ] }, {\"nombre\": \"nombre1\", \"id\": \"id1\", \"children\": [{\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] }, {\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] } ] }, {\"nombre\": \"nombre1\", \"id\": \"id1\", \"children\": [{\"nombre\": \"nombre2\", \"id\": \"id2\", \"children\": [{\"nombre\": \"nombre3\", \"id\": \"id3\", \"children\": [{\"nombre\": \"nombre4\", \"id\": \"id4\", \"children\": [{\"nombre\": \"nombre5\", \"id\": \"id5\", \"children\": [{\"nombre\": \"nombre6\", \"id\": \"id6\", \"children\": [ ] } ] } ] } ] } ] } ] } ] } }";
            ImageList imageList = new ImageList();
            System.Drawing.Bitmap folderOpen = NAPS2.Icons.folderOpen;

            //imageList.Images.Add(Image.FromFile(AquariusUtils.getFilePathImageFolder()), Color.Transparent);
            imageList.Images.Add(folderOpen, Color.Transparent);

            JObject jsonExito = JObject.Parse(json);
            JObject jsonArbol = JObject.Parse(jsonExito.GetValue("exito").ToString());
            tvCarpeta.ImageList = imageList;

            TreeNode nodoArbol = new TreeNode();
            JArray arraychildren = JArray.FromObject(jsonArbol.GetValue("children"));
            try
            {
                foreach (JObject data in arraychildren)
                {

                    nodoArbol = new TreeNode();
                    nodoArbol.Text = data.GetValue("value").ToString();
                    nodoArbol.Name = data.GetValue("id").ToString();
                    nodoArbol.ImageIndex = 0;
                    nodoArbol.SelectedImageIndex = 0;
                 

                    JArray array = JArray.FromObject(data.GetValue("children"));
                    if (array.Count > 0)
                    {
                        foreach (JObject item in array)
                        {
                            nodoArbol.Nodes.Add(getNodeRecursiva(item));
                        }
                    }

                    tvCarpeta.Nodes.Add(nodoArbol);
                    
                    
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
            }
        }
        private void tvCarpetas_AfterSelect(object sender, TreeViewEventArgs e)
        {
            EntityNodo.Nombre = e.Node.Text;
            EntityNodo.Id = e.Node.Name;
        }

        private void FTreeView_Load(object sender, EventArgs e)
        {
            Loadcombo();
            LoadTreeview();
            LoadDomain();
        }  

        private void Loadcombo() {

            //Combobox DocumentalClass
            
            String json = combobox(ResultadoLogin.token);
            JObject jsonResultadoCombo = JObject.Parse(json);
            JArray jsonExito = JArray.FromObject(jsonResultadoCombo.GetValue("exito"));
            ComboboxItem item = new ComboboxItem();

            try
            {
                if (jsonExito.Count > 0)
                {
                    DataTable dtDocumentalClass = new DataTable();
                    dtDocumentalClass.Columns.Add("value");
                    dtDocumentalClass.Columns.Add("text");
                    dtDocumentalClass.Rows.Add(0, "Seleccionar clase..");

                    foreach (JObject data in jsonExito)
                    {
                        dtDocumentalClass.Rows.Add(data.GetValue("id"), data.GetValue("label"));
                    }
                    ddlDocumentClass.Items.Clear();
                    ddlDocumentClass.DataSource = dtDocumentalClass;
                    ddlDocumentClass.DisplayMember = "text";
                    ddlDocumentClass.ValueMember = "value";

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

        //Servicio Combobox DocumentClass
        public static String combobox(String token)
        {
            String url = AquariusUtils.getHostNamePath() + "documentClass/";
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

        //Get id domain LoadDomain
        private void LoadDomain() {
            String json = domain(ResultadoLogin.token);
            JObject jsonResultado = JObject.Parse(json);
            JArray jsonExito = JArray.FromObject(jsonResultado.GetValue("exito"));
            
            //JObject exito = JObject.Parse(jsonExito[1].ToString());
            //JObject profileObj = JObject.Parse(exito.GetValue("properties").ToString());
            ComboboxItem item = new ComboboxItem();

            try
            {
                if (jsonExito.Count > 0)
                {
                    DataTable dtDomain = new DataTable();
                    dtDomain.Columns.Add("value");
                    dtDomain.Columns.Add("text");
                    dtDomain.Rows.Add(0, "Seleccionar dominio..");

                    foreach (JObject data in jsonExito)
                    {
                    dtDomain.Rows.Add(data.GetValue("id"), data.GetValue("properties").First().Last());
                    }

                    ddlDomain.Items.Clear();
                    ddlDomain.DataSource = dtDomain;
                    ddlDomain.DisplayMember = "text";
                    ddlDomain.ValueMember = "value";

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
            }

        }

        //Servicio domain
        public static String domain(String token)
        {
            String url = AquariusUtils.getHostNamePath() + "domain/";
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


        private void button1_DockChanged(object sender, EventArgs e)
        {

        }

        private void button1_BackColorChanged(object sender, EventArgs e)
        {

        }

        private void button1_TextChanged(object sender, EventArgs e)
        {

        }

        private void btn1_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void tvCarpeta_AfterSelect(object sender, TreeViewEventArgs e)
        {
            EntityNodo.Nombre = e.Node.Text;
            EntityNodo.Id = e.Node.Name;
            tvCarpeta.HideSelection = false;

        }

        private void comboBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
                EntityNodo.DocumentClass = ddlDocumentClass.SelectedValue.ToString();
            
        }

        private void checkBox1_Click(object sender, EventArgs e)
        {
            
        }

        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
         
        }

        private void button1_AutoSizeChanged(object sender, EventArgs e)
        {

        }
        
        private void button1_Click_1(object sender, EventArgs e)
        {
            // se valida que tenga toda la inforamción requerida
            if (EntityNodo.Id == null || ddlDocumentClass.SelectedIndex == 0 || ddlDomain.SelectedIndex == 0)
            {
                MessageBox.Show("Debe completar la información requerida");
            } else
            {
                EntityNodo.cancelUpdate = false;
                Close();
            }
            //btnClicklScanAut();
        }

        private void ddlDomain_SelectedIndexChanged(object sender, EventArgs e)
        {
                EntityNodo.Domain = ddlDomain.SelectedValue.ToString();
            
        }

        private void tvCarpeta_ForeColorChanged(object sender, EventArgs e)
        {

        }

        private void btnClose_Click(object sender, EventArgs e)
        {
            EntityNodo.cancelUpdate = true;
            Close();
        }
    }
}
