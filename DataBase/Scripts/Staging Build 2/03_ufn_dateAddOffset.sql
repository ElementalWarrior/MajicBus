if (exists (select top 1 * from sys.objects where type = 'FN' and object_id = object_id('ufn_dateAddOffset')))
begin
	drop function ufn_dateAddOffset
end
go
create function ufn_dateAddOffset(@date datetime, @utcOffset varchar(50))
returns datetime
as 
begin
	declare @negative bit = case when substring(@utcOffset, 1, 1) = '-' then 1 else 0 end
	declare @utcTime varchar(10) = case when substring(@utcOffset, 1, 1) in ('-', '+') then substring(@utcOffset, 2, len(@utcoffset)-1) else @utcOffset end
	declare @localTime datetime = @utcTime
	return case when @negative = 1 then @date - @localTime else @date + @localTime end
end