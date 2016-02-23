var TimeArray = new Array();

function start() {
		var d = new Date();
		s = d.getTime();	
	}

counter = 0; 

function finish(x){		
			//if(x == str){
			var elapsedtime = 0;
			var d = new Date();
			f = d.getTime();
			elapsedtime = f-s;

			//window.alert(elapsedtime + "ms");
			TimeArray.push(elapsedtime);

			console.log(TimeArray);

			
			
			//function record(TimeArray,index){

			//	dataString = TimeArray.join(",");
			//	csvContent += index < data.length ? dataString + "\n" : dataString;
			//}

			f = 0;
			s = 0; 
			if(counter < 49){
			counter = counter + 1; 
			//window.alert(counter);
			chooseTxt();

			} else {
				window.alert("You're done :D");
				
				var csvContent = "data:text/csv;charset=utf-8,";
				var outputstring = "data:text/csv;charset=utf-8,";
				
				for(var i = 0; i < TimeArray.length;i++){
					var iplus1 = i + 1;
					outputstring += "time" + iplus1 + "," + TimeArray[i] + ","; 
				}
				csvContent += outputstring;
				
				var encodedUri = encodeURI(csvContent);
				var link = document.createElement("a");
				link.setAttribute("href", encodedUri);
				link.setAttribute("download", "my_data.csv");
				link.click();
			} 
			
			//} else {
			//window.alert("Not the correct icon");	
			}
	
$('#btnContainer button').each(function() {
//console.log(this);
    $(this).width(50 + 'px')
    $(this).height(50 + 'px')

})

