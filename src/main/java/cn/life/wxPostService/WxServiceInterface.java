package cn.life.wxPostService;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by yejy_ on 2017-09-10.
 */
public class WxServiceInterface {

    /**
     * 来自微信的文本消息
     * （wx/customer/message/text） post
     */
    public static void textMsgFromWx(RoutingContext routingContext) {
        System.out.println("wx/customer/message/text");
        System.out.println(routingContext.getBodyAsString());
        JsonObject jsonObject = routingContext.getBodyAsJson();
        String toUserName = jsonObject.getString("ToUserName");
        String fromUserName = jsonObject.getString("FromUserName");
        int createTime = jsonObject.getInteger("CreateTime");
        String msgType = jsonObject.getString("MsgType");
        String content = jsonObject.getString("Content");
        long msgId = jsonObject.getLong("MsgId");


        HttpServerResponse response = routingContext.response();
        if (msgType == null || !msgType.equals("text")) {
            response.end("");
            return;
        }
        response.end(
                toUserName +
                        fromUserName +
                        createTime +
                        msgType +
                        content +
                        msgId
        );

    }
}
