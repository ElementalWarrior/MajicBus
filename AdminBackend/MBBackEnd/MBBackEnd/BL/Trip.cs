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
    using System.Collections.Generic;
    
    public partial class Trip
    {
        public int TripID { get; set; }
        public Nullable<int> ShapeID { get; set; }
        public Nullable<int> RouteID { get; set; }
        public Nullable<int> SortID { get; set; }
        public Nullable<int> Direction { get; set; }
        public System.DateTime dtCreated { get; set; }
        public string CalendarID { get; set; }
    }
}