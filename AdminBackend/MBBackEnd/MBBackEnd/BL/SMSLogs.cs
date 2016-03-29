using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.BL
{
    public partial class SMSLog
    {
        IEnumerable<Stop> Stop { get; set; }
        public static List<BL.SMSLog> GetSMSLogs()
        {
            var context = new MajicBusEntities();
            return (from sms in context.SMSLogs
                    select sms).ToList();
        }
        //get total sms messages 
        //get stopId
    }
}