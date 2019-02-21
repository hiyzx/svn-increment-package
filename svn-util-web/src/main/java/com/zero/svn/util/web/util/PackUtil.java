package com.zero.svn.util.web.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ZipUtil;
import com.zero.svn.diff.DiffFileLister;
import com.zero.svn.diff.DiffFilePacker;
import com.zero.svn.domain.ChangeVO;
import com.zero.svn.util.SysLog;
import com.zero.svn.version.SVNVersion;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yezhaoxing
 * @since 2019/02/21
 */
public class PackUtil extends ConfigDto {

    private List<String> libList = new ArrayList<>(20);

    private Map<String, Map<String, Integer>> fileCountMap = new LinkedHashMap<>();

    public static PackUtil of(ConfigDto configDto) {
        PackUtil packUtil = new PackUtil();
        BeanUtil.copyProperties(configDto, packUtil);
        return packUtil;
    }

    public String packer() throws Exception {
        String warDir = getSavePath() + String.format("/war包/%s/%s-%s", getProjectName(), "war",
                new SimpleDateFormat("YY-MM-dd-HH").format(new Date()));
        Long lastVersion = getLastVersion();
        Long startVersion = getStartVersion();
        fileCountMap.put("project", new HashMap<>(10));
        addQzNewJar(startVersion, lastVersion);
        packageQzModule(warDir, startVersion, lastVersion);
        writeVersion(startVersion, lastVersion, getProjectName());
        File zip = ZipUtil.zip(warDir);
        return zip.getAbsolutePath();
    }

    private void packageQzModule(String warDir, Long startVersion, Long lastVersion) throws Exception {
        String warList = getWarList();
        String[] wars = warList.split(",");
        for (String war : wars) {
            commonPackage(warDir, war, startVersion, lastVersion);
        }
    }

    private void addQzNewJar(Long startVersion, Long lastVersion) throws Exception {
        String selfLibList = getSelfLibList();
        if (StringUtils.hasText(selfLibList)) {
            String[] selfLibs = selfLibList.split(",");
            for (String selfLib : selfLibs) {
                addNewJar(selfLib, startVersion, lastVersion);
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
        String url = String.format(getSvnUrl(), moduleName);
        String exeHome = String.format(getTargetPath(), moduleName, moduleName);
        List<SVNVersion> chageList = new ArrayList<>();
        chageList.add(new SVNVersion(exeHome, url, getUsername(), getPassword(), startVersion, lastVersion));
        SysLog.log("开始执行请等待。。。。。。  ");
        // 根据版本取得差异文件
        DiffFileLister oper = new DiffFileLister(chageList);
        List<ChangeVO> list = oper.listChange();
        // 打包
        DiffFilePacker p = new DiffFilePacker(warDir);
        p.pack(exeHome, moduleName, list, libList, fileCountMap);
        SysLog.log("处理完成 。。。。。。    ");
    }

    private void addNewJar(String moduleName, Long startVersion, Long lastVersion) throws Exception {
        String url = String.format(getSvnUrl(), moduleName);
        String exeHome = String.format(getTargetPath(), moduleName, moduleName);
        SVNVersion svnVersion = new SVNVersion(exeHome, url, getUsername(), getPassword(), startVersion, lastVersion);
        if (svnVersion.getWorker().judgeVersion()) {
            String jar = String.format("%s-%s.jar", moduleName, getProjectVersion());
            libList.add(jar);
            SysLog.log("添加jar包--", jar);
        }
        String outLibList = getOutLibList();
        if (StringUtils.hasText(outLibList)) {
            String[] outLib = outLibList.split(",");
            libList.addAll(Arrays.asList(outLib));
        }
    }

    private void writeVersion(Long recordVersion, Long lastVersion, String projectName) throws IOException {
        File versionFile = new File(
                String.format("%s%srecord-%s.txt", System.getProperty("user.dir"), File.separator, projectName));
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

    private String resolveMap(String moduleName, Map<String, Integer> projectCountMap) {
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

}
