package com.zero.svn.util.web.util;

import lombok.Data;

/**
 * @author yezhaoxing
 * @since 2019/02/21
 */
@Data
public class ConfigDto {

    private String projectName;

    private String svnUrl;

    private String username;

    private String password;

    private Long lastVersion;

    private Long startVersion;

    private String targetPath;

    private String projectVersion;

    private String warList;// 需要打包的模块

    private String selfLibList;// 自身的jar包

    private String outLibList;// 外部jar包

    private String savePath;// 增量包保存位置
}
