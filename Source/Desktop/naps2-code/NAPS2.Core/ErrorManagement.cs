using NAPS2.Entity;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace NAPS2
{
    class ErrorManagement
    {
        //Servicio de Manejo de Errores
        public ResultadoError MensajeError(String locale, String id)
        {
            String url = "http://192.168.2.10:8080/aquarius/error/";
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);

            String json = "";
            ResultadoError resultado = new ResultadoError();
            try
            {
                request.Headers["locale"] = locale;
                request.Headers["id"] = id;
                WebResponse response = request.GetResponse();
                using (Stream responseStream = response.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.UTF8);
                    json = reader.ReadToEnd();
                    JObject jObjectexito = JObject.Parse(json);
                    JObject jObjectMsgError = JObject.Parse(jObjectexito.GetValue("exito").ToString());

                    resultado.exito = jObjectMsgError.GetValue("message").ToString();

                }
            }
            catch (WebException ex)
            {
                WebResponse errorResponse = ex.Response;
                using (Stream responseStream = errorResponse.GetResponseStream())
                {
                    StreamReader reader = new StreamReader(responseStream, System.Text.Encoding.GetEncoding("utf-8"));
                    json = reader.ReadToEnd();
                }

            }
            return resultado;
        }
    }
}
