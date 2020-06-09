//时间格式化
function fmatterDate(cellvalue){
	var time = new Date(cellvalue);
	var y = time.getFullYear();
	var m = time.getMonth()+1;
	if(m<10){
		m="0"+m;
	}
	var d = time.getDate();
	if(d<10){
		d="0"+d;
	}
	var h = time.getHours();
	if(h<10){
		h="0"+h;
	}
	var mm = time.getMinutes();
	if(mm<10){
		mm="0"+mm;
	}
	var s = time.getSeconds();
	if(s<10){
		s="0"+s;
	}
	return y+'-'+m+'-'+d+' '+h+':'+mm+':'+s;
}
function cpmDatePick(elem){  
   $(elem).datetimepicker({
       language: 'zh-CN',
       autoclose: true,
       todayHighlight: true
   }); 
}