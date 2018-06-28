package com.zero.svn.domain;

import com.zero.svn.version.SVNVersion;

/**
 * 变化目录
 * 
 * @author liubq
 * @since 2018年4月13日
 */
public class ChangeVO {

    // 变化版本信息
    private SVNVersion version;

    // 变化文件信息
    private ChangeInfo info;

    public SVNVersion getVersion() {
        return version;
    }

    public void setVersion(SVNVersion version) {
        this.version = version;
    }

    public ChangeInfo getInfo() {
        return info;
    }

    public void setInfo(ChangeInfo info) {
        this.info = info;
    }

}
