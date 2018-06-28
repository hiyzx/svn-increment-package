package com.zero.svn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 文件复制
 *
 * @author liubq
 */
public class FileCopy {

    /**
     * 复制单个文件
     *
     * @param oldFile
     *            原文件
     * @param newFile
     *            复制后文件
     */
    public static void copyFile(File oldFile, File newFile) throws Exception {
        if (!oldFile.exists() || oldFile.isDirectory()) {
            return;
        }
        int byteRead = 0;
        int byteSum = 0;
        if (newFile.exists()) {
            if (!newFile.delete()) {
                SysLog.log(newFile.getAbsolutePath() + " 删除失败");
            }
        }
        try (InputStream inStream = new FileInputStream(oldFile); FileOutputStream fs = new FileOutputStream(newFile)) {
            // 读入原文件
            byte[] buffer = new byte[1444];
            while ((byteRead = inStream.read(buffer)) != -1) {
                // 字节数 文件大小
                byteSum += byteRead;
                fs.write(buffer, 0, byteRead);
            }
            fs.flush();
            fs.close();
            SysLog.log("复制文件： " + newFile.getAbsolutePath());
        }
    }

}
