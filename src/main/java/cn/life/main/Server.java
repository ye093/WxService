package cn.life.main;

import cn.life.qiNiuPostService.QiNiuServiceInterface;
import cn.life.util.Runner;
import cn.life.wxPostService.WxServiceInterface;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;

/**
 * Created by yejy_ on 2017-09-10.
 */
public class Server extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(Server.class);
    }

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/wx").handler(this::handleGetWx);
        router.post("/wx/customer/message/text").produces("application/json").handler(WxServiceInterface::textMsgFromWx);
        //监听来自七牛服务器的信息
        router.post("/qiniu/picresult").consumes("application/json")
                .produces("application/json").handler(QiNiuServiceInterface::picUploadResult);

        //用户获取上传凭证
        router.get("/file/uploadtoken").produces("application/json").handler(QiNiuServiceInterface::getUploadToken);

        //根据图片名，获取图片连接
        router.post("/file/getimageurl").produces("application/json").handler(QiNiuServiceInterface::getImageUrl);



        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    /**
     * 验证是否是微信服务器返回的
     */
    private void handleGetWx(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        HttpServerRequest request = routingContext.request();
        String signature = request.getParam("signature");
        String timestamp = request.getParam("timestamp");
        String nonce = request.getParam("nonce");
        String echostr = request.getParam("echostr");
        if (signature == null || timestamp == null || nonce == null
                || echostr == null) {
            response.end("hello, this is handle view");
            return;
        }
        String token = "YeJinYun8899";
        String[] arrays = new String[]{
                token, timestamp, nonce
        };
        Arrays.sort(arrays);
        StringBuilder sb = new StringBuilder();
        for (String s : arrays) {
            sb.append(s);
        }
        String signStr = DigestUtils.sha1Hex(sb.toString());
        if (signStr.equals(signature)) {
            response.end(echostr);
        } else {
            response.end("");
        }
    }


}
