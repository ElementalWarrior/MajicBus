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
    
    public partial class SMSLog
    {
        public int SMSLogID { get; set; }
        public string ReceivedFrom { get; set; }
        public string SentTo { get; set; }
        public string MessageBody { get; set; }
        public Nullable<System.DateTime> dtReceived { get; set; }
        public Nullable<System.DateTime> dtSent { get; set; }
    }
}
