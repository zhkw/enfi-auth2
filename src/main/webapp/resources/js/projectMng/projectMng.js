$(function(){
	//新增项目的日期
	$(".projAdd #planstartdate_add").val("");
	$(".projAdd #planstartdate_add").datepicker('hide');
	$(".projAdd #planenddate_add").val("");
	$(".projAdd #planenddate_add").datepicker('hide');
	
	//编辑项目的日期
	$(".projEdit #planstartdate_edit").val("");
	$(".projEdit #planstartdate_edit").datepicker('hide');	
	$(".projEdit #planenddate_edit").val("");
	$(".projEdit #planenddate_edit").datepicker('hide');
	
	//创建项目
	$(document).on("click", ".createProject", function(event) {
		$(".editProject").css({"display" : "none"});
		$(".newProject").css({"display" : "block"});
	});
	
	//项目信息（编辑项目）
	$(document).on("click", ".project", function(event) {
		var projId = $(this).attr("id");
		getProjInfo(projId);
		$(".newProject").css({"display" : "none"});
		$(".editProject").css({"display" : "block"});
	});
	
	$("#dept").chosen();
});

//Get projects of tenant
function getProjectsOfTenant() {
	
}

//get all users
function getAllUser() {
	
}

//Get project basic infomation
function getProjInfo(projId) {
	$.ajax({
		type : "GET",
		url : "project/projectInfo",
		dataType : "json",
		data : {
			projId : projId
		},
		success : function(data) {
			setProjInfo(data);
		}
	});
}

//Set project infomation editable
function setProjInfo(data) {
	$(".projEdit #projName_edit").val(data.proj_name);
	$(".projEdit #projLongName_edit").val(data.proj_long_name);
	$(".projEdit #projNum_edit").val(data.proj_num);
	$(".projEdit #projManager_edit").val("");
	$(".projEdit #ownerName_edit").val(data.proj_owner_name);
	$(".projEdit #ownerNum_edit").val(data.proj_owner_num);
	$(".projEdit #projType_edit").val(data.proj_type);
	$(".projEdit #productType_edit").val(data.proj_product_type);
	$(".projEdit #planstartdate_edit").val(getNowFormatDate(new Date(data.proj_plan_start_date)));
	$(".projEdit #planenddate_edit").val(data.proj_plan_finish_date);
}
