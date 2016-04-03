using MBBackEnd.BL;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using MBBackEnd.Models.TransitViewModels;

namespace MBBackEnd.Controllers
{
    public class BusController : Controller
    {
        //
        // GET: /Bus/
        public ActionResult GetBusPosition()
        {
            int routeID = 97;
            MajicBusEntities context = new MajicBusEntities();
            List<usp_estimatedBusLineSegment_Result> res = context.usp_estimatedBusLineSegment(DateTime.UtcNow, "-7:00", routeID).ToList();
            List<RouteShape> shapePosition = (from rs in context.RouteShapes
                                              where rs.RouteID == routeID
                                              select rs).ToList();
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
                BusPositions = busPos.Select(bp => new Classes.Coordinate(bp.Latitude, bp.Longitude)).ToList()
            });
        }
	}
}