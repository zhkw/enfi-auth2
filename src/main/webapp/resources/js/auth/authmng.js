var basePath=$('#basePath').attr("value");
$(function(){
	//加载角色
	queryRoleList(1,{pageSize:10});
	$('.project-role').click(function(){
		queryPRoleList(1,{pageSize:10});
	});
	$('.system-role').click(function(){
		queryRoleList(1,{pageSize:10});
	});
	
	$(document).on("change", "#rolelist-pagination select", function(event) {
		var pageSize = $(this).val();
		queryRoleList(1, {pageSize : pageSize});
	});
	
	$(document).on("change", "#p-rolelist-pagination select", function(event) {
		var pageSize = $(this).val();
		queryPRoleList(1, {pageSize : pageSize});
	});
	enterKey();
});
/**
 * START M PROJECT MNG
 */
var restoreTrObj=null;
var systemSMTree=null;
//选中列表角色列表第一行数据
function chosenFirstRolesRows(){
	$("#roleData").find('.role-selected').removeClass('role-selected');
	$("#roleData tr:first").addClass('role-selected');
	//加载服务树
	$.fn.zTree.init($("#servicesTree"), serviceTreeSetting,null);
	systemSMTree = $.fn.zTree.getZTreeObj("servicesTree");
}
//检索角色列表
function  queryRoleList(currentPage,searchmap){
	var keyword=$("#roleSearchInput").val();
	var pageSize = 10;
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
	var data =new Object();
	data.keyword = keyword,
	data.currentPage = currentPage; 
	data.pageSize = pageSize;
	data.type="M";
	searchmap.pageSize = pageSize;
	$.ajax({
		url:basePath+'/authMng/queryList',
		type:'POST',
		dataType : "json",
		data : data,
		success:function(data){
			if(data.PAGE_DATA.length>0){
				formatRoleData(data.PAGE_DATA, data.currentPage, pageSize);
				chosenFirstRolesRows();
				var pagemap = {total:data.PAGE_TOTALCOUNT,pageNum:data.currentPage,pagesNum:data.pagesNum};
				searchmap = JSON.stringify(searchmap);
				var pages = paginationNew(pagemap,"queryRoleList",searchmap);
				$("#rolelist-pagination ul").html(pages);
				$("#rolelist-pagination select option[value='" + pageSize + "']").attr("selected", "true");
			}else{
				$("tbody#roleData").find("tr").remove();
				$("tbody#roleData").append("");
				$("#rolelist-pagination ul").html("没有角色数据");
				systemSMTree.reAsyncChildNodes(null, "refresh");
			}
		}	
	});
}

//格式化数据
function formatRoleData(obj, currentPage, pageSize){
	var html='';
	$('.role-panel .table tbody').empty();
	if(obj.length>0){
		for(var i=0;i<obj.length;i++){
			var description = "";
			if (obj[i].description != null) {
				description = obj[i].description;
			}
		html+='<tr id="'+obj[i].id+'" onclick="queryRoleOfSM(this);"><td style="display:none">'+obj[i].id+'</td>'+
			  '<td class="role-edit rolenum">'+ ((currentPage - 1)* pageSize + i + 1) + '</td>'+
			  '<td class="role-edit mustinput rolename">'+obj[i].name+'</td>'+
			  '<td>'+fmatterDate(obj[i].createtime)+'</td>'+
			  '<td>'+fmatterDate(obj[i].updatetime)+'</td>'+
			  '<td class="role-edit description">'+description+'</td>'+
			  '<td><i class="icon-edit bigger-150 blue role-edit updaterole" id="'+obj[i].id+'" onclick="editRole(this);"></i><i class="icon-trash bigger-150  blue  role-edit" id="'+obj[i].id+'" onclick="deleteRole(this);"></i></td></tr>';
		}
	}
	$('.role-panel .table tbody').append(html);
}

//添加角色
function addRole(){
	if($('#roleData .icon-save').length>0){
		var id=$('#roleData').find('.icon-save').attr('id');
		modalReady('confirm','存在编辑中的数据，是否放弃编辑？','restore('+id+')');
	}else{
		var html= '<tr><td class="role-edit"></td>'+
		  '<td class="role-edit mustinput"><input id="rolename"></input></td>'+
		  '<td></td>'+
		  '<td></td>'+
		  '<td class="role-edit"><input id="roledes"></input></td>'+
		  '<td><i class="icon-save bigger-150 blue  addrole" onclick="editRole(this);"></i><i class="icon-trash bigger-150  blue  addrole" id="addroleDel" onclick="deleteRole(this);"></i></td></tr>';
      $('.role-panel .table tbody').prepend(html);
	}	
}

//点击保存事件
function editRole(obj){
	if($(obj).hasClass('icon-save')){
		var mustCheck=true;
		var editTds=$(obj).closest("tr").find('.role-edit');
		for(var i=0;i<editTds.length;i++){
			if($(editTds[i]).hasClass('mustinput')){
				mustCheck= isFilled2($(editTds[i]).find('input').attr('id'))&&mustCheck;	
			}
		}
		if(mustCheck){
			 saveRole(obj);
		}	
	}else{
		if($('#roleData .icon-save').length>0){
			var id=$('#roleData').find('.icon-save').attr('id');
			modalReady('confirm','存在编辑中的数据，是否放弃编辑？','restore('+id+')');
		}else{
			restoreTrObj=$(obj).closest("tr").html();
			var etds=$(obj).closest("tr").find('.role-edit');
			for(var i=0;i<etds.length;i++){
				if($(etds[i]).hasClass('rolename')){
					var inhtml="<input id='rolename' value="+$(etds[i]).html()+"></input>";
					$(etds[i]).html(inhtml);
				}
				if($(etds[i]).hasClass('description')){
					var inhtml="<input id='roledes' value="+$(etds[i]).html()+"></input>";
					$(etds[i]).html(inhtml);
				}
			}
			 $(obj).removeClass('icon-edit').addClass('icon-save');
		}
	}
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
//保存角色
function saveRole(obj){
	var oper='add';
	var id='';
	if($(obj).hasClass('updaterole')){
		oper='update';
		id=$(obj).attr('id');
	}
	var rolename=$('#rolename').val();
	var description=$('#roledes').val();
	var data={oper:oper,id:id,rolename:rolename,description:description,type:'M'};
	$.ajax({
		url:basePath+'/authMng/handleRole',
		type:'POST',
		data:data,
		dataType:"json",
		success:function(data){
			if (!data) {
				alert("该角色已经存在");
			}
			queryRoleList(1,{pageSize:10});
		}
	});
}
//删除行数据
function deleteRole(obj){
	if($('#roleData .icon-save').length>0){
		var id=$('#roleData').find('.icon-save').attr('id');
		modalReady('confirm','存在编辑中的数据，是否放弃编辑？','restore('+id+')');
	}else{
		removeAllError();
		var id=$(obj).attr('id');
	    modalReady('confirm','确定要删除该行数据？','executeDelRole('+id+')');	
    }
}
//执行删除操作
function executeDelRole(obj) {
	if($("#"+obj).hasClass('addrole')){
		$("#"+obj).closest('tr').remove();
	}else{
		var id=$("#"+obj).attr('id');
		$.ajax({
			url:basePath+'/authMng/handleRole',
			type:'POST',
			data:{id:id,oper:'del',type:'M'},
			dataType:"json",
			success:function(data){
				queryRoleList(1,{pageSize:10});
			}
		});
	}
}

//检索角色数据
function searchRoles(obj){
	removeAllError();
	if($(obj).hasClass('icon-search')){
		var keyword=$("#roleSearchInput").val();
		if(keyword==''||keyword==null){
			setToltip("请输入检索条件", $('.role-search'));
		}else{
			$(obj).removeClass('icon-search').addClass('icon-remove');
			queryRoleList(1,{pageSize:10});
		}
	}else{
		$("#roleSearchInput").val('');
		$(obj).removeClass('icon-remove').addClass('icon-search');
		queryRoleList(1,{pageSize:10});
	}
}
//服务模块树
var serviceTreeSetting = {
		async: {
			enable: true,
			dataType:"json" ,
			url:basePath+"/authMng/querySM",
			otherParam: ["roleId",function(){
				return  $("#roleData .role-selected").attr("id");
			},"type","M"]
		},
		view: {
			dblClickExpand: false,
			showLine: true
		},
		check: {
			enable: true,
	    },
		data: {
			simpleData: {
				enable:true,
				idKey: "id",
				pIdKey: "pid",
				rootPId: "moduleTree"
			}
		}
	};
//检索角色已经分配的模块
function queryRoleOfSM(obj){
	$("#roleData").find('.role-selected').removeClass('role-selected');
	$(obj).addClass('role-selected');
	systemSMTree.reAsyncChildNodes(null, "refresh");
}
//角色模块分配
function handelRoleModules(obj){
	var roleId=$("#roleData .role-selected").attr("id");//角色ID
	var moudleIds="";//模块IDS
	var oper=null;//操作
	//确定(组装选中的模块列表id)
	if($(obj).hasClass('sm-update')){
		 oper="update";
		var nodes = systemSMTree.getCheckedNodes(true);
		for(var i=0;i<nodes.length;i++){
			if(i==(nodes.length-1)){
				moudleIds+=nodes[i].id;
			}else{
				moudleIds+=nodes[i].id+",";
			}
		}	
	}
	//重置
	if($(obj).hasClass('sm-reset')){
		oper="reset";
		queryRoleOfSM(obj);
	}
	var data=new Object();
	data.roleId=roleId;
	data.moudleIds=moudleIds;
	data.oper=oper;
	data.type="M";
	$.ajax({
		url:basePath+"/authMng/allocatSM",
		data:data,
		type:'POST',
		success:function (data){
			if (data) {
				modalReady('info','操作成功',null);
			} else {
				modalReady('info','操作失败',null);
			}
		}
	});
}
/**
 * END M PROJECT MNG
 */

/**
 * START S PROJECT MNG
 */
var restorePTrObj=null
var projectSMTree=null;
//检索单项目角色列表
function  queryPRoleList(currentPage,searchmap){
	var keyword=$("#p-roleSearchInput").val();
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
	var data =new Object();
	data.keyword = keyword,
	data.currentPage =currentPage; 
	data.pageSize = pageSize;
	data.type="S";
	searchmap.pageSize = pageSize;
	$.ajax({
		url:basePath+'/authMng/queryList',
		type:'POST',
		dataType : "json",
		data : data,
		success:function(data){
			if(data.PAGE_DATA.length>0){
				formatPRoleData(data.PAGE_DATA, data.currentPage, pageSize);
				chosenFirstPRolesRows();
				var pagemap = {total:data.PAGE_TOTALCOUNT,pageNum:data.currentPage, pagesNum:data.pagesNum};
				searchmap = JSON.stringify(searchmap);
				var pages = paginationNew(pagemap,"queryPRoleList",searchmap);
				$("#p-rolelist-pagination ul").html(pages);
				$("#p-rolelist-pagination select option[value='" + pageSize + "']").attr("selected", "true");
			}else{
				$("tbody#p-roleData").find("tr").remove();
				$("tbody#p-roleData").append("");
				$("#p-rolelist-pagination ul").html("没有角色数据");
				projectSMTree.reAsyncChildNodes(null, "refresh");
			}
		}	
	});
}

//格式化单项目数据
function formatPRoleData(obj, currentPage, pageSize){
	var html='';
	$('.p-role-panel .table tbody').empty();
	if(obj.length>0){
		for(var i=0;i<obj.length;i++){
			var description = "";
			if (obj[i].description != null) {
				description = obj[i].description;
			}
			
			html+='<tr id="'+obj[i].id+'" onclick="queryPRoleOfSM(this);"><td style="display:none">'+obj[i].id+'</td>'+
			  '<td class="p-role-edit p-rolenum">'+ ((currentPage - 1)* pageSize + i + 1) +'</td>'+
			  '<td class="p-role-edit mustinput p-rolename">'+obj[i].name+'</td>'+
			  '<td>'+fmatterDate(obj[i].createtime)+'</td>'+
			  '<td>'+fmatterDate(obj[i].updatetime)+'</td>'+
			  '<td class="p-role-edit p-description">' + description +'</td>'+
			  '<td><i class="icon-edit bigger-150 blue p-role-edit updaterole" id="'+obj[i].id+'" onclick="editPRole(this);"></i><i class="icon-trash bigger-150  blue  p-role-edit" id="'+obj[i].id+'" onclick="deletePRole(this);"></i></td></tr>';
		}
	}
	$('.p-role-panel .table tbody').append(html);
}

//选中单项目角色列表第一行数据
function chosenFirstPRolesRows(){
	$("#p-roleData").find('.p-role-selected').removeClass('p-role-selected');
	$("#p-roleData tr:first").addClass('p-role-selected');
	//加载服务树
	$.fn.zTree.init($("#p-servicesTree"), servicePTreeSetting,null);
	projectSMTree= $.fn.zTree.getZTreeObj("p-servicesTree");
}

//添加单项目角色
function addPRole(){
	if($('#p-roleData .icon-save').length>0){
		var id=$('#p-roleData').find('.icon-save').attr('id');
		modalReady('confirm','存在编辑中的数据，是否放弃编辑？','prestore('+id+')');
	}else{
		var html= '<tr><td class="p-role-edit"></td>'+
		  '<td class="p-role-edit mustinput"><input id="p-rolename"></input></td>'+
		  '<td></td>'+
		  '<td></td>'+
		  '<td class="p-role-edit"><input id="p-proledes"></input></td>'+
		  '<td><i class="icon-save bigger-150 blue  addprole" onclick="editPRole(this);"></i><i class="icon-trash bigger-150  blue  addprole" id="addPRoleDel" onclick="deletePRole(this);"></i></td></tr>';
      $('.p-role-panel .table tbody').prepend(html);
	}	
}

//单项目点击保存事件
function editPRole(obj){
	if($(obj).hasClass('icon-save')){
		var mustCheck=true;
		var editTds=$(obj).closest("tr").find('.p-role-edit');
		for(var i=0;i<editTds.length;i++){
			if($(editTds[i]).hasClass('mustinput')){
				mustCheck= isFilled2($(editTds[i]).find('input').attr('id'))&&mustCheck;	
			}
		}
		if(mustCheck){
			 savePRole(obj);
		}	
	}else{
		if($('#p-roleData .icon-save').length>0){
			var id=$('#p-roleData').find('.icon-save').attr('id');
			modalReady('confirm','存在编辑中的数据，是否放弃编辑？','prestore('+id+')');
		}else{
			restorePTrObj=$(obj).closest("tr").html();
			var etds=$(obj).closest("tr").find('.p-role-edit');
			for(var i=0;i<etds.length;i++){
				if($(etds[i]).hasClass('p-rolename')){
					var inhtml="<input id='p-rolename' value="+$(etds[i]).html()+"></input>";
					$(etds[i]).html(inhtml);
				}
				if($(etds[i]).hasClass('p-description')){
					var inhtml="<input id='p-roledes' value="+$(etds[i]).html()+"></input>";
					$(etds[i]).html(inhtml);
				}
			}
			 $(obj).removeClass('icon-edit').addClass('icon-save');
		}
	}
}

//恢复单项目编辑行数据
function prestore(id){
	removeAllError();
	if(id !== "" && "undefined" != id && "undefined" != typeof(id) && null !=id) {
		$("#p-roleData #"+id).html(restorePTrObj);	
	} else {
		$("#p-roleData tr .icon-save").closest("tr").remove();
	}
}

//保存单项目角色
function savePRole(obj){
	var oper='add';
	var id='';
	if($(obj).hasClass('updaterole')){
		oper='update';
		id=$(obj).attr('id');
	}
	var rolenum=$('#p-rolenum').val();
	var rolename=$('#p-rolename').val();
	var description=$('#p-roledes').val();
	var data={oper:oper,id:id,rolenum:rolenum,rolename:rolename,description:description,type:'S'}
	$.ajax({
		url:basePath+'/authMng/handleRole',
		type:'POST',
		data:data,
		dataType:"json",
		success:function(data){
			if (!data) {
				alert("该角色已经存在");
			}
			queryPRoleList(1,{pageSize:10});
		}
	});
}

//删除当项目行数据
function deletePRole(obj){
	if($('#p-roleData .icon-save').length>0){
		var id=$('#p-roleData').find('.icon-save').attr('id');
		modalReady('confirm','存在编辑中的数据，是否放弃编辑？','prestore('+id+')');
	}else{
		removeAllError();
		var id=$(obj).attr('id');
		modalReady('confirm','确定要删除该行数据？','executeDelPRole('+id+')');
	}
}

//执行删除操作
function  executeDelPRole(obj){
	if($("#"+obj).hasClass('addprole')){
		$("#"+obj).closest('tr').remove();
	}else{
		var id=$("#"+obj).attr('id');
		$.ajax({
			url:basePath+'/authMng/handleRole',
			type:'POST',
			data:{id:id,oper:'del',type:'S'},
			dataType:"json",
			success:function(data){
				queryPRoleList(1,{pageSize:10});
			}
		});
	}
}

//检索单项目角色数据
function searchPRoles(obj){
	removeAllError();
	if($(obj).hasClass('icon-search')){
		var keyword=$("#p-roleSearchInput").val();
		if(keyword==''||keyword==null){
			setToltip("请输入检索条件", $('.p-role-search'));
		}else{
			$(obj).removeClass('icon-search').addClass('icon-remove');
			queryPRoleList(1,{pageSize:10});
		}
	}else{
		$("#p-roleSearchInput").val('');
		$(obj).removeClass('icon-remove').addClass('icon-search');
		queryPRoleList(1,{pageSize:10});
	}
}

//单项目服务模块树
var servicePTreeSetting = {
		async: {
			enable: true,
			dataType:"json" ,
			url:basePath+"/authMng/querySM",
			otherParam: ["roleId",function(){
				return  $("#p-roleData .p-role-selected").attr("id");
			},"type","S"]
		},
		view: {
			dblClickExpand: false,
			showLine: true
		},
		check: {
			enable: true,
	    },
		data: {
			simpleData: {
				enable:true,
				idKey: "id",
				pIdKey: "pid",
				rootPId: "moduleTree"
			}
		}
	};

//检索单项目角色已经分配的模块
function queryPRoleOfSM(obj){
	$("#p-roleData").find('.p-role-selected').removeClass('p-role-selected');
	$(obj).addClass('p-role-selected');
	projectSMTree.reAsyncChildNodes(null, "refresh");
}

//单项目角色模块分配
function handelPRoleModules(obj){
	var roleId=$("#p-roleData .p-role-selected").attr("id");//角色ID
	var moudleIds="";//模块IDS
	var oper=null;//操作
	//确定(组装选中的模块列表id)
	if($(obj).hasClass('p-sm-update')){
		 oper="update";
		var nodes = projectSMTree.getCheckedNodes(true);
		for(var i=0;i<nodes.length;i++){
			if(i==(nodes.length-1)){
				moudleIds+=nodes[i].id;
			}else{
				moudleIds+=nodes[i].id+",";
			}
		}	
	}
	//重置
	if($(obj).hasClass('p-sm-reset')){
		oper="reset";
	}
	var data=new Object();
	data.roleId=roleId;
	data.moudleIds=moudleIds;
	data.oper=oper;
	data.type="S";
	$.ajax({
		url:basePath+"/authMng/allocatSM",
		data:data,
		type:'POST',
		success:function (data){
			modalReady('info','分配成功',null);
		}
	});
}

/**
 * END S PROJECT MNG
 */

//回车绑定
function enterKey(){
	$('#roleSearchInput').bind('keypress',function(event){
	       if(event.keyCode == "13"){
	    	   searchRoles($("#roleSearchIco"));
	         }
	 });
	$('#p-roleSearchInput').bind('keypress',function(event){
	       if(event.keyCode == "13"){
	    	   searchPRoles($("#p-roleSearchIco"));
	         }
	 });
}