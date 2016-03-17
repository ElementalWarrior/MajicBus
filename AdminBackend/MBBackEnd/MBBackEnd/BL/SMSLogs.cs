using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.BL
{
    public partial class SMSLog
    {
        IEnumerable<Stop> Stop { get; set; }
        public static List<SMSLog> GetSMSLog()
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
                    let t = context.Trips.FirstOrDefault(tr => tr.RouteID == r.RouteID)
                    join st in context.StopTimes on t.TripID equals st.TripID
                    join s in context.Stops on st.StopID equals s.StopID
                    where r.RouteID == routeID
                    select s).ToList();
        }
    }
}