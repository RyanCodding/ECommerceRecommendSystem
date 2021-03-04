function searchGoods(){
    var name = $("#query").val();
    $.ajax({
        type: "get",
        url: "http://localhost:8090/rest/product/search",
        dataType: "json",
        async: true,
        data: {query: name},
        success: function (data) {
           console.log(data)

        }
    });
}
offlineGoods();
function offlineGoods(){

    $.ajax({
        type: "get",
        url: "http://localhost:8090/rest/product/offline",
        dataType: "json",
        async: true,
        data: {username: "1228218655@qq.com",num:6},
        success: function (result) {
            var goods = result.data;
            if(goods.length>0){
                for (var i = 0; i < goods.length; i++) {
                    $("#goodsInfo").append( "<div class=\"goodsContainer\"><a class=\"goodsInfo\" href='http://localhost:8090/page/detail' target=\"_blank\">" +
                        "<img class=\"goodsImg\" src="+ goods[i].imageUrl +" >"+
                                            "<span class=\"goodsTit\">"+Math.round(goods[i].score * 100)/100+"</span>"+
                                             "<span class=\"goodsTit\">"+goods[i].name+"</span>"+
                                             "<span class=\"goodsDesc\">"+goods[i].categories+"</span></a></div>"
                                           )
                    if(i == 5){
                        break;
                    }
                }
            }


        }
    });
}

hotGoods();
function hotGoods(){

    $.ajax({
        type: "get",
        url: "http://localhost:8090/rest/product/hot",
        dataType: "json",
        async: true,
        data: {num:6},
        success: function (result) {
            var goods = result.data;
            if(goods.length>0){
                for (var i = 0; i < goods.length; i++) {
                    $("#hotGoods").append( "<div class=\"goodsContainer\"><a class=\"goodsInfo\"  target=\"_blank\">" +
                        "<img class=\"goodsImg\" src="+ goods[i].imageUrl +" >"+
                        "<span class=\"goodsTit\">"+Math.round(goods[i].score * 100)/100+"</span>"+
                        "<span class=\"goodsTit\">"+goods[i].name+"</span>"+
                        "<span class=\"goodsDesc\">"+goods[i].categories+"</span></a></div>"
                    )
                    if(i == 5){
                        break;
                    }
                }
            }


        }
    });
}

rateGoods();
function rateGoods(){

    $.ajax({
        type: "get",
        url: "http://localhost:8090/rest/product/rate",
        dataType: "json",
        async: true,
        data: {num:6},
        success: function (result) {
            var goods = result.data;
            if(goods.length>0){
                for (var i = 0; i < goods.length; i++) {
                    $("#rateMoreGoods").append( "<div class=\"goodsContainer\"><a class=\"goodsInfo\"  target=\"_blank\">" +
                        "<img class=\"goodsImg\" src="+ goods[i].imageUrl +" >"+
                        "<span class=\"goodsTit\">"+Math.round(goods[i].score * 100)/100+"</span>"+
                        "<span class=\"goodsTit\">"+goods[i].name+"</span>"+
                        "<span class=\"goodsDesc\">"+goods[i].categories+"</span></a></div>"
                    )
                    if(i == 5){
                        break;
                    }
                }
            }


        }
    });
}

