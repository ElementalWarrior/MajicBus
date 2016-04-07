using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MBBackEnd.Models.SmsViewModels;

namespace MBBackEnd.Controllers
{
    public class SmsController : Controller
    {
        // GET: Sms
        public ActionResult Receive(String from, String to, String body)
        {
            BL.SMSLog.UpdateSmsLog(from, to, body.Substring(0, Math.Min(159, body.Length)));
            int stopID = Int32.Parse(body);
            return Json(new { Phone = from, Data = BL.Route.GetStopByStopID(stopID) }, JsonRequestBehavior.AllowGet);
        }
        public ActionResult LogMessageSent(String from, String to, String body)
        {
            BL.SMSLog.UpdateSmsLog(from, to, body.Substring(0, Math.Min(159, body.Length)));
            return Json("", JsonRequestBehavior.AllowGet);
        }
        [Authorize]
        public ActionResult Index()
        {
            //data domain models.
            List<BL.SMSLog> smsLogs = BL.SMSLog.GetSMSLogs();

            //create our page model
            SMSLogPage pg = new SMSLogPage();

            //we need to convert to the mvc model to view on the page
            List<SMSLogEntry> allSmsMessages = smsLogs.Select(smsRecord => new SMSLogEntry
            {
                SMSLogID = smsRecord.SMSLogID,
                SentTo = smsRecord.SentTo,
                ReceivedFrom = smsRecord.ReceivedFrom,
                MessageBody = smsRecord.MessageBody, //need to be wary of XSS scripting
                dtSent = smsRecord.dtSent.Value,
                dtReceived = smsRecord.dtReceived.Value
            }).ToList();

            DateTime beginningOfDayUTC = DateTime.UtcNow;
            beginningOfDayUTC = beginningOfDayUTC.AddHours(-7); //kelowna = PST, PST w/o DST = -7
            beginningOfDayUTC = beginningOfDayUTC.AddHours(beginningOfDayUTC.Hour * -1); //change to midnight
            beginningOfDayUTC = beginningOfDayUTC.AddMinutes(beginningOfDayUTC.Minute * -1); //change to midnight
            beginningOfDayUTC = beginningOfDayUTC.AddSeconds(beginningOfDayUTC.Second * -1); //change to midnight
            beginningOfDayUTC = beginningOfDayUTC.AddHours(7); //convert back to utc

            //get all sms's today
            pg.Daily = allSmsMessages.Where(s => s.dtReceived >= beginningOfDayUTC && s.dtReceived <= new DateTime(beginningOfDayUTC.Ticks).AddHours(24)).ToList();

            DateTime beginningOfWeekUtc = DateTime.UtcNow.AddHours(-7);
            beginningOfWeekUtc = beginningOfWeekUtc.AddDays(-1 * (int)beginningOfWeekUtc.DayOfWeek); // change to sunday
            beginningOfWeekUtc = beginningOfWeekUtc.AddHours(beginningOfWeekUtc.Hour * -1); //change to midnight
            beginningOfWeekUtc = beginningOfWeekUtc.AddMinutes(beginningOfWeekUtc.Minute * -1); //change to midnight
            beginningOfWeekUtc = beginningOfWeekUtc.AddSeconds(beginningOfWeekUtc.Second * -1); //change to midnight
            beginningOfWeekUtc = beginningOfWeekUtc.AddHours(7);

            //get all sms's week
            pg.Weekly = allSmsMessages.Where(s => s.dtReceived >= beginningOfWeekUtc && s.dtReceived <= new DateTime(beginningOfWeekUtc.Ticks).AddDays(6)).ToList();

            DateTime beginningOfMonthUtc = DateTime.UtcNow.AddHours(-7);
            beginningOfMonthUtc = beginningOfMonthUtc.AddDays(-1 * beginningOfMonthUtc.Day); //set to first day of month
            beginningOfMonthUtc = beginningOfMonthUtc.AddHours(beginningOfMonthUtc.Hour * -1); //change to midnight
            beginningOfMonthUtc = beginningOfMonthUtc.AddMinutes(beginningOfMonthUtc.Minute * -1); //change to midnight
            beginningOfMonthUtc = beginningOfMonthUtc.AddSeconds(beginningOfMonthUtc.Second * -1); //change to midnight
            beginningOfMonthUtc = beginningOfMonthUtc.AddHours(7);

            int numDaysInMonth = beginningOfMonthUtc.AddMonths(1).AddDays(-1).Day;
            //get all sms's monthly
            pg.Monthly = allSmsMessages.Where(s => s.dtReceived >= beginningOfMonthUtc && s.dtReceived <= new DateTime(beginningOfMonthUtc.Ticks).AddDays(numDaysInMonth + 1)).ToList();

            //get message counts
            List<BL.SMSLog.MessageCounts> messageCounts = pg.Daily.GroupBy(msg => msg.MessageBody).Select(agg => new BL.SMSLog.MessageCounts { MessageBody = agg.Key, Count = agg.Count() }).OrderByDescending(agg => agg.Count).ToList();
            int y = 0;
            if (messageCounts.Count() != 0)
            {
                for (int i = 0; i < messageCounts.Count(); i++)
                {
                    if (Int32.TryParse(messageCounts[i].MessageBody, out y))
                    {
                        break;
                    }
                }
            }
            pg.MostPopularDaily = y;

            messageCounts = pg.Weekly.GroupBy(msg => msg.MessageBody).Select(agg => new BL.SMSLog.MessageCounts { MessageBody = agg.Key, Count = agg.Count() }).OrderByDescending(agg => agg.Count).ToList();
            int x = 0;
            if (messageCounts.Count() != 0)
            {
                for (int i = 0; i < messageCounts.Count(); i++)
                {
                    if (Int32.TryParse(messageCounts[i].MessageBody, out x))
                    {
                        break;
                    }
                }
            }
            pg.MostPopularWeekly = x;

            messageCounts = pg.Monthly.GroupBy(msg => msg.MessageBody).Select(agg => new BL.SMSLog.MessageCounts { MessageBody = agg.Key, Count = agg.Count() }).OrderByDescending(agg => agg.Count).ToList();
            int z = 0;
            if (messageCounts.Count() != 0)
            {
                for (int i = 0; i < messageCounts.Count(); i++)
                {
                    if (Int32.TryParse(messageCounts[i].MessageBody, out z))
                    {
                        break;
                    }
                }
            }
            pg.MostPopularMonthly = z;

            return View(pg);
        }
        [Authorize]
        [Route("Sms/ByPhone/{phoneNumber}")]
        public ActionResult SMSByPhone(string phoneNumber)
        {
            List<BL.SMSLog> msgs = BL.SMSLog.GetMessagesByPhone(phoneNumber);

            List<SMSLogEntry> modelMsgs = msgs.Select(blLog => new SMSLogEntry
            {
                dtReceived = blLog.dtReceived.Value,
                dtSent = blLog.dtSent.Value,
                MessageBody = blLog.MessageBody,
                ReceivedFrom = blLog.ReceivedFrom,
                SentTo = blLog.SentTo,
                SMSLogID = blLog.SMSLogID
            }).ToList();

            return View(new SMSFilterPage
            {
                Messages = modelMsgs,
                PhoneNumber = phoneNumber,
                SanitizedPhoneNumber = Classes.Utility.SanitizePhoneNumber(phoneNumber)
            });
        }
    }
}