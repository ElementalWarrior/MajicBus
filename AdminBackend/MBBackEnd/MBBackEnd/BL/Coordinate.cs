using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.BL
{
    public class Coordinate
    {
        public Double Latitude { get; set; }
        public Double Longitude { get; set; }

        public Coordinate() { }
        public Coordinate(Double lat, Double lon)
        {
            Latitude = lat;
            Longitude = lon;
        }
        public Coordinate(decimal lat, decimal lon) : this((double)lat, (Double)lon)
        {
        }
        public double ComputeDistance(Coordinate pos2)
        {
            return Coordinate.ComputeDistance(this, pos2);
        }
        public static double ComputeDistance(Coordinate pos1, Coordinate pos2)
        {

            double R = 6371000; // metres
            double lat1Rads = pos1.Latitude * Math.PI / 180; //convert to rads
            double lat2Rads = pos2.Latitude * Math.PI / 180; //convert to rads
            double deltaLat = (pos2.Latitude - pos1.Latitude) * Math.PI / 180;
            double deltaLon = (pos2.Longitude - pos1.Longitude) * Math.PI / 180;

            var a = Math.Sin(deltaLat / 2) * Math.Sin(deltaLat / 2) +
                    Math.Cos(lat1Rads) * Math.Cos(lat2Rads) *
                    Math.Sin(deltaLon / 2) * Math.Sin(deltaLon / 2);
            var c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            return Math.Abs(R * c); //distance
        }
    }
}