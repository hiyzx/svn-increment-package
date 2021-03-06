package com.zero.svn.util.web.controller;

import com.zero.svn.util.web.dto.ConfigDto;
import com.zero.svn.util.web.dto.RecordDto;
import com.zero.svn.util.web.service.ConfigService;
import com.zero.svn.util.web.service.PackRecordService;
import com.zero.svn.util.web.util.CmdUtil;
import com.zero.svn.util.web.util.PackUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author yezhaoxing
 * @since 2019/02/21
 */
@RestController
public class PackController {

    @Resource
    private ConfigService configService;
    @Resource
    private PackRecordService packRecordService;


    @PostMapping("/pack")
    public RecordDto pack(@RequestBody ConfigDto config) throws Exception {
        Boolean isFirst = configService.saveOrUpdate(config);
        install(isFirst, config);
        config.setTargetPath(config.getTargetPath()+ "/%s/target/%s");
        RecordDto recordDto = PackUtil.of(config).packer();
        packRecordService.save(recordDto);
        return recordDto;
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response, @RequestParam String warDir) {
        File file = new File(warDir);
        try (OutputStream os = response.getOutputStream(); InputStream is = new FileInputStream(file)) {
            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Content-Disposition", String.format("attachment; filename=%s",
                    new String((file.getName()).getBytes("GB2312"), "iso8859-1")));
            byte[] content = new byte[1024];
            int length = 0;
            while ((length = is.read(content)) != -1) {
                os.write(content, 0, length);
            }
            os.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void install(boolean isFirst, ConfigDto config) {
        if (isFirst) {
            File targetPath = new File(config.getTargetPath());
            if (!targetPath.exists()) {
                targetPath.mkdirs();
            }
            CmdUtil.callCMD(String.format("cd %s", config.getTargetPath()));
            CmdUtil.callCMD(String.format("svn checkout %s", config.getSvnUrl()));
            CmdUtil.callCMD("mvn clean install -Dmaven.test.skip=true");
        } else {
            CmdUtil.callCMD(String.format("cd %s", config.getTargetPath()));
            CmdUtil.callCMD("svn update");
            CmdUtil.callCMD("mvn clean install -Dmaven.test.skip=true");
        }
    }
}
