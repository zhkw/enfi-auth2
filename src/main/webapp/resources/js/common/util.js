/*!
 * @Description:
 *              共同方法
 * @Author:
 *              Zheng Jia
 * @time:
 *              2015/01/07
* @Author: dejuhu 2015/02/09
 */
var defaultPageSize = 10;
$(function() {
	//回车跳转到某页
	$(document).on("keydown","#pagetext",function(event){		
		if(event.keyCode===13){
			event.preventDefault();
			$(this).closest("ul").find("#goToPage").click();
		}
	});	
	//每页设置条数change
	$(document).on("change","select#pageSetSelect",function(){
		var page=1;//每次重设条数后，显示第1条
		//tbodyID
		var tbodyId = $(this).closest("div").siblings("table.table").find("tbody").attr("id");
		if(null == tbodyId || typeof(tbodyId)=="undefined" || tbodyId.length<1) {
			tbodyId = $(this).closest("div.box-content").find("table.table tbody").attr("id");
		} 
		if(null == tbodyId || typeof(tbodyId)=="undefined" || tbodyId.length<1){
			tbodyId = $(this).closest("div.box-content").find("dl.listboxFullLine").attr("id");
		}
		if(null == tbodyId || typeof(tbodyId)=="undefined" || tbodyId.length<1){
			tbodyId = $(this).closest("div.box-content").find("dd.ddOneline").attr("id");
		}
		var pageSize = pageSetSelectVal(tbodyId);
		//tbodyId,page,pageSize
		reQueryForPagination(tbodyId,page,pageSize);
	});	
});

/**
 * 保持当前条件重刷列表,
 * @param tbodyId必传值
 * @param page不传值时，则保持当前页
 * @param pageSize不传时，则获取页面当前pageSize
 */
function reQueryForPagination(tbodyId,page,pageSize) {
	var $obj = $("#"+tbodyId).closest("div");
	if($("#"+tbodyId).closest("div").find("li#pageSetLi").length<1) {
		$obj = $("#"+tbodyId).closest("div.box-content");
	}
	if(null == page || typeof(page)=="undefined"||page.length<1) {
		page = $obj.find("li.active a").text();
	}
	if(null == pageSize || typeof(pageSize)=="undefined"||pageSize.length<1) {
		pageSize = pageSetSelectVal(tbodyId);
	}
	var searchmap = $obj.find("li#pageSetLi").attr("searchmap");
	var funId = $obj.find("li#pageSetLi").attr("funId");	
	//console.log(searchmap);
	if(null != searchmap && typeof(searchmap)!="undefined") {
		searchmap = JSON.parse(searchmap);
		searchmap.pageSize=pageSize;
		searchmap = JSON.stringify(searchmap);
		eval(funId+"("+page+","+searchmap+")"); 
	}	
}

/**
 * 清空检索条件
 * @param searchInputId
 * @param searchErrId
 */
function clearSearchCondition(searchInputId,searchErrId) {
	var searchIcoObj = $("input#"+searchInputId).closest("div.input-append").find("span.add-on i");
	if(searchIcoObj.hasClass("icon-remove")) {
		//清空检索输入框内容
	    $("#"+searchInputId).val("");
	    if(null != searchErrId && searchErrId.length>1) {
	    	 //隐藏信息
			$("#"+searchErrId).css("display","none");
	    }	   
	    //改变检索图标
		searchIcoObj.attr("class","icon-search");
	}	
}

/**
 * 不保持当前条件重刷列表、并清空检索条件（用于新建后刷新）,
 * @param tbodyId 必传值
 * @param searchInputId 检索框
 * @param searchErrId 错误提示信息
 */
/*function reQueryAdd(tbodyId,searchInputId,searchErrId) {
	var $obj = $("#"+tbodyId).closest("div");
	if($("#"+tbodyId).closest("div").find("li#pageSetLi").length<1) {
		$obj = $("#"+tbodyId).closest("div.box-content");
	}
	var page = 1
	var pageSize = pageSetSelectVal(tbodyId);
	var searchmap = $obj.find("li#pageSetLi").attr("searchmap");
	var funId = $obj.find("li#pageSetLi").attr("funId");
	if(null == searchmap || typeof(searchmap)!="undefined") {
		searchmap={};
	} 
	searchmap = JSON.parse(searchmap);
	searchmap.pageSize=pageSize;
	if(searchmap.hasOwnProperty("searchKey")){
		searchmap.searchKey=null;
	} else {
		searchmap.keyword=null;
	}
	searchmap = JSON.stringify(searchmap);
	eval(funId+"("+page+","+searchmap+")"); 	
	clearSearchCondition(searchInputId,searchErrId);
}
*/
/**
 * 获取当前pageSize
 * @param tbodyId
 * @returns pageSize
 */
function pageSetSelectVal(tbodyId) {
	
	var pageSize;
	if(null != tbodyId && typeof(tbodyId) != "undefined" && tbodyId.length>0) {
		var $obj = $("#"+tbodyId).closest("div");
		if($("#"+tbodyId).closest("div").find("li#pageSetLi").length<1) {
			$obj = $("#"+tbodyId).closest("div.box-content");
		}
		pageSize = $obj.find("select#pageSetSelect").val();
	} else {
		pageSize = $("select#pageSetSelect").val();
	}	
	if(null== pageSize || typeof(pageSize)=="undefined" || pageSize.length<1 || pageSize<10) {
		pageSize = defaultPageSize;
	}
	pageSize = parseInt(pageSize);
	return pageSize;
}
/*
* 去除左边的空格
* 
* @param obj input对象
*/
function LTrim(obj){
	var str = obj.value;
	return str.replace( /^\s+/, "" );
}

/*
* 去除右边的空格
* 
* @param obj input对象
*/
function RTrim(obj){
	var str = obj.value;
	return str.replace( /\s+$/, "" );
}

/*
* 去除左右两边的空格
* 
* @param obj input对象
*/
function Trim(obj){
	var str = obj.value;
	return str.replace( /^\s+/, "" ).replace( /\s+$/, "" );
}

/*
* 去除所有的空格
* 
* @param obj input对象
*/
function AllTrim(obj){
	var str = obj.value;
	return str.replace(/[ ]/g,""); 
} 

/**
 * 分页共通方法
 * @param pagemap 页面设置MAP(JSON:pagemap.total,pagemap.pageNum,pagemap.pagesNum)
 * @param funId 调用后台分页方法
 * @param searchmap 搜索条件MAP
 * @returns {String}
 */
function pagination(pagemap,funId,searchmap){
	//总条数
	var total = pagemap.total;
	//当前页
	var pageNum = pagemap.pageNum;
	//实际总页数
	var pagesNum = pagemap.pagesNum;
	//限制显示页码个数4页
	var pageMarkLimit = 2;

	//构建翻页标签【首页】
	var first = "";
	if(pageNum === 1){
		first = "<li class='disabled'><a href='' onclick='return false;'><<</a></li>";
	}else{
		first = "<li><a href='#' onclick='"+funId+"("+1+","+searchmap+")'><<</a></li>";
	}
	//构建翻页标签【上一页】
	var prev = "";
	if(pageNum === 1){
		prev = "<li class='disabled'><a href='' onclick='return false;'><</a></li>";
	}else{
		prev = "<li><a href='#' onclick='"+funId+"("+(pageNum-1)+","+searchmap+")'><</a></li>";
	}
	//构建翻页标签【下一页】
	var next = "";
	if(pagesNum === pageNum){
		next = "<li class='disabled'><a href='' onclick='return false;'>></a></li>";
	}else{
		next = "<li><a href='#' onclick='"+funId+"("+(pageNum+1)+","+searchmap+")'>></a></li>";
	}
	//构建翻页标签【末页】
	var last = "";
	var lastIndex = pagesNum;
	if(pagesNum === pageNum){
		last = "<li class='disabled'><a href='' onclick='return false;'>>></a></li>";
	}else{
		last = "<li><a href='#' onclick='"+funId+"("+lastIndex+","+searchmap+")'>>></a></li>";
	}
	//构建翻页标签【数字】
	var nums = "";
	
	//构建翻页信息
	var cntInfo = "";
	cntInfo = "<li><span>共&nbsp;" + pagesNum + "&nbsp;页" + "</span></li>";
	//取得页码构筑次数
	var pageQuot = parseInt(pageNum / pageMarkLimit); //整除数
	var pageRemainder = pageNum % pageMarkLimit;//余数
	var pagesReBuild = pageQuot;//实际总构筑次数
	if(pageRemainder > 0){
		pagesReBuild++;
	}
	//计算开始页码和结束页码
	if (pagesReBuild == 1) {
		var startIndex = 1;
	} else {
		var startIndex = parseInt((pagesReBuild - 1) * pageMarkLimit + 1);
	}
	var endIndex = parseInt(pagesReBuild * pageMarkLimit);
	for(var j=1; j<=pagesNum; j++){
		var li = "";
		if (j <= endIndex  && j >= startIndex) {
			if(j === pageNum){
				li += "<li class='active'><a href='' onclick='return false;'>"+j+"</a></li>";
			}else{
				li += "<li><a href='javascript:void(0)' onclick='"+funId+"("+j+","+searchmap+")'>"+j+"</a></li>";
			}
		}
		nums += li;
	}

	//构建翻页标签【GO】
	var gotopage= "<li><span>跳转到：</span></li>" +
			"<li><span class='custom'>" +
			"<input id='pagetext' type='text' size='3' style='height:15px; width:40px; line-height:15px; margin:-2px 0 0 0; padding:1px; font-size:12px; text-align:center;'/>" +
			"</span></li>" +
			"<li><a id='goToPage' href='javascript:void(0)' onclick='GoToAppointPage(event,this,&quot;"+ funId +"&quot;,"+pagesNum+","+searchmap+")'>GO</a></li>";

	var pages = cntInfo + first + prev + nums + next + last + gotopage;
	return pages;
}

//跳转
function GoToAppointPage(event,obj,funId,pagesNum,searchmap) {
//    var page_goto = $(e).prev().val();
	event.preventDefault();
	var page_goto = $(obj).closest("ul").find("#pagetext").val();
	if(null == page_goto || typeof(page_goto) =="undefined"||page_goto.length<1 ) {
		$("#messagModal").remove();
		$("div.modal-backdrop").remove();
		modalReady("info","请输入数字！",null);
		return false;
	}
    if (isNaN(page_goto)) {
    	$("#messagModal").remove();
    	$("div.modal-backdrop").remove();
    	modalReady("info","请输入数字！",null);
    	return false;
    }
    var page = parseInt(page_goto);
    if (page <= 0 || page >pagesNum) {
    	$("#messagModal").remove();
    	$("div.modal-backdrop").remove();
    	modalReady("info","请输入有效的页面范围！",null);
    }
    else {
    	searchmap = JSON.stringify(searchmap);
    	eval(funId+"("+page+","+searchmap+")"); 
    }
}
/**
 * 分页共通方法--带页面设置
 * @param pagemap 页面设置MAP(JSON:pagemap.total,pagemap.pageNum,pagemap.pagesNum)
 * @param funId 调用后台分页方法
 * @param searchmap 搜索条件MAP
 * @returns {String}
 */
function paginationNew(pagemap,funId,searchmap){
	//总条数
	var total = pagemap.total;
	//当前页
	var pageNum = pagemap.pageNum;
	//实际总页数
	var pagesNum = pagemap.pagesNum;
	//限制显示页码个数4页
	var pageMarkLimit = 4;

	//构建翻页标签【首页】
	var first = "";
	if(pageNum === 1){
		first = "<li class='disabled'><a href='' onclick='return false;'><<</a></li>";
	}else{
		first = "<li><a href='#' onclick='"+funId+"("+1+","+searchmap+")'><<</a></li>";
	}
	//构建翻页标签【上一页】
	var prev = "";
	if(pageNum === 1){
		prev = "<li class='disabled'><a href='' onclick='return false;'><</a></li>";
	}else{
		prev = "<li><a href='#' onclick='"+funId+"("+(pageNum-1)+","+searchmap+")'><</a></li>";
	}
	//构建翻页标签【下一页】
	var next = "";
	if(pagesNum === pageNum){
		next = "<li class='disabled'><a href='' onclick='return false;'>></a></li>";
	}else{
		next = "<li><a href='#' onclick='"+funId+"("+(pageNum+1)+","+searchmap+")'>></a></li>";
	}
	//构建翻页标签【末页】
	var last = "";
	var lastIndex = pagesNum;
	if(pagesNum === pageNum){
		last = "<li class='disabled'><a href='' onclick='return false;'>>></a></li>";
	}else{
		last = "<li><a href='#' onclick='"+funId+"("+lastIndex+","+searchmap+")'>>></a></li>";
	}
	//构建翻页标签【数字】
	var nums = "";
	
	//构建翻页信息
	var cntInfo = "";
	cntInfo = "<li><span>共&nbsp;" + pagesNum + "&nbsp;页" + "</span></li>";
	//设置每页显示条数
	var pageSetInfo=""
	pageSetInfo = "<li id='pageSetLi' searchmap='"+searchmap+"' funId='"+funId+"'><span>每页：</span></li><li><span class='custom'>"
	  +"<select id='pageSetSelect' style='height:20px; width:55px; line-height:15px; margin:-3px 0 0 0; padding:0px; font-size:12px; text-align:center;' >";
	
	//var pageSetOptions = defaultPageSet(pageSet);
	var pageSetOptions = "<option value='10'>10</option>"
	  +"<option value='20'>20</option>"
	  +"<option value='30'>30</option>"
	  +"<option value='50'>50</option>"
	  +"<option value='100'>100</option>"
	  +"<option value='200'>200</option>"
	  +"<option value='500'>500</option>";
	pageSetInfo = pageSetInfo + pageSetOptions +"</select></span></li>";
	//取得页码构筑次数
	var pageQuot = parseInt(pageNum / pageMarkLimit); //整除数
	var pageRemainder = pageNum % pageMarkLimit;//余数
	var pagesReBuild = pageQuot;//实际总构筑次数
	if(pageRemainder > 0){
		pagesReBuild++;
	}
	//计算开始页码和结束页码
	if (pagesReBuild == 1) {
		var startIndex = 1;
	} else {
		var startIndex = parseInt((pagesReBuild - 1) * pageMarkLimit + 1);
	}
	var endIndex = parseInt(pagesReBuild * pageMarkLimit);
	for(var j=1; j<=pagesNum; j++){
		var li = "";
		if (j <= endIndex  && j >= startIndex) {
			if(j === pageNum){
				li += "<li class='active'><a href='' onclick='return false;'>"+j+"</a></li>";
			}else{
				li += "<li><a href='javascript:void(0)' onclick='"+funId+"("+j+","+searchmap+")'>"+j+"</a></li>";
			}
		}
		nums += li;
	}

	//构建翻页标签【GO】
	var gotopage= "<li><span>跳转到：</span></li>" +
			"<li><span class='custom'>" +
			"<input id='pagetext' type='text' size='3' style='height:15px; width:40px; line-height:15px; margin:-2px 0 0 0; padding:1px; font-size:12px; text-align:center;'/>" +
			"</span></li>" +
			"<li><a id='goToPage' href='javascript:void(0)' onclick='GoToAppointPage(event,this,&quot;"+ funId +"&quot;,"+pagesNum+","+searchmap+")'>GO</a></li>";

	var pages = cntInfo+ pageSetInfo + first + prev + nums + next + last + gotopage;
	return pages;
}

//数字加逗号
function addComma(obj){
	var numStr = obj.val() + "";
	var end;
	var index = numStr.indexOf(".");
	if(index > 0){
		end = index; 
	}else{
		end = numStr.length;
	}
	var intPartStr = numStr.substring(0, end);
	var floatPartStr = numStr.substring(end);
	var commaStr = addCommaToStr(intPartStr);
	obj.val(commaStr+floatPartStr);
}
//递归加逗号
function addCommaToStr(str){
	var len = str.length;
	if(len <= 3){
		return str;
	}else{
		var index = str.length-3;
		return addCommaToStr(str.substring(0,index)) + "," + str.substring(index); 
	}
}
//移除数字中逗号
function removeComma(obj){
	var str = obj.val();
	str = str.replace(/,/g,'');
	obj.val(str);
}
//移除数字中逗号
function removeCommaAndReturn(obj){
	var str = obj.val();
	str = str.replace(/,/g,'');
	return str;
}

/*
 * 下拉列表检索事件【单选项目】
 * 
 * @param searchObj 检索控件对象this
 * @param blankFlag 是否添加空行标记
 * 
 */
function projectSearch(searchObj,blankFlag){
	var key = "";
	if(searchObj.hasClass("search")){
		key = $.trim(searchObj.siblings().eq(0).val());
	}else{
		key = searchObj.val();
	}
	key = $.trim(key);
	var selectId = searchObj.closest(".chzn-container").siblings().eq(0).attr("id");
	var select = null;
	var selectValue = $("#"+selectId+" option:selected").val();
	if (null != selectValue && "" != selectValue && 
			typeof(selectValue)!="undefined" && " " != selectValue) {
		//保存以前选择项
		select = $("#"+ selectId+" option:selected");
	}
	if(key != ""){
		var query = {keyword : key};
		$.ajax({
			url: "../" + "searchProjects",
			type : "POST",
			dataType : "json",
			contentType : "application/json",
			data: JSON.stringify(query),
			success:function(data){
				var options = "";
				if (data.length > 0) {
					for (var i=0;i<data.length;i++) {
						if(selectValue != data[i].proj_num) {
							options += "<option value='"+data[i].proj_num+"' proj_id='"+data[i].id+"' proj_num='"+data[i].proj_num+"' proj_name='"+data[i].proj_name+"'>"
							+data[i].proj_num+"&nbsp;"+data[i].proj_name+"</option>";
						}						
					}
				}/*else{
					options += "<option>查询无匹配记录！</option>";
				}*/
				$("#"+selectId).html(options);
				if (null !=select){
					// 还原已选项
					$("#"+selectId).prepend(select);	
					if(blankFlag === "blankOption") {
						$("#"+selectId).prepend("<option>&nbsp;</option>");
					}
				} else {
					$("#"+selectId).prepend("<option></option>");
				}				
				$("#"+selectId).trigger("liszt:updated");
			}
		});
	}
}

/*
 * 下拉列表检索事件【多选搜索人】
 * 
 * @param searchObj 检索控件对象this
 * @param blankFlag 是否添加空行标记
 * 
 */
function searchMultiPeople(searchObj, blankFlag){
	var name = "";
	if(searchObj.hasClass("search-multi")){//搜索按钮
		name = $.trim(searchObj.siblings().eq(0).val());
	}else{//搜索框
		name = searchObj.val();
	}
	name = $.trim(name);
	//select id
	var selectId = searchObj.closest(".chzn-container").siblings().eq(0).attr("id");
	//搜索query的key
	var searchName = "name";
	//调用的url
	var url = "searchPersons";
	//如果搜索业主
	if(selectId === "projOwnerSelect"){
		searchName = "cust_name";
		url = "searchCustomers";
	}
	if(name === "" || name=="0"){
		if(searchName === "cust_name"){
			modalReady("info","请输入业主名称/编号",null);
		} else {
			modalReady("info","请输入姓名/工号",null);
		}
	}else{
		
		var select = null;
		var selectValue = $("#"+selectId+" option:selected").val();
		if (null != selectValue && "" != selectValue && 
				typeof(selectValue)!="undefined" && " " != selectValue) {
			//保存以前选择项
			select = $("#"+ selectId+" option:selected");
		}
		var query = '{"'+searchName+'":"'+name+'"}';
		$.ajax({
			url: "../" + url,
			type : "POST",
			dataType : "json",
			contentType : "application/json",
			data: query,//JSON.stringify(query),
			success:function(data){
				var options = "";
				if (data.length > 0) {
					var i = 0;
					if(searchName === "cust_name"){
						//客户
						for(i; i<data.length; i++){
							if(selectValue != data[i].id) {
								options += "<option value='"+data[i].id+"' cust_num='"+data[i].cust_num+"'>"+data[i].cust_name+"</option>";
							}
						}
					}else{
						//人员
						for(i; i<data.length; i++){
							var flag = false;
							$(select).each(function(){
								var tmp_val = $(this).val();
								if (tmp_val == data[i].id) {
									flag = true;
								}
							  });
							if(!flag) {
								options += "<option value='"+data[i].employee_num+"' employee_id='"+data[i].id+"'>"+data[i].name+"</option>";
							}							
						}
					}
				}
				
				$("#"+selectId).html(options);	
				if (null !=select){
					// 还原已选项
					$("#"+selectId).prepend(select);	
					if(blankFlag === "blankOption") {
						$("#"+selectId).prepend("<option>&nbsp;</option>");
					}
				} else {
					$("#"+selectId).prepend("<option></option>");
				}	
				$("#"+selectId).trigger("liszt:updated");
			}
		});
	}
}

/**
 * 一级下拉列表change事件
 * @param obj this
 */
function dropDownLevel1Change(obj) {	
	//一级下拉列表属性ID
	var drive_id = $(obj).closest("td").attr("attr_id");
	var drive_attr_valueId = $(obj).val();
	var drive_attr_value = "";
	$(obj).find("option[selected]").removeAttr("selected");
//	$(obj).find("option[value='"+drive_attr_value+"']").attr('selected', 'selected');
	
	//二级下拉列表属性
	var attr_id = $(obj).closest("td").attr("drive_id");
	var attr_value = "";
	var blankFlg = "blankOption";
	if(null != drive_attr_valueId && typeof(drive_attr_valueId)!="undefined" && drive_attr_valueId.length>0) {
		drive_attr_value = $(obj).find("option[value='"+drive_attr_valueId+"']").text();
		$(obj).find("option[id='"+drive_attr_valueId+"']").attr('selected', 'selected');
	}
	dropDownLevel2List(drive_id,drive_attr_value,attr_id,attr_value,blankFlg);	
}

/**
 * 二级下拉列表change事件
 * @param obj this
 */
function dropDownLevel2Change(obj) {	
	var attr_val = $(obj).val();
	$(obj).find("option[selected]").removeAttr("selected");
	$(obj).find("option[id='"+attr_val+"']").attr('selected', 'selected');
}

/**
 * 生成下拉列表HTML(1级，2级初始化)
 * @param enum array
 * @param attr_id 属性ID
 * @param attr_value 选中值
 * @param blankFlg 是否添加空白选项
 */
function dropDownList(enumlt,attr_id,attr_value,blankFlg,hasSubDropLtFlg,isModified) {
	var dropDownHtml = "";
	if("Y"==isModified) {
		if("subDropLtFlg"==hasSubDropLtFlg){
			dropDownHtml = "<select id='select_"+attr_id+"'  onchange='dropDownLevel1Change(this);' class='' style='margin:0px; line-height:15px'>" ;
		} else {
			dropDownHtml = "<select id='select_"+attr_id+"' style='margin:0px; line-height:15px'>" ;		
		}
	} else {
		dropDownHtml = "<select id='select_"+attr_id+"' disabled='disabled' class='' style='margin:0px; line-height:15px'>" ;		
	}
	if("blankOption"==blankFlg) {
		dropDownHtml+="<option>&nbsp;</option>";
	}
	for(var i=0;i<enumlt.length;i++) {
		if(""!=attr_value && attr_value==enumlt[i].value) {
			dropDownHtml+="<option  selected='selected'  value ='"+enumlt[i].id+"'>"+enumlt[i].value+"</option>";
		} else {
			dropDownHtml+="<option value ='"+enumlt[i].id+"'>"+enumlt[i].value+"</option>";
		}		
	}
	dropDownHtml+="</select>";
	return dropDownHtml;
}

/**
 * 生成级联二级下拉列表HTML
 * @param enum array
 * @param attr_id 属性ID
 * @param attr_value 选中值
 * @param blankFlg 是否添加空白选项
 */
function dropDownLevel2List(drive_id,drive_attr_value,attr_id,attr_value,blankFlg) {
	//一级菜单空选项，二级菜单去掉列表
	if(null ==drive_attr_value || typeof(drive_attr_value)=="undefined" ||""==drive_attr_value||drive_attr_value.length<1) {
		var dropDownLevel2Html = "<select id='select_"+attr_id+"' class='span8' style='margin:0px; line-height:15px'>" ;
		if("blankOption"==blankFlg) {
			dropDownLevel2Html+="<option>&nbsp;</option>";
		}
		dropDownLevel2Html+="</select>";
		$("td[attr_id='"+attr_id+"']").html(dropDownLevel2Html);
		return;
	}
	var rootPath = getRootPath_web()+"/getCascadeList";
	$.ajax({
		type:"POST",
		url:rootPath,
		dataType : "json",
//		data : {
//			"attr_id":drive_id,"attr_value":drive_attr_value
//		},
		data : {
			"attr_id":attr_id,"attr_value":drive_attr_value
		},
		success : function(dataList) {
			console.log(dataList);
			var dropDownLevel2Html = "<select id='select_"+attr_id+"' onchange='dropDownLevel2Change(this);' class='span8' style='margin:0px; line-height:15px'>" ;
			if("blankOption"==blankFlg) {
				dropDownLevel2Html+="<option>&nbsp;</option>";
			}
			for(var i=0;i<dataList.length;i++) {
				if(""!=attr_value && attr_value==dataList[i].value) {
					dropDownLevel2Html+="<option  selected='selected'  value ='"+dataList[i].id+"'>"+dataList[i].value+"</option>";
				} else {
					dropDownLevel2Html+="<option value ='"+dataList[i].id+"'>"+dataList[i].value+"</option>";
				}		
			}
			dropDownLevel2Html+="</select>";
			$("td[attr_id='"+attr_id+"']").html(dropDownLevel2Html);
		}
	});	
}
//
function getProjectUrl(){
	var pathName = window.document.location.pathname;
	var projectUrl = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
	return projectUrl;
}