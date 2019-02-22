package com.zero.svn.util.web.util;

import cn.hutool.core.util.ZipUtil;
import com.zero.svn.diff.DiffFileLister;
import com.zero.svn.diff.DiffFilePacker;
import com.zero.svn.domain.ChangeVO;
import com.zero.svn.util.SysLog;
import com.zero.svn.util.web.dto.ConfigDto;
import com.zero.svn.util.web.dto.ModuleRecordDto;
import com.zero.svn.util.web.dto.RecordDto;
import com.zero.svn.version.SVNVersion;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yezhaoxing
 * @since 2019/02/21
 */
@Data
public class PackUtil {

    private ConfigDto config;

    private List<String> libList = new ArrayList<>(20);

    private Map<String, Map<String, Integer>> fileCountMap = new LinkedHashMap<>();

    public static PackUtil of(ConfigDto config) {
        PackUtil packUtil = new PackUtil();
        packUtil.setConfig(config);
        return packUtil;
    }

    public RecordDto packer() throws Exception {
        String warDir = config.getSavePath() + String.format("/war包/%s/%s-%s", config.getProjectName(), "war",
                new SimpleDateFormat("YY-MM-dd-HH").format(new Date()));
        Long lastVersion = config.getLastVersion();
        Long startVersion = config.getStartVersion();
        fileCountMap.put("project", new HashMap<>(10));
        addNewJar(startVersion, lastVersion);
        packageModule(warDir, startVersion, lastVersion);
        ModuleRecordDto moduleRecordDto = recordModule(startVersion, lastVersion);
        File zip = ZipUtil.zip(warDir);
        RecordDto recordDto = new RecordDto();
        recordDto.setProjectName(config.getProjectName());
        recordDto.setWarPath(zip.getAbsolutePath());
        recordDto.setRecord(moduleRecordDto);
        return recordDto;
    }

    private void packageModule(String warDir, Long startVersion, Long lastVersion) throws Exception {
        String warList = config.getWarList();
        String[] wars = warList.split(",");
        for (String war : wars) {
            commonPackage(warDir, war, startVersion, lastVersion);
        }
    }

    private void addNewJar(Long startVersion, Long lastVersion) throws Exception {
        String selfLibList = config.getSelfLibList();
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
        String url = String.format(config.getSvnUrl(), moduleName);
        String exeHome = String.format(config.getTargetPath(), moduleName, moduleName);
        List<SVNVersion> chageList = new ArrayList<>();
        chageList.add(new SVNVersion(exeHome, url, config.getUsername(), config.getPassword(), startVersion,
                lastVersion));
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
        String url = String.format(config.getSvnUrl(), moduleName);
        String exeHome = String.format(config.getTargetPath(), moduleName, moduleName);
        SVNVersion svnVersion = new SVNVersion(exeHome, url, config.getUsername(), config.getPassword(), startVersion,
                lastVersion);
        if (svnVersion.getWorker().judgeVersion()) {
            String jar = String.format("%s-%s.jar", moduleName, config.getProjectVersion());
            libList.add(jar);
            SysLog.log("添加jar包--", jar);
        }
        String outLibList = config.getOutLibList();
        if (StringUtils.hasText(outLibList)) {
            String[] outLib = outLibList.split(",");
            libList.addAll(Arrays.asList(outLib));
        }
    }

    private ModuleRecordDto recordModule(Long recordVersion, Long lastVersion) throws IOException {
        ModuleRecordDto moduleRecordDto = new ModuleRecordDto();
        moduleRecordDto.setPackVersion(String.format("%s-%s", recordVersion, lastVersion));
        moduleRecordDto.setPackDate(String.format("  %s", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
        List<String> changeFileCountList = new ArrayList<>();
        for (String keySet : fileCountMap.keySet()) {
            changeFileCountList.add(resolveMap(keySet, fileCountMap.get(keySet)));
        }
        moduleRecordDto.setChangeFileCount(changeFileCountList);
        return moduleRecordDto;
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
