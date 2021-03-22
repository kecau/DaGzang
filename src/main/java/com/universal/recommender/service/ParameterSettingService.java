package com.universal.recommender.service;

import com.universal.recommender.dto.ParameterSettingDto;

public interface ParameterSettingService {

    ParameterSettingDto getParameterSetting();

    ParameterSettingDto saveParameterSetting(ParameterSettingDto parameterSetting);
}
