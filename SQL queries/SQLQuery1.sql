alter table Trips
add constraint fkTripsRouteID foreign key (RouteID) references Routes(RouteID)

alter table StopTimes
add constraint fkStopTimesTripID foreign key (TripID) references Trips(TripID)

alter table StopTimes
add constraint fkStopTimesStopID foreign key (StopID) references Stops(StopID)
