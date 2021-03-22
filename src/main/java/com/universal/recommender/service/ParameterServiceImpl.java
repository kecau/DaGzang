package com.universal.recommender.service;

import com.universal.recommender.constant.CommonConstant;
import com.universal.recommender.dto.FrequencyDataDto;
import com.universal.recommender.dto.ParameterCustomDto;
import com.universal.recommender.dto.ParameterSettingDto;
import com.universal.recommender.model.ParameterSettingEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ParameterServiceImpl implements ParameterService {

    @Override
    public void saveFile(ParameterCustomDto parameterCustom) {
        List<FrequencyDataDto> itemDataList = parameterCustom.getItem();
        saveItemFile(itemDataList);
        List<FrequencyDataDto> userDataList = parameterCustom.getUser();
        saveUserFile(userDataList);
        List<FrequencyDataDto> ratingDataList = parameterCustom.getRating();
        saveRatingFile(ratingDataList);
    }

    @Override
    @Transactional
    public ParameterSettingDto getParameterSetting() {

        return null;
    }

    @Override
    public void saveParameterSetting(ParameterSettingDto parameterSetting) {
        ParameterSettingEntity parameterSettingEntity = ParameterSettingEntity.builder().build();
    }

    @Override
    public ParameterCustomDto getParameterCustom() {
        List<FrequencyDataDto> itemDataList = getItemDataFromFile();
        List<FrequencyDataDto> userDataList = getUserDataFromFile();
        List<FrequencyDataDto> ratingDataList = getRatingDataFromFile();
        return ParameterCustomDto.builder()
                .item(itemDataList)
                .user(userDataList)
                .rating(ratingDataList)
                .build();
    }

    private List<FrequencyDataDto> getItemDataFromFile() {
        String itemFilePath = getFilePath(CommonConstant.ITEM_FILE_NAME);
        return getFrequencyDataFromFile(itemFilePath);
    }

    private List<FrequencyDataDto> getUserDataFromFile() {
        String userFilePath = getFilePath(CommonConstant.USER_FILE_NAME);
        return getFrequencyDataFromFile(userFilePath);
    }

    private List<FrequencyDataDto> getRatingDataFromFile() {
        String ratingFilePath = getFilePath(CommonConstant.RATING_FILE_NAME);
        return getFrequencyDataFromFile(ratingFilePath);
    }

    private List<FrequencyDataDto> getFrequencyDataFromFile(String filePath) {
        List<FrequencyDataDto> frequencyDataDtoList = new ArrayList<>();
        try {
            URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileUrl.openStream()));
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] parameters = StringUtils.split(line, CommonConstant.COMMA);
                frequencyDataDtoList.add(FrequencyDataDto.builder()
                        .frequency(Double.valueOf(parameters[0]))
                        .value(parameters[1])
                        .build());
            }
            return frequencyDataDtoList;

        }catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private void saveItemFile(List<FrequencyDataDto> itemDataList) {
        String itemFilePath = getFilePath(CommonConstant.ITEM_FILE_NAME);
        saveFrequencyFile(itemDataList, itemFilePath);
    }

    private void saveUserFile(List<FrequencyDataDto> userDataList) {
        String userFilePath = getFilePath(CommonConstant.USER_FILE_NAME);
        saveFrequencyFile(userDataList, userFilePath);
    }

    private void saveRatingFile(List<FrequencyDataDto> ratingDataList) {
        String ratingFilePath = getFilePath(CommonConstant.RATING_FILE_NAME);
        saveFrequencyFile(ratingDataList, ratingFilePath);
    }

    private String getFilePath(String fileName) {
        return CommonConstant.CUSTOM_PARAMETER_FILE_PATH + fileName;
    }

    private void saveFrequencyFile(List<FrequencyDataDto> frequencyDataList, String filePath) {
        try {
            File file = new File(ClassLoader.getSystemResource(filePath).toURI());
            String dataFile = buildFileData(frequencyDataList);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(dataFile.getBytes());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private String buildFileData(List<FrequencyDataDto> frequencyDataList) {
        if (CollectionUtils.isEmpty(frequencyDataList)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        frequencyDataList.forEach(frequencyData -> {
            stringBuilder.append(frequencyData.getFrequency());
            stringBuilder.append(",");
            stringBuilder.append(frequencyData.getValue());
            stringBuilder.append("\n");
        });
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
