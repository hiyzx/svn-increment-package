package com.zero.svn.diff;

import com.zero.svn.util.FileCopy;
import com.zero.svn.util.PathUtil;
import com.zero.svn.util.SysLog;
import com.zero.svn.domain.ChangeVO;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * 变更文件打包工具
 *
 * @author liubq
 * @since 2017年12月19日
 */
public class DiffFilePacker {

    // 保存目录
    private String exportSavePath;
    // 删除文件列表
    private String deleteFile;

    /**
     * 变更文件打包工具
     */
    public DiffFilePacker(String warDir) {
        this.exportSavePath = PathUtil.SAVE_PATH + warDir;
        File file = new File(exportSavePath);
        file.mkdirs();

    }

    /**
     * 打包运行
     *
     * @param exeHome
     *            target目录
     * @param cList
     *            被打包的文件列表
     */
    public List<String> pack(String exeHome, String moduleName, List<ChangeVO> cList, List<String> libList)
            throws Exception {
        // 实际打包的文件
        List<String> actFileList = new ArrayList<>();
        if (cList == null || cList.size() == 0) {
            return actFileList;
        }
        for (ChangeVO entry : cList) {
            List<String> propertiesFileList = new ArrayList<>();
            // 取得变更文件对应可执行文件
            File targetFile = new File(entry.getVersion().getTargetPath());
            if (!targetFile.exists()) {
                throw new Exception("文件路径:" + entry.getVersion().getTargetPath() + "不存在");
            }
            // 查询变化文件
            List<File> exeChangeFileList = findChangeFile(targetFile, entry.getInfo().getChangeFiles());
            // 排序
            exeChangeFileList.sort(Comparator.comparing(File::getAbsolutePath));
            // 拷贝文件
            SysLog.log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            int len = targetFile.getAbsolutePath().length() + 1;
            File newFile;
            // 保存的文件前缀
            String saveFilePre = exportSavePath + "/" + entry.getVersion().getExportProjectName() + "/";
            for (File f : exeChangeFileList) {
                // 目录不用拷贝
                if (f.isDirectory()) {
                    continue;
                }
                newFile = new File(saveFilePre + f.getAbsolutePath().substring(len));
                if (!newFile.getParentFile().exists()) {
                    newFile.mkdirs();
                }
                try {
                    FileCopy.copyFile(f, newFile);
                    actFileList.add(newFile.getAbsolutePath());
                    // 特殊文件记录，用户提醒
                    if (newFile.getAbsolutePath().endsWith(".properties")) {
                        propertiesFileList.add(newFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    SysLog.log("文件复制异常", e);
                }
            }
            Set<String> delList = entry.getInfo().getDelSet();
            if (delList != null && delList.size() > 0) {
                SysLog.log("\r\n删除文件列表---------------------------------");
                String delBasePath = "tomcat\\webapp\\";
                File delFile = new File(exportSavePath + "/" + entry.getVersion().getProjectName() + "_删除文件列表.txt");
                deleteFile = delFile.getAbsolutePath();
                StringBuilder delTxt = new StringBuilder();
                String newLine;
                for (String line : delList) {
                    line = PathUtil.replace(line);
                    newLine = delBasePath + PathUtil.replaceToTargetDir(line);
                    newLine = PathUtil.replace(newLine);
                    delTxt.append(newLine + "\r\n");
                    SysLog.log(newLine);
                }
                Files.write(Paths.get(deleteFile), delTxt.toString().getBytes());
                SysLog.log("删除文件列表---------------------------------");
            }
            // 配置文件，打印处理提醒使用者
            if (propertiesFileList.size() > 0) {
                SysLog.log("\r\n配置文件变化列表，请手动检查是否需要@@@@@@@@@@@@@@@@");
                for (String line : propertiesFileList) {
                    SysLog.log(line);
                }
                SysLog.log("配置文件变化列表，请手动检查是否需要@@@@@@@@@@@@@@@@");
            }
            SysLog.log("\r\n 项目：" + entry.getVersion().getProjectName() + " 打包完成，打包到地址：" + saveFilePre);
            SysLog.log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
        SysLog.log("\r\n打包完成 共打包" + actFileList.size() + "个文件");
        SysLog.log("************************************************************");
        moveList(exeHome, String.format("%s/%s/WEB-INF/lib", exportSavePath, moduleName), libList);
        return actFileList;
    }

    /**
     * 取得变化文件
     */
    private List<File> findChangeFile(File targetDir, List<String> fileNameList) throws Exception {
        String basePath = targetDir.getAbsolutePath();
        File dir;
        List<File> exeChangeFileList = new ArrayList<File>();
        boolean find = false;
        for (String fileName : fileNameList) {
            find = false;
            dir = getChangeFileDir(basePath, fileName);
            if (dir.exists()) {
                for (File tFile : dir.listFiles()) {
                    if (isEquals(tFile, fileName)) {
                        find = true;
                        exeChangeFileList.add(tFile);
                    }
                }
            }
            if (!find) {
                SysLog.log("文件(" + fileName + ")该在目标目录下(" + dir + ")没有找到对应文件，可能该文件已经被删除，跳过，请确认是否正确！");
            }
        }
        return exeChangeFileList;
    }

    /**
     * 取得变化文件所在目录
     */
    private File getChangeFileDir(String basePath, String fileName) {
        fileName = PathUtil.replace(fileName);
        if (fileName.lastIndexOf("/") >= 0) {
            String dir = fileName.substring(0, fileName.lastIndexOf("/"));
            String allDir = PathUtil.replaceToTargetDir(basePath + "/" + dir);
            return new File(allDir);
        } else {
            String allDir = PathUtil.replaceToTargetDir(basePath + "/" + fileName);
            return new File(allDir);
        }

    }

    /**
     * 判断是否相等
     *
     * @param file
     *            待对比文件名
     * @param inPathName
     *            变化文件名
     */
    private boolean isEquals(File file, String inPathName) {
        if (!file.exists()) {
            return false;
        }
        String inFileName = inPathName.substring(inPathName.lastIndexOf("/") + 1);
        String inName = inFileName;
        boolean equals = true;
        // java类变化
        if (PathUtil.ignoreEx(inFileName)) {
            inName = PathUtil.trimEx(inFileName);
            equals = false;
        }
        // 去掉发布源码
        String name = file.getName();
        if (name.endsWith(".java")) {
            return false;
        }
        if (!equals) {
            return name.startsWith(inName);
        } else {
            return name.equalsIgnoreCase(inName);
        }
    }

    /**
     * 移动jar包
     * 
     * @param exeHome
     *            项目地址
     * @param targetLibDir
     *            移动指定目录
     */
    private void moveList(String exeHome, String targetLibDir, List<String> libList) throws Exception {
        String libDir = String.format("%s%s", exeHome, "/WEB-INF/lib");
        File libFile = new File(libDir);
        if (libFile.isDirectory()) {
            File[] files = libFile.listFiles();
            if (files != null) {
                for (File lib : files) {
                    if (libList.contains(lib.getName())) {
                        File newFile = new File(String.format("%s/%s", targetLibDir, lib.getName()));
                        File parent = newFile.getParentFile();
                        // 如果PDF保存路径不存在，则创建路径
                        if (!parent.exists()) {
                            parent.mkdirs();
                        }
                        FileCopy.copyFile(lib, newFile);
                    }
                }
            }
        }
    }

}
