if exists (Select top 1 * from sys.tables where object_id = object_id('SMSLogs'))
begin
	drop table SMSLogs
end

create table SMSLogs (
	SMSLogID int primary key identity(1,1),
	ReceivedFrom varchar(15),
	SentTo varchar(15),
	MessageBody varchar(160),
	dtReceived datetime,
	dtSent datetime
	)
