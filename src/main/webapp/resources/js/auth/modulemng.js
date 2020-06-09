 $(function(){
     $("#moduletree").treetable({
        expandable : true
     });
     $('#moduletree').treetable('expandAll');
     
//     $(".ui-dialog-buttonset button").attr("class","btn btn-primary");

});
 
//添加模块为id模块的子模块
function add(id) {	
     $("#add").dialog({
    	 resizable: false,
    	 modal : true,
    	 buttons : {
	         "确定" : function() {
		           if($("#moduleName").val()==""){
		                 modalReady('info','请输入模块名称', null);
		                 return false;
		             }
		             if(!/^[0-9]\d*$/.test($('#indexNum').val())){
		            	 modalReady('info','index只能为大于等于0的数字', null);
		            	 return false;
		             }
		             
		             var data = {
		            	 parentid : id,
		            	 moduletype : $("#moduleType").val(),
		            	 modulename : $("#moduleName").val(),
		            	 name:$("#name").val(),
		            	 target : $("#target").val(),
		            	 indexnum : $("#indexNum").val(),
		            	 groupid : $("#groupId").val(),
		             };  
		             
		             $.ajax({
		                  url : "module/add",
		                  type : "post",
		                  data : JSON.stringify(data),
		                  dataType : "text",
		          		  contentType : "application/json",
		                  success : function(data) {
		                	  location.reload();
		                  }
		             });
		             $(this).dialog("close");
	     	         $(this).dialog("destroy");
             	},
	         
             "取消" : function() {
             	$(this).dialog("close");
             }
    	 }	
     });
}

//删除模块（子节点）
function deleteM(id) {
	if(id==null){
		modalReady('info','id为空，不允许删除', null);
	}
	var data = {id : id};
	jQuery.ajax({
		url : "module/delete",
		type : "post",
		data : JSON.stringify(data),
		dataType : "text",
		contentType : "application/json",
		success : function(result) {
			location.reload();
		}
	});
}

//删除模块的弹出框
function deleteV(id,name){
	if(name=="root"){
		$("#deleteall").html("确认删除全部结点？");
	}else{
		$("#deleteall").html("确认删除【"+name+"】结点以及子节点？");
	}
	$("#deleteall").dialog({
		modal : true,
		buttons : {
			"确定" :function(){
				deleteM(id);
				$(this).dialog("close");
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
}

//编辑模块
function edit(id, moduleName, name, target, moduleType, indexNum, groupId) {
	$("#edit").dialog({
		modal : true,
		autoOpen:false,
		open : function(e, ui) {
			$("#edit_moduleType").val(moduleType);
			$("#edit_moduleName").val(moduleName);
			$("#edit_name").val(name);
			$("#edit_target").val(target);
			$("#edit_indexNum").val(indexNum);
			$("#edit_groupId option[value='"+groupId+"']").attr("selected", "selected");
			$("#edit_moduleType option[value='"+moduleType+"']").attr("selected", "selected");
		},
		buttons : {
			"确定" : function vadil() {
				if($("#edit_moduleName").val()==""){
					modalReady('info','模块名称不能为空', null);
					return false;
				}
				if(!/^[0-9]\d*$/.test($('#edit_indexNum').val())){
					modalReady('info','index只能为大于等于0的数字', null);
				}
				else{
				if($("#edit_moduleType").val()!="MENU"){
					var child = $("tr[data-tt-parent-id='" + id + "']");
					if(child.length!=0){
						modalReady('info','父节点模块类型不可修改', null);
						$(this).dialog("close");
						return false;
					}
				}
				var data = {
						id : id,
						moduletype : $("#edit_moduleType").val(),
						modulename : $("#edit_moduleName").val(),
						name:$("#edit_name").val(),
						target : $("#edit_target").val(),
						indexnum : $("#edit_indexNum").val(),
						groupid : $("#edit_groupId").val(),
					};
				$.ajax({
					url : "module/edit",
					type : "post",
					data : JSON.stringify(data),
					dataType : "text",
					contentType:"application/json",
					success : function(result) {
						if (result == "error") {
							modalReady('info','节点修改失败', null);
						} 
						
					},
				});
				$(this).dialog("close");
				$(this).dialog("destroy");
				location.reload();
				}
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
	$("#edit").dialog("open");
}

 
