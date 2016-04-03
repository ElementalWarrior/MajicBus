using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace MBBackEnd.Controllers
{
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            return View();
        }
        
        public ActionResult ShowRoutes()
        {
            //get data from the database in the BL folder (BL = Business Logic of the site)
            //all the requests form the database should be in methods inside of classes here.
            //the class files directly inside of the BL folder should inherit from the class files
            //inside of MajicBus.tt inside of MajicBus.edmx
            //dont edit MajicBus.tt > Route.cs for example because this is a generated file from 
            //the database
            List<BL.Route> routes = BL.Route.GetRoutes();

            //we want to abstract the information away from the database schema into the view
            //schema therefore we create "View Models" inside the Models folder and convert the 
            //data into those classes
            
            //Selects all stops 
            List<Models.RouteView> viewRoutes = routes.Select(r => new Models.RouteView
                {
                    Description = r.Description,
                    dtCreated = r.dtCreated,
                    NameLong = r.NameLong,
                    NameShort = r.NameShort,
                    RouteID = r.RouteID,
                    TripCount = r.Trips.Count(),
                    Stops = r.Trips.First().StopTimes.Select(st => st.Stop).Select(s => new Models.StopView
                    {
                        StopID = s.StopID,
                        lat = s.Lat,
                        lon = s.Lon,

                    }).ToList()
            }).ToList();

            //pass this information to the view (Views/Home/ShowRoutes.cshtml)
            return View(viewRoutes);

            //or for the api return plain JSON data for the app to render.
            //return Json(viewRoutes, JsonRequestBehavior.AllowGet);
        }
      

        public ActionResult ShowStops()
        {

            List<BL.Stop> stops = BL.Route.GetStopsByRouteID(97);

            //we want to abstract the information away from the database schema into the view
            //schema therefore we create "View Models" inside the Models folder and convert the 
            //data into those classes
            List<Models.StopView> viewStops = stops.Select(r => new Models.StopView
            {
                StopID = r.StopID,
                lat = r.Lat,
                lon = r.Lon,
                Dtimes = r.Dtimes
            }).ToList();

            //pass this information to the view (Views/Home/ShowRoutes.cshtml)
            return View(viewStops);
        }

        public ActionResult ShowRoutesJSON()
        {
            List<BL.Route> routes = BL.Route.GetRoutesJ();
            List<Models.RouteViewJ> viewRoutes = routes.Select(r => new Models.RouteViewJ
            {
                NameLong = r.NameLong,
                NameShort = r.NameShort,
                RouteID = r.RouteID,
                //TripCount = r.Trips.Count(),
            }).ToList();
            //Return plain JSON data for the app to render.
            return Json(viewRoutes, JsonRequestBehavior.AllowGet);
        }

        public ActionResult ShowStopsJSON(List<int> routeIDs)
        {
            //Wrapper to hold route id and the stops
            List<Models.RouteStopViewJ> Routes = new List<Models.RouteStopViewJ>();

            //For each route given
            for (int i = 0; i < routeIDs.Count; i++) {
                //Find the stops of the route, make the stopView
                List<BL.Stop> stops = BL.Route.GetStopsByRouteID(routeIDs.ElementAt(i));
                List<Models.StopViewJ> viewStops = stops.Select(r => new Models.StopViewJ
                {
                    StopID = r.StopID,
                    lat = (double)r.Lat,
                    lon = (double)r.Lon,
                    StopName = r.StopName,
                    Dtimes = r.Dtimes,
                }).ToList();

                //Add the stopView and id to the list
                Models.RouteStopViewJ Route = new Models.RouteStopViewJ
                {
                    routeID = routeIDs.ElementAt(i),
                    routeStops = viewStops,
                };
                Routes.Add(Route);
            }
            Routes.ToList();
            
            //Return plain JSON data for the app to render.
            return Json(Routes, JsonRequestBehavior.AllowGet);
        }

        public ActionResult ShowShapesJSON(List<int> routeIDs)
        {
            //Wrapper to hold route id and the stops
            List<Models.RouteShapeViewJ> Routes = new List<Models.RouteShapeViewJ>();

            //For each route given
            for (int i = 0; i < routeIDs.Count; i++)
            {
                //Find the shapes of the route and add the route id
                Models.RouteShapeViewJ Route = new Models.RouteShapeViewJ
                {
                    routeID = routeIDs.ElementAt(i),
                    Shape = BL.Route.GetRouteShapeByRouteID(routeIDs.ElementAt(i)).Select(r => new Models.RouteShapeJ
                    {
                        Lat = (double)r.Lat,
                        Lon = (double)r.Lon
                    }).ToList()
                };
                Routes.Add(Route);
            }
            Routes.ToList();

            //Return plain JSON data for the app to render.
            return Json(Routes, JsonRequestBehavior.AllowGet);
        }


        //This Doesn't Work, Please help
        public ActionResult ShowBusPositionsJSON(List<int> routeIDs)
        {
            //Wrapper to hold route id and the stops
            List<Models.RouteBusViewJ> Routes = new List<Models.RouteBusViewJ>();

            //For each route given
            for (int i = 0; i < routeIDs.Count; i++)
            {
                //Add a list of buses to the routeviewJ
                Models.RouteBusViewJ Route = new Models.RouteBusViewJ
                {
                    routeID = routeIDs.ElementAt(i),
                    Buses = BL.Bus.GetBusPosition(routeIDs.ElementAt(i)).Select(r => new Models.BusJ
                    {
                        Lat = r.Latitude,
                        Lon = r.Longitude
                    }).ToList()
            };

                Routes.Add(Route);
            }
            Routes.ToList();

            //Return plain JSON data for the app to render.
            return Json(Routes, JsonRequestBehavior.AllowGet);
        }


        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }

        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";

            return View();
        }
        [Authorize]
        public ActionResult AdminPage()
        {
            //data domain models.
            List<BL.SMSLog> smsLogs = BL.SMSLog.GetSMSLogs();

            //create our page model
            Models.SMSLogPage pg = new Models.SMSLogPage();
            
            //we need to convert to the mvc model to view on the page
            List<Models.SMSLogEntry> allSmsMessages = smsLogs.Select(smsRecord => new Models.SMSLogEntry
            {
                SMSLogID = smsRecord.SMSLogID,
                SentTo = smsRecord.SentTo,
                ReceivedFrom = smsRecord.ReceivedFrom,
                MessageBody = smsRecord.MessageBody, //need to be wary of XSS scripting
                dtSent = smsRecord.dtSent.Value,
                dtReceived = smsRecord.dtReceived.Value
            }).ToList();

            DateTime beginningOfDayUTC = DateTime.UtcNow;
            beginningOfDayUTC.AddHours(-7); //kelowna = PST, PST w/o DST = -7
            beginningOfDayUTC = beginningOfDayUTC.AddHours(beginningOfDayUTC.Hour * -1); //change to midnight
            beginningOfDayUTC = beginningOfDayUTC.AddMinutes(beginningOfDayUTC.Minute * -1); //change to midnight
            beginningOfDayUTC = beginningOfDayUTC.AddSeconds(beginningOfDayUTC.Second * -1); //change to midnight
            beginningOfDayUTC.AddHours(7); //convert back to utc

            //get all sms's today
            pg.Daily = allSmsMessages.Where(s => s.dtReceived >= beginningOfDayUTC && s.dtReceived <= beginningOfDayUTC.AddHours(24)).ToList();

            DateTime beginningOfWeekUtc = DateTime.UtcNow.AddHours(-7);
            beginningOfWeekUtc = beginningOfWeekUtc.AddDays(-1 * (int)beginningOfWeekUtc.DayOfWeek); // change to sunday
            beginningOfWeekUtc = beginningOfWeekUtc.AddHours(beginningOfWeekUtc.Hour * -1); //change to midnight
            beginningOfWeekUtc = beginningOfWeekUtc.AddMinutes(beginningOfWeekUtc.Minute * -1); //change to midnight
            beginningOfWeekUtc = beginningOfWeekUtc.AddSeconds(beginningOfWeekUtc.Second * -1); //change to midnight
            beginningOfWeekUtc = beginningOfWeekUtc.AddHours(7);

            //get all sms's week
            pg.Weekly = allSmsMessages.Where(s => s.dtReceived >= beginningOfWeekUtc && s.dtReceived <= beginningOfWeekUtc.AddDays(6)).ToList();

            DateTime beginningOfMonthUtc = DateTime.UtcNow.AddHours(-7);
            beginningOfMonthUtc = beginningOfMonthUtc.AddDays(-1 * beginningOfMonthUtc.Day); //set to first day of month
            beginningOfMonthUtc = beginningOfMonthUtc.AddHours(beginningOfMonthUtc.Hour * -1); //change to midnight
            beginningOfMonthUtc = beginningOfMonthUtc.AddMinutes(beginningOfMonthUtc.Minute * -1); //change to midnight
            beginningOfMonthUtc = beginningOfMonthUtc.AddSeconds(beginningOfMonthUtc.Second * -1); //change to midnight
            beginningOfMonthUtc = beginningOfMonthUtc.AddHours(7);

            int numDaysInMonth = beginningOfMonthUtc.AddMonths(1).AddDays(-1).Day;
            //get all sms's monthly
            pg.Monthly = allSmsMessages.Where(s => s.dtReceived >= beginningOfMonthUtc && s.dtReceived <= beginningOfMonthUtc.AddDays(numDaysInMonth + 1)).ToList();

            //get message counts
            List<BL.SMSLog.MessageCounts> messageCounts = pg.Daily.GroupBy(msg => msg.MessageBody).Select(agg => new BL.SMSLog.MessageCounts { MessageBody = agg.Key, Count = agg.Count() }).OrderByDescending(agg => agg.Count).ToList();
            int y = 0;
            if (messageCounts.Count() != 0)
            {
                for (int i = 0; i < messageCounts.Count(); i++)
                {
                   if(Int32.TryParse(messageCounts[i].MessageBody, out y))
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
        [Route("Home/SMSByPhone/{phoneNumber}")]
        public ActionResult SMSByPhone(string phoneNumber)
        {
            List<BL.SMSLog> msgs = BL.SMSLog.GetMessagesByPhone(phoneNumber);

            List<Models.SMSLogEntry> modelMsgs = msgs.Select(blLog => new Models.SMSLogEntry
            {
                dtReceived = blLog.dtReceived.Value,
                dtSent = blLog.dtSent.Value,
                MessageBody = blLog.MessageBody,
                ReceivedFrom = blLog.ReceivedFrom,
                SentTo = blLog.SentTo,
                SMSLogID = blLog.SMSLogID
            }).ToList();

            return View(new Models.SMSFilterPage
            {
                Messages = modelMsgs,
                PhoneNumber = phoneNumber,
                SanitizedPhoneNumber = Classes.Utility.SanitizePhoneNumber(phoneNumber)
            });
        }
    }
}