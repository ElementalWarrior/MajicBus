--construct linestring from route
declare @str varchar(max)
SELECT @str = coalesce(@str + ', ','') + convert(varchar(21), lon) + ' ' + convert(varchar(21), lat)
FROM 
(select top 1 * from trips where routeid = 97) t join
 RouteShapes rs on rs.routeshapeid = t.shapeid where t.routeid = 97 order by routeshapeid, rs.sortid

declare @route geometry = 'linestring(' + @str + ')'
select @route
 

 --point to find on routeshape
declare @p1 geometry = 'point(-119.4591870000000000 49.8818080000000000)'

select @route.ToString()

--find closest line from point to route
select @p1.ShortestLineTo(@route.MakeValid()).ToString();