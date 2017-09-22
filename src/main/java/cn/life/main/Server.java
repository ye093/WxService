package cn.life.main;

import cn.life.login.LoginInterface;
import cn.life.qiNiuPostService.QiNiuServiceInterface;
import cn.life.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

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

        //session for save User login message
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(BodyHandler.create());

        //----------------监听第三方回调开始--------------------------------
        //监听来自七牛服务器的信息
        router.post("/qiniu/picresult").consumes("application/json")
                .produces("application/json").handler(QiNiuServiceInterface::picUploadResult);
        //----------------监听第三方回调结束--------------------------------


        //----------------用户自定义接口开始--------------------------------

        //用户登录
        router.get("/wxlogin").produces("application/json").handler(LoginInterface::onLogin);
        //用户获取上传凭证
        router.get("/file/uploadtoken").produces("application/json").handler(QiNiuServiceInterface::getUploadToken);
        //根据图片名，获取图片连接
        router.post("/file/getimageurl").produces("application/json").handler(QiNiuServiceInterface::getImageUrl);
        //----------------用户自定义接口结束--------------------------------

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }


}
