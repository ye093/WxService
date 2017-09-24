package cn.life.pageConfig;

import cn.life.dbhelper.MongoDBManager;
import cn.life.qiniu.DownloadService;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yejy_ on 2017-09-24.
 */
public class PageConfigInterface {

    public static void pageMessage(RoutingContext routingContext) {
        String pageName = routingContext.request().getParam("page");
        if (pageName == null || pageName.length() <= 0) {
            JsonObject json = new JsonObject();
            json.put("code", 0);
            json.put("error", "params error");
            HttpServerResponse serverResponse = routingContext.response();
            serverResponse.putHeader("content-type", "application/json");
            serverResponse.end(json.toString());
            return;
        }
        MongoClient mongoClient = MongoDBManager.getReadClient(routingContext.vertx());
        mongoClient.findOne("icons", new JsonObject().put("page", pageName),
                new JsonObject().put("_id", false), result -> {
                    if (result.succeeded()) {
                        JsonObject object = result.result();
                        List<Object> icons = object.getJsonArray("icons").getList();
                        List<JsonObject> newArray = icons.stream().map(PageConfigInterface::toJson).
                                collect(Collectors.toList());
                        JsonArray data = new JsonArray(newArray);
                        JsonObject json = new JsonObject();
                        json.put("code", 1);
                        json.put("data", data);
                        HttpServerResponse serverResponse = routingContext.response();
                        serverResponse.putHeader("content-type", "application/json");
                        serverResponse.end(json.toString());
                    } else {
                        JsonObject json = new JsonObject();
                        json.put("code", 0);
                        json.put("error", "page not found");
                        HttpServerResponse serverResponse = routingContext.response();
                        serverResponse.putHeader("content-type", "application/json");
                        serverResponse.end(json.toString());
                    }
                });
    }

    private static JsonObject toJson(Object icon) {
        String iconName = String.valueOf(icon);
        String imageUrl = DownloadService.privateDownloadUrl(iconName);
        JsonObject obj = new JsonObject();
        obj.put("fileName", iconName).put("url", imageUrl);
        return obj;
    }
}
