begin tran

declare @type int
select top 1 @type = system_type_id from sys.types where name = 'varchar'
if(exists(select * from sys.columns where name = 'dtDeparture' and object_id = object_id('StopTimes') and system_type_id = @type))
begin
	exec sp_rename 'dbo.StopTimes.dtDeparture', 'Departure'
end

if(exists(select * from sys.columns where name = 'dtArrival' and object_id = object_id('StopTimes') and system_type_id = @type))
begin
	exec sp_rename 'dbo.StopTimes.dtArrival', 'Arrival'
end

go

if (not exists (select top 1 * from sys.columns where name = 'dtDeparture' and object_id = object_id('StopTimes')))
begin
	print 'Adding dtDeparture(Datetime) to StopTimes'
	print ''
	alter table StopTimes
	add dtDeparture datetime
end

go

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

--commit tran
--rollback tran