package com.universal.recommender.controller;

import com.universal.recommender.constant.CommonConstant;
import com.universal.recommender.dto.MaeDto;
import com.universal.recommender.dto.RecommendParamDto;
import com.universal.recommender.dto.RecommendedDataDto;
import com.universal.recommender.enums.SVDAlgorithm;
import com.universal.recommender.model.DataFromFileEntity;
import com.universal.recommender.model.FileNameEntity;
import com.universal.recommender.service.DefaultDataService;
import com.universal.recommender.service.FileNameService;
import com.universal.recommender.service.RecommenderService;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RecommenderController {

    private final RecommenderService recommenderService;

    private final DefaultDataService defaultDataService;

    private final FileNameService fileNameService;

    public RecommenderController(final RecommenderService recommenderService,
                                 final DefaultDataService defaultDataService,
                                 FileNameService fileNameService) {
        this.recommenderService = recommenderService;
        this.defaultDataService = defaultDataService;
        this.fileNameService = fileNameService;
    }

    @GetMapping("recommender")
    public String showRecommendPage(Model model) {
        List<FileNameEntity> fileNames = fileNameService.getAllFileName();
        model.addAttribute("fileNames", fileNames.stream().map(FileNameEntity::getFileName).collect(Collectors.toList()));
        return "recommender";
    }

    @GetMapping("recommended-result")
    public String showRecommendedResultPage(Model model) {
        List<MaeDto> maeDtoList = recommenderService.getAllMaeValue();
        model.addAttribute("maeValues", maeDtoList);
        return "recommended-result";
    }

    @PostMapping("recommended-result-data")
    @ResponseBody
    public DataTablesOutput<RecommendedDataDto> loadRecommendedResultData(@Valid @RequestBody DataTablesInput input) {
        return recommenderService.getRecommendedData(input);
    }

    @PostMapping("recommend/upload-file")
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("svdAlgorithm") SVDAlgorithm svdAlgorithm) {
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(CommonConstant.UPLOADED_FOLDER_PATH + CommonConstant.DATA_SAMPLE_NAME);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recommenderService.recommend();
        recommenderService.calculateMae();
        return "redirect:/recommended-result";
    }

    @PostMapping("recommend/file")
    @ResponseBody
    public boolean singleFileUpload(@RequestBody RecommendParamDto recommendParamDto) {
        FileNameEntity fileNameEntity = fileNameService.getFileNameByName("ratings_Baby.csv");
        List<DataFromFileEntity> dataFromFileEntities = defaultDataService.getByFileName(fileNameEntity);
        StringBuilder stringBuilder = new StringBuilder();
        dataFromFileEntities.forEach(dataFromFileEntity -> {
            stringBuilder
                    .append(dataFromFileEntity.getUserId())
                    .append(CommonConstant.COMMA)
                    .append(dataFromFileEntity.getItemId())
                    .append(CommonConstant.COMMA)
                    .append(dataFromFileEntity.getRating())
                    .append(CommonConstant.SEPARATE_LINE);
        });
        byte[] bytes = stringBuilder.toString().getBytes();
        Path path = Paths.get(CommonConstant.UPLOADED_FOLDER_PATH + CommonConstant.DATA_SAMPLE_NAME);
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recommenderService.recommend();
        recommenderService.calculateMae();
        return true;
    }
}
