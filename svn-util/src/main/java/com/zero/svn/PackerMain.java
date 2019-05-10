package com.zero.svn;

import com.zero.svn.domain.ConfigDto;
import com.zero.svn.util.PackUtil;

/**
 * @author liubq
 * @since 2017年12月21日
 */
public class PackerMain {

    public static void main(String[] args) throws Exception {
        ConfigDto configDto = new ConfigDto();

        configDto.setLastVersion(30215L);// 最后版本号
        configDto.setStartVersion(30215L);// 开始打包版本号

        configDto.setProjectName("项目名称");
        configDto.setSvnUrl("http://svn地址");
        configDto.setUsername("用户名");
        configDto.setPassword("密码");
        configDto.setTargetPath("E:\\app\\");// 项目地址
        configDto.setProjectVersion("0.0.1-SNAPSHOT");
        configDto.setWarList("vz-bankorg-MYBANK");
        configDto.setSelfLibList("");// 项目内jar包-建议所有都加上,会通过版本号去判断是否要更新
        configDto.setSelfLibList("");// 项目新增jar吧
        configDto.setSavePath("C:/Users/Administrator/Desktop/war包/");// 保存路径
        configDto.setMavenHome("D:/apache-maven-3.2.5");// maven地址,打包使用
        configDto.setUpdate(true);// 是否要update-clean-install
        new PackUtil(configDto, true).packer();
    }
}
