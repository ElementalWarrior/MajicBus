//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace MBBackEnd.BL
{
    using System;
    
    public partial class usp_estimatedBusLineSegment_Result
    {
        public Nullable<int> routeid { get; set; }
        public int tripid { get; set; }
        public int stopid { get; set; }
        public Nullable<System.DateTime> dtdeparture { get; set; }
        public Nullable<int> sortID { get; set; }
        public Nullable<long> RowNumber { get; set; }
        public int TripID1 { get; set; }
        public int StopID1 { get; set; }
        public string Arrival { get; set; }
        public string Departure { get; set; }
        public Nullable<int> SortID1 { get; set; }
        public System.DateTime dtCreated { get; set; }
        public Nullable<System.DateTime> dtDeparture1 { get; set; }
    }
}