using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace MBBackEnd.BL
{
    public class Bus
    {
        
        public static List<Coordinate> GetBusPosition(int routeID)
        {
            MajicBusEntities context = new MajicBusEntities();
            List<usp_estimatedBusLineSegment_Result> res = context.usp_estimatedBusLineSegment(DateTime.UtcNow, "-7:00", routeID).ToList();
            List<RouteShape> shapePosition = (from rs in context.RouteShapes
                                                  where rs.RouteID == routeID
                                                  select rs).OrderBy(rs => rs.SortID).ToList();

            int routeShape = shapePosition.First().RouteShapeID;
            //shapePosition = shapePosition.Where(s => s.RouteShapeID == routeShape).OrderBy(rs => rs.SortID).ToList();
            List<Coordinate> busPositions = new List<Coordinate>();
            foreach (usp_estimatedBusLineSegment_Result startStop in res)
            {
                RouteShape closestStart = null, closestFinish = null;
                double minStartDist = Double.MaxValue;
                double minFinishDist = Double.MaxValue;
                double distToStart = Double.MaxValue;
                double distToFinish = Double.MaxValue;
                foreach (RouteShape pos in shapePosition)
                {
                    if (closestStart == null && closestFinish == null)
                    {
                        closestStart = pos;
                        closestFinish = pos;
                    }
                    distToStart = Classes.Utility.ComputeDistance((double)pos.Lat, (double)pos.Lon, (double)startStop.s1Lat, (double)startStop.s1Lon);
                    if (distToStart < minStartDist)
                    {
                        minStartDist = distToStart;
                        closestStart = pos;
                    }
                    distToFinish = Classes.Utility.ComputeDistance((double)pos.Lat, (double)pos.Lon, (double)startStop.s2Lat, (double)startStop.s2Lon);
                    if (distToFinish < minFinishDist)
                    {
                        minFinishDist = distToFinish;
                        closestFinish = pos;
                    }
                }
                if (closestStart.SortID == closestFinish.SortID)
                {
                    busPositions.Add(new Coordinate((double)closestStart.Lat, (double)closestStart.Lon));
                    break;
                }
                DateTime normalizedUtcNow = DateTime.UtcNow.AddHours(-7).AddYears(-1 * DateTime.UtcNow.Year + 1900);
                normalizedUtcNow = normalizedUtcNow.AddMonths(1 - normalizedUtcNow.Month);
                normalizedUtcNow = normalizedUtcNow.AddDays(1 - normalizedUtcNow.Day);
                double ticksBetweenStops = startStop.dtS2Departure.Value.Ticks - startStop.dtS1Departure.Value.Ticks;
                double ticksBetweenNowAndStart = normalizedUtcNow.Ticks - startStop.dtS1Departure.Value.Ticks;
                double percentageAlong = ticksBetweenNowAndStart / ticksBetweenStops;

                double totalDistance = 0;
                //this is the stops we want
                List<RouteShape> shapesBetweenStops = shapePosition.Where(rs => rs.RouteShapeID == closestStart.RouteShapeID && rs.SortID >= closestStart.SortID && rs.SortID <= closestFinish.SortID).OrderBy(r => r.SortID).ToList();
                for (int i = 0; i < shapesBetweenStops.Count - 1; i++)
                {
                    Coordinate one = new Coordinate(shapesBetweenStops[i].Lat.Value, shapesBetweenStops[i].Lon.Value);
                    Coordinate two = new Coordinate(shapesBetweenStops[i + 1].Lat.Value, shapesBetweenStops[i + 1].Lon.Value);
                    totalDistance += Coordinate.ComputeDistance(one, two);
                }
                totalDistance *= percentageAlong;
                for (int i = 0; i < shapesBetweenStops.Count - 1; i++)
                {
                    Coordinate one = new Coordinate(shapesBetweenStops[i].Lat.Value, shapesBetweenStops[i].Lon.Value);
                    Coordinate two = new Coordinate(shapesBetweenStops[i + 1].Lat.Value, shapesBetweenStops[i + 1].Lon.Value);
                    double distance = Coordinate.ComputeDistance(one, two);
                    if (totalDistance - distance > 0)
                    {
                        totalDistance -= distance;
                    }
                    else
                    {
                        double percentageBetweenRouteShapes = totalDistance / distance;
                        double lat = (two.Latitude - one.Latitude) * percentageBetweenRouteShapes + one.Latitude;
                        double lon = (two.Longitude - one.Longitude) * percentageBetweenRouteShapes + one.Longitude;
                        busPositions.Add(new Coordinate(lat, lon));
                        break;
                    }
                }
            }
            return busPositions;
        }
    }
}