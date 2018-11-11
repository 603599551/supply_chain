<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
</head>
<body>
登录：<br>
<form id="loginForm" action="" method="post">
    账号:<input name="username"><br>
    密码:<input name="password"><br>
    <input type="button" id="loginBtn" value="登录"><span id="loginMsg"></span>
</form>
<form action="" method="post">
    地址:<input id="url" size="150"><br>
    参数:<input id="key" size="150"><br>
    数值:<input id="value" size="150"><br>
    类型:<input id="types"><br>
    <input type="button" id="btn" value="测试">
</form>
<div id="msg">

</div>
</body>
<script src="static/js/jquery-1.10.1.js"></script>
<script>
    $(function () {
        $("#msg").html("");
        $("#loginMsg").html("");
        $("#loginBtn").click(function () {
            $.ajax({
                url : "http://localhost:8080/loginCtrl",
                type : "post",
                data : $("#loginForm").serialize(),
                dataType : "text",
                success : function (msg) {
                    $("#loginMsg").html(msg);
                }
            })
        });
        $("#btn").click(function () {
            $("#msg").html("");
            var url = $("#url").val();
            var type = $("#type").val();
            if(type == null || type.length == 0){
                type="post";
            }
            var keyArr = $("#key").val().split(",");
            var valueArr = $("#value").val().split(",");
            console.log(url);
            console.log(type);
            console.log(keyArr);
            console.log(valueArr);
            var data = {};
            console.log(data);
            if($("#value").val() != null && $("#value").val().length > 0){
                for(var i = 0; i < keyArr.length; i++){
                    var k = keyArr[i];
                    var v = valueArr[i];
                    data[k] = v;
                }
            }else{
                data = $("#key").val();
            }

            $.ajax({
                url : url,
                type : type,
                data : data,
                dataType : "text",
                success : function(msg){
                    $("#msg").html(msg);
                }
            });
        });
    });
</script>
</html>
