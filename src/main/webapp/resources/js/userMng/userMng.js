var restoreTrObj = null;
var defaultPageSize = 10;
$(function() {
	$(document).on("click", ".adduser", function(event) {
		addUserTr();
	});
	getUserList(1, {
		pageSize : 10
	});
	enterKey();
});
// 查询用户列表
function getUserList(currentPage, searchmap) {
	var keyword = $("#cpmuserSearchInput").val();
	var pageSize;
	if (null == keyword || '' == keyword) {
		keyword = null;
	}
	if (null != searchmap) {
		pageSize = searchmap.pageSize;
	} else {
		searchmap = {};
	}
	if (null == pageSize || "" == pageSize || typeof (pageSize) == "undefined") {
		pageSize = 10;
	}
	var data = new Object();
	data.keyword = keyword, data.nextPage = currentPage;
	data.pageSize = pageSize;
	searchmap.pageSize = pageSize;
	$.ajax({
		type : "POST",
		url : "user/nextPage",
		dataType : "json",
		data : data,
		success : function(data) {
			setUserData(data);
			// 默认选中第一行数据
			chosenFirst();
			// 分页按钮加载
			if (data.PAGE_TOTALCOUNT != 0) {
				var pagemap = {
					total : data.PAGE_TOTALCOUNT,
					pageNum : data.CURRENT_PAGE,
					pagesNum : data.TOTAL_PAGE
				};
				searchmap = JSON.stringify(searchmap);
				var pages = paginationNew(pagemap, "getUserList", searchmap);
				$("#userlist-pagination ul").html(pages);
				$("select#pageSetSelect option[value='" + pageSize + "']")
						.attr("selected", "true");
			} else {
				$("#userlist-pagination ul").html("没有用户数据");
			}
		}
	});
}

// 格式化用户列表数据
function setUserData(data) {
	var userTr = "";
	var userData = data.PAGE_DATA;
	var personColumnMap = data.personColumnMap;
	
	//姓名
	var name = "name";
	//工号
	var employee_num = "employee_num";
	//email
	var email = "email";
	//电话
	var phone = "phone";
	//专业 
	var specialty = "specialty";
	
	if (personColumnMap.isModified == "Y") {
		name = personColumnMap.name;
		employee_num = personColumnMap.employee_num;
		email = personColumnMap.email;
		phone = personColumnMap.phone;
		specialty = personColumnMap.specialty;
	}
	
	for (var i = 0; i < userData.length; i++) {
		var perUser = userData[i];
		userTr += '<tr id="'
				+ perUser.id
				+ '" class="user" name="'
				+ perUser.name
				+ '"  onclick="getProjectOfUser(this);">'
				+ '<td class="user-edit mustinput username">'
				+ perUser.username
				+ '</td>'
				+ '<td class="user-edit mustinput password">******</td>'
				+ '<td class="user-edit mustinput name">'
				+ perUser[name]
				+ '</td>'
				+ '<td class="user-edit mustinput employnum">'
				+ (perUser[employee_num] == null ? "" : perUser[employee_num])
				+ '</td>'
				+ '<td class="user-edit user-gender-select">'
				+ (perUser.gender == null ? "" : perUser.gender)
				+ '</td>'
				+ '<td class="user-edit email">'
				+ (perUser[email] == null ? "" : perUser[email])
				+ '</td>'
				+ '<td class="user-edit phone">'
				+ (perUser[phone] == null ? "" : perUser[phone])
				+ '</td>'
				+ '<td class="user-edit specialty">'
				+ (perUser[specialty] == null ? "" : perUser[specialty])
				+ '</td>'
				+ '<td class="handel"><i class="icon-edit bigger-150 editUserTr blue update"  onclick="editUserData(this);" uid='
				+ perUser.id
				+ '></i>'
				+ '<i class="icon-trash bigger-150 editUserTr blue" onclick="deleteUser(this);"  uid='
				+ perUser.id + '></i></td></tr>';
	}
	$("tbody#user_data").find("tr").remove();
	$("tbody#user_data").append(userTr);
}

// 添加用户数据
function addUserTr() {
	var userData = $("#user_data");
	if (check($("#user_data"))) {
		var newUser = "<tr>";
		newUser += '<td class="userId" style="display:none" id="userId"></td>';
		newUser += '<td class="username mustinput user-edit"><input id="username"/></td>';
		newUser += '<td class="password  mustinput user-edit"><input type="password" id="password" /></td>';
		newUser += '<td class="name mustinput user-edit"><input id="name"/></td>';
		newUser += '<td class="employnum  mustinput user-edit"><input id="employnum"/></td>';
		newUser += '<td class="gender user-gender-select"><select id="gender"><option value ="男">男</option><option value ="女">女</option></select></td>';
		newUser += '<td class="email user-edit"><input id="email" /></td>';
		newUser += '<td class="phone user-edit"><input  id="phone"/></td>';
		newUser += '<td class="special user-edit"><input id="specialty"/></td>';
		newUser += '<td class="handel"><i class="icon-save bigger-150  editUserTr blue add" onclick="editUserData(this);"></i><i class="icon-trash bigger-150 delUserTr blue" id="notexisted" onclick="deleteUser(this);"></i></td>';
		newUser += "</tr>";
		userData.prepend(newUser);
	} else {
		modalReady('confirm', '存在编辑中的数据，是否放弃编辑？', 'restore()');
	}
}

// 保存用户数据
function handleUser(oper, uid) {
	var data = {};
	data.userId = uid;
	data.oper = oper;
	data.userName = $("#username").val();
	data.password = $("#password").val();
	data.name = $("#name").val();
	data.employee_num = $("#employnum").val();
	data.gender = $("#gender").val();
	data.email = $("#email").val();
	data.phone = $("#phone").val();
	data.specialty = $("#specialty").val();
	$.ajax({
		type : "POST",
		url : "user/handle",
		dataType : "json",
		contentType : "application/json",
		data : JSON.stringify(data),
		success : function(data) {
			getUserList(1, {
				pageSize : 10
			});
		}
	});
}

// 删除用户数据
function deleteUser(obj) {
	if (!check($("#user_data"))) {
		modalReady('confirm', '存在编辑中的数据，是否放弃编辑？', 'restore()');
	}else{
		var id = $(obj).attr('uid');
		modalReady('confirm', '确定要删除该行数据？', 'executeDel(' + id + ')');
	}
}

// 执行删除
function executeDel(id) {
	if ("" != id && "undefined" != id && null != id) {
		$.ajax({
			type : "POST",
			url : "user/deleteUser",
			dataType : "json",
			contentType : "application/json",
			data : JSON.stringify({
				userId : id
			}),
			success : function(data) {
				var pageNum = 1;// 每次重设条数后，显示第1条
				var pageSize = $("select#pageSetSelect").val();
				getUserList(pageNum, {
					pageSize : pageSize
				});
			}
		});
		removeAllError();
	} else {
		removeAllError();
		$('#notexisted').closest("tr").remove();
	}
}

// 检索用户参与项目列表
function getProjectOfUser(obj) {
	$('.project-list .panel-heading').empty();
	if ($(obj).attr('name') == null || $(obj).attr('name') == undefined
			|| $(obj).attr('name') == 'undefined') {
		$('.project-list .panel-heading').prepend("无项目数据");
	} else {
		$('.project-list .panel-heading').prepend(
				$(obj).attr('name') + ":参与项目列表");
	}
	$("tbody#project_data").empty();
	var userId = $(obj).attr('id');
	$("#user_data").find('.user-selected').removeClass('user-selected');
	$(obj).addClass('user-selected');
	$("#t_userProj thead").attr("id", userId);
	userId = $("#t_userProj thead").attr("id");
	var keyword = $("#projSearchInput").val();
	$.ajax({
		type : "POST",
		url : "user/projectOfUser",
		dataType : "json",
		data : {
			userId : userId,
			keyword : keyword
		},
		success : function(data) {
			var projectList = "";
			var projects = data.data;
			var projectColumnMap = data.projectColumnMap;
			//项目名称
			var proj_name = "proj_name";
			//项目编号
			var proj_num = "proj_num";
			if ("Y" == projectColumnMap.isModified) {
				proj_name = projectColumnMap.proj_name;
				proj_num = projectColumnMap.proj_num;
			}
			if (projects.length > 0) {
				for (var i = 0; i < projects.length; i++) {
					projectList += '<tr id="' + projects[i].id + '" class="user">'
							+ '<td class="proj_num">' + (projects[i][proj_num] == null ? "" : projects[i][proj_num])
							+ '</td>' + '<td class="projectname">'
							+ (projects[i][proj_name] == null ? "" : projects[i][proj_name]) + '</td>';
				}
			} else {
				projectList = "无项目数据";
			}
			$('.project-list #project_data').find("tr").remove();
			$("tbody#project_data").empty();
			$("tbody#project_data").append(projectList);
		}
	});
}

// 编辑用户数据
function editUserData(obj) {
	if ($(obj).hasClass('icon-edit')) {
		if (check($('#user_data'))) {
			restoreTrObj = $(obj).closest("tr").html();
			var tds = $(obj).parent().parent().children();
			for (var i = 0; i < tds.length; i++) {
				if ($(tds[i]).hasClass('user-edit')
						&& $(tds[i]).hasClass('mustinput')
						&& $(tds[i]).hasClass('username')) {
					var inhtml = "<input id='username' value='"
							+ $(tds[i]).html() + "'></input>";
					$(tds[i]).html(inhtml);
				}
				if ($(tds[i]).hasClass('user-edit')
						&& $(tds[i]).hasClass('mustinput')
						&& $(tds[i]).hasClass('password')) {
					var inhtml = "<input type='password'  id='password' value='"
							+ $(tds[i]).html() + "'></input>";
					$(tds[i]).html(inhtml);
				}
				if ($(tds[i]).hasClass('user-edit')
						&& $(tds[i]).hasClass('mustinput')
						&& $(tds[i]).hasClass('name')) {
					var inhtml = "<input id='name' value='" + $(tds[i]).html()
							+ "'></input>";
					$(tds[i]).html(inhtml);
				}
				if ($(tds[i]).hasClass('user-edit')
						&& $(tds[i]).hasClass('mustinput')
						&& $(tds[i]).hasClass('employnum')) {
					var inhtml = "<input id='employnum' value='"
							+ $(tds[i]).html() + "'></input>";
					$(tds[i]).html(inhtml);
				}
				if ($(tds[i]).hasClass('user-edit')
						&& $(tds[i]).hasClass('email')) {
					var inhtml = "<input id='email' value='" + $(tds[i]).html()
							+ "'></input>";
					$(tds[i]).html(inhtml);
				}
				if ($(tds[i]).hasClass('user-edit')
						&& $(tds[i]).hasClass('phone')) {
					var inhtml = "<input id='phone' value='" + $(tds[i]).html()
							+ "'></input>";
					$(tds[i]).html(inhtml);
				}
				if ($(tds[i]).hasClass('user-edit')
						&& $(tds[i]).hasClass('specialty')) {
					var inhtml = "<input id='specialty' value='"
							+ $(tds[i]).html() + "'></input>";
					$(tds[i]).html(inhtml);
				}
				if ($(tds[i]).hasClass('user-gender-select')) {
					var inhtml = '<select id="gender" style="margin:0px; line-height:15px"><option value ="男">男</option><option value ="女">女</option></select>';
					$(tds[i]).html(inhtml);
				}
				// 移除编辑图标
				if ($(tds[i]).hasClass('handel')) {
					$(obj).removeClass('icon-edit').addClass('icon-save');
				}
			}
		} else {
			var id = $('#user_data').find('.handel .icon-save').attr('uid');
			modalReady('confirm', '存在编辑中的数据，是否放弃编辑？', 'restore(' + id + ')');
		}
	} else {
		// 保存
		var tds = $(obj).parent().parent().children();
		var mustCheck = true;
		var phoneCheck = true;
		var emailCheck = true;
		// 有效性检测
		for (var i = 0; i < tds.length; i++) {
			if ($(tds[i]).hasClass('user-edit')
					&& $(tds[i]).hasClass('mustinput')) {
				mustCheck = isFilled2($(tds[i]).find('input').attr('id'))
						&& mustCheck;
			}
			if ($(tds[i]).hasClass('user-edit') && $(tds[i]).hasClass('email')) {
				emailCheck = checkEmail2($(tds[i]).find('input'));
			}
			if ($(tds[i]).hasClass('user-edit') && $(tds[i]).hasClass('phone')) {
				if ($(tds[i]).find('input').val() != '') {
					phoneCheck = check_mobile($(tds[i]).find('input').val())
							|| check_phone($(tds[i]).find('input').val());
				}
				if (!phoneCheck) {
					setError2("请输入正确的联系电话", $(tds[i]).find('input'));
				}
			}
		}
		if (phoneCheck && emailCheck && mustCheck) {
			removeAllError();
			// 来自添加
			if ($(obj).hasClass('add')) {
				handleUser('add', '');
			} else {
				// 来自编辑
				var uid = $(obj).attr('uid');
				handleUser('update', uid);
			}
		}
	}
}

// 检测是否存在处于编辑编辑状态的行数据
function check(obj) {
	var objSave = $(obj).find('.handel .icon-save');
	if (objSave.length > 0) {
		return false;
	} else {
		return true;
	}
}

// 恢复编辑行数据
function restore(id) {
	removeAllError();
	if (id !== "" && "undefined" != id && "undefined" != typeof (id)
			&& null != id) {
		$("#user_data #" + id).html(restoreTrObj);
	} else {
		$("#user_data tr .icon-save").closest("tr").remove();
	}
}

// 改变分页条数
$(function() {
	// 每页设置条数change
	$(document).on("change", "select#pageSetSelect", function() {
		var pageNum = 1;// 每次重设条数后，显示第1条
		var pageSize = $("select#pageSetSelect").val();
		getUserList(pageNum, {
			pageSize : pageSize
		});
	});
});

// 用户检索按钮
function clickSearchUser(obj) {
	$('.cpmuser-search .tooltip').remove();
	var keyword = $('#cpmuserSearchInput').val();
	var icon = $(obj).children();
	if ($(icon).hasClass('icon-search')) {
		if (keyword == '' || keyword == null) {
			setToltip("请输入检索条件", $('.cpmuser-search'));
		} else {
			$(icon).removeClass().addClass('icon-remove');
			getUserList(1, {
				pageSize : 10
			});
		}
	} else {
		$(icon).removeClass().addClass('icon-search');
		$('#cpmuserSearchInput').val("");
		getUserList(1, {
			pageSize : 10
		});
	}
}


// 用户检索按钮
function clickSearchProj(obj) {
	$('.proj-search .tooltip').remove();
	var keyword = $('#projSearchInput').val();
	var icon = $(obj).children();
	if ($(icon).hasClass('icon-search')) {
		if (keyword == '' || keyword == null) {
			setToltip("请输入检索条件", $('.proj-search'));
		} else {
			$(icon).removeClass().addClass('icon-remove');
			getProjectOfUser($("#user_data").find('.user-selected'));
		}
	} else {
		$(icon).removeClass().addClass('icon-search');
		$('#projSearchInput').val("");
		getProjectOfUser($("#user_data").find('.user-selected'));
	}
}
// 选中第一行数据
function chosenFirst() {
	$("#user_data").find('.user-selected').removeClass('user-selected');
	$('#user_data tr :first').addClass('user-selected');
	getProjectOfUser($('#user_data tr:first'));
}
//回车绑定
function enterKey(){
	$('#cpmuserSearchInput').bind('keypress',function(event){
       if(event.keyCode == "13"){
    	   clickSearchUser($('#clickSearchUserA')); 
         }
     });
	$('#projSearchInput').bind('keypress',function(event){
	       if(event.keyCode == "13"){
	    	   clickSearchProj($('#clickSearchProjA')); 
	         }
	     });
}