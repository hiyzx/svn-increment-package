package com.zero.svn.util.web.model.po;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author yezhaoxing
 * @since 2019/02/22
 */
@Data
@Entity
public class PackRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "war_path")
    private String warPath;

    @Column(name = "record")
    private String record;

    @Column(name = "create_time")
    private Date createTime;
}
