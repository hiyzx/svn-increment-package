package com.zero.svn.util.web.dto;

import lombok.Data;

/**
 * @author yezhaoxing
 * @since 2019/02/22
 */
@Data
public class RecordDto {

    private String projectName; // 项目名称

    private String warPath;// 打包完zip地址

    private ModuleRecordDto record;// 记录
}
