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
<textarea rows="40" cols="150" id="msg"></textarea><br>
routes.add("/mgr/works/pc/addressCtrl", AddressCtrl.class);<br>
routes.add("/mgr/works/pc/catalogCtrl", CatalogCtrl.class);<br>
routes.add("/mgr/works/pc/commonColorCtrl", CommonColorCtrl.class);<br>
routes.add("/mgr/works/pc/dictionaryCtrl", DictionaryCtrl.class);<br>
routes.add("/mgr/works/pc/goodsCtrl", GoodsCtrl.class);<br>
routes.add("/mgr/works/pc/materialCtrl", MaterialCtrl.class);<br>
routes.add("/mgr/works/pc/orderCtrl", OrderCtrl.class);<br>
routes.add("/mgr/works/pc/orderNumberCtrl", OrderNumberCtrl.class);<br>
routes.add("/mgr/works/pc/orderReturnCtrl", OrderReturnCtrl.class);<br>
routes.add("/mgr/works/pc/orderScrapCtrl", OrderScrapCtrl.class);<br>
routes.add("/mgr/works/pc/productCtrl", ProductCtrl.class);<br>
routes.add("/mgr/works/pc/purchaseOrderCtrl", PurchaseOrderCtrl.class);<br>
routes.add("/mgr/works/pc/purchasePurchasereturnProcessCtrl", PurchasePurchasereturnProcessCtrl.class);<br>
routes.add("/mgr/works/pc/purchaseReturnCtrl", PurchaseReturnCtrl.class);<br>
routes.add("/mgr/works/pc/storeCtrl", StoreCtrl.class);<br>
routes.add("/mgr/works/pc/storeCountCtrl", StoreCountCtrl.class);<br>
routes.add("/mgr/works/pc/storeProductRelationCtrl", StoreProductRelationCtrl.class);<br>
routes.add("/mgr/works/pc/storeStockCtrl", StoreStockCtrl.class);<br>
routes.add("/mgr/works/pc/supplierCtrl", SupplierCtrl.class);<br>
routes.add("/mgr/works/pc/sysAuthCtrl", SysAuthCtrl.class);<br>
routes.add("/mgr/works/pc/sysMenuCtrl", SysMenuCtrl.class);<br>
routes.add("/mgr/works/pc/sysRolesCtrl", SysRolesCtrl.class);<br>
routes.add("/mgr/works/pc/sysUserCtrl", SysUserCtrl.class);<br>
routes.add("/mgr/works/pc/warehouseMovementOrderCtrl", WarehouseMovementOrderCtrl.class);<br>
routes.add("/mgr/works/pc/warehouseOutOrderCtrl", WarehouseOutOrderCtrl.class);<br>
routes.add("/mgr/works/pc/warehouseCtrl", WarehouseCtrl.class);<br>
routes.add("/mgr/works/pc/warehouseCountCtrl", WarehouseCountCtrl.class);<br>
routes.add("/mgr/works/pc/warehouseScrapCtrl", WarehouseScrapCtrl.class);<br>
routes.add("/mgr/works/pc/warehouseStockCtrl", WarehouseStockCtrl.class);<br>
</body>
<script src="static/js/jquery-1.10.1.js"></script>
<script>
    $(function () {
        $("#msg").val("");
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
            $("#msg").val("");
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
                    $("#msg").val(msg);
                }
            });
        });
    });
</script>
</html>
