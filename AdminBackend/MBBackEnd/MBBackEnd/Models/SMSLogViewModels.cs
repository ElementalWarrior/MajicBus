using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.Models
{
    public class SMSLogPage
    {
        public List<SMSLogEntry> Daily { get; set; }
        public List<SMSLogEntry> Weekly { get; set; }
        public List<SMSLogEntry> Monthly { get; set; }
        public int MessagesSent { get; set; }
        public int MessagesReceived { get; set; }
        public Models.StopView MostPopular { get; set; }
    }
    public class SMSLogEntry
    {
        public int SMSLogID { get; set; }
        public string ReceivedFrom { get; set; }
        public string SentTo { get; set; }
        public string MessageBody { get; set; }
        public DateTime dtReceived { get; set; }
        public DateTime dtSent { get; set; }
    }
    public class SMSFilterPage
    {
        public List<SMSLogEntry> Messages { get; set; }
        public String PhoneNumber { get; set; }
        public String SanitizedPhoneNumber { get; set; }
    }
}