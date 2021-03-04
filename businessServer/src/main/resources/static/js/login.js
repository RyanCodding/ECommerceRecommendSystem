function loginHome() {
    var name = $("#inputEmail").val();//
    var pwd = $("#inputPassword").val();
    if (name == "") {
        return "用户名不能为空";
    }
    if (pwd == "") {
        return "密码不能为空";
    } else {
        $.ajax({
            type: "post",       //请求类型
            url: "http://localhost:8090/rest/users/login",
            dataType: "json",
            async: true,
            data: {username: name, password: pwd},
            success: function (data) {
                window.location.href = "http://localhost:8090/page/home";

            }
        });
    }
}