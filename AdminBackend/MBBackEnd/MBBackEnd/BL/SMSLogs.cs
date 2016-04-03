using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.BL
{
    public partial class SMSLog
    {
        public class MessageCounts
        {
            public String MessageBody { get; set; }
            public int Count { get; set; }
        }
        public static List<BL.SMSLog> GetSMSLogs()
        {
            var context = new MajicBusEntities();
            return (from sms in context.SMSLogs
                    select sms).ToList();
        }
        public static List<BL.SMSLog.MessageCounts> GetMessageCounts()
        {
            var context = new MajicBusEntities();
            return (from sms in context.SMSLogs
                    group sms by sms.MessageBody into aggregate
                    select new BL.SMSLog.MessageCounts { MessageBody = aggregate.Key, Count = aggregate.Count() }).ToList();
        }
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

        public static void UpdateSmsLog(String from, String to, String body)
        {
            MajicBusEntities context = new MajicBusEntities();
            context.SMSLogs.Add(new SMSLog
            {
                MessageBody = body,
                ReceivedFrom = from,
                SentTo = to,
                dtSent = DateTime.UtcNow,
                dtReceived = DateTime.UtcNow
            });
        }
    }
}