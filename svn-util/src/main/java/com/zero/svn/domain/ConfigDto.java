package com.zero.svn.domain;

import lombok.Data;

/**
 * @author yezhaoxing
 * @since 2019/02/22
 */
@Data
public class ConfigDto {

    private String projectName;// 项目名称

    private String projectDesc; // 项目描述

    private String svnUrl;// svn地址

    private String username;// svn用户名

    private String password;// svn密码

    private Long lastVersion;// 上一次打包的最后版本

    private Long startVersion;// 上一次打包的开始版本

    private String targetPath; // 目标目录(最新版的文件)

    private String projectVersion;// 项目版本

    private String warList;// 需要打包的模块

    private String selfLibList;// 自身的jar包

    private String outLibList;// 外部jar包

    private String savePath;// 增量包保存位置

    private String mavenHome; //maven安装位置

    private Boolean update = false;// 是否要更新本地代码
}
