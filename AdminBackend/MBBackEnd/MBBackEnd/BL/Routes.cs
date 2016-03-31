﻿using System;
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

            //
            return context.Routes.Include("Trips.StopTimes.Stop").ToList();
        }
     //create stops to return lat / long  and stop ids 
        public static List<Stop> GetStopsByRouteID(int routeID)
        {
            var context = new MajicBusEntities();
            List<Stop> stops = (from r in context.Routes
                               let t = context.Trips.FirstOrDefault(tr => tr.RouteID == r.RouteID)
                               join st in context.StopTimes on t.TripID equals st.TripID
                               join s in context.Stops on st.StopID equals s.StopID
                               where r.RouteID == routeID
                               orderby st.SortID
                               select s).ToList();

            //TODO: select the next 5 times or so from the database based on routeID (for each stop!!)
            //you will need to compare the time for this, so compare the dtDeparture >= DateTime.UtcNow.AddHours(-7)
            //compile this information into a List<KeyValuePair<int, List<String>>>, the first list's key will be the stopid,
            //and the nested list will be the times. We only really care about departure times, just ignore arrival for now.

            //I've added a stops.cs class in the BL folder. you will need to modify that and add the list of stop times to the stop class.

            return stops;

            
            
        }
    }
     

}