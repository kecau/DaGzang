package com.universal.recommender.controller;

import com.universal.recommender.cache.DataGeneratedModel;
import com.universal.recommender.constant.CommonConstant;
import com.universal.recommender.convert.RatingStatisticConvert;
import com.universal.recommender.dto.GenerateParamDto;
import com.universal.recommender.dto.ParameterCustomDto;
import com.universal.recommender.dto.ParameterSettingDto;
import com.universal.recommender.service.GeneratorService;
import com.universal.recommender.service.ParameterService;
import com.universal.recommender.service.ParameterSettingService;
import com.universal.recommender.service.StatisticService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.util.concurrent.ConcurrentMap;

@Controller
public class GeneratorController {

    private final GeneratorService generatorService;

    private final ParameterService parameterService;

    private final StatisticService statisticService;

    private final ParameterSettingService parameterSettingService;

    public GeneratorController(final GeneratorService generatorService,
                               final ParameterService parameterService,
                               final StatisticService statisticService,
                               final ParameterSettingService parameterSettingService) {
        this.generatorService = generatorService;
        this.parameterService = parameterService;
        this.statisticService = statisticService;
        this.parameterSettingService = parameterSettingService;
    }

    @GetMapping("generator-page")
    public String showGeneratorPage(Model model) {
        ParameterSettingDto parameterCustom = parameterSettingService.getParameterSetting();
        model.addAttribute("parameterCustom", parameterCustom);
        return "generator";
    }

    @GetMapping("generated-output")
    public String showGeneratedOutPut() {
        return "generated-output";
    }

    @PostMapping("generated-result")
    @ResponseBody
    public DataTablesOutput<DataGeneratedModel> getGeneratedResult(@Valid @RequestBody DataTablesInput input) {
        return generatorService.getGeneratedData(input);
    }

    @PostMapping("generate")
    @ResponseBody
    public boolean generateData(@RequestBody GenerateParamDto generateParamDto) {
        ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap =
                generatorService.generateDataSample(generateParamDto);
        statisticService.saveAllRatingStatistic(RatingStatisticConvert.fromGeneratedDataToRatingStatistic(dataGeneratedMap));
        return true;
    }

    @GetMapping("parameter-config")
    public String parameterConfig(Model model) {
        ParameterSettingDto parameterCustom = parameterSettingService.getParameterSetting();
        model.addAttribute("parameterCustom", parameterCustom);
        return "parameter-config";
    }

    @PostMapping("parameter-config/save")
    @ResponseBody
    public Boolean saveParameterConfig(@RequestBody ParameterSettingDto parameterSetting) {
        try {
            parameterSettingService.saveParameterSetting(parameterSetting);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("parameter-config")
    @ResponseBody
    public Boolean customData(@RequestBody ParameterCustomDto parameterCustom) {
        try {
            parameterService.saveFile(parameterCustom);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("export")
    @ResponseBody
    public ResponseEntity<InputStreamResource> exportDataSample() {
        ByteArrayInputStream in = generatorService.exportDataSample();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + CommonConstant.DATA_SAMPLE_NAME);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}
