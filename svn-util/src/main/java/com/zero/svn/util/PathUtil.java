package com.zero.svn.util;

import com.zero.svn.domain.PathVO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 * 
 * @author liubq
 * @since 2017年12月20日
 */
public class PathUtil {
    // 排除的文件目录
    private static List<String> exclusiveList = new ArrayList<>();
    // 初始化排除文件列表
    static {
        exclusiveList.add("target");
        exclusiveList.add("classes");
    }

    // 目录对应文件
    private static List<PathVO> resPathList = new ArrayList<>();
    // 初始化目录对应关系
    static {
        resPathList.add(new PathVO("/src/main/java/", "/WEB-INF/classes/"));
        resPathList.add(new PathVO("/src/main/resources/", "/WEB-INF/classes/"));
        resPathList.add(new PathVO("/src/main/webapp/", "/"));
        resPathList.add(new PathVO("/src/java/", "/WEB-INF/classes/"));
        resPathList.add(new PathVO("/src/webapp/", "/"));
        resPathList.add(new PathVO("/src/WebRoot/", "/"));
        resPathList.add(new PathVO("/src/webroot/", "/"));
        resPathList.add(new PathVO("/src/WebContent/", "/"));
        resPathList.add(new PathVO("/src/webcontent/", "/"));
        resPathList.add(new PathVO("/src/", "/WEB-INF/classes/"));
        resPathList.add(new PathVO("/WebRoot/", "/"));
        resPathList.add(new PathVO("/WebContent/", "/"));
        resPathList.add(new PathVO("/webroot/", "/"));
        resPathList.add(new PathVO("/webcontent/", "/"));
    }

    /**
     * 目录对应文件
     */
    public static String replaceToTargetDir(String path) {
        String inPath = replace(path);
        inPath = "/" + inPath + "/";
        for (PathVO entry : resPathList) {
            inPath = inPath.replace(entry.getSrcPath(), entry.getTargetPath());
        }
        return replace(inPath);
    }

    /**
     * 取得排除的文件列表
     */
    public static List<String> getExclusiveList() {
        return exclusiveList;
    }

    /**
     * 替换
     */
    public static String replace(String path) {
        if (path == null || path.trim().length() == 0) {
            return "";
        }
        String res = path;
        while (res.indexOf("\\") >= 0) {
            res = res.replace("\\", "/");
        }
        while (res.indexOf("//") >= 0) {
            res = res.replace("//", "/");
        }
        while (res.endsWith("/")) {
            res = res.substring(0, res.length() - 1);
        }
        while (res.startsWith("/")) {
            res = res.substring(1);
        }
        return res;
    }

    /**
     * svn方式取得名称
     * 
     * @return 不包含工程名称的相对地址
     */
    public static String trimName(String svnFile, String projectName) {
        if (svnFile.length() <= projectName.length()) {
            return svnFile;
        }
        String tSvnFile = svnFile.substring(svnFile.indexOf(projectName) + projectName.length());
        return PathUtil.replace(tSvnFile);
    }

    /**
     * java 和 class 需要忽略扩展名
     */
    public static boolean ignoreEx(File file) {
        return ignoreEx(file.getName());
    }

    /**
     * java 和 class 需要忽略扩展名
     */
    public static boolean ignoreEx(String fileName) {
        if (fileName.lastIndexOf(".") > 0) {
            String type = fileName.substring(fileName.lastIndexOf(".") + 1);
            if ("java".equalsIgnoreCase(type)) {
                return true;
            }
            return "class".equalsIgnoreCase(type);
        }
        return false;
    }

    /**
     * 取得去掉扩展名后的名称
     */
    public static String trimEx(String fileName) {
        String name = fileName;
        if (name.lastIndexOf(".") > 0) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        return name;
    }

}
