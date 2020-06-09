<link rel="stylesheet" href="${basePath}/resources/css/auth/authmng.css"/>
<link rel="stylesheet" href="${basePath}/resources/js/zTree_v3/css/zTreeStyle/zTreeStyle.css" type="text/css">
<script type="text/javascript" src="${basePath}/resources/js/zTree_v3/js/jquery.ztree.core-3.5.min.js"></script>
<script type="text/javascript" src="${basePath}/resources/js/zTree_v3/js/jquery.ztree.excheck-3.5.min.js"></script>
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
        <li class="active">权限分配</li>
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
<div class="role-content">
    <ul id="cpm-role-tab" class="nav nav-tabs" style="height:50px;">
        <li class="active">
            <a href="#system-role"  class="system-role" data-toggle="tab">系统角色</a>
        </li>
        <li>
            <a href="#project-role"  class="project-role" data-toggle="tab">项目角色</a>
        </li>
    </ul>
    <div id="cpm-role-tabContent" class="tab-content">
        <div class="tab-pane fade in active" id="system-role">
            <div class="role-mng-content">
                <div class="role-list">
                    <div class="buttons">
                        <button type="button" class="btn btn-default btn-progress btn-group-lg  addRole" onclick="addRole();">新建角色</button>
                        <div style="float:right;">
                            <div class="input-append role-search" >
                                <input id="roleSearchInput" type="text" class="span13" size="30" placeholder="角色名称"/>
                                <span class="add-on">
		                    <a href="#"><i id="roleSearchIco" class="icon-search role-search" onclick="searchRoles(this);"></i></a>
		                    </span>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-primary role-panel">
                        <div class="panel-heading">角色列表</div>
                        <div class="panel-body">
                            <table class="table table-hover">
                                <thead>
                                <tr>
                                    <th id="role-id" style="display:none"></th>
                                    <th style="width:10%">序号</th>
                                    <th style="width:20%">角色名称<span style="color:red">*</span></th>
                                    <th style="width:20%">创建时间</th>
                                    <th style="width:20%">更新时间</th>
                                    <th style="width:20%">说明</th>
                                    <th style="width:10%;">操作</th>
                                </tr>
                                </thead>
                                <tbody id="roleData">
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="pagination pagination-right" id="rolelist-pagination">
                        <ul></ul>
                    </div>
                </div>
                <div class="services-tree">
                    <div class="panel panel-info services-panel">
                        <div class="panel-heading">服务模块</div>
                        <div class="panel-body">
                            <ul id="servicesTree" class="ztree" style="width:260px; overflow:auto;"></ul>
                        </div>
                    </div>
                    <div class="buttons">
                        <button type="button" class="btn btn-default btn-progress btn-group-lg sm-update" onclick="handelRoleModules(this);">确定</button>
                        <button type="button" class="btn btn-default btn-progress btn-group-lg sm-reset"  onclick="handelRoleModules(this);">重置</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="tab-pane fade" id="project-role">
            <div class="p-role-mng-content">
                <div class="p-role-list">
                    <div class="buttons">
                        <button type="button" class="btn btn-default btn-progress btn-group-lg  p-addRole" onclick="addPRole();">新建角色</button>
                        <div style="float:right;">
                            <div class="input-append p-role-search" >
                                <input id="p-roleSearchInput" type="text" class="span13" size="30" placeholder="角色名称"/>
                                <span class="add-on">
		                    <a href="#"><i id="p-roleSearchIco" class="icon-search p-role-search" onclick="searchPRoles(this);"></i></a>
		                    </span>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-primary p-role-panel">
                        <div class="panel-heading">角色列表</div>
                        <div class="panel-body">
                            <table class="table table-hover">
                                <thead>
                                <tr>
                                    <th id="role-id" style="display:none"></th>
                                    <th style="width:10%">序号</th>
                                    <th style="width:20%">角色名称<span style="color:red">*</span></th>
                                    <th style="width:20%">创建时间</th>
                                    <th style="width:20%">更新时间</th>
                                    <th style="width:20%">说明</th>
                                    <th style="width:10%;">操作</th>
                                </tr>
                                </thead>
                                <tbody id="p-roleData">
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="pagination pagination-right" id="p-rolelist-pagination">
                        <ul></ul>
                    </div>
                </div>
                <div class="p-services-tree">
                    <div class="panel panel-info p-services-panel">
                        <div class="panel-heading">服务模块</div>
                        <div class="panel-body">
                            <ul id="p-servicesTree" class="ztree" style="width:260px; overflow:auto;"></ul>
                        </div>
                    </div>
                    <div class="buttons">
                        <button type="button" class="btn btn-default btn-progress btn-group-lg p-sm-update" onclick="handelPRoleModules(this);">确定</button>
                        <button type="button" class="btn btn-default btn-progress btn-group-lg p-sm-reset"  onclick="handelPRoleModules(this);">重置</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--end-content -->
<script type="text/javascript" src="${basePath}/resources/js/auth/authmng.js"></script>