package cn.life.test;

import cn.life.qiniu.DownloadService;
import org.junit.Test;

/**
 * Created by yejinyun on 2017/9/19.
 */
public class MyTest {

    @Test
    public void test() {
        System.out.println(DownloadService.privateDownloadUrl("bar_icon_mytrip.png"));
    }
}
