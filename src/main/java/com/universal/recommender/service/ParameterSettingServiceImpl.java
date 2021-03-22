package com.universal.recommender.service;

import com.universal.recommender.dto.ParameterSettingDto;
import com.universal.recommender.enums.ParameterSetting;
import com.universal.recommender.model.DistributionTypeEntity;
import com.universal.recommender.model.FileNameEntity;
import com.universal.recommender.model.ParameterSettingEntity;
import com.universal.recommender.repository.DistributionTypeRepository;
import com.universal.recommender.repository.FileNameRepository;
import com.universal.recommender.repository.ParameterSettingRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParameterSettingServiceImpl implements ParameterSettingService {

    private final ParameterSettingRepository parameterSettingRepository;

    private final DistributionTypeRepository distributionTypeRepository;

    private final FileNameRepository fileNameRepository;

    public ParameterSettingServiceImpl(ParameterSettingRepository parameterSettingRepository,
                                       DistributionTypeRepository distributionTypeRepository,
                                       FileNameRepository fileNameRepository) {
        this.parameterSettingRepository = parameterSettingRepository;
        this.distributionTypeRepository = distributionTypeRepository;
        this.fileNameRepository = fileNameRepository;
    }

    @Override
    @Transactional
    public ParameterSettingDto getParameterSetting() {
        List<ParameterSettingEntity> parameterSettingEntities = parameterSettingRepository.findAll();
        ParameterSettingDto parameterSettingDto = convertToDto(parameterSettingEntities);
        parameterSettingDto.setDistributions(getDistribution());
        parameterSettingDto.setFileNameList(getFileNames());
        return parameterSettingDto;
    }

    private ParameterSettingDto convertToDto(List<ParameterSettingEntity> parameterSettingEntities) {
        ParameterSettingDto parameterSettingDto = ParameterSettingDto.builder()
                .distributions(new ArrayList<>())
                .build();
        parameterSettingEntities.forEach(parameterSettingEntity ->
                setParamFromEntityParameterKey(parameterSettingDto, parameterSettingEntity));
        return parameterSettingDto;
    }

    private void setParamFromEntityParameterKey(ParameterSettingDto parameterSettingDto, ParameterSettingEntity parameterSettingEntity) {
        switch (parameterSettingEntity.getParameterKey()) {
            case USER_AMOUNT: parameterSettingDto.setUserAmount(Integer.valueOf(parameterSettingEntity.getParameterValue()));
                break;
            case ITEM_AMOUNT: parameterSettingDto.setItemAmount(Integer.valueOf(parameterSettingEntity.getParameterValue()));
                break;
            case ITEM_PER_USER_AMOUNT: parameterSettingDto.setItemPerUserAmount(Integer.valueOf(parameterSettingEntity.getParameterValue()));
                break;
            case RATING_DISTRIBUTION: parameterSettingDto.setSelectedDistribution(parameterSettingEntity.getParameterValue());
                break;
            case RATING_SPARSITY: parameterSettingDto.setRatingSparsity(Double.valueOf(parameterSettingEntity.getParameterValue()));
                break;
            case USING_COMMON_USER: parameterSettingDto.setIsUsingUserCommon(Boolean.valueOf(parameterSettingEntity.getParameterValue()));
                break;
            case DATA_SET: parameterSettingDto.setSelectedFileName((parameterSettingEntity.getParameterValue()));
                break;
            case NUMBER_OF_COMMON_USER: parameterSettingDto.setNumberOfCommonUser(Integer.valueOf(parameterSettingEntity.getParameterValue()));
                break;

        }
    }

    @Override
    @Transactional
    public ParameterSettingDto saveParameterSetting(ParameterSettingDto parameterSetting) {
        List<ParameterSettingEntity> parameterSettingEntities = convertToEntity(parameterSetting);
        parameterSettingRepository.deleteAllInBatch();
        List<ParameterSettingEntity> savedParameterSettingEntities = parameterSettingRepository.saveAll(parameterSettingEntities);
        ParameterSettingDto savedParameterSettingDto = convertToDto(savedParameterSettingEntities);
        savedParameterSettingDto.setDistributions(getDistribution());
        return savedParameterSettingDto;
    }

    private List<ParameterSettingEntity> convertToEntity(ParameterSettingDto parameterSettingDto) {
        ParameterSettingEntity userAmountParameter = ParameterSettingEntity.builder()
                .parameterKey(ParameterSetting.USER_AMOUNT)
                .parameterValue(String.valueOf(parameterSettingDto.getUserAmount()))
                .build();
        ParameterSettingEntity itemAmountParameter = ParameterSettingEntity.builder()
                .parameterKey(ParameterSetting.ITEM_AMOUNT)
                .parameterValue(String.valueOf(parameterSettingDto.getItemAmount()))
                .build();
        ParameterSettingEntity itemPerUserAmountParameter = ParameterSettingEntity.builder()
                .parameterKey(ParameterSetting.ITEM_PER_USER_AMOUNT)
                .parameterValue(String.valueOf(parameterSettingDto.getItemPerUserAmount()))
                .build();
        ParameterSettingEntity distributionParameter = ParameterSettingEntity.builder()
                .parameterKey(ParameterSetting.RATING_DISTRIBUTION)
                .parameterValue(String.valueOf(parameterSettingDto.getSelectedDistribution()))
                .build();
        ParameterSettingEntity ratingSparsityParameter = ParameterSettingEntity.builder()
                .parameterKey(ParameterSetting.RATING_SPARSITY)
                .parameterValue(String.valueOf(parameterSettingDto.getRatingSparsity()))
                .build();
        ParameterSettingEntity isUsingUserCommonParameter = ParameterSettingEntity.builder()
                .parameterKey(ParameterSetting.USING_COMMON_USER)
                .parameterValue(String.valueOf(parameterSettingDto.getIsUsingUserCommon()))
                .build();
        ParameterSettingEntity fileNameParameter = ParameterSettingEntity.builder()
                .parameterKey(ParameterSetting.DATA_SET)
                .parameterValue(String.valueOf(parameterSettingDto.getSelectedFileName()))
                .build();
        ParameterSettingEntity numberOfCommonUserParameter = ParameterSettingEntity.builder()
                .parameterKey(ParameterSetting.NUMBER_OF_COMMON_USER)
                .parameterValue(String.valueOf(parameterSettingDto.getNumberOfCommonUser()))
                .build();
        return Arrays.asList(userAmountParameter, itemAmountParameter, itemPerUserAmountParameter, distributionParameter,
                ratingSparsityParameter, isUsingUserCommonParameter, fileNameParameter, numberOfCommonUserParameter);
    }

    private List<String> getDistribution() {
        return distributionTypeRepository.findAll().stream().map(DistributionTypeEntity::getName).collect(Collectors.toList());
    }

    private List<String> getFileNames() {
        return fileNameRepository.findAll().stream().map(FileNameEntity::getFileName).collect(Collectors.toList());
    }
}
