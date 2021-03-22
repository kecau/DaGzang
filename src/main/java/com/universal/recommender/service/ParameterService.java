package com.universal.recommender.service;

import com.universal.recommender.dto.ParameterCustomDto;
import com.universal.recommender.dto.ParameterSettingDto;

public interface ParameterService {

    void saveFile(ParameterCustomDto parameterCustomDto);

    ParameterSettingDto getParameterSetting();

    void saveParameterSetting(ParameterSettingDto parameterSetting);

    ParameterCustomDto getParameterCustom();
}
