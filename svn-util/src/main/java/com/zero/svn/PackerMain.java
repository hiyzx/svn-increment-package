package com.zero.svn;

import com.zero.svn.diff.DiffFileLister;
import com.zero.svn.diff.DiffFilePacker;
import com.zero.svn.util.SysLog;
import com.zero.svn.domain.ChangeVO;
import com.zero.svn.version.SVNVersion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SVN方式打包 注意，必须保持本地最新代码，因为要取本地tomcat下编译好的class,js等文件，本项目不能自动编译 resource
 * 会自动跳过，配置文件自己拷贝
 *
 * @author liubq
 * @since 2017年12月21日
 */
public class PackerMain {

    private static final String TARGET_PROJECT = "E:\\app\\project-name\\%s\\target\\%s";;

    private static final String SVN_URL = "http://localhost/project-name/%s";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static final String PROJECT_VERSION = "0.0.1-SNAPSHOT";

    private static List<String> libList = new ArrayList<>(20);

    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-HH");
        String warDir = String.format("/war包/%s-%s", "war", sdf.format(new Date()));
        Long lastVersion = 1L;
        addNewJar(lastVersion);
        commonPackage(warDir, "module-name-war", lastVersion);
    }

    /**
     * @param moduleName
     *            工程所用的tomcat地址（主要是为了Copy class等文件）
     * @param firstVersion
     *            开始增量打包版本号
     */
    private static void commonPackage(String warDir, String moduleName, Long firstVersion) throws Exception {
        String url = String.format(SVN_URL, moduleName);
        String exeHome = String.format(TARGET_PROJECT, moduleName, moduleName);
        List<SVNVersion> chageList = new ArrayList<>();
        chageList.add(new SVNVersion(exeHome, url, USERNAME, PASSWORD, firstVersion));
        SysLog.log("开始执行请等待。。。。。。  ");
        // 根据版本取得差异文件
        DiffFileLister oper = new DiffFileLister(chageList);
        List<ChangeVO> list = oper.listChange();
        // 打包
        DiffFilePacker p = new DiffFilePacker(warDir);
        p.pack(exeHome, moduleName, list, libList);
        SysLog.log("处理完成 。。。。。。    ");
    }

    private static void addNewJar(Long lastVersion) throws Exception {
        addNewJar("module-name-jar", lastVersion);
    }

    private static void addNewJar(String moduleName, Long lastVersion) throws Exception {
        String url = String.format(SVN_URL, moduleName);
        String exeHome = String.format(TARGET_PROJECT, moduleName, moduleName);
        SVNVersion svnVersion = new SVNVersion(exeHome, url, USERNAME, PASSWORD, lastVersion);
        if (svnVersion.getWorker().judgeVersion(lastVersion)) {
            libList.add(String.format("%s-%s.jar", moduleName, PROJECT_VERSION));
        }
    }
}
