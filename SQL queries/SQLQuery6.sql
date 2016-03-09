/****** Script for SelectTopNRows command from SSMS  ******/

SELECT Distinct Stops.StopID, lat, lon
FROM Routes, Trips, stoptimes, Stops
WHERE Routes.RouteId = Trips.RouteId
AND Trips.TripID = stoptimes.TripId
AND stoptimes.StopID = Stops.StopID
AND Routes.RouteId = 8; 
