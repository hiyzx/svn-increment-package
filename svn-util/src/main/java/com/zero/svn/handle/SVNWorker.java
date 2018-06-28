package com.zero.svn.handle;

import com.zero.svn.util.PathUtil;
import com.zero.svn.util.SysLog;
import com.zero.svn.domain.ChangeInfo;
import com.zero.svn.version.SVNVersion;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.util.*;

/**
 * SVN工具类
 * 
 * @author liubq
 * @since 2017年12月20日
 */
public class SVNWorker {

    // 版本信息
    private SVNVersion version;

    /**
     * SVN操作者
     */
    public SVNWorker(SVNVersion version) {
        this.version = version;
        DAVRepositoryFactory.setup();
    }

    /**
     * 根据版本号查询变化文件名列表
     */
    public ChangeInfo listAllChange() throws Exception {
        // 定义svn版本库的URL。
        SVNURL repositoryURL = null;
        // 定义版本库。
        SVNRepository repository = null;
        try {
            // 获取SVN的URL。
            repositoryURL = SVNURL.parseURIEncoded(version.getSvnUrl());
            // 根据URL实例化SVN版本库。
            repository = SVNRepositoryFactory.create(repositoryURL);
        } catch (Exception e) {
            SysLog.log("创建版本库实例时失败，版本库的URL是 '" + version.getSvnUrl() + "'", e);
            throw e;
        }
        // 对版本库设置认证信息。
        String user = version.getUsername();
        String pwd = version.getPassword();
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, pwd.toCharArray());
        repository.setAuthenticationManager(authManager);
        // 下载这个期间内所有变更文件
        try {
            long startRevision = version.getStartVersion();
            long endRevision = version.getEndVersion();
            if (startRevision < 0) {
                startRevision = 1;
            }
            if (endRevision < 0) {
                endRevision = repository.getLatestRevision();
            }
            Collection<SVNLogEntry> logEntries = repository.log(new String[] { "" }, null, startRevision, endRevision,
                    true, true);
            List<String> fileList = new ArrayList<>();
            Set<String> delSet = new HashSet<>();
            // 我测试这个是有顺序
            for (SVNLogEntry log : logEntries) {
                String key;
                for (Map.Entry<String, SVNLogEntryPath> entry : log.getChangedPaths().entrySet()) {
                    key = entry.getKey();
                    if (!entry.getValue().getKind().equals(SVNNodeKind.DIR)) {
                        // 排除文件跳过
                        if (isExclusive(key)) {
                            continue;
                        }
                        if ("D".equalsIgnoreCase(String.valueOf(entry.getValue().getType()))) {
                            fileList.remove(key);
                            // 删除了
                            delSet.add(key);

                        } else {
                            if (!fileList.contains(key)) {
                                fileList.add(key);
                            }
                            // 删除了又加回来了
                            if (delSet.contains(key)) {
                                SysLog.log(key + "删除后又回复了这个文件，请判断是否合理 ");
                                delSet.remove(key);
                            }
                        }
                    }
                }
            }
            ChangeInfo resVO = new ChangeInfo();
            resVO.setChangeFiles(fileList);
            resVO.setDelSet(delSet);
            return resVO;
        } catch (Exception e) {
            SysLog.log("下载这个期间内所有变更文件错误: ", e);
            throw e;
        }
    }

    /**
     * 判断从lastVersion是否有文件修改
     */
    public boolean judgeVersion(Long lastVersion) throws Exception {
        // 定义svn版本库的URL。
        SVNURL repositoryURL = null;
        // 定义版本库。
        SVNRepository repository = null;
        try {
            // 获取SVN的URL。
            repositoryURL = SVNURL.parseURIEncoded(version.getSvnUrl());
            // 根据URL实例化SVN版本库。
            repository = SVNRepositoryFactory.create(repositoryURL);
        } catch (Exception e) {
            SysLog.log("创建版本库实例时失败，版本库的URL是 '" + version.getSvnUrl() + "'", e);
            throw e;
        }
        // 对版本库设置认证信息。
        String user = version.getUsername();
        String pwd = version.getPassword();
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, pwd.toCharArray());
        repository.setAuthenticationManager(authManager);
        List<SVNLogEntry> entries = new ArrayList<>(100);
        long latestRevision = repository.getLatestRevision();
        while (entries.isEmpty() && lastVersion <= latestRevision) {
            repository.log(new String[] { "" }, entries, latestRevision, latestRevision, true, true);
            latestRevision--;
        }
        return !entries.isEmpty();
    }

    /**
     * 是否需要排除
     * 
     * 这样仍然排除不了所有目录 例如：abc/ddd/asd.d这个类型目录
     */
    private boolean isExclusive(String file) {
        // 空对象
        if (file == null) {
            return true;
        }
        // 标准文件路径
        String name = PathUtil.replace(file);
        // 特殊文件排除
        if (name.indexOf(version.getProjectName() + "/.") > 5) {
            return true;
        }
        // 指定目录排除
        for (String subPath : PathUtil.getExclusiveList()) {
            if (name.indexOf(version.getProjectName() + "/" + subPath) > 5) {
                return true;
            }
        }
        return false;
    }
}
