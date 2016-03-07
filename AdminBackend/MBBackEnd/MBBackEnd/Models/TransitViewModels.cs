using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.Models
{
    public class RouteView
    {
        public int RouteID { get; set; }
        public string NameShort { get; set; }
        public string NameLong { get; set; }
        public string Description { get; set; }
        public System.DateTime dtCreated { get; set; }
    }
}