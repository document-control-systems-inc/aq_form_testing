using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using NAPS2.Config;
using System.IO;
using System.Windows.Forms;
using System.Net;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using System.Xml.Linq;
using NAPS2.Entity;
using System.Xml;
using treeView_Data;
using System.Net.Http;
using NAPS2.WinForms;
using NAPS2.Scan;
using System.Xml.Serialization;


namespace NAPS2.Util
{
    static class AquariusUtils
    {
        private static String SYSTEM_PATH = null;
        private static String HOSTNAME_PATH = null;
        private static String FILE_PATH_RECOVERY_COPIA = null;
        private static String FILE_PATH_IMAGE_FOLDER = null;
        private static Dictionary<String, String> list;
        private static IUserConfigManager UserConfigManager = null;

        public static void setUserConfig(IUserConfigManager userconfig)
        {
            UserConfigManager = userconfig;
        }

        public static String getImageFormat(String text)
        {
            switch(text)
            {
                case "Bitmap Image (*.bpm)":
                    return ".bpm";
                case "Enhanced MetaFile (*.emf)":
                    return ".emf";
                case "GIF (*.gif)":
                    return ".gif";
                case "ICO (*.ico)":
                    return ".ico";
                case "JPEG (*.jpg, *.jpeg)":
                    return ".jpg";
                case "PNG (*.png)":
                    return ".png";
                case "TIFF (*.tif, *.tiff)":
                    return ".tif";
                case "Windows Metafile Format (*.wmf)":
                    return ".wmf";
            default:
                    return ".png";
        }
        }

        public static String getSystemPath()
        {
            if (SYSTEM_PATH == null)
            {
                String fileProfiles = UserConfigManager.getPrimaryConfigPath();
                int indxConfig = fileProfiles.IndexOf("config.xml");
                SYSTEM_PATH = fileProfiles.Substring(0, indxConfig);
            }
            return SYSTEM_PATH;
        }

        public static Boolean login(String user, String pass)
        {
            String url = AquariusUtils.getHostNamePath() + "login/";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            String resultado = "";
            try
            {
                request.Headers["user"] = user;
                request.Headers["password"] = pass;
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.UTF8);
                    resultado = reader.ReadToEnd();
                }
            }
            catch (WebException ex)
            {
                MessageBox.Show(ex.Message);

            }
            if (resultado.Length > 0)
            {
                JObject jObject = JObject.Parse(resultado);
                if (jObject.GetValue("status").ToString().Equals("0"))
                {
                    // Exito en el login
                    ResultadoLogin.token = jObject.GetValue("exito").ToString();
                    ResultadoLogin.userName = user;
                    return true;
                } else
                {
                    MessageBox.Show(mensajeError("es_mx", jObject.GetValue("status").ToString()));
                    return false;
                }
            }
            MessageBox.Show(mensajeError("es_mx", "-1"));
            return false;
        }

        public static Boolean logout(String token)
        {
            String url = AquariusUtils.getHostNamePath() + "logout";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = "GET";
            String resultado = "";
            try
            {
                request.Headers["token"] = token;
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.UTF8);
                    resultado = reader.ReadToEnd();
                }
            }
            catch (WebException ex)
            {
                MessageBox.Show(ex.Message);

            }
            if (resultado.Length > 0)
            {
                JObject jObject = JObject.Parse(resultado);
                if (jObject.GetValue("status").ToString().Equals("0"))
                {
                    return true;
                }
                else
                {
                    MessageBox.Show(mensajeError("es_mx", jObject.GetValue("status").ToString()));
                    return false;
                }
            }
            MessageBox.Show(mensajeError("es_mx", "-1"));
            return false;
        }

        public static String mensajeError(String locale, String id)
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
                    resultado = reader.ReadToEnd();
                }
            }
            catch (WebException ex)
            {
                resultado = ex.Message;
            }
            if (resultado.Length > 0)
            {
                JObject jObject = JObject.Parse(resultado);
                resultado = JObject.Parse(jObject.GetValue("exito").ToString()).GetValue("message").ToString();

            } else
            {
                resultado = "Error desconocido";
            }
            return resultado;
        }

        public static String getHostNamePath()
        {
            if (HOSTNAME_PATH == null)
            {
                String propFile = getSystemPath() + "aquarius.properties";
                if (readProperties(propFile))
                {
                    HOSTNAME_PATH = (list.ContainsKey("HOSTNAME_PATH")) ? (list["HOSTNAME_PATH"]) : (null);
                }
                if (HOSTNAME_PATH == null)
                {
                    ShowInputDialog("HOSTNAME_PATH", ref HOSTNAME_PATH);
                    saveProperty(propFile, "HOSTNAME_PATH", HOSTNAME_PATH);
                }
            } 
            return HOSTNAME_PATH;
        }

        public static String getFilePathRecoveryCopia()
        {
            if (FILE_PATH_RECOVERY_COPIA == null)
            {
                String path = getSystemPath() + "profile_" + ResultadoLogin.userName;
                if (!System.IO.Directory.Exists(path))
                    System.IO.Directory.CreateDirectory(path);
                FILE_PATH_RECOVERY_COPIA = path;
                /*
                String propFile = getSystemPath() + "aquarius.properties";
                if (readProperties(propFile))
                {
                    FILE_PATH_RECOVERY_COPIA = (list.ContainsKey("FILE_PATH_RECOVERY_COPIA")) ? (list["FILE_PATH_RECOVERY_COPIA"]) : (null);
                }
                if (FILE_PATH_RECOVERY_COPIA == null)
                {
                    ShowInputDialog("FILE_PATH_RECOVERY_COPIA", ref FILE_PATH_RECOVERY_COPIA);
                    saveProperty(propFile, "FILE_PATH_RECOVERY_COPIA", FILE_PATH_RECOVERY_COPIA);
                }
                */
            }
            return FILE_PATH_RECOVERY_COPIA;
        }

        //FILE_PATH_IMAGE_FOLDER
        public static String getFilePathImageFolder()
        {
            if (FILE_PATH_IMAGE_FOLDER == null)
            {
                String path = getSystemPath() + "profile_" + ResultadoLogin.userName;
                if (!System.IO.Directory.Exists(path))
                    System.IO.Directory.CreateDirectory(path);
                FILE_PATH_IMAGE_FOLDER = path;
                /*
                String propFile = getSystemPath() + "aquarius.properties";
                if (readProperties(propFile))
                {
                    FILE_PATH_IMAGE_FOLDER = (list.ContainsKey("FILE_PATH_IMAGE_FOLDER")) ? (list["FILE_PATH_IMAGE_FOLDER"]) : (null);
                }
                if (FILE_PATH_IMAGE_FOLDER == null)
                {
                    ShowInputDialog("FILE_PATH_IMAGE_FOLDER", ref FILE_PATH_IMAGE_FOLDER);
                    saveProperty(propFile, "FILE_PATH_IMAGE_FOLDER", FILE_PATH_IMAGE_FOLDER);
                }
                */
            }
            return FILE_PATH_IMAGE_FOLDER;
        }

        private static Boolean readProperties(String fileName)
        {
            list = new Dictionary<String, String>();
            if (System.IO.File.Exists(fileName))
            {
                var fileStream = new FileStream(fileName, FileMode.Open, FileAccess.Read);
                using (var streamReader = new StreamReader(fileStream, Encoding.UTF8))
                {
                    String line = "";
                    while ((line = streamReader.ReadLine()) != null)
                    {
                        if (line.IndexOf("#") == -1)
                        {
                            if (line.IndexOf("=") != -1)
                            {
                                String key = line.Substring(0, line.IndexOf("=")).Trim();
                                String value = line.Substring(line.IndexOf("=") + 1).Trim();
                                list.Add(key, value);
                            }
                        }
                    }
                }
                fileStream.Close();
                return true;
            } else
            {
                return false;
            }
        }

        private static Boolean saveProperty(String fileName, String key, String value)
        {
            //if (!System.IO.File.Exists(fileName))
            //    System.IO.File.Create(fileName);
            System.IO.StreamWriter file = new System.IO.StreamWriter(fileName);
            file.WriteLine(key + "=" + value);
            file.Close();
            return true;
        }

            private static DialogResult ShowInputDialog(String title, ref String input)
        {
            System.Drawing.Size size = new System.Drawing.Size(400, 70);
            Form inputBox = new Form();

            inputBox.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            inputBox.ClientSize = size;
            inputBox.Text = title;

            System.Windows.Forms.TextBox textBox = new TextBox();
            textBox.Size = new System.Drawing.Size(size.Width - 10, 23);
            textBox.Location = new System.Drawing.Point(5, 5);
            textBox.Text = input;
            inputBox.Controls.Add(textBox);

            Button okButton = new Button();
            okButton.DialogResult = System.Windows.Forms.DialogResult.OK;
            okButton.Name = "okButton";
            okButton.Size = new System.Drawing.Size(75, 23);
            okButton.Text = "Ok";
            okButton.Location = new System.Drawing.Point(size.Width - 80 - 80, 39);
            inputBox.Controls.Add(okButton);

            /*
            Button cancelButton = new Button();
            cancelButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            cancelButton.Name = "cancelButton";
            cancelButton.Size = new System.Drawing.Size(75, 23);
            cancelButton.Text = "&Cancel";
            cancelButton.Location = new System.Drawing.Point(size.Width - 80, 39);
            inputBox.Controls.Add(cancelButton);
            */

            inputBox.AcceptButton = okButton;
            //inputBox.CancelButton = cancelButton;

            DialogResult result = inputBox.ShowDialog();
            input = textBox.Text;
            return result;
        }

        public static int getScanProfile()
        {
            String url = AquariusUtils.getHostNamePath() + "scan/profile";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);

            String resultado = "";
            try
            {
                request.Headers["token"] = ResultadoLogin.token;
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.UTF8);
                    resultado = reader.ReadToEnd();
                }
            }
            catch (WebException ex)
            {
                MessageBox.Show(ex.Message);

            }
            if (resultado.Length > 0)
            {
                JObject jObject = JObject.Parse(resultado);
                if (jObject.GetValue("status").ToString().Equals("0"))
                {
                    // Éxito en el servicio:
                    return generateScanXml(JArray.Parse(jObject.GetValue("exito").ToString()));
                }
                else
                {
                    MessageBox.Show(mensajeError("es_mx", jObject.GetValue("status").ToString()));
                }
            }
            return -1;
        }

        private static XmlNode setXmlNode(XmlDocument doc, JToken element)
        {
            JProperty property = null;
            if (element.Type.Equals(JTokenType.Property))
            {
                property = (JProperty)element;
            }
            if (property.Name.Equals("@xmlns:xsd") || property.Name.Equals("@xmlns:xsi"))
            {
                return null;
            }
            XmlNode nodeElement = doc.CreateElement(property.Name);
            
            if (property.Value.Type.Equals(JTokenType.Object))
            {
                foreach (JProperty children in property.Value)
                {
                    XmlNode nodeChild = setXmlNode(doc, children);
                    if (nodeChild != null)
                    {
                        nodeElement.AppendChild(nodeChild);
                    }
                }
            } else
            {
                nodeElement.InnerText = property.Value.ToString();
            }
            return nodeElement;
        }

        public static int generateScanXml(JArray jObjectExito)
        {
            
            XmlDocument doc = new XmlDocument();
            doc.LoadXml("<ArrayOfScanProfile></ArrayOfScanProfile>");
            Int32 contador = 0;
            JObject jsonFile = new JObject();
            JArray jsonArrayOfScanProfile = new JArray();
            XmlNode nodeArray = doc.DocumentElement.SelectSingleNode("/ArrayOfScanProfile");
            foreach (JObject objeto in jObjectExito)
            {
                JObject jsonProfile = new JObject();
                jsonProfile = JObject.Parse(objeto.GetValue("profile").ToString());
                if (jsonProfile != null)
                {
                    JObject jsonScanProfile = new JObject();
                    jsonScanProfile = JObject.Parse(jsonProfile.GetValue("ScanProfile").ToString());
                    if (jsonScanProfile != null)
                    {
                        XmlNode nodeScanProfile = doc.CreateElement("ScanProfile");
                        foreach (JProperty children in jsonScanProfile.Properties())
                        {
                            XmlNode nodeChildren = setXmlNode(doc, children);
                            if (nodeChildren != null)
                            {
                                nodeScanProfile.AppendChild(nodeChildren);
                            }
                        }
                        nodeArray.AppendChild(nodeScanProfile);
                        contador++;
                    }
                }
            }
            String fileProfiles = AquariusUtils.getSystemPath() + "profiles.xml";
            Stream fsxml = File.Open(fileProfiles, FileMode.Create);
            // XML Document Saved
            doc.Save(fsxml);
            fsxml.Close();
            doc = null;
            return contador;
        }

        public static async Task uploadFiles(List<String> filesToUpload)
        {
            //Subida de Archivos
            String idParent = EntityNodo.Id;
            String domain = EntityNodo.Domain;
            String documentClass = EntityNodo.DocumentClass;

            String resultado = "";
            try
            {
                if (filesToUpload != null)
                {
                    if (EntityNodo.DocumentClass != null && EntityNodo.Id != null)
                    {
                        foreach (string ruta in filesToUpload)
                        {
                            try
                            {
                                String nombre = "image_";
                                if (ruta.EndsWith("pdf"))
                                {
                                    nombre = "scan_";
                                }
                                DateTime fecha = DateTime.Now;
                                nombre = nombre + fecha.ToString("dd-MM-yyyy HH:mm:ss.zzz");

                                resultado = await uploadFileAsync(ruta, ResultadoLogin.token, idParent, domain, documentClass, nombre);

                                if (resultado.Length > 0)
                                {
                                    JObject jObject = JObject.Parse(resultado);
                                    if (jObject.GetValue("status").ToString().Equals("0"))
                                    {
                                        // Éxito en el servicio:
                                        //Eliminar archivo local
                                        File.Delete(ruta);
                                    }
                                    else
                                    {
                                        MessageBox.Show(mensajeError("es_mx", jObject.GetValue("status").ToString()));
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                MessageBox.Show("Error al subir el archivo. " + e.Message);
                            }
                        }
                    }
                }
            }
            catch (Exception error)
            {
                MessageBox.Show(error.Message);
            }
            // this.Close();
        }//Fin subida archivos

        //Subida de Archivos
        public static async Task<String> uploadFileAsync(String filePath, String TokenString, String idParent, String domain, String documentClass, String nombre)
        {
            byte[] fileBytes = GetBytesAsync(filePath);
            String resultado = "";
            try
            {
                String contentMIMEType = MimeType.GetMimeType(Path.GetExtension(filePath));
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
                HttpResponseMessage response = await httpClient.PostAsync(AquariusUtils.getHostNamePath() + "document/", form);
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

        public static byte[] GetBytesAsync(String fileName)
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

        //guardar profile
        public static String setProfile(String token, String profile)
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
        }

        //eliminar el profile:
        public static String deleteProfile(String token, String profile)
        {
            String url = AquariusUtils.getHostNamePath() + "scan/profile";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
            request.Method = "DELETE";

            String resultado = "";
            try
            {
                request.Headers["token"] = token;
                request.Headers["perfil"] = profile;
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

        // Obtener arbol
        public static String getTree(String token)
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

        // Obtener Document Class
        public static String getDocumentClass(String token)
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

        // Obtener Dominio
        public static String getDomain(String token)
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

        public static String validateIsNull(Object objeto)
        {
            if (objeto == null)
            {
                return null;
            } else
            {
                return objeto.ToString();
            }
        }

        public static void addProfile(ScanProfile scanProfile)
        {
            /*
            scanProfile.AquariusProfile = AquariusUtils.validateIsNull(scanProfile.DisplayName);
            scanProfile.AquariusIdParent = AquariusUtils.validateIsNull(EntityNodo.Id);
            scanProfile.AquariusNameParent = AquariusUtils.validateIsNull(EntityNodo.Nombre);
            scanProfile.AquariusDocumentClass = AquariusUtils.validateIsNull(EntityNodo.DocumentClass);
            scanProfile.AquariusDomain = AquariusUtils.validateIsNull(EntityNodo.Domain);
            */

            var serializer = new XmlSerializer(typeof(ScanProfile));
            MemoryStream stream = new MemoryStream();
            serializer.Serialize(stream, scanProfile);
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(Encoding.UTF8.GetString(stream.ToArray()));

            /*
            XmlNode nodeArray = doc.DocumentElement.SelectSingleNode("//ScanProfile");
            XmlNode nodeProfile = doc.CreateElement("AquariusProfile");
            nodeProfile.InnerText = AquariusUtils.validateIsNull(scanProfile.DisplayName);
            XmlNode nodeIdParent = doc.CreateElement("AquariusIdParent");
            nodeIdParent.InnerText = AquariusUtils.validateIsNull(EntityNodo.Id);
            XmlNode nodeNameParent = doc.CreateElement("AquariusNameParent");
            nodeNameParent.InnerText = AquariusUtils.validateIsNull(EntityNodo.Nombre);
            XmlNode nodeDocumentClass = doc.CreateElement("AquariusDocumentClass");
            nodeDocumentClass.InnerText = AquariusUtils.validateIsNull(EntityNodo.DocumentClass);
            XmlNode nodeDomain = doc.CreateElement("AquariusDomain");
            nodeDomain.InnerText = AquariusUtils.validateIsNull(EntityNodo.Domain);
            nodeArray.AppendChild(nodeProfile);
            nodeArray.AppendChild(nodeIdParent);
            nodeArray.AppendChild(nodeNameParent);
            nodeArray.AppendChild(nodeDocumentClass);
            nodeArray.AppendChild(nodeDomain);
            */

            EntityNodo.ScanPerfil = JsonConvert.SerializeXmlNode(doc);
        }
    }
    
}
