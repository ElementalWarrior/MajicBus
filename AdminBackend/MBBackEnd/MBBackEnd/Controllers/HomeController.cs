﻿using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MBBackEnd.Models.TransitViewModels;
using MBBackEnd.BL;

namespace MBBackEnd.Controllers
{
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            return RedirectToAction("ShowRoutes");
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
            List<RouteView> viewRoutes = routes.Select(r => new RouteView
                {
                    Description = r.Description,
                    dtCreated = r.dtCreated,
                    NameLong = r.NameLong,
                    NameShort = r.NameShort,
                    RouteID = r.RouteID
            }).ToList();

            //pass this information to the view (Views/Home/ShowRoutes.cshtml)
            return View(viewRoutes);

            //or for the api return plain JSON data for the app to render.
            //return Json(viewRoutes, JsonRequestBehavior.AllowGet);
        }
      

        public ActionResult ShowStops(int routeID)
        {

            List<BL.Stop> stops = BL.Route.GetStopsByRouteID(routeID);

            //we want to abstract the information away from the database schema into the view
            //schema therefore we create "View Models" inside the Models folder and convert the 
            //data into those classes
            List<StopView> viewStops = stops.Select(r => new StopView
            {
                StopID = r.StopID,
                lat = r.Lat,
                lon = r.Lon,
                Dtimes = r.Dtimes
            }).ToList();


            MajicBusEntities context = new MajicBusEntities();
            List<usp_estimatedBusLineSegment_Result> res = context.usp_estimatedBusLineSegment(DateTime.UtcNow, "-7:00", routeID).ToList();
            List<RouteShape> shapePosition = (from rs in context.RouteShapes
                                              where rs.RouteID == routeID
                                              select rs).ToList();
            List<Classes.Coordinate> busPositions = BL.Bus.GetBusPosition(routeID).Select(bp => new Classes.Coordinate(bp.Latitude, bp.Longitude)).ToList();
            int routeShape = shapePosition.GroupBy(rs => rs.RouteShapeID).OrderByDescending(agg => agg.Count()).First().Key;
            shapePosition = shapePosition.Where(s => s.RouteShapeID == routeShape).OrderBy(rs => rs.SortID).ToList();
            List<BL.Coordinate> busPos = BL.Bus.GetBusPosition(routeID);
            return View(new BusPositionPage
            {
                RouteShapes = shapePosition.Select(s => new StopView
                {
                    lat = s.Lat,
                    lon = s.Lon
                }).ToList(),
                BusPositions = busPos.Select(bp => new Classes.Coordinate(bp.Latitude, bp.Longitude)).ToList(),
                Stops = viewStops
            });
        }

        public ActionResult ShowRoutesJSON()
        {
            List<BL.Route> routes = BL.Route.GetRoutesJ();
            List<RouteViewJ> viewRoutes = routes.Select(r => new RouteViewJ
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
            List<RouteStopViewJ> Routes = new List<RouteStopViewJ>();

            //For each route given
            for (int i = 0; i < routeIDs.Count; i++) {
                //Find the stops of the route, make the stopView
                List<BL.Stop> stops = BL.Route.GetStopsByRouteID(routeIDs.ElementAt(i));
                List<StopViewJ> viewStops = stops.Select(r => new StopViewJ
                {
                    StopID = r.StopID,
                    lat = (double)r.Lat,
                    lon = (double)r.Lon,
                    StopName = r.StopName,
                    Dtimes = r.Dtimes,
                }).ToList();

                //Add the stopView and id to the list
                RouteStopViewJ Route = new RouteStopViewJ
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
            List<RouteShapeViewJ> Routes = new List<RouteShapeViewJ>();

            //For each route given
            for (int i = 0; i < routeIDs.Count; i++)
            {
                //Find the shapes of the route and add the route id
                RouteShapeViewJ Route = new RouteShapeViewJ
                {
                    routeID = routeIDs.ElementAt(i),
                    Shape = BL.Route.GetRouteShapeByRouteID(routeIDs.ElementAt(i)).Select(r => new RouteShapeJ
                    {
                        sID = r.RouteShapeID,
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


        //This Doesn't Work?, Please help
        public ActionResult ShowBusPositionsJSON(List<int> routeIDs)
        {
            //Wrapper to hold route id and the stops
            List<RouteBusViewJ> Routes = new List<RouteBusViewJ>();

            //For each route given
            for (int i = 0; i < routeIDs.Count; i++)
            {
                //Add a list of buses to the routeviewJ
                RouteBusViewJ Route = new RouteBusViewJ
                {
                    routeID = routeIDs.ElementAt(i),
                    Buses = BL.Bus.GetBusPosition(routeIDs.ElementAt(i)).Select(r => new BusJ
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



        //    return View();
        //}

        //public ActionResult Contact()
        //{
        //    ViewBag.Message = "Your contact page.";

        //    return View();
        //}
    }
}