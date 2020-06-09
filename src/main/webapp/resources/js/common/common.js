//月份补0
function fMonth(dateObj){
	var month = dateObj.getMonth() + 1;
	var monthStr = month<10? "0"+month : month;
	return monthStr;
}
//天补0
function fDay(dateObj){
	var day = dateObj.getDate();
	var dayStr = day < 10 ? "0"+day : day;
	return dayStr;
}
//日期显示转换【YYYY-MM-DD】
function fDate(dateStr) {
	var newDate = new Date(dateStr.replace(/-/g,'/'));
	var fullYear = newDate.getFullYear();
	var month = fMonth(newDate);
	var day = fDay(newDate);
	return fullYear + "-" + month + "-" + day;
}

$(document).ready(function(){
	var rootPath = "${request.getContextPath()}";
	var ajaxbg = $("#background,#progressBar");
	ajaxbg.hide();
	$(document).ajaxStart(function(){
		$("#background").height(document.body.scrollHeight);
		ajaxbg.show(); 
	}).ajaxStop(function(){
		ajaxbg.hide();
	});
	$(document).ajaxError(function(event, jqxhr, settings, exception) {
		if(jqxhr.status !=520) {
			//window.location.href=getRootPath_web()+"/ajaxErrorException";
		} else {
			if(jqxhr.responseText.length<1) {
				modalReady("error","接口调用失败！",null);	
			} else {
				modalReady("error",jqxhr.responseText,null);	
			}					
		}
	});
	//icon-reorder
	var pathName = window.document.location.pathname;
	if("/cpm-web/singleproject/initialize"!=pathName) {
		$("a#main-menu-toggle").remove();
	}
	//console.log(pathName);
	
	/* mouse drag modal header*/
	var mouseMove;
	var mmdx,mmdy;//mouseModalDistance
	$(".modal-header").on("mousedown", function(e){
		mouseMove = true;
		var modal = $(this).parent();
		mmdx = e.pageX - parseInt(modal.offset().left);
		mmdy = e.pageY - parseInt(modal.offset().top);
	});
	$(document).on("mousemove", function(e){
		if(mouseMove){
			var ny = e.pageY-mmdy;
			var nx = e.pageX-mmdx;
			
			//console.log(mmdx+"::"+mmdy);
			$(".modal").offset({"top":ny,"left":nx});
			$(".modal").css("cursor","move");
		}
	});
	$(document).on("mouseup", function(){
		mouseMove = false;
		$(".modal").css("cursor","default");
	});
	/* end */
	
	/* menu adaption */
	//dropdown-submenu
	$("#nav .dropdown-submenu a[data-toggle='dropdown']").on("mouseover",function(){
		var neighbor = $(this).next();
		if(neighbor.hasClass("dropdown-menu")){
			var linkX = $(this).offset().left;
			var nx = neighbor.offset().left;
			var nw = neighbor.width();
			var navWidth = $("#nav").closest(".navbar").width();
			if(nx+nw>navWidth){
				$(this).parent().removeClass("pull-right").addClass("pull-left");
				nw = neighbor.width();
				neighbor.css("left",-nw+"px");
			}
		}
	});
	//keybord control modal footer buttons
	$(document).on("keyup", ".modal.in", function(event){
		if(event.keyCode==37 || event.keyCode==39){
			var footer = $(this).children(".modal-footer");
			var curBtn = footer.children(".btn-progress");
			if(event.keyCode==37){
				//alert("left");
				if(curBtn.prev().length>0){
					curBtn.prev().focus();
					curBtn.prev().addClass("btn-progress");
					curBtn.removeClass("btn-progress");
				}
			}else if(event.keyCode==39){
				//alert("right");
				if(curBtn.next().length>0){
					curBtn.next().focus();
					curBtn.next().addClass("btn-progress");
					curBtn.removeClass("btn-progress");
				}
			}
		}
	});
	//turn off form autocomplete
	$("form").attr("autocomplete","off");
});

function getRootPath_web() {
    //获取当前网址，如： http://localhost:8000/cpm-web/commondoc/initialize
    var curWwwPath = window.document.location.href;
    //获取主机地址之后的目录，如： cpm-web/commondoc/initialize
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    //获取主机地址，如：http://localhost:8000
    var localhostPath = curWwwPath.substring(0, pos);
    //获取带"/"的项目名，如：/cpm-web
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    return (localhostPath + projectName);
}

function changeLang(lang){
	$.get(getRootPath_web()+"/lang/"+lang,function(data,status){
    	location.reload();
    });
}
/**
 * format message with given parameters array.
 * eg: when call 
 *            formatMsg("Hello,{0}!",new Array("world"))
 *     we get "hello,world!"
 
 * @param {Object} message
 * @param {Object} parametersArray
 */
function formatMsg(message,parametersArray){
    var matchesArray = message.match(/\{(\d+)\}/g);
    
    var replacedMsg=message;
    for (var i=0; i<matchesArray.length; i++) {
        replacedMsg = replacedMsg.replace(matchesArray[i], parametersArray[i]);
    }
    return replacedMsg;
}

/**
 * 格式化日期格式
 * 
 * @param day
 * @returns {String}
 */
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