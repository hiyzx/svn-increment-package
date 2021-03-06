package com.zero.svn.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 变化集合
 * 
 * @author liubq
 * @since 2018年4月13日
 */
public class ChangeInfo {

    // 变化文件列表
    private List<String> changeFiles;

    // 删除文件目录
    private Set<String> delSet = new HashSet<>();

    public List<String> getChangeFiles() {
        return changeFiles;
    }

    public void setChangeFiles(List<String> changeFiles) {
        this.changeFiles = changeFiles;
    }

    public Set<String> getDelSet() {
        return delSet;
    }

    public void setDelSet(Set<String> delSet) {
        this.delSet = delSet;
    }

}
