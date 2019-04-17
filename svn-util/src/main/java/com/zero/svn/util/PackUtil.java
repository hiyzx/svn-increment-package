package com.zero.svn.util;

import com.zero.svn.diff.DiffFilePacker;
import com.zero.svn.domain.ChangeVO;
import com.zero.svn.domain.ConfigDto;
import com.zero.svn.version.SVNVersion;
import lombok.Data;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author yezhaoxing
 * @since 2019/02/21
 */
@Data
public class PackUtil {

    private ConfigDto configDto;

    private Boolean open;

    private List<String> libList = new ArrayList<>(20);

    private Map<String, Map<String, Integer>> fileCountMap = new LinkedHashMap<>();

    public PackUtil(ConfigDto configDto, Boolean open) {
        this.configDto = configDto;
        this.open = open;
    }

    public void packer() throws Exception {
        String warDir = configDto.getSavePath() + String
                .format("/%s-%s", "war", new SimpleDateFormat("YY-MM-dd-HH").format(new Date()));
        Long lastVersion = configDto.getLastVersion();
        Long startVersion = configDto.getStartVersion();
        fileCountMap.put("project", new HashMap<>(10));
        addNewJar(startVersion, lastVersion);
        packageModule(warDir, startVersion, lastVersion);
        writeVersion(startVersion, lastVersion);
        if (open) {
            Desktop.getDesktop().open(new File(warDir));
        }
    }

    private void packageModule(String warDir, Long startVersion, Long lastVersion) throws Exception {
        if (configDto.getUpdate()) {
            String targetPath = configDto.getTargetPath();
            SVNVersion svnVersion = new SVNVersion(targetPath, configDto.getSvnUrl(), configDto.getUsername(),
                    configDto.getPassword(), startVersion, lastVersion);
            svnVersion.update();// 更新
            MavenUtil.install(new File(targetPath + "pom.xml"), configDto.getMavenHome());// install
        }
        String warList = configDto.getWarList();
        if (isNotEmpty(warList)) {
            String[] wars = warList.split(",");
            for (String war : wars) {
                commonPackage(warDir, war, startVersion, lastVersion);
            }
        }
    }

    private void addNewJar(Long startVersion, Long lastVersion) throws Exception {
        String selfLibList = configDto.getSelfLibList();
        if (isNotEmpty(selfLibList)) {
            String[] selfJar = selfLibList.split(",");
            for (String s : selfJar) {
                addNewJar(s, startVersion, lastVersion);
            }
        }

    }

    /**
     * @param moduleName
     *            工程所用的tomcat地址（主要是为了Copy class等文件）
     * @param startVersion
     *            开始增量打包版本号
     */
    private void commonPackage(String warDir, String moduleName, Long startVersion, Long lastVersion) throws Exception {
        fileCountMap.put(moduleName, new HashMap<>(10));
        String url = String.format(configDto.getSvnUrl() + "/%s", moduleName);
        String exeHome = String.format(configDto.getTargetPath() + "%s\\target\\%s", moduleName, moduleName);
        SVNVersion svnVersion = new SVNVersion(exeHome, url, configDto.getUsername(), configDto.getPassword(),
                startVersion, lastVersion);
        SysLog.log("开始执行请等待。。。。。。  ");
        // 根据版本取得差异文件
        List<ChangeVO> list = Collections.singletonList(svnVersion.get());
        // 打包
        DiffFilePacker p = new DiffFilePacker(warDir);
        p.pack(exeHome, moduleName, list, libList, fileCountMap);
        SysLog.log("处理完成 。。。。。。    ");
    }

    private void addNewJar(String moduleName, Long startVersion, Long lastVersion) throws Exception {
        String url = String.format(configDto.getSvnUrl() + "/%s", moduleName);
        String exeHome = String.format(configDto.getTargetPath() + "%s\\%s", moduleName, moduleName);
        SVNVersion svnVersion = new SVNVersion(exeHome, url, configDto.getUsername(), configDto.getPassword(),
                startVersion, lastVersion);
        if (svnVersion.getWorker().judgeVersion()) {
            String jar = String.format("%s-%s.jar", moduleName, configDto.getProjectVersion());
            libList.add(jar);
            SysLog.log("添加jar包--", jar);
        }
    }

    private void writeVersion(Long recordVersion, Long lastVersion) throws IOException {
        File versionFile = new File(String.format("%s%srecord-%s.txt", System.getProperty("user.dir"), File.separator,
                configDto.getProjectName()));
        if (!versionFile.exists()) {
            versionFile.createNewFile();
        }
        FileWriter fw = new FileWriter(versionFile, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("\n\t");
        bw.write(String.format("%s-%s", recordVersion, lastVersion));
        bw.write(String.format("  %s", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
        bw.write("\n\t");
        for (String keySet : fileCountMap.keySet()) {
            String moduleStr = resolveMap(keySet, fileCountMap.get(keySet));
            if (moduleStr != null) {
                bw.write(moduleStr);
                bw.write("\n\t");
            }
        }
        bw.flush();
        bw.close();
        fw.close();
    }

    private static String resolveMap(String moduleName, Map<String, Integer> projectCountMap) {
        if (projectCountMap.size() == 0) {
            SysLog.log(String.format("%s没有更新文件", moduleName));
            return null;
        } else {
            StringBuilder rtn = new StringBuilder();
            rtn.append(moduleName).append(": ").append("{ ");
            for (String keySet : projectCountMap.keySet()) {
                rtn.append(String.format("%s:%s", keySet, projectCountMap.get(keySet))).append(" ");
            }
            rtn.append(" }");
            return rtn.toString();
        }
    }

    private Boolean isNotEmpty(String source) {
        return source != null && !"".equals(source);
    }
}
