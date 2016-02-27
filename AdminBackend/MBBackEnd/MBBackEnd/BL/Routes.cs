using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.BL
{
    public partial class Route
    {
        public static List<Route> GetRoutes()
        {
            var context = new MajicBusEntities();
            return (from r in context.Routes
                    select r).ToList();
        }
    }
}