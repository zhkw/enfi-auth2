var basePath=$('#basePath').attr("value");
var ik=0;
$(function(){
	queryUserList(1,{pageSize:10,type:'M'});
	checkAllRole();
	
	$(document).on("change", "#system-member-list-pagination select", function(event) {
		var pageSize = $(this).val();
		queryUserList(1, {pageSize : pageSize, type: 'M'});
	});
	
	$(document).on("change", "#project-member-list-pagination select", function(event) {
		var pageSize = $(this).val();
		queryUserList(1, {pageSize : pageSize, type : 'S'});
	});
	if(ik==0){
		enterKey();
		ik=1;
	}
});

//tab切换
function tabClick(obj){
	if($(obj).hasClass('sysm')){
		queryUserList(1,{pageSize:10,type:'M'});
	}else{
		$('#selectProject').chosen();
		searchProject("#personSelect_chzn .chzn-search input",'SC');
		$("#selectProject_chzn .chzn-search .search").click(function(){
			   removeAllError();
	    	   if($('#selectProject_chzn .chzn-search input').val().length<3){
	    		    setToltip("请输入3位以上字符作为检索条件",$('#selectProject_chzn'));
	    	   }else{
	    		  searchProject("#selectProject_chzn .chzn-search input",'UC');
	    	   }
		});
		$('#selectProject_chzn .chzn-drop .chzn-search input').bind('keyup',function(event){
		       if(event.keyCode == "13"){
		    	   removeAllError();
		    	   if($('#selectProject_chzn .chzn-search input').val().length<3){
		    		    setToltip("请输入3位以上字符作为检索条件",$('#selectProject_chzn'));
		    	   }else{
		    		  searchProject("#selectProject_chzn .chzn-search input",'UC');
		    	   }
		        }
		 });
		changeProjectChosen();
	}
}
//用户加载
function  queryUserList(currentPage,searchmap){
	var keyword=null;
	var data =new Object();
	if(searchmap.type=='M'){
		data.type="M";
		keyword=$("#systemMemberSearchInput").val();
	}else{
		data.type="S";
		var pid=$("#selectProject").find('option:selected').attr("value");
		data.projectId=pid;
		keyword=$("#projectMemberSearchInput").val();
	}
	var pageSize;
	if (null == keyword||''==keyword) {		
		keyword = null;
	}
	if (null != searchmap) {	
		pageSize = searchmap.pageSize;		
	} else {
		searchmap = {};
	}
	if (null == pageSize || "" == pageSize || typeof(pageSize) == "undefined") {
		pageSize = 10;
	}
	data.keyword = keyword,
	data.pageNumber = currentPage; 
	data.pageSize = pageSize;
	searchmap.pageSize = pageSize;
	$.ajax({
		url:basePath+'/roleMng/getSysUser',
		type:'POST',
		dataType : "json",
		data : data,
		success:function(data){
			var pagemap = {total:data.PAGE_TOTALCOUNT, pageNum:data.currentPage,pagesNum:data.pagesNum};
			var type=searchmap.type;
			searchmap = JSON.stringify(searchmap);
			var pages = paginationNew(pagemap,"queryUserList",searchmap);
			if(type=='M'){
				formatUserData(data.PAGE_DATA,'sysUserData', data.personColumnMap);	
				$("#system-member-list-pagination ul").html(pages);
				$("#system-member-list-pagination select option[value='" + pageSize + "']").attr("selected", "true");
				chosenFirstRow('sysUserData','M');
			}else{
				formatUserData(data.PAGE_DATA,'projectUserData', data.personColumnMap);	
				$("#project-member-list-pagination ul").html(pages);
				$("#project-member-list-pagination select option[value='" + pageSize + "']").attr("selected", "true");
				chosenFirstRow('projectUserData','S');
			}
		}	
	});
}

//格式化用户数据
function formatUserData(obj, bodyId, personColumnMap) {
	var html = '';
	$('#' + bodyId).empty();
	
	//工号
	var column_employee_num = "employee_num";
	//人员字段映射
	if (personColumnMap != null) {
		column_employee_num = personColumnMap.employee_num;
	}
	
	if(obj.length>0){
		var otherHtml = '';
		var username = "";
		var name = "";
		var employee_num = "";
		var user;
		for ( var i = 0; i < obj.length; i++) {
			user = obj[i];
			if (user.name != null) {
				name = user.name;
			}
			if (user.username != null) {
				username = user.username;
			}
			if (user[column_employee_num] != null) {
				employee_num = user[column_employee_num];
			}
			if(bodyId=='projectUserData'){
				otherHtml = '<td><i class="icon-trash bigger-150 blue" id="' + user.id + '" onclick="deleteProjectUser(this);"></i></td>';
			}
			html += '<tr id="' + user.id + '" onclick="userRowcleck(this,'+ bodyId + ');">' +
			  	'<td class="name">' + username + '</td>' +
			  	'<td class="username">' + name + '</td>' +
			  	'<td class="employ_num">' + employee_num + '</td>' +
			  	otherHtml +
			  	'</tr>';
			}
		}
	$('#' + bodyId).append(html);
}

//格式化角色数据
function formatRoleData(obj, bodyId) {
	var html = '';
	$('#' + bodyId).empty();
	if(obj.length>0){
		for(var i = 0; i < obj.length; i++) {
			var checked = '';
			if(obj[i].isChecked == true){
				checked = 'checked';
			}
		   html += '<tr id="' + obj[i].id+'">'+
		 	  '<td><input type="checkbox" ' + checked + ' rid="' + obj[i].id + '"></td>'+
			  '<td class="rname">' + obj[i].name + '</td>'+
			  '</tr>';
			}
	}
	$('#' + bodyId).append(html);
}

//默认选中第一行数据
function chosenFirstRow(obj,type){
	$(obj).find('.user-selected').removeClass('user-selected');
	$("#"+obj+" tr:first").addClass('user-selected');
	queryRoles("#" + obj,type);
}

//检索角色
function queryRoles(obj, type){
	var data = new Object();
	data.type = type;
	data.userId = $(obj).find('.user-selected').attr('id');
	data.projId = $("#selectProject").find('option:selected').attr("value");
	$.ajax({
		url:basePath+'/roleMng/queryRoles',
		data:data,
		type:'POST',
		success:function(data){
			if(type=='M'){
				 formatRoleData(data.PAGE_DATA,'sysRoleData');	
			}else{
				 formatRoleData(data.PAGE_DATA,'projectRoleData');	
			}
		}
	});
}

//人员角色分配
function updateRoles(obj, type) {
	var rids = '';
	var data = new Object();
	
	if($(obj).hasClass('sysRoleSave')){
		data.userId = $('#sysUserData').find('.user-selected').attr('id');
		var checkedRoles = $('#sysRoleData').find('tr input:checked');
		for (var i = 0; i < checkedRoles.length; i++) {
			if (i == checkedRoles.length-1) {
				rids += $(checkedRoles[i]).attr('rid');
			} else {
				rids += $(checkedRoles[i]).attr('rid') + ",";
			}
		}	
		data.roleIds = rids;
	}
	if ($(obj).hasClass('projectRoleSave')) {
		data.projectId = $("#selectProject").find('option:selected').attr("value");
		data.userId = $('#projectUserData').find('.user-selected').attr('id');
		var checkedRoles = $('#projectRoleData').find('tr input:checked');
		for (var i = 0; i < checkedRoles.length; i++) {
			if (i == checkedRoles.length-1) {
				rids += $(checkedRoles[i]).attr('rid');
			} else {
				rids += $(checkedRoles[i]).attr('rid') + ",";
			}
		}	
		
		data.roleIds = rids;
	}
	
	if ($(obj).hasClass('sysRoleReset')) {
		data.userId = $('#sysUserData').find('.user-selected').attr('id');
		data.roleIds = "";
	}
	if ($(obj).hasClass('projectRoleReset')) {
		data.projectId = $("#selectProject").find('option:selected').attr("value");
		data.userId = $('#projectUserData').find('.user-selected').attr('id');
		data.roldeIds = "";
	}
	data.type = type;
	
	$.ajax({
		url:basePath + '/roleMng/updateRoles',
		data:data,
		type:'POST',
		success:function(data){
			//刷新角色列表
			if (type == "M") {
				queryRoles("#sysUserData", type);
			} else {
				queryRoles("#projectUserData", type);
			}
			modalReady('info','分配成功', null);
		}
	});
}

//检索项目
function searchProject(obj,flag){
	removeAllError();
	var data=new Object();
	data.flag=flag;
	data.keyword=$(obj).val();
	$.ajax({
		url:basePath+'/roleMng/queryProjects',
		type:'POST',
		data:data,
		success: function (data){
			$("#selectProject").empty();
			$(".chzn-results").empty();
			var dataList = data.projectList;
			var projectColumnMap = data.projectColumnMap;
			
			var column_proj_num = "proj_num";
			var column_proj_name = "proj_name";
			var column_proj_id = "id";
			
			if ("Y" == projectColumnMap.isModified) {
				column_proj_num = projectColumnMap.proj_num;
				column_proj_name = projectColumnMap.proj_name;
				column_proj_id = projectColumnMap.id;
			}
			
			if (null != dataList && dataList.length > 0) {
				for(var i=0; i<dataList.length; i++){
					$("#selectProject").append("<option value='" + dataList[i][column_proj_id] + "'>"
							+ dataList[i][column_proj_num] + dataList[i][column_proj_name] + "</option>");
				}
				$("#selectProject").trigger('liszt:updated');
				$('.addprojectuser').removeAttr('disabled');
				queryUserList(1,{pageSize:10,type:'S'});
			}
		}
	});
}
//项目筛选事件
function changeProjectChosen(){
	$('#selectProject').change(function(){
		queryUserList(1,{pageSize:10,type:'S'});
	});
}
//用户行点击事件
function userRowcleck(obj,bodyId){
	$(bodyId).find('.user-selected').removeClass('user-selected');
	$(obj).addClass('user-selected');
	if($(bodyId).attr('id')=='sysUserData'){
		queryRoles(bodyId,"M");
	}
	if($(bodyId).attr('id')=='projectUserData'){
		queryRoles(bodyId,"S");
	}
}
//单项目人员添加
function addProjectUser(){
	if($('#projectUserData .icon-save').length>0){
		modalReady('info','存在编辑中的数据，不能新建？',null);
	}else{
		var html="<tr><td><select id='selectUser' class='span13' data-rel='chosen' style='margin:0px; height:20px line-height:15px' data-placeholder='请输入人员名'><option value='-1'></option></select></td>" +
				 "<td class='username'></td><td class='employ_num'></td>"+
				 "<td><i class='icon-save bigger-150 blue' onclick='addPMemeber(this);'></i><i class='icon-trash bigger-150 blue newInsert' style='margin-left:15px' id='delProjectUser' onclick='deleteProjectUser(this);'></i></td>"+
				  "</tr>";
		$('#projectUserData').prepend(html);
		$("#selectUser").chosen({search_contains: false});
		$("#selectUser_chzn .chzn-search .search").click(function(){
			   removeAllError();
	    	   if($('#selectUser_chzn .chzn-search  input').val().length<3){
	    		    setToltip("请输入3位以上字符作为检索条件",$('#selectUser_chzn'));
	    	   }else{
	    		   queryUserOfAll();
	    	   }  
		});
		$('#selectUser_chzn .chzn-search  input').bind('keyup',function(event){
		       if(event.keyCode == "13"){
		    	   removeAllError();
		    	   if($('#selectUser_chzn .chzn-search  input').val().length<3){
		    		    setToltip("请输入3位以上字符作为检索条件",$('#selectUser_chzn'));
		    	   }else{
		    		   queryUserOfAll();
		    	   }  
		        }
		 });
	}
}
//查询人员添加为项目小组成员
function queryUserOfAll(){
	var pid=$("#selectProject").find('option:selected').attr("value");
	var keyWord=$("#selectUser_chzn .chzn-search input").val();
	var data=new Object();
	data.projectId=pid;
	data.keyword=keyWord;
	$.ajax({
		url:basePath+'/roleMng/queryUserOfAll',
		data:data,
		type:'POST',
		success:function (data){
			$("#selectUser").empty();
			$("#selectUser_chzn .chzn-results").empty();
			var dataList = data.userList;
			var personColumnMap = data.personColumnMap;
			
			//字段映射
			var column_employee_num = "employee_num";
			var column_name = "name";
			if ("Y" == personColumnMap.isModified) {
				column_employee_num = personColumnMap.employee_num;
				column_name = personColumnMap.name;
			}
			
			if (null != dataList && dataList.length > 0) {
				$("#selectUser").append("<option value='-1'></option>");
				var employee_num = "";
				var name = "";
				for(var i=0; i<dataList.length; i++){
					employee_num = dataList[i].employee_num == null ? "" : dataList[i][column_employee_num];
					name = dataList[i].name == null ? "" : dataList[i][column_name];
					$("#selectUser").append("<option value='"+dataList[i].id+"' name='"+dataList[i][column_name]
							+"' employ_num='"+dataList[i][column_employee_num]+"' username='"+dataList[i].username+"' >" + employee_num + " " + name + "</option>");
				}
				$("#selectUser").trigger('liszt:updated');
				changeProjectUserChosen();
			}
		}
	});
}
//单项目人员添加chosen事件
function changeProjectUserChosen(){
	$('#selectUser').change(function(){
		$('#projectUserData tr:first .username').empty();
		$('#projectUserData tr:first .employ_num').empty();
		$('#projectUserData tr:first .username').html($('#selectUser').find('option:selected').attr('name'));
		$('#projectUserData tr:first .employ_num').html($('#selectUser').find('option:selected').attr('employ_num'));
	});
}
//单项目人员删除
function deleteProjectUser(obj){
	if($('#projectUserData .icon-save').length>0){
		var id=$('#delProjectUser').attr('id');
		modalReady('confirm','存在编辑中的数据，是否放弃编辑？','executeDelPMember('+id+')');
	}else{
		removeAllError();
		var id=$(obj).attr('id');
	    modalReady('confirm','确定要删除该行数据？','executeDelPMember('+id+')');	
	}
}
//单项目人员删除执行
function executeDelPMember(obj){
	var pid=$("#selectProject").find('option:selected').attr("value");
	var userId=$("#"+obj).attr('id');
	if($("#"+obj).hasClass('newInsert')){
		$("#"+obj).closest('tr').remove();
	}else{
		$.ajax({
			url:basePath+'/roleMng/delPMemeber',
			data:{userId:userId,projectId:pid},
			type:'POST',
			success:function(data){
				queryUserList(1,{pageSize:10,type:'S'});
			}
		});
	}
}
//添加项目小组成员
function addPMemeber(obj){
	var pid=$("#selectProject").find('option:selected').attr("value");
	var uid=$("#selectUser").find('option:selected').attr("value");
	if(uid=='-1'||uid==null||uid==''){
		modalReady('info','请先添加成员信息！',null);
	}else{
		$.ajax({
			url:basePath+'/roleMng/savePmember',
			data:{projectId:pid,userId:uid},
			type:'POST',
			success:function(data){
				queryUserList(1,{pageSize:10,type:'S'});
				modalReady('info','添加成功',null);
			}
		});
	}
}
//检索用户
function searchUsers(obj){
	removeAllError();
	if($(obj).hasClass('icon-search')){
		var keyword=$(obj).closest('div').children('input').val();
		if(keyword==''||keyword==null){
			setToltip("请输入检索条件", $(obj).closest('div'));
		}else{
			$(obj).removeClass('icon-search').addClass('icon-remove');
			if($(obj).attr('id')=='systemMemberSearchIco'){
				queryUserList(1,{pageSize:10,type:'M'});
			}else{
				queryUserList(1,{pageSize:10,type:'S'});
			}	
		}
	}else{
		$(obj).closest('div').children('input').val('');
		$(obj).removeClass('icon-remove').addClass('icon-search');
		if($(obj).attr('id')=='systemMemberSearchIco'){
			queryUserList(1,{pageSize:10,type:'M'});
		}else{
			queryUserList(1,{pageSize:10,type:'S'});
		}	
	}	
}
//单项目角色全选
function checkAllRole(){
	$('#projectCheckAll').change(function(){
		if($('#projectCheckAll').attr('checked')=='checked'){
			$('#projectRoleData input[type=checkbox]').attr("checked","checked"); 
		}else{
			$('#projectRoleData input[type=checkbox]').attr("checked",false); 
		}
	});
	$('#systemCheckAll').change(function(){
		if($('#systemCheckAll').attr('checked')=='checked'){
			$('#sysRoleData input[type=checkbox]').attr("checked","checked"); 
		}else{
			$('#sysRoleData input[type=checkbox]').attr("checked",false); 
		}
	});
}
//回车绑定
function enterKey(){
	$('#systemMemberSearchInput').bind('keyup',function(event){
	       if(event.keyCode == "13"){
	    	   searchUsers($("#systemMemberSearchIco"));
	         }
	 });
	$('#projectMemberSearchInput').bind('keyup',function(event){
	       if(event.keyCode == "13"){
	    	   searchUsers($("#projectMemberSearchIco"));
	         }
	 });
}
//恢复编辑行数据
function restore(id){
	removeAllError();
	if(id !== "" && "undefined" != id && "undefined" != typeof(id) && null !=id) {
		$("#roleData #"+id).html(restoreTrObj);	
	} else {
		$("#roleData tr .icon-save").closest("tr").remove();
	}
}