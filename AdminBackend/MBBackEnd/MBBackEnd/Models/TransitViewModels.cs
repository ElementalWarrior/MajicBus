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
        public DateTime dtCreated { get; set; }
        public int TripCount { get; set; }
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
    }
}