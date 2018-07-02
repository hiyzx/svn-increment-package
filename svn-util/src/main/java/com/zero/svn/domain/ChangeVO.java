package com.zero.svn.domain;

import com.zero.svn.version.SVNVersion;
import lombok.Data;

/**
 * 变化目录
 * 
 * @author liubq
 * @since 2018年4月13日
 */
@Data
public class ChangeVO {

    // 变化版本信息
    private SVNVersion version;

    // 变化文件信息
    private ChangeInfo info;
}
