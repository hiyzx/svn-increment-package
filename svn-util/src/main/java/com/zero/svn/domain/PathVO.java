package com.zero.svn.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 变化文件
 * 
 * @author liubq
 * @since 2018年1月4日
 */
@Data
@AllArgsConstructor
public class PathVO {

    private String srcPath;

    private String targetPath;
}
