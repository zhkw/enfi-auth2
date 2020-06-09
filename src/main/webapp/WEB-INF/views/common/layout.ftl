<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <title>后台管理</title>
    <meta name="keywords" content="CPM" />
    <meta name="description" content="后台管理" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <!-- basic styles -->
    <link href="${rc.contextPath}/resources/plugins/assets/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="${rc.contextPath}/resources/plugins/assets/css/font-awesome.min.css" />
    <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans:400,300" />
    <!-- ace styles -->
    <link rel="stylesheet" href="${rc.contextPath}/resources/plugins/assets/css/ace.min.css" />
    <link rel="stylesheet" href="${rc.contextPath}/resources/plugins/assets/css/ace-rtl.min.css" />
    <link rel="stylesheet" href="${rc.contextPath}/resources/plugins/assets/css/ace-skins.min.css" />
    <link rel="stylesheet" href="${rc.contextPath}/resources/css/common/cpm-authmng.css"/>

    <!-- ace settings handler -->
    <script src="${rc.contextPath}/resources/plugins/assets/js/ace-extra.min.js"></script>
    <script src="${rc.contextPath}/resources/js/jquery-1.10.2.min.js"></script>
    <script src="${rc.contextPath}/resources/js/jquery-migrate-1.2.1.min.js"></script>
    <script src="${rc.contextPath}/resources/js/jquery.uniform.min.js"></script>
    <style type="text/css">
        body {
            font-family: "Microsoft YaHei" ! important;
        }
    </style>
</head>

<body>
<#assign basePath="${rc.contextPath}">
<div id="basePath" value="${basePath}" style="display:none"></div>
<div class="headers">
    <!--头部部分-->
    <#include "/${header_path}">
</div>
<div class="main-container" id="main-container">
    <script type="text/javascript">
        try{ace.settings.check('main-container' , 'fixed')}catch(e){}
    </script>
    <div class="main-container-inner">
        <!--menu-->
        <div class="left">
            <#include "/${left_path}">
        </div>
        <!--content-->
        <div class="content">
            <div class="main-content"><#include "/${content_path}"></div>
        </div>
    </div><!-- /.main-container-inner -->
</div><!-- /.main-container -->

<!--footer-->
<div class="footer" style="width:100%; bottom:0;">
    <#include  "/${footer_path}">
</div>
<!-- basic scripts -->
<script src="${rc.contextPath}/resources/plugins/assets/js/bootstrap.min.js"></script>
<script src="${rc.contextPath}/resources/plugins/assets/js/typeahead-bs2.min.js"></script>
<!-- ace scripts -->
<script src="${rc.contextPath}/resources/plugins/assets/js/ace-elements.min.js"></script>
<script src="${rc.contextPath}/resources/plugins/assets/js/ace.min.js"></script>
<!--TOOLS-->
<script src="${rc.contextPath}/resources/js/common/timeUitl.js"></script>
<script src="${rc.contextPath}/resources/js/common/popUpUitl.js"></script>
<script src="${rc.contextPath}/resources/js/common/util.js"></script>
<script src="${rc.contextPath}/resources/js/common/validate.js"></script>
<script src="${rc.contextPath}/resources/js/common/common.js"></script>
</body>
</html>

