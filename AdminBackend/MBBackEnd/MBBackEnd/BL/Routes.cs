using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.BL
{
   
    public partial class Route
    {
        public static List<RouteShape> GetRouteShapeByRouteID(int routeID)
        {
            MajicBusEntities context = new MajicBusEntities();
            List<RouteShape> shapePosition = (from rs in context.RouteShapes
                                              where rs.RouteID == routeID
                                              orderby rs.RouteShapeID, rs.SortID
                                              select rs).ToList();
            return shapePosition;
        }

        IEnumerable<Stop> Stop { get; set;}
        public static List<Route> GetRoutes()
        {
            var context = new MajicBusEntities();
            //return (from r in context.Routes
            //        join t in context.Trips on r.RouteID equals t.RouteID
            //        select r).Distinct().ToList();

            //
            return context.Routes.ToList();
            //return context.Routes.Include("Trips.StopTimes.Stop").ToList();
        }

        //For RouteList for App
        public static List<Route> GetRoutesJ()
        {
            var context = new MajicBusEntities();
            //return (from r in context.Routes
            //        join t in context.Trips on r.RouteID equals t.RouteID
            //        select r).Distinct().ToList();

            //
            return context.Routes.ToList();
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
        
            DateTime now = DateTime.UtcNow.AddHours(-7);
            DateTime utcMidnight = DateTime.UtcNow.AddHours(-7);
            utcMidnight = utcMidnight.AddHours(-1 * utcMidnight.Hour);
            utcMidnight = utcMidnight.AddMinutes(-1 * utcMidnight.Minute);
            utcMidnight = utcMidnight.AddSeconds(-1 * utcMidnight.Second);
            TimeSpan ts = now - utcMidnight;
            DateTime normalizedNow = new DateTime(1900,1,1) + ts;

            List<StopTime> times = (from r in context.Routes
                                join t in context.Trips on r.RouteID equals t.RouteID
                                join st in context.StopTimes on t.TripID equals st.TripID
                                where r.RouteID == routeID && st.dtDeparture >= normalizedNow
                                    orderby st.dtDeparture, st.SortID
                                select st).ToList(); 

            foreach(Stop s in stops)
            {
                s.Dtimes = times.Where(st => st.StopID == s.StopID).Take(5).Select(st => st.Departure).ToList();
            }
            
            //TODO: select the next 5 times or so from the database based on routeID (for each stop!!)
            //you will need to compare the time for this, so compare the dtDeparture >= DateTime.UtcNow.AddHours(-7)
            //compile this information into a List<KeyValuePair<int, List<String>>>, the first list's key will be the stopid,
            //and the nested list will be the times. We only really care about departure times, just ignore arrival for now.

            //I've added a stops.cs class in the BL folder. you will need to modify that and add the list of stop times to the stop class.

            return stops;
        }

        public class S
        {
            public int RouteID { get; set; }
            public int StopID { get; set; }
            public List<string> Dtimes { get; set; }
        }
        public static List<S> GetStopByStopID(int stopID)
        {
            MajicBusEntities context = new MajicBusEntities();

            DateTime now = DateTime.UtcNow.AddHours(-7);
            DateTime utcMidnight = DateTime.UtcNow.AddHours(-7);
            utcMidnight = utcMidnight.AddHours(-1 * utcMidnight.Hour);
            utcMidnight = utcMidnight.AddMinutes(-1 * utcMidnight.Minute);
            utcMidnight = utcMidnight.AddSeconds(-1 * utcMidnight.Second);
            TimeSpan ts = now - utcMidnight;
            DateTime normalizedNow = new DateTime(1900, 1, 1) + ts;

            List<S> times = (from st in context.StopTimes
                                    join t in context.Trips on st.TripID equals t.TripID
                                    where st.StopID == stopID && st.dtDeparture >= normalizedNow
                                     orderby st.dtDeparture.Value
                                     group new { t, st } by t.RouteID into aggregate
                                     select new S
                                     {
                                         RouteID = aggregate.Select(a => a.t.RouteID.Value).FirstOrDefault(),
                                         StopID = stopID,
                                         Dtimes = aggregate.Select(a => a.st.Departure).Take(5).ToList()
                                         
                                     }).ToList();
            
            
            return times;
        }
    }
     

}