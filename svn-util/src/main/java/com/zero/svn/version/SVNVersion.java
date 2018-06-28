package com.zero.svn.version;

import com.zero.svn.util.PathUtil;
import com.zero.svn.domain.ChangeInfo;
import com.zero.svn.domain.ChangeVO;
import com.zero.svn.handle.SVNWorker;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SVN变化版本
 * 
 * @author liubq
 * @since 2018年1月16日
 */
@Data
public class SVNVersion{
	// svn地址
	private final String svnUrl;
	// 用户名称
	private final String username;
	// 用户密码
	private final String password;
	// 开始版本
	private final Long startVersion;
	// 结束版本
	private final Long endVersion;
	// 项目名称
	private final String projectName;
	// 导出工程名称
	private final String exportProjectName;
	// 工作者
	private final SVNWorker worker;
	// 可运行程序执行保存地址
	private String targetPath;

	/**
	 * SVN变化版本
	 * 
	 * @param target 可运行程序（编译后程序）保存地址
	 * @param inSvnUrl svn 地址
	 * @param name 用户名称
	 * @param pwd 用户密码
	 * @param startVersion 开始版本号
	 */
	public SVNVersion(String target, String inSvnUrl, String name, String pwd, Long startVersion) throws Exception {
		this(target, inSvnUrl, name, pwd, startVersion, null);
	}

	public SVNVersion(String target, String inSvnUrl, String name, String pwd, Long startVersion, String inExportProjectName) throws Exception {
		this.setTargetPath(target);
		this.svnUrl = inSvnUrl;
		String tempSvnUrl = PathUtil.replace(svnUrl);
		this.projectName = tempSvnUrl.substring(tempSvnUrl.lastIndexOf("/") + 1);
		if (inExportProjectName == null || inExportProjectName.trim().length() == 0) {
			exportProjectName = projectName;
		} else {
			exportProjectName = inExportProjectName.trim();
		}
		this.username = name;
		this.password = pwd;
		this.startVersion = startVersion;
		this.endVersion = -1L;
		worker = new SVNWorker(this);
	}

	/**
	 * 列出所有变化文件
	 */
	public ChangeVO get() throws Exception {
		// 变化的文件列表
		ChangeInfo svnInfo = worker.listAllChange();
		if (svnInfo == null) {
			return null;
		}
		ChangeVO resVO = new ChangeVO();
		resVO.setVersion(this);
		resVO.setInfo(new ChangeInfo());
		// SVN，地址转换为标准地址
		// 变化文件转换
		List<String> changeFiles = new ArrayList<String>();
		String tempFile;
		for (String svnFileName : svnInfo.getChangeFiles()) {
			tempFile = PathUtil.trimName(svnFileName, this.projectName);
			changeFiles.add(tempFile);
		}
		resVO.getInfo().setChangeFiles(changeFiles);
		// 删除文件转换
		Set<String> delSet = new HashSet<String>();
		for (String svnFileName : svnInfo.getDelSet()) {
			tempFile = PathUtil.trimName(svnFileName, this.projectName);
			delSet.add(tempFile);
		}
		resVO.getInfo().setDelSet(delSet);
		return resVO;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = PathUtil.replace(targetPath);
	}
}