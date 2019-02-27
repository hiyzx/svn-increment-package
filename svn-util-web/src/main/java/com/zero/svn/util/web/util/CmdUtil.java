package com.zero.svn.util.web.util;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yezhaoxing
 * @since 2019/02/25
 */
@Slf4j
public class CmdUtil {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void callCMD(String cmd) {
        Process exec = null;
        try {
            exec = Runtime.getRuntime().exec(cmd);
            final InputStream is1 = exec.getInputStream(); // 获取进程的标准输入流
            CmdInterceptor interceptor = new CmdInterceptor(is1);
            executorService.execute(interceptor);
            exec.waitFor();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        } finally {
            if (null != exec) {
                exec.destroy();
            }
        }
    }
}
