<!--自适应菜单-->
<a class="menu-toggler" id="menu-toggler" href="#">
    <span class="menu-text"></span>
</a>

<div class="sidebar" id="sidebar">
    <script type="text/javascript">
        try{ace.settings.check('sidebar' , 'fixed')}catch(e){}
    </script>

    <div class="sidebar-shortcuts" id="sidebar-shortcuts">
        <div class="sidebar-shortcuts-large" id="sidebar-shortcuts-large">
            <button class="btn btn-success">
                <i class="icon-signal"></i>
            </button>

            <button class="btn btn-info">
                <i class="icon-pencil"></i>
            </button>

            <button class="btn btn-warning">
                <i class="icon-group"></i>
            </button>

            <button class="btn btn-danger">
                <i class="icon-cogs"></i>
            </button>
        </div>

        <div class="sidebar-shortcuts-mini" id="sidebar-shortcuts-mini">
            <span class="btn btn-success"></span>

            <span class="btn btn-info"></span>

            <span class="btn btn-warning"></span>

            <span class="btn btn-danger"></span>
        </div>
    </div><!-- #sidebar-shortcuts -->

    <ul class="nav nav-list">
        <li>
            <a href="${rc.contextPath}/user">
                <i class="icon-group"></i>
                <span class="menu-text">用户管理</span>
            </a>
        </li>

        <li>
            <a href="${rc.contextPath}/module">
                <i class="icon-th-large"></i>
                <span class="menu-text">模块管理</span>
            </a>
        </li>


        <li>
            <a href="#" class="dropdown-toggle">
                <i class="icon-key"></i>
                <span class="menu-text">权限管理</span>

                <b class="arrow icon-angle-down"></b>
            </a>

            <ul class="submenu">
                <li>
                    <a href="${rc.contextPath}/authMng/init">
                        <i class="icon-double-angle-right"></i>
                        权限分配
                    </a>
                </li>

                <li>
                    <a href="${rc.contextPath}/roleMng/init">
                        <i class="icon-double-angle-right"></i>
                        角色分配
                    </a>
                </li>
            </ul>
        </li>
    </ul><!-- /.nav-list -->

    <!--
    <div class="sidebar-collapse" id="sidebar-collapse">
           <i class="icon-double-angle-left" data-icon1="icon-double-angle-left" data-icon2="icon-double-angle-right"></i>
      </div>
      -->

    <script type="text/javascript">
        try{ace.settings.check('sidebar' , 'collapsed')}catch(e){}
    </script>
</div>