using System;
using System.Collections.Generic;
using System.Linq;

namespace NAPS2.Scan
{
    [Serializable]
    public class ScanRepository
    {
        public string IdParent { get; set; }

        public string NameParent { get; set; }

        public string DocumentClass { get; set; }

        public string Domain { get; set; }

        public ScanRepository(string idParent, string nameParent, string documentClass, string domain)
        {
            IdParent = idParent;
            NameParent = nameParent;
            DocumentClass = documentClass;
            Domain = domain;
        }

        public ScanRepository()
        {
        }
    }
}
