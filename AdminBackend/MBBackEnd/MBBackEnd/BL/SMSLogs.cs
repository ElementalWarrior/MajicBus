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
        public static List<SMSLog> GetMessagesByPhone(string phoneNumber)
        {
            MajicBusEntities context = new MajicBusEntities();
            phoneNumber = Classes.Utility.SanitizePhoneNumber(phoneNumber);
            List<SMSLog> messages = (from sms in context.SMSLogs
                                     where sms.ReceivedFrom == phoneNumber
                                     || sms.SentTo == phoneNumber
                                     select sms).ToList();
            return messages; //Classes.Utility.S
        }
    }
}