declare @utcOffset varchar(50) = '-7:00:00'
declare @currDate datetime = dbo.ufn_dateAddOffset(dateadd(day, -3, getutcdate()), @utcOffset);
--declare @currDate datetime = dbo.ufn_dateAddOffset(getutcdate(), @utcOffset);
--select @currdate

with sts as (
--get stoptimes for all trips on route where the departure time is after current time
select * from (
	select t.routeid, t.tripid, st.stopid, st.dtdeparture, st.sortID, row_number() over (partition by t.tripid order by t.tripid, st.sortid desc) as RowNumber from trips t
	join Calendar c on c.CalendarID = t.CalendarID
	join StopTimes st on st.TripID = t.TripID
	where
	--routeid = 97
	--and 
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

select * from sts s
join stoptimes st on s.tripid = st.tripid and st.sortid = s.sortid + 1
