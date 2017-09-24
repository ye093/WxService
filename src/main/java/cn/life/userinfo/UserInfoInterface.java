package cn.life.userinfo;

import cn.life.config.ServiceConfig;
import cn.life.dbhelper.MongoDBManager;
import cn.life.util.WXBizDataCrypt;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

/**
 * Created by yejy_ on 2017-09-23.
 * 获取用户信息
 */
public class UserInfoInterface {

    public static void parseUserMessage(RoutingContext routingContext) {
        Session session = routingContext.session();
        String openId = session.get("openid");
        String sessionKey = session.get("session_key");
        if (openId == null || sessionKey == null) {
            //响应
            JsonObject json = new JsonObject();
            json.put("code", 2);
            json.put("message", "not login");
            HttpServerResponse serverResponse = routingContext.response();
            serverResponse.putHeader("content-type", "application/json");
            serverResponse.end(json.toString());
            return;
        }

        JsonObject jsonObject = routingContext.getBodyAsJson();
        String encryptedData = jsonObject.getString("encryptedData");
        String iv = jsonObject.getString("iv");
        if (encryptedData == null || iv == null) {
            JsonObject json = new JsonObject();
            json.put("code", 0);
            json.put("message", "params error");
            HttpServerResponse serverResponse = routingContext.response();
            serverResponse.putHeader("content-type", "application/json");
            serverResponse.end(json.toString());
            return;
        }
        //解析用户信息
        WXBizDataCrypt wxBizDataCrypt = new WXBizDataCrypt(ServiceConfig.APP_ID, sessionKey);
        String data = wxBizDataCrypt.decryptData(encryptedData, iv);
        if (data == null) {
            JsonObject json = new JsonObject();
            json.put("code", 0);
            json.put("error", "decryptData error");
            HttpServerResponse serverResponse = routingContext.response();
            serverResponse.putHeader("content-type", "application/json");
            serverResponse.end(json.toString());
            return;
        }
        System.out.println(data);
        JsonObject useData = new JsonObject(data);
        String nickName = useData.getString("nickName", "");
        String avatarUrl = useData.getString("avatarUrl", "");
        int gender = useData.getInteger("gender", 0);
        String city = useData.getString("city", "");
        String province = useData.getString("province", "");
        String openId2 = useData.getString("openId", "");
        MongoClient mongoClient = MongoDBManager.getWriteClient(routingContext.vertx());

        JsonObject setJson = new JsonObject().put("$set",
                new JsonObject().put("wxUserInfo",
                        new JsonObject().put("nickName", nickName)
                            .put("avatarUrl", avatarUrl)
                            .put("gender", gender)
                            .put("city", city)
                            .put("province", province)));
        mongoClient.updateCollectionWithOptions("users", new JsonObject().put("openId", openId2),
                setJson, new UpdateOptions(true), result -> {
                    if (result.succeeded()) {
                        JsonObject json = new JsonObject();
                        json.put("code", 1)
                                .put("data", new JsonObject()
                                .put("nickName", nickName)
                                .put("avatarUrl", avatarUrl));
                        HttpServerResponse serverResponse = routingContext.response();
                        serverResponse.putHeader("content-type", "application/json");
                        serverResponse.end(json.toString());
                    } else {
                        JsonObject json = new JsonObject();
                        json.put("code", 0)
                                .put("error", "update user info error");
                        HttpServerResponse serverResponse = routingContext.response();
                        serverResponse.putHeader("content-type", "application/json");
                        serverResponse.end(json.toString());
                    }
                });

    }
}
