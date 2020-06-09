function getNowFormatDate(day) { 
	if (day == null) {
		return "";
	}
	var dayDate = new (day);
	var Year = 0; 
	var Month = 0; 
	var Day = 0; 
	var CurrentDate = ""; 
	//初始化时间 
	Year= dayDate.getFullYear();
	Month= dayDate.getMonth()+1; 
	Day = dayDate.getDate(); 
	CurrentDate += Year + "-"; 
	if (Month >= 10 ) { 
		CurrentDate += Month + "-"; 
	} else { 
		CurrentDate += "0" + Month + "-"; 
	} 
	if (Day >= 10 ) { 
		CurrentDate += Day ; 
	} else { 
		CurrentDate += "0" + Day ; 
	} 
	return CurrentDate; 
} 