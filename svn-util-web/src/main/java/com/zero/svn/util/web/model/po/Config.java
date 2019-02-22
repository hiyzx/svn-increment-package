package com.zero.svn.util.web.model.po;

import lombok.Data;

import javax.persistence.*;

/**
 * @author yezhaoxing
 * @since 2019/02/21
 */
@Data
@Entity
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_desc")
    private String projectDesc;

    @Column(name = "svn_url")
    private String svnUrl;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "last_version")
    private Long lastVersion;

    @Column(name = "start_version")
    private Long startVersion;

    @Column(name = "target_path")
    private String targetPath;

    @Column(name = "project_version")
    private String projectVersion;

    @Column(name = "war_list")
    private String warList;// 需要打包的模块

    @Column(name = "self_lib_list")
    private String selfLibList;// 自身的jar包

    @Column(name = "out_lib_list")
    private String outLibList;// 外部jar包

    @Column(name = "save_path")
    private String savePath;// 增量包保存位置
}
