using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace MBBackEnd.Controllers
{
    public class SmsController : Controller
    {
        // GET: Sms
        public ActionResult Receive(String from, String to, String body)
        {
            BL.SMSLog.UpdateSmsLog(from, to, body);
            int stopID = Int32.Parse(body);
            return Json(new { Phone = from, Data = BL.Route.GetStopByStopID(stopID) }, JsonRequestBehavior.AllowGet);
        }
    }
}