using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.Models.TransitViewModels
{
    public class BusPositionPage
    {
        public List<StopView> RouteShapes { get; set; }
        public List<Classes.Coordinate> BusPositions { get; set; }
        public List<StopView> Stops { get; set; }
    }
    public class RouteView
    {
        public int RouteID { get; set; }
        public string NameShort { get; set; }
        public string NameLong { get; set; }
        public string Description { get; set; }
        public DateTime dtCreated { get; set; }
        public int TripCount { get; set; }
        public List<StopView> Stops { get; set; }
    }

    public class TripView
    {
        public int TripID { get; set; }
        public int? ShapeID { get; set; }
        public int? RouteID { get; set; }
        public int? SortID { get; set; }
        public int? Direction { get; set; }
        public DateTime dtCreated { get; set; }
    }

    public class StopView
    {
        public int StopID { get; set; }
        public String StopName { get; set; }
        public decimal? lat { get; set; }
        public decimal? lon { get; set; }
        public string arrival_time { get; set; }
        public string departure_time { get; set; }
        public List<String> Dtimes { get; set; } 
    }

   
    public class RouteShapeViewJ
    {
        public int routeID { get; set; }
        public List<RouteShapeJ> Shape { get; set; }
    }


    public class RouteViewJ
    {
        public int RouteID { get; set; }
        public string NameShort { get; set; }
        public string NameLong { get; set; }
    }

    public class RouteStopViewJ
    {
        public int routeID { get; set; }
        public List<StopViewJ> routeStops { get; set; }
    }

    public class StopViewJ
    {
        public int StopID { get; set; }
        public String StopName { get; set; }
        public double lat { get; set; }
        public double lon { get; set; }
        public List<String> Dtimes { get; set; }
    }

    public partial class RouteShapeJ
    {
        public int sID { get; set; }
        public double Lon { get; set; }
        public double Lat { get; set; }
    }

    public partial class RouteBusViewJ
    {
        public int routeID { get; set; }
        public List<BusJ> Buses { get; set; }
    }

    public partial class BusJ
    {
        public double Lat { get; set; }
        public double Lon { get; set; }
        
    }

}