package cn.life.login;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

/**
 * Created by yejinyun on 2017/9/22.
 */
public class LoginInterface {

    public static void onLogin(RoutingContext routingContext) {
        Session session = routingContext.session();
        if (session.get("foo") != null) {
            JsonObject json = new JsonObject();
            json.put("key", "hasLogin");
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json");
            response.end(json.toString());
            return;
        }
        session.put("foo", "bar");

        JsonObject json = new JsonObject();
        json.put("3rd_session", "32lkdfjlfdjjj=");
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "application/json");
        response.end(json.toString());
    }
}
