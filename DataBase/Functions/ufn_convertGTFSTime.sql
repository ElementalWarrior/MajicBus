if exists (select top 1 * from sys.objects where type = 'FN' and object_id = object_id('ufn_convertGTFSTime'))
begin
	drop function ufn_convertGTFSTime
end 

go

create function ufn_convertGTFSTime(@utcOffset varchar(50), @time varchar(50), @date datetime = null)
returns datetime
as
begin 
	if(@date is null)
	begin
		set @date = getutcdate()
	end
	--@time format: 10:00:00
	--@utcOffset format -7:00:00 or +7:00:00/7:00:00
	declare @dt datetime = @date
	declare @hour varchar(2) = substring(@time, 0, charindex(':', @time))
	declare @afterHour varchar(57) = right(@time, len(@time) - len(@hour)-1)
	declare @nextDate bit = 0

	
	declare @negative bit = case when substring(@utcOffset, 1, 1) = '-' then 1 else 0 end
	declare @utcTime varchar(10) = case when substring(@utcOffset, 1, 1) in ('-', '+') then substring(@utcOffset, 2, len(@utcoffset)-1) else @utcOffset end
	declare @localTime datetime = @utcTime
	set @dt = case when @negative = 1 then @dt - @localTime else @dt + @localTime end
	

	if(@hour > 23)
	begin
		set @nextDate = 1
		set @hour = convert(varchar(50), convert(int, @hour) - 24)
		set @dt = dateadd(day, 1, convert(datetime, convert(varchar, @dt, 110) + ' ' + @hour +  ':' + @afterHour))
		
	end
	else 
	begin 
		set @dt = convert(varchar, @dt, 110) + ' ' + @time
	end

	return @dt
end
