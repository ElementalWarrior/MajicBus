if(exists (select top 1 * from sys.tables where object_id = object_id('ErrorLogs')))
begin
	drop table ErrorLogs
end

create table ErrorLogs
(
	ErrorLogID int primary key identity(1,1),
	Application varchar(200),
	Machine varchar(max) not null,
	Method varchar(max) not null,
	Message varchar(max),
	Stack varchar(max),
	dtCreated datetime not null
)