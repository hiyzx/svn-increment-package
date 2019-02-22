package com.zero.svn.util.web.dto;

import lombok.Data;

import java.util.List;

/**
 * @author yezhaoxing
 * @since 2019/02/22
 */
@Data
public class ModuleRecordDto {

    private String packVersion; // 打包的版本 -

    private String packDate;// 日期

    private List<String> changeFileCount;// 变化文件列表
}
