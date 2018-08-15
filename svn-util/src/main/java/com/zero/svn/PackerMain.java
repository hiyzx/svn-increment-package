package com.zero.svn;

import com.zero.svn.diff.DiffFileLister;
import com.zero.svn.diff.DiffFilePacker;
import com.zero.svn.domain.ChangeVO;
import com.zero.svn.util.SysLog;
import com.zero.svn.version.SVNVersion;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
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

    private static final String SAVE_PATH = "C:/Users/Administrator/Desktop";

    private static final String RECORD_PATH = "E:\\app\\svn-increment-package\\record.txt";

    private static List<String> libList = new ArrayList<>(20);

    private static Map<String, Map<String, Integer>> fileCountMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        String warDir = SAVE_PATH
                + String.format("/war包/%s-%s", "war", new SimpleDateFormat("MM-dd-HH").format(new Date()));
        Long lastVersion = 2L;
        Long startVersion = 1L;
        SysLog.log("确定已经更新并打包最新项目?y:n?");
        Scanner scan = new Scanner(System.in);
        if ("y".equals(scan.next())) {
            fileCountMap.put("project", new HashMap<>());
            addNewJar(startVersion, lastVersion);
            packageModule(warDir, startVersion, lastVersion);
            writeVersion(startVersion, lastVersion);
            Desktop.getDesktop().open(new File(warDir));
        } else {
            throw new RuntimeException("未更新并打包最新项目");
        }
    }

    private static void packageModule(String warDir, Long startVersion, Long lastVersion) throws Exception {
        commonPackage(warDir, "module-name-war", startVersion, lastVersion);
    }

    /**
     * @param moduleName
     *            工程所用的tomcat地址（主要是为了Copy class等文件）
     * @param startVersion
     *            开始增量打包版本号
     */
    private static void commonPackage(String warDir, String moduleName, Long startVersion, Long lastVersion)
            throws Exception {
        fileCountMap.put(moduleName, new HashMap<>());
        String url = String.format(SVN_URL, moduleName);
        String exeHome = String.format(TARGET_PROJECT, moduleName, moduleName);
        List<SVNVersion> chageList = new ArrayList<>();
        chageList.add(new SVNVersion(exeHome, url, USERNAME, PASSWORD, startVersion, lastVersion));
        SysLog.log("开始执行请等待。。。。。。  ");
        // 根据版本取得差异文件
        DiffFileLister oper = new DiffFileLister(chageList);
        List<ChangeVO> list = oper.listChange();
        // 打包
        DiffFilePacker p = new DiffFilePacker(warDir);
        p.pack(exeHome, moduleName, list, libList, fileCountMap);
        SysLog.log("处理完成 。。。。。。    ");
    }

    private static void addNewJar(Long startVersion, Long lastVersion) throws Exception {
        addNewJar("module-name-jar", startVersion, lastVersion);
    }

    private static void addNewJar(String moduleName, Long startVersion, Long lastVersion) throws Exception {
        String url = String.format(SVN_URL, moduleName);
        String exeHome = String.format(TARGET_PROJECT, moduleName, moduleName);
        SVNVersion svnVersion = new SVNVersion(exeHome, url, USERNAME, PASSWORD, startVersion, lastVersion);
        if (svnVersion.getWorker().judgeVersion()) {
            String jar = String.format("%s-%s.jar", moduleName, PROJECT_VERSION);
            libList.add(jar);
            SysLog.log("添加jar包--", jar);
        }
    }

    private static void writeVersion(Long startVersion, Long lastVersion) throws IOException {
        File versionFile = new File(RECORD_PATH);
        FileWriter fw = new FileWriter(versionFile, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("\n\t");
        bw.write(String.format("%s-%s", startVersion, lastVersion));
        bw.write(String.format("  %s", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
        bw.write("\n\t");
        for (String keySet : fileCountMap.keySet()) {
            bw.write(resolveMap(keySet, fileCountMap.get(keySet)));
            bw.write("\n\t");
        }
        bw.flush();
        bw.close();
        fw.close();
    }

    private static String resolveMap(String moduleName, Map<String, Integer> projectCountMap) {
        StringBuilder rtn = new StringBuilder();
        rtn.append(moduleName).append(": ");
        for (String keySet : projectCountMap.keySet()) {
            rtn.append(String.format("%s:%s", keySet, projectCountMap.get(keySet))).append(" ");
        }
        return rtn.toString();
    }
}
