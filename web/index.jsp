<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
</head>
<body>
<form action="/mgr/works/pc/goodsCtrl/add" method="post">
    String:<input name="name"><br>
    Decimal:<input name="price"><br>
    int:<input name="num"><br>
    int:<input name="numddd"><br>
    <input type="submit">
</form>
<form action="/mgr/works/pc/goodsCtrl/updateById" method="post">
    String:<input name="name"><br>
    Decimal:<input name="price"><br>
    int:<input name="num"><br>
    id:<input name="id">
    <input type="submit">
</form>
<a href="http://localhost:8080/mgr/works/pc/goodsCtrl/deleteByIds?ids=702a433b69d54e309adb0857ce08fda9&ids=da7137fcdf0b4141a48ec321f294a75a">shanchu</a>
<a href="http://localhost:8080/mgr/works/pc/goodsCtrl/showById?id=702a433b69d54e309adb0857ce08fda9">chaxun</a>
<a href="http://localhost:8080/mgr/works/pc/goodsCtrl/list">xiugai</a>
<a href="http://localhost:8080/mgr/works/pc/goodsCtrl/query">shanchu</a>

</body>
</html>
