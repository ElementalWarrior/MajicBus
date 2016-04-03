if(exists(select top 1 * from sys.procedures where name = 'usp_estimatedBusLineSegment'))
begin
	drop procedure usp_estimatedBusLineSegment
end

go

create procedure usp_estimatedBusLineSegment(@timeUtc datetime, @utcOffset varchar(50), @routeid int = null)
as
begin
	declare @currDate datetime = dbo.ufn_dateAddOffset(@timeutc, @utcOffset);
	with sts as (
	--get stoptimes for all trips on route where the departure time is after current time
	select * from (
		select t.routeid, t.tripid, st.stopid, st.dtdeparture, st.sortID, row_number() over (partition by t.tripid order by t.tripid, st.sortid desc) as RowNumber from trips t
		join Calendar c on c.CalendarID = t.CalendarID
		join StopTimes st on st.TripID = t.TripID
		where
		(@routeid is null or routeid = @routeid)
		and 
		c.dtStartDate <= @currDate
		and c.dtEndDate >= @currDate
		and case 
			when datename(weekday, @currdate) = 'Monday' and Monday = 1 then 1
			when datename(weekday, @currdate) = 'Tuesday' and Tuesday = 1 then 1
			when datename(weekday, @currdate) = 'Wednesday' and Wednesday = 1 then 1
			when datename(weekday, @currdate) = 'Thursday' and Thursday = 1 then 1
			when datename(weekday, @currdate) = 'Friday' and Friday = 1 then 1
			when datename(weekday, @currdate) = 'Saturday' and Saturday = 1 then 1
			when datename(weekday, @currdate) = 'Sunday' and Sunday = 1 then 1
			else 0 end = 1
		and dtdeparture <= convert(datetime, convert(time, @currdate))
		group by t.routeid, t.tripid, st.stopid, st.sortid, st.dtDeparture
		) agg
	where rownumber = 1
	)

	select
		st1.routeid, st1.tripid,
		st1.stopid as s1ID, st1.dtdeparture as dtS1Departure, s.lat as s1Lat, s.lon as s1Lon --point 1 info
		,
		st2.stopid as s2ID, st2.dtdeparture as dtS2Departure, s2.lat as s2Lat, s2.lon as s2Lon --point 2 info
	from sts st1
	join stoptimes st2 on st1.tripid = st2.tripid and st2.sortid = st1.sortid + 1
	join stops s on s.stopid = st1.stopid
	join stops s2 on s2.stopid = st2.stopid
end