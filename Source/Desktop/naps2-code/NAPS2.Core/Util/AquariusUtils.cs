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
using NAPS2.Entity;
using System.Xml;
using treeView_Data;
using System.Net.Http;
using NAPS2.WinForms;


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
                    return 0;
                }
            }
            return -1;
        }

        public static int generateScanXml(JArray jObjectExito)
        {

            XmlDocument doc = new XmlDocument();
            doc.LoadXml("<ArrayOfScanProfile></ArrayOfScanProfile>");
            //doc.Load(UserConfigManager.getPrimaryConfigPath());
            XmlNode node = doc.DocumentElement.SelectSingleNode("/ArrayOfScanProfile");
            foreach (var x in node.SelectNodes("ScanProfile"))
            {
                node.RemoveChild((XmlNode)x);
            }

            JObject jsonProfile = new JObject();
            XmlNode nodeChildDevice = null;

            XmlNode nodeScanProfile = null;
            XmlNode nodeChildName = null;
            XmlNode nodeChildID = null;

            XmlNode nodeArray = null;
            XmlNode nodeChildDriverName = null;
            XmlNode nodeChildDisplayName = null;
            XmlNode nodeChildIconID = null;
            XmlNode nodeChildMaxQuality = null;
            XmlNode nodeChildIsDefault = null;
            XmlNode nodeChildVersion = null;
            XmlNode nodeChildUseNativeUI = null;
            XmlNode nodeChildAfterScanScale = null;
            XmlNode nodeChildBrightness = null;
            XmlNode nodeChildContrast = null;
            XmlNode nodeChildBitDepth = null;
            XmlNode nodeChildPageAlign = null;
            XmlNode nodeChildPageSize = null;
            XmlNode nodeChildResolution = null;
            XmlNode nodeChildPaperSource = null;
            XmlNode nodeChildEnableAutoSave = null;
            XmlNode nodeChildQuality = null;
            XmlNode nodeChildAutoDeskew = null;
            XmlNode nodeChildBrightnessContrastAfterScan = null;
            XmlNode nodeChildForcePageSize = null;
            XmlNode nodeChildForcePageSizeCrop = null;
            XmlNode nodeChildTwainImpl = null;
            XmlNode nodeChildExcludeBlankPages = null;
            XmlNode nodeChildBlankPageWhiteThreshold = null;
            XmlNode nodeChildBlankPageCoverageThreshold = null;
            XmlNode nodeChildWiaOffsetWidth = null;
            XmlNode nodeChildWiaRetryOnFailure = null;
            XmlNode nodeChildWiaDelayBetweenScans = null;
            XmlNode nodeChildWiaDelayBetweenScansSeconds = null;
            XmlNode nodeChildFlipDuplexedPages = null;

            // se agrega la info del repositorio:
            XmlNode nodeChildRepository = null;
            XmlNode nodeChildIdParent = null;
            XmlNode nodeChildNameParent = null;
            XmlNode nodeChildDocumentClass = null;
            XmlNode nodeChildDomain = null;

            Int32 contador = 0;
            foreach (JObject objeto in jObjectExito)
            {
                nodeArray = doc.DocumentElement.SelectSingleNode("/ArrayOfScanProfile");
                nodeChildName = doc.CreateElement("Name");
                nodeChildID = doc.CreateElement("ID");
                nodeScanProfile = doc.CreateElement("ScanProfile");
                nodeChildDevice = doc.CreateElement("Device");
                nodeChildDriverName = doc.CreateElement("DriverName");
                nodeChildDisplayName = doc.CreateElement("DisplayName");
                nodeChildIconID = doc.CreateElement("IconID");
                nodeChildMaxQuality = doc.CreateElement("MaxQuality");
                nodeChildIsDefault = doc.CreateElement("IsDefault");
                nodeChildVersion = doc.CreateElement("Version");
                nodeChildUseNativeUI = doc.CreateElement("UseNativeUI");
                nodeChildAfterScanScale = doc.CreateElement("AfterScanScale");
                nodeChildBrightness = doc.CreateElement("Brightness");
                nodeChildContrast = doc.CreateElement("Contrast");
                nodeChildBitDepth = doc.CreateElement("BitDepth");
                nodeChildPageAlign = doc.CreateElement("PageAlign");
                nodeChildPageSize = doc.CreateElement("PageSize");
                nodeChildResolution = doc.CreateElement("Resolution");
                nodeChildPaperSource = doc.CreateElement("PaperSource");
                nodeChildEnableAutoSave = doc.CreateElement("EnableAutoSave");
                nodeChildQuality = doc.CreateElement("Quality");
                nodeChildAutoDeskew = doc.CreateElement("AutoDeskew");
                nodeChildBrightnessContrastAfterScan = doc.CreateElement("BrightnessContrastAfterScan");
                nodeChildForcePageSize = doc.CreateElement("ForcePageSize");
                nodeChildForcePageSizeCrop = doc.CreateElement("ForcePageSizeCrop");
                nodeChildTwainImpl = doc.CreateElement("TwainImpl");
                nodeChildExcludeBlankPages = doc.CreateElement("ExcludeBlankPages");
                nodeChildBlankPageWhiteThreshold = doc.CreateElement("BlankPageWhiteThreshold");
                nodeChildBlankPageCoverageThreshold = doc.CreateElement("BlankPageCoverageThreshold");
                nodeChildWiaOffsetWidth = doc.CreateElement("WiaOffsetWidth");
                nodeChildWiaRetryOnFailure = doc.CreateElement("WiaRetryOnFailure");
                nodeChildWiaDelayBetweenScans = doc.CreateElement("WiaDelayBetweenScans");
                nodeChildWiaDelayBetweenScansSeconds = doc.CreateElement("WiaDelayBetweenScansSeconds");
                nodeChildFlipDuplexedPages = doc.CreateElement("FlipDuplexedPages");
                // se agregan los elementos del repositorio:
                nodeChildRepository = doc.CreateElement("Repository");
                nodeChildIdParent = doc.CreateElement("IdParent");
                nodeChildNameParent = doc.CreateElement("NameParent");
                nodeChildDocumentClass = doc.CreateElement("DocumentClass");
                nodeChildDomain = doc.CreateElement("Domain");


                jsonProfile = JObject.Parse(objeto.GetValue("profile").ToString());

                nodeChildName.InnerText = jsonProfile.GetValue("name").ToString();
                nodeChildID.InnerText = jsonProfile.GetValue("iD").ToString();

                nodeChildDevice.AppendChild(nodeChildName);
                nodeChildDevice.AppendChild(nodeChildID);

                nodeChildDriverName.InnerText = jsonProfile.GetValue("driverName").ToString();
                nodeChildDisplayName.InnerText = jsonProfile.GetValue("displayName").ToString();
                nodeChildIconID.InnerText = jsonProfile.GetValue("iconID").ToString();
                nodeChildMaxQuality.InnerText = jsonProfile.GetValue("maxQuality").ToString().ToLower();
                nodeChildIsDefault.InnerText = contador == 0 ? "true" : jsonProfile.GetValue("isDefault").ToString().ToLower();
                nodeChildVersion.InnerText = jsonProfile.GetValue("version").ToString();
                nodeChildUseNativeUI.InnerText = jsonProfile.GetValue("useNativeUI").ToString().ToLower();
                nodeChildAfterScanScale.InnerText = jsonProfile.GetValue("afterScanScale").ToString();
                nodeChildBrightness.InnerText = jsonProfile.GetValue("brightness").ToString();
                nodeChildContrast.InnerText = jsonProfile.GetValue("contrast").ToString();
                nodeChildBitDepth.InnerText = jsonProfile.GetValue("bitDepth").ToString();
                nodeChildPageAlign.InnerText = jsonProfile.GetValue("pageAlign").ToString();
                nodeChildPageSize.InnerText = jsonProfile.GetValue("pageSize").ToString();
                nodeChildResolution.InnerText = jsonProfile.GetValue("resolution").ToString();
                nodeChildPaperSource.InnerText = jsonProfile.GetValue("paperSource").ToString();
                nodeChildEnableAutoSave.InnerText = jsonProfile.GetValue("enableAutoSave").ToString().ToLower();
                nodeChildQuality.InnerText = jsonProfile.GetValue("quality").ToString();
                nodeChildAutoDeskew.InnerText = jsonProfile.GetValue("autoDeskew").ToString().ToLower();
                nodeChildBrightnessContrastAfterScan.InnerText = jsonProfile.GetValue("brightnessContrastAfterScan").ToString().ToLower();
                nodeChildForcePageSize.InnerText = jsonProfile.GetValue("forcePageSize").ToString().ToLower();
                nodeChildForcePageSizeCrop.InnerText = jsonProfile.GetValue("forcePageSizeCrop").ToString().ToLower();
                nodeChildTwainImpl.InnerText = jsonProfile.GetValue("twainImpl").ToString();
                nodeChildExcludeBlankPages.InnerText = jsonProfile.GetValue("excludeBlankPages").ToString().ToLower();
                nodeChildBlankPageWhiteThreshold.InnerText = jsonProfile.GetValue("blankPageWhiteThreshold").ToString();
                nodeChildBlankPageCoverageThreshold.InnerText = jsonProfile.GetValue("blankPageCoverageThreshold").ToString();
                nodeChildWiaOffsetWidth.InnerText = jsonProfile.GetValue("wiaOffsetWidth").ToString().ToLower();
                nodeChildWiaRetryOnFailure.InnerText = jsonProfile.GetValue("wiaRetryOnFailure").ToString().ToLower();
                nodeChildWiaDelayBetweenScans.InnerText = jsonProfile.GetValue("wiaDelayBetweenScans").ToString().ToLower();
                nodeChildWiaDelayBetweenScansSeconds.InnerText = jsonProfile.GetValue("wiaDelayBetweenScansSeconds").ToString();
                nodeChildFlipDuplexedPages.InnerText = jsonProfile.GetValue("flipDuplexedPages").ToString().ToLower();
                // se agregan los elementos del Repositorio:
                if (jsonProfile.GetValue("repositoryIdParent") != null)
                {
                    nodeChildIdParent.InnerText = jsonProfile.GetValue("repositoryIdParent").ToString();
                }
                if (jsonProfile.GetValue("repositoryNameParent") != null)
                {
                    nodeChildNameParent.InnerText = jsonProfile.GetValue("repositoryNameParent").ToString();
                }
                if (jsonProfile.GetValue("repositoryDocumentClass") != null)
                {
                    nodeChildDocumentClass.InnerText = jsonProfile.GetValue("repositoryDocumentClass").ToString();
                }
                if (jsonProfile.GetValue("repositoryDomain") != null)
                {
                    nodeChildDomain.InnerText = jsonProfile.GetValue("repositoryDomain").ToString();
                }
                nodeChildRepository.AppendChild(nodeChildIdParent);
                nodeChildRepository.AppendChild(nodeChildNameParent);
                nodeChildRepository.AppendChild(nodeChildDocumentClass);
                nodeChildRepository.AppendChild(nodeChildDomain);

                nodeScanProfile.AppendChild(nodeChildDevice);
                nodeScanProfile.AppendChild(nodeChildDriverName);
                nodeScanProfile.AppendChild(nodeChildDisplayName);
                nodeScanProfile.AppendChild(nodeChildIconID);
                nodeScanProfile.AppendChild(nodeChildMaxQuality);
                nodeScanProfile.AppendChild(nodeChildIsDefault);
                nodeScanProfile.AppendChild(nodeChildVersion);
                nodeScanProfile.AppendChild(nodeChildUseNativeUI);
                nodeScanProfile.AppendChild(nodeChildAfterScanScale);
                nodeScanProfile.AppendChild(nodeChildBrightness);
                nodeScanProfile.AppendChild(nodeChildContrast);
                nodeScanProfile.AppendChild(nodeChildBitDepth);
                nodeScanProfile.AppendChild(nodeChildPageAlign);
                nodeScanProfile.AppendChild(nodeChildPageSize);
                nodeScanProfile.AppendChild(nodeChildResolution);
                nodeScanProfile.AppendChild(nodeChildPaperSource);
                nodeScanProfile.AppendChild(nodeChildEnableAutoSave);
                nodeScanProfile.AppendChild(nodeChildQuality);
                nodeScanProfile.AppendChild(nodeChildAutoDeskew);
                nodeScanProfile.AppendChild(nodeChildBrightnessContrastAfterScan);
                nodeScanProfile.AppendChild(nodeChildForcePageSize);
                nodeScanProfile.AppendChild(nodeChildForcePageSizeCrop);
                nodeScanProfile.AppendChild(nodeChildTwainImpl);
                nodeScanProfile.AppendChild(nodeChildExcludeBlankPages);
                nodeScanProfile.AppendChild(nodeChildBlankPageWhiteThreshold);
                nodeScanProfile.AppendChild(nodeChildBlankPageCoverageThreshold);
                nodeScanProfile.AppendChild(nodeChildWiaOffsetWidth);
                nodeScanProfile.AppendChild(nodeChildWiaRetryOnFailure);
                nodeScanProfile.AppendChild(nodeChildWiaDelayBetweenScans);
                nodeScanProfile.AppendChild(nodeChildWiaDelayBetweenScansSeconds);
                nodeScanProfile.AppendChild(nodeChildFlipDuplexedPages);
                nodeScanProfile.AppendChild(nodeChildRepository);
                nodeArray.AppendChild(nodeScanProfile);
                contador++;
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
    }
}
