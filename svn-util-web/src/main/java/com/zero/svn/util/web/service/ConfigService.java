package com.zero.svn.util.web.service;

import com.zero.svn.util.web.dto.ConfigDto;
import com.zero.svn.util.web.model.po.Config;
import com.zero.svn.util.web.dao.ConfigRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yezhaoxing
 * @since 2019/02/22
 */
@Service
public class ConfigService {

    @Resource
    private ConfigRepository configRepository;

    public Boolean saveOrUpdate(ConfigDto configDto) {
        Config configDb = configRepository.findByProjectName(configDto.getProjectName());
        Boolean isFirst;
        if (configDb == null) {
            configDb = new Config();
            BeanUtils.copyProperties(configDto, configDb);
            configRepository.save(configDb);
            isFirst = true;
        } else {
            BeanUtils.copyProperties(configDto, configDb);
            configRepository.save(configDb);
            isFirst = false;
        }
        return isFirst;
    }
}
