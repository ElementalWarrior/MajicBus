using System;
using System.Collections.Generic;
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
            List<Models.RouteView> viewRoutes = routes.Select(r => new Models.RouteView
                {
                    Description = r.Description,
                    dtCreated = r.dtCreated,
                    NameLong = r.NameLong,
                    NameShort = r.NameShort,
                    RouteID = r.RouteID,
                    TripCount = r.Trips.Count()
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
          
            }).ToList();

            //pass this information to the view (Views/Home/ShowRoutes.cshtml)
            return View(viewStops);
        }

        public ActionResult ShowRoutesJSON()
        {
            List<BL.Route> routes = BL.Route.GetRoutes();
            List<Models.RouteView> viewRoutes = routes.Select(r => new Models.RouteView
            {
                Description = r.Description,
                dtCreated = r.dtCreated,
                NameLong = r.NameLong,
                NameShort = r.NameShort,
                RouteID = r.RouteID,
                TripCount = r.Trips.Count()
            }).ToList();
            //Return plain JSON data for the app to render.
            return Json(viewRoutes, JsonRequestBehavior.AllowGet);
        }

        public ActionResult ShowStopsJSON()
        {
            List<BL.Stop> stops = BL.Route.GetStopsByRouteID(97);
            List<Models.StopView> viewStops = stops.Select(r => new Models.StopView
            {
                StopID = r.StopID,
                lat = r.Lat,
                lon = r.Lon,

            }).ToList();
            //Return plain JSON data for the app to render.
            return Json(viewStops, JsonRequestBehavior.AllowGet);
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
        public ActionResult AdminPage()
        {
            return View();
        }
    }
}