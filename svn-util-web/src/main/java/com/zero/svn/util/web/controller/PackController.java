package com.zero.svn.util.web.controller;

import com.zero.svn.util.web.util.ConfigDto;
import com.zero.svn.util.web.util.PackUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author yezhaoxing
 * @since 2019/02/21
 */
@Controller
public class PackController {

    @RequestMapping("/")
    public String index(ModelMap map) {
        // 查询最近一次打包的配置
        map.put("", "");
        return "index";
    }

    @PostMapping("/pack")
    @ResponseBody
    public String pack(HttpServletRequest request, @RequestBody ConfigDto configDto) throws Exception {
        return PackUtil.of(configDto).packer();
    }

    @GetMapping("/download")
    @ResponseBody
    public void download(HttpServletResponse response, @RequestParam String warDir) throws Exception {
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
}
