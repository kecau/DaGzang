package com.universal.recommender.controller;

import com.universal.recommender.service.DefaultDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DefaultDataController {

    private final DefaultDataService defaultDataService;

    public DefaultDataController(final DefaultDataService defaultDataService) {
        this.defaultDataService = defaultDataService;
    }

    @PostMapping("default-data/upload")
    public String uploadDefaultData(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        defaultDataService.saveDefaultData(file);
        redirectAttributes.addAttribute("defaultFile", file.getOriginalFilename());
        return "redirect:/default-data-page";
    }

    @GetMapping("default-data-page")
    public String showDefaultDataPage() {
        return "import-data-from-file";
    }

    @GetMapping("default-data")
    public String getDefaultData(Model model) {
        List<String> fileNames = defaultDataService.getAllFileNames();
        model.addAttribute("fileNames", fileNames);
        return "default-data";
    }

    @GetMapping("default-data/file")
    public String getDefaultDataWithFile(Model model, @RequestParam("defaultFile") String fileName) {
        /*List<DataFromFileEntity> dataFromFileEntities = defaultDataService.getByFileName(fileName);
        model.addAttribute("defaultDataList", dataFromFileEntities);
        List<String> fileNames = defaultDataService.getAllFileNames();
        model.addAttribute("fileNames", fileNames);*/
        return "default-data";
    }

}
