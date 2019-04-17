package com.zero.svn.util;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;

/**
 * @author yezhaoxing
 * @since 2019/04/17
 */
public class MavenUtil {

    public static void install(File pomFile, String mavenHome) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pomFile);
        request.setGoals(Collections.singletonList("clean install -Dmaven.test.skip=true"));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(mavenHome));

        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
    }
}
