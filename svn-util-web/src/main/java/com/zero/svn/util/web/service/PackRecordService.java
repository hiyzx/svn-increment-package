package com.zero.svn.util.web.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.zero.svn.util.web.dao.PackRecordRepository;
import com.zero.svn.util.web.dto.RecordDto;
import com.zero.svn.util.web.model.po.PackRecord;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yezhaoxing
 * @since 2019/02/22
 */
@Service
public class PackRecordService {

    @Resource
    private PackRecordRepository packRecordRepository;

    public void save(RecordDto recordDto){
        PackRecord packRecord = new PackRecord();
        packRecord.setProjectName(recordDto.getProjectName());
        packRecord.setWarPath(recordDto.getWarPath());
        packRecord.setRecord(JSONUtil.toJsonPrettyStr(recordDto.getRecord()));
        packRecord.setCreateTime(DateUtil.date());
        packRecordRepository.save(packRecord);
    }
}
