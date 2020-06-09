/**
 * @Description:
 *              弹出对话框
 * @Author:
 *              Zheng Jia
 * @time:
 *              2015/01/20
 * @Author: 	deju hu
 * @time:		2015/07/27           
 */


/**
 * 弹出窗口创建事件
 * 
 * @param infoEvent 信息事件 【info:提示消息 / error:错误消息 / confirm:确认消息/pop_info弹窗中提示消息，含[继续][关闭]】【必须】
 * @param infoContent 信息内容【必须】
 * @param buttonEvent 按钮事件【可选】
 */
function modalReady(infoEvent,infoContent,buttonEvent) {
	
	$("#messagModal").remove();
	
	//弹出窗口创建
	createModal(infoEvent);
	//弹出窗口信息设定
	messageSet(infoContent);
	//弹出窗口按钮事件绑定
	eventBinding(infoEvent,buttonEvent);
	//显示弹出窗口
	$("#messagModal").modal("show");
	
	$("#messagModal").on("shown", function(){
		$("#messagModal .modal-footer .btn.btn-progress").focus();
	});
}

/**
 * 弹出窗口创建
 * 
 * @param infoEvent 信息事件
 */
function createModal(infoEvent) {
	infoEvent = infoEvent.replace(/^\s+/,"").replace(/\s+$/,"");
	var info = "";
	if (infoEvent == "info" || "pop_info" || "redir" || "logout") {
		info = "提示";
	} else if (infoEvent == "confirm") {
		info = "确认";
	} else if (infoEvent == "error") {
		info = "错误";
	}
	var modal = "<div class='modal hide fade' id='messagModal'>"
					+"<div class='modal-header'>"
						+"<button type='button' class='close' data-dismiss='modal'>×</button>"
						+"<h3>"+info+"</h3>"
					+"</div>"
					+"<div class='modal-body'></div>"
					+"<div class='modal-footer'></div>"
				+"</div>";
	$("body").append(modal);
}

/**
 * 弹出信息窗口信息设定
 * 
 * @param infoContent 信息内容
 */
function messageSet(infoContent) {
	//分割信息内容
	var messageContentList = new Array();
	if (infoContent.indexOf(".")||infoContent.indexOf("。") == -1) {
		messageContentList[0] = infoContent;
	} else {
		if(infoContent.indexOf(".") != -1){
			var infoContentList = infoContent.split(".");
		}
		if(infoContent.indexOf("。") != -1){
			var infoContentList = infoContent.split("。");
		}
		for (var i=0;i<infoContentList.length;i++) {
			messageContentList[i] = infoContentList[i];
		}
	}
	//填充信息内容
	var messageContent = "";
	for (var i=0;i<messageContentList.length;i++) {
		messageContent = messageContent + "<p>"+messageContentList[i]+"</p>";
	}
	$("#messagModal .modal-body").html(messageContent);	
}

/**
 * 弹出窗口按钮事件绑定
 * 
 * @param infoContent 信息内容
 * @param buttonEvent 按钮事件
 */
function eventBinding(infoEvent,buttonEvent) {
	infoEvent = infoEvent.replace(/^\s+/,"").replace(/\s+$/,"");
	var button = "";
	if (infoEvent == "confirm") {
		buttonEvent = buttonEvent.replace(/^\s+/,"").replace(/\s+$/,"");
		//取得事件参数
		var leftHalfBracket = buttonEvent.substring(0,buttonEvent.indexOf('(')+1);//截取'('之前的字符串【包括'('】
		var rightHalfBracket = buttonEvent.substring(buttonEvent.indexOf(')'));//截取'('字符串之后的字符串【包括')'】
		var eventParamAll = buttonEvent.substring(buttonEvent.indexOf('(')+1,buttonEvent.indexOf(')'));//截取'('到')'之间的参数
		var eventParamter = new Array();
		if (eventParamAll.indexOf(",") == -1) {
			eventParamter[0] = eventParamAll;
		} else {
			var parm = eventParamAll.split(",")
			for (var i=0;i<parm.length;i++) {
				eventParamter[i] = parm[i];
			}
		}
		//事件参数重新拼接
		var buttonEventParamter = "";
		var quot = "&quot;";
		var comma = ",";
		for (var i=0;i<eventParamter.length;i++) {
			if (eventParamter[i] == "event") {//特殊对象参数不用拼接“”
				buttonEventParamter = buttonEventParamter + eventParamter[i];
			} else {
				buttonEventParamter = buttonEventParamter + quot + eventParamter[i] + quot;
			}
			if (i < eventParamter.length - 1) {
				buttonEventParamter = buttonEventParamter + comma;
			}
		}
		buttonEvent = leftHalfBracket + buttonEventParamter + rightHalfBracket +";";
		var confirmButton = "<a href='#' class='btn btn-progress' data-dismiss='modal' onclick='"+buttonEvent+"'>确定</a>";
		
		button = confirmButton + "<a href='#' class='btn' data-dismiss='modal'>取消</a>";
	} else if(infoEvent == "pop_info"){
		button = "<a href='javascript:;' class='btn btn-progress' data-dismiss='modal'>继续</a>";
		button += "<a href='javascript:;' class='btn btn-progress' onclick='closeAllModal();'>关闭</a>";
	} else if(infoEvent == "redir"){
		button = "<a href='javascript:;' class='btn btn-progress' data-dismiss='modal'>继续</a>";
		button += "<a href='initialize' class='btn btn-progress'>查看</a>";
	} else if(infoEvent == "logout"){
		button = "<a href='javascript:;' class='btn btn-progress' onclick="+buttonEvent+">重新登陆</a>";
	} else {
		button = "<a href='javascript:;' class='btn btn-progress' data-dismiss='modal'>确认</a>";
	}
	$("#messagModal .modal-footer").html(button);
}

function closeAllModal() {
	$(".modal").modal("hide");
}