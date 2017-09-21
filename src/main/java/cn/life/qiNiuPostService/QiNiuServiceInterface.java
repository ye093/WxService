package cn.life.qiNiuPostService;

import cn.life.qiniu.DownloadService;
import cn.life.qiniu.UploadToken;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by yejinyun on 2017/9/19.
 */
public class QiNiuServiceInterface {

    /**
     * 图片上传到七牛的回调URL
     * （qiniu/picresult） post
     */
    public static void picUploadResult(RoutingContext routingContext) {
        JsonObject jsonObject = routingContext.getBodyAsJson();
        String key = String.valueOf(jsonObject.getValue("key"));
        String hash = String.valueOf(jsonObject.getValue("hash"));
        String bucket = String.valueOf(jsonObject.getValue("bucket"));
        String fsize = String.valueOf(jsonObject.getValue("fsize"));
        String objectid = String.valueOf(jsonObject.getValue("objectid"));
        String type = String.valueOf(jsonObject.getValue("type"));

        System.out.println(
                " key:" + key +
                        " hash:" + hash +
                        " bucket:" + bucket +
                        " fsize:" + fsize +
                        " objectid:" + objectid +
                        " type:" + type
        );

        //设置返回给七牛的json格式的数据
        JsonObject json = new JsonObject();
        json.put("response", "success");
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/json");
        response.end(json.toString());
    }

    /**
     * 获取上传Token
     */
    public static void getUploadToken(RoutingContext routingContext) {
        MultiMap map = routingContext.request().params();
        System.out.println("获取上传token: " + map.names().toString());
        String fileName = map.get("fileName");
        String uploadToken = UploadToken.generateOverrideToken(fileName);


        JsonObject json = new JsonObject();
        json.put("uptoken", uploadToken);

        //自定义属性
        json.put("objectid", String.valueOf((int) (Math.random() * 100)));
        json.put("type", "111");

        HttpServerResponse response = routingContext.response();

        response.putHeader("content-type", "application/json");
        response.end(json.toString());
    }

    /**
     * 获取图片连接
     */
    public static void getImageUrl(RoutingContext routingContext) {
        MultiMap map = routingContext.request().params();
        System.out.println("获取图片连接: " + map.names().toString());
        String fileName = map.get("fileName");
        if (fileName == null || fileName.isEmpty()) {
            JsonObject json = new JsonObject();
            json.put("code", -1);
            json.put("error", "图片名不能为空");
            HttpServerResponse response = routingContext.response();
            response.end(json.toString());
            return;
        }
        String uploadToken = DownloadService.privateDownloadUrl(fileName);
        JsonObject json = new JsonObject();
        json.put("url", uploadToken);
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/json");
        response.end(json.toString());
    }
}
