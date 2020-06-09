<link rel="stylesheet" href="${basePath}/resources/css/userMng/userMng.css"/>
<!--start breadcrumb -->
<div class="breadcrumbs" id="breadcrumbs">
    <script type="text/javascript">
        try {
            ace.settings.check('breadcrumbs', 'fixed')
        } catch (e) {
        }
    </script>
    <ul class="breadcrumb">
        <li><i class="icon-home home-icon"></i> <a href="${basePath}">首页</a></li>
        <li class="active">用户管理</li>
    </ul>
    <!-- .breadcrumb -->
    <!--
        <div class="nav-search" id="nav-search">
            <form class="form-search">
                <span class="input-icon">
                    <input type="text" placeholder="Search ..." class="nav-search-input" id="nav-search-input" autocomplete="off" />
                    <i class="icon-search nav-search-icon"></i>
                </span>
            </form>
        </div><!-- #nav-search -->
</div>
<!--end  breadcrumb -->
<!-- start content -->
<div class="user-mng-content">
    <div class="user-list">
        <div class="buttons">
            <button type="button" class="btn btn-default btn-progress btn-group-lg" disabled>下载模板</button>
            <button type="button" class="btn btn-default btn-progress btn-group-lg" disabled>导入用户</button>
            <button type="button" class="btn btn-default btn-progress btn-group-lg  adduser">新建用户</button>
            <div style="float:right;">
                <div class="input-append cpmuser-search" >
                    <input id="cpmuserSearchInput" type="text" class="span13" size="30" placeholder="用户名/姓名/工号"/>
                    <span class="add-on">
				<a href="javascript:void(0)" id="clickSearchUserA" onclick="clickSearchUser(this);"><i id="cpmuserSearchIco" class="icon-search"></i></a>
				</span>
                </div>
            </div>
        </div>
        <div class="panel panel-primary">
            <div class="panel-heading">用户列表</div>
            <div class="panel-body">
                <table class="table  table-hover">
                    <thead>
                    <tr>
                        <th  style="display:none"></th>
                        <th  style="width:10%">用户名<span style="color:red">*</span></th>
                        <th  style="width:10%" >密码<span style="color:red">*</span></th>
                        <th  style="width:10%">姓名<span style="color:red">*</span></th>
                        <th  style="width:10%">工号<span style="color:red">*</span></th>
                        <th  style="width:10%">性别</th>
                        <th  style="width:10%">Email</th>
                        <th  style="width:10%">电话</th>
                        <th  style="width:10%">专业</th>
                        <th  style="width:10%">操作</th>
                    </tr>
                    </thead>
                    <tbody id="user_data">
                    </tbody>
                </table>
            </div>
        </div>
        <div class="pagination pagination-right" id="userlist-pagination">
            <ul></ul>
        </div>
    </div>
    <div class="project-list" style="margin-right:10px;">
        <div style="float:right;">
            <div class="input-append proj-search" >
                <input id="projSearchInput" type="text" class="span13" size="30" placeholder="项目名/项目号"/>
                <span class="add-on serachProjects">
				<a href="javascript:void(0);" id="clickSearchProjA"  onclick="clickSearchProj(this)"><i id="cpmuserSearchIco" class="icon-search"></i></a>
				</span>
            </div>
        </div>
        <div class="panel panel-info">
            <div class="panel-heading">参与项目列表</div>
            <div class="panel-body" style="padding:0px">
                <table class="table table-hover" id="t_userProj">
                    <thead>
                    <tr>
                        <th  style="display:none"></th>
                        <th  style="width:10%">项目号</th>
                        <th  style="width:10%" >项目名</th>
                    </tr>
                    </thead>
                    <tbody id="project_data" >
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<!--end-content -->
<script type="text/javascript" src="${rc.contextPath}/resources/js/userMng/userMng.js"  charset="utf-8"></script>