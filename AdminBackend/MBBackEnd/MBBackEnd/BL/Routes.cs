using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.BL
{
   
    public partial class Route
    {
        IEnumerable<Stop> Stop { get; set;}
        public static List<Route> GetRoutes()
        {
            var context = new MajicBusEntities();
            //return (from r in context.Routes
            //        join t in context.Trips on r.RouteID equals t.RouteID
            //        select r).Distinct().ToList();
            return context.Routes.Include("Trips").ToList();

        }
        //TODO: create stops to return lat / long  and stop ids 
        public static List<Stop> GetStopsByRouteID(int routeID)
        { 
            var context = new MajicBusEntities();
            return (from r in context.Routes
                    where r.RouteID == routeID
                    select r).First().Trips.First().StopTimes.Select(st => st.Stop).ToList();

            
            
        }
    }
     

}