function chooseTxt() {
	//This just chooses a random phrase to be typed. (not required, and may impact results?)
    var rnd = Math.floor((Math.random() * 10) + 1);
	if (rnd == 1)
		str = "vertical-glyphicon glyphicon glyphicon-camera gi-5x";
	if (rnd == 2)
		str = "vertical-glyphicon glyphicon glyphicon-heart gi-5x";
	if (rnd == 3)
		str = "vertical-glyphicon glyphicon glyphicon-leaf gi-5x";
	if (rnd == 4)
		str = "vertical-glyphicon glyphicon glyphicon-fire gi-5x";
	if (rnd == 5)
		str = "vertical-glyphicon glyphicon glyphicon-tint gi-5x";
	if (rnd == 6)
		str = "vertical-glyphicon glyphicon glyphicon-ok gi-5x";
	if (rnd == 7)
		str = "vertical-glyphicon glyphicon glyphicon-remove gi-5x";
	if (rnd == 8)
		str = "vertical-glyphicon glyphicon glyphicon-globe gi-5x";
	if (rnd == 9)
		str = "vertical-glyphicon glyphicon glyphicon-thumbs-up gi-5x";
	if (rnd == 10)
		str = "vertical-glyphicon glyphicon glyphicon-thumbs-down gi-5x";
    document.getElementById("pic").className = str;
}

