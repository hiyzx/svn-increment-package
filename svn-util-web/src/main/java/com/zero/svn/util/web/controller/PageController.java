package com.zero.svn.util.web.controller;

import com.zero.svn.util.web.dao.ConfigRepository;
import com.zero.svn.util.web.model.po.Config;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yezhaoxing
 * @since 2019/02/22
 */
@Controller
public class PageController {

    @Resource
    private ConfigRepository configRepository;

    @RequestMapping("/")
    public String index(ModelMap map) {
        // 查询最近一次打包的配置
        List<Config> configs = configRepository.findAll();
        if (configs.isEmpty()) {// 未配置过,直接进入配置打包页
            return "index";
        } else { // 进入选择页
            map.addAttribute("configs", configs);
            return "select";
        }
    }

    @RequestMapping("/index")
    public String pack(ModelMap map) {
        // 查询最近一次打包的配置
        String projectName = (String) map.get("projectName");
        Config config = configRepository.findByProjectName(projectName);
        map.addAttribute("config", config);
        return "index";
    }
}
