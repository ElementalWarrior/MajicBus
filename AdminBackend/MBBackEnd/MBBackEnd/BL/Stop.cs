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
    
    public partial class Stop
    {
        public Stop()
        {
            this.StopTimes = new HashSet<StopTime>();
        }
    
        public int StopID { get; set; }
        public string StopName { get; set; }
        public string StopNameShort { get; set; }
        public Nullable<decimal> Lat { get; set; }
        public Nullable<decimal> Lon { get; set; }
        public System.DateTime dtCreated { get; set; }
    
        public virtual ICollection<StopTime> StopTimes { get; set; }
    }
}
