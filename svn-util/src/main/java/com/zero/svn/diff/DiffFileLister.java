package com.zero.svn.diff;

import com.zero.svn.domain.ChangeVO;
import com.zero.svn.version.SVNVersion;

import java.util.ArrayList;
import java.util.List;

/**
 * 变更文件列出工具
 * 
 * @author liubq
 * @since 2017年12月19日
 */
public class DiffFileLister {

    // 项目目录
    private List<SVNVersion> changeList;

    /**
     * 构造方法
     */
    public DiffFileLister(List<SVNVersion> list) throws Exception {
        if (list == null || list.size() <= 0) {
            throw new Exception("参数不正确，异常结束");
        }
        changeList = new ArrayList<>();
        changeList.addAll(list);
    }

    /**
     * 取得差异文件
     */
    public List<ChangeVO> listChange() throws Exception {
        List<ChangeVO> cList = new ArrayList<>();
        ChangeVO vo;
        for (SVNVersion version : changeList) {
            vo = version.get();
            if (vo != null && vo.getInfo() != null) {
                cList.add(version.get());
            }
        }
        return cList;
    }
}
