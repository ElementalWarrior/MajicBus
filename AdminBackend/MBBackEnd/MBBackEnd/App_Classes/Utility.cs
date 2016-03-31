using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.Classes
{
    public class Utility
    {
        public static string SanitizePhoneNumber(string unsanitized)
        {
            string retStr = "";
            for(int i = 0; i < unsanitized.Length; i++)
            {
                if((int)unsanitized[i] >= (int)'0' && (int)unsanitized[i] <= (int)'9')
                {
                    retStr += unsanitized[i];
                }
            }
            return retStr;
        }
        public static double ComputeDistance(double Latitude1, double Longitude1, double Latitude2, double Longitude2)
        {

            double R = 6371000; // metres
            double lat1Rads = Latitude1 * Math.PI / 180; //convert to rads
            double lat2Rads = Latitude2 * Math.PI / 180; //convert to rads
            double deltaLat = (Latitude2 - Latitude1) * Math.PI / 180;
            double deltaLon = (Longitude2 - Longitude1) * Math.PI / 180;

            var a = Math.Sin(deltaLat / 2) * Math.Sin(deltaLat / 2) +
                    Math.Cos(lat1Rads) * Math.Cos(lat2Rads) *
                    Math.Sin(deltaLon / 2) * Math.Sin(deltaLon / 2);
            var c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
            return Math.Abs(R * c); //distance
        }
    }
}