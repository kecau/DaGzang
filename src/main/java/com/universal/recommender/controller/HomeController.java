package com.universal.recommender.controller;

import com.universal.recommender.constant.CommonConstant;
import com.universal.recommender.util.ZipUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Controller
public class HomeController {

    @GetMapping()
    public String home() {
        return "home";
    }

    @GetMapping("source-code")
    public void getSourceCode(HttpServletResponse response
    ) {
        ZipUtils.zipSource();

        try {
            File sourceFile = new File(CommonConstant.ZIP_FILE_PATH);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + "DakgalbiSourceCode.zip" + "\"");
            response.setHeader("Content-Type", "application/zip");
            response.getOutputStream().write(FileUtils.readFileToByteArray(sourceFile));
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
