sp_rename 'dbo.StopTimes.dtDeparture' 'Departure'
go
sp_rename 'dbo.StopTimes.dtArrival' 'Arrival'
go

if (not exists (select top 1 * from sys.columns where name = 'Departure' and object_id = object_id('StopTimes')))
begin
	print 'Adding dtDeparture(Datetime) to StopTimes'
	print ''
	alter table StopTimes
	add dtDeparture datetime
end

if (exists (select top 1 * from sys.triggers where name = 'trg_StopTimesAltered' and parent_id = object_id('StopTimes')))
begin
	drop trigger trg_StopTimesAltered
end
go
create trigger trg_StopTimesAltered
on StopTimes
after insert, update
as
begin
	update stoptimes set dtDeparture = convert(datetime, 0) + dbo.ufn_convertGTFSTime('00:00', i.Departure, convert(datetime, 0)) from stoptimes st join inserted i on i.Tripid = st.Tripid and i.stopid = st.stopid
end

update StopTimes set dtDeparture = convert(datetime, 0) + dbo.ufn_convertGTFSTime('00:00', i.Departure, convert(datetime, 0))