package com.zero.svn.util.web.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
public class CmdInterceptor extends Thread {
    private InputStream is;

    public CmdInterceptor(InputStream is) {
        this.is = is;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            log.info(e.getMessage(), e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                log.info(e.getMessage(), e);
            }
        }
    }
}