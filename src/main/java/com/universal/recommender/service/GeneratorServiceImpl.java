package com.universal.recommender.service;

import com.imsweb.datagenerator.naaccr.NaaccrXmlDataGenerator;
import com.imsweb.layout.LayoutFactory;
import com.imsweb.naaccrxml.entity.Patient;
import com.universal.recommender.cache.DataGeneratedCache;
import com.universal.recommender.cache.DataGeneratedModel;
import com.universal.recommender.constant.CommonConstant;
import com.universal.recommender.constant.DataGeneratorConstant;
import com.universal.recommender.convert.DataGeneratedConverter;
import com.universal.recommender.convert.DataGeneratorConverter;
import com.universal.recommender.dto.DataGeneratedParamDto;
import com.universal.recommender.dto.GenerateParamDto;
import com.universal.recommender.dto.ParameterSettingDto;
import com.universal.recommender.model.DataFromFileEntity;
import com.universal.recommender.model.DataSampleEntity;
import com.universal.recommender.model.DistributionTypeEntity;
import com.universal.recommender.model.FileNameEntity;
import com.universal.recommender.repository.DefaultDataRepository;
import com.universal.recommender.repository.DistributionTypeRepository;
import com.universal.recommender.repository.FileNameRepository;
import com.universal.recommender.util.ItemUtil;
import com.universal.recommender.util.RandomUtil;
import com.universal.recommender.util.RatingUtil;
import com.universal.recommender.util.UserUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.Order;
import org.springframework.data.jpa.datatables.mapping.Search;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.universal.recommender.constant.DataGeneratorConstant.*;

@Service
public class GeneratorServiceImpl implements GeneratorService {

    private final ParameterSettingService parameterSettingService;

    private final DistributionTypeRepository distributionTypeRepository;

    private final FileNameRepository fileNameRepository;

    private final DefaultDataRepository defaultDataRepository;

    private final DataGeneratedCache dataGeneratedCache;

    public GeneratorServiceImpl(ParameterSettingService parameterSettingService,
                                DistributionTypeRepository distributionTypeRepository,
                                FileNameRepository fileNameRepository,
                                DefaultDataRepository defaultDataRepository,
                                DataGeneratedCache dataGeneratedCache) {
        this.parameterSettingService = parameterSettingService;
        this.distributionTypeRepository = distributionTypeRepository;
        this.fileNameRepository = fileNameRepository;
        this.defaultDataRepository = defaultDataRepository;
        this.dataGeneratedCache = dataGeneratedCache;
    }

    @Override
    public DataTablesOutput<DataGeneratedModel> getGeneratedData(DataTablesInput dataTablesInput) {
        DataTablesOutput<DataGeneratedModel> dataTablesOutput = new DataTablesOutput<>();
        ConcurrentMap<DataGeneratedParamDto, ConcurrentMap<String, DataGeneratedModel>> allDataGenerated =
                dataGeneratedCache.getAllDataGeneratedMap();
        ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap = new ConcurrentHashMap<>();
        allDataGenerated.forEach((k, v) -> {
            dataGeneratedMap.putAll(v);
        });
        if (dataGeneratedMap.isEmpty()) {
            return dataTablesOutput;
        }

        dataTablesOutput.setDraw(dataTablesInput.getDraw());
        dataTablesOutput.setRecordsTotal(dataGeneratedMap.size());

        Integer startIndex = dataTablesInput.getStart();
        Integer dataLength = dataTablesInput.getLength();

        // make order
        List<DataGeneratedModel> oderDataGeneratedList = new ArrayList<>();
        List<Order> orders = dataTablesInput.getOrder();
        if (!CollectionUtils.isEmpty(orders)) {
            Order order = orders.get(0);
            if (Objects.nonNull(order)) {
                oderDataGeneratedList = getDataByOrder(dataGeneratedMap, order);
            }
        } else {
            oderDataGeneratedList = dataGeneratedMap.values().stream()
                    .collect(Collectors.toList());
        }

        // filter by search
        Search search = dataTablesInput.getSearch();
        String searchValue = search.getValue();
        if (!Strings.isBlank(searchValue)) {
            oderDataGeneratedList = oderDataGeneratedList.stream().filter(dataGeneratedModel ->
                    dataGeneratedModel.getUserId().contains(searchValue) ||
                    dataGeneratedModel.getItemId().contains(searchValue) ||
                    dataGeneratedModel.getRating().contains(searchValue))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(oderDataGeneratedList)) {
            dataTablesOutput.setRecordsFiltered(0);
            dataTablesOutput.setData(Collections.emptyList());
            return dataTablesOutput;
        }

        dataTablesOutput.setRecordsFiltered(oderDataGeneratedList.size());

        // pagination
        List<DataGeneratedModel> pagingDataList = getPagingData(oderDataGeneratedList, startIndex, dataLength);
        dataTablesOutput.setData(pagingDataList);

        return dataTablesOutput;
    }

    private List<DataGeneratedModel> getPagingData(List<DataGeneratedModel> dataGeneratedList,
                                                   Integer startIndex, Integer dataLength) {
        List<DataGeneratedModel> pagingDataList = dataGeneratedList.stream().skip(startIndex)
                .limit(dataLength)
                .collect(Collectors.toList());

        return pagingDataList;
    }

    private List<DataGeneratedModel> getDataByOrder(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap,
                                                           Order order) {
        Integer columnIndex = order.getColumn();
        String direction = order.getDir();

        switch (columnIndex) {
            case 0: return orderByUser(dataGeneratedMap, direction);
            case 1: return orderByItem(dataGeneratedMap, direction);
            case 2: return orderByRating(dataGeneratedMap, direction);
        }
        return new ArrayList<>();
    }

    private List<DataGeneratedModel> orderByUser(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap,
                                                        String direction)  {
        if (CommonConstant.SORT_DIRECTION_ASC.equals(direction)) {
            return orderByUserAsc(dataGeneratedMap);
        } else {
            return orderByUserDesc(dataGeneratedMap);
        }
    }

    private List<DataGeneratedModel> orderByUserAsc(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap) {
        List<DataGeneratedModel> dataGeneratedList = new ArrayList<>();
        dataGeneratedMap.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getUserId()))
                .forEachOrdered(x -> dataGeneratedList.add(x.getValue()));
        return dataGeneratedList;
    }

    private List<DataGeneratedModel> orderByUserDesc(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap) {
        List<DataGeneratedModel> dataGeneratedList = new ArrayList<>();
        Comparator<DataGeneratedModel> stringComparator = Comparator.comparing(DataGeneratedModel::getUserId).reversed();
        dataGeneratedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(stringComparator))
                .forEachOrdered(x -> dataGeneratedList.add(x.getValue()));
        return dataGeneratedList;
    }

    private List<DataGeneratedModel> orderByItem(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap,
                                                        String direction)  {
        if (CommonConstant.SORT_DIRECTION_ASC.equals(direction)) {
            return orderByItemAsc(dataGeneratedMap);
        } else {
            return orderByItemDesc(dataGeneratedMap);
        }
    }

    private List<DataGeneratedModel> orderByItemAsc(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap) {
        List<DataGeneratedModel> dataGeneratedList = new ArrayList<>();
        dataGeneratedMap.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getItemId()))
                .forEachOrdered(x -> dataGeneratedList.add(x.getValue()));
        return dataGeneratedList;
    }

    private List<DataGeneratedModel> orderByItemDesc(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap) {
        List<DataGeneratedModel> dataGeneratedList = new ArrayList<>();
        Comparator<DataGeneratedModel> stringComparator = Comparator.comparing(DataGeneratedModel::getItemId).reversed();
        dataGeneratedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(stringComparator))
                .forEachOrdered(x -> dataGeneratedList.add(x.getValue()));
        return dataGeneratedList;
    }

    private List<DataGeneratedModel> orderByRating(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap,
                                                        String direction)  {
        if (CommonConstant.SORT_DIRECTION_ASC.equals(direction)) {
            return orderByRatingAsc(dataGeneratedMap);
        } else {
            return orderByRatingDesc(dataGeneratedMap);
        }
    }

    private List<DataGeneratedModel> orderByRatingAsc(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap) {
        List<DataGeneratedModel> dataGeneratedList = new ArrayList<>();
        dataGeneratedMap.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getRating()))
                .forEachOrdered(x -> dataGeneratedList.add(x.getValue()));
        return dataGeneratedList;
    }

    private List<DataGeneratedModel> orderByRatingDesc(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap) {
        List<DataGeneratedModel> dataGeneratedList = new ArrayList<>();
        Comparator<DataGeneratedModel> stringComparator = Comparator.comparing(DataGeneratedModel::getRating).reversed();
        dataGeneratedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(stringComparator))
                .forEachOrdered(x -> dataGeneratedList.add(x.getValue()));
        return dataGeneratedList;
    }

    @Override
    @Transactional
    public ConcurrentMap<String, DataGeneratedModel> generateDataSample(GenerateParamDto generateParamDto) {
        ConcurrentMap<DataGeneratedParamDto, ConcurrentMap<String, DataGeneratedModel>> allDataGeneratedMap =
                generateAllData(generateParamDto.getSelectedFileName());
        dataGeneratedCache.getAllDataGeneratedMap().clear();
        dataGeneratedCache.setAllDataGeneratedMap(allDataGeneratedMap);
        return allDataGeneratedMap.entrySet().iterator().next().getValue();
    }

    private void cacheGeneratedData(ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap) {
        dataGeneratedCache.getDataGeneratedMap().clear();
        dataGeneratedCache.setDataGeneratedMap(dataGeneratedMap);
    }

    private ConcurrentMap<String, DataGeneratedModel> generateDataUsingCustomizeParameter() {
        ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap = new ConcurrentHashMap<>();
        ParameterSettingDto parameterSetting = parameterSettingService.getParameterSetting();
        Integer userAmount = getUserAmount(parameterSetting);
        Integer itemAmount = getItemAmount(parameterSetting);
        Integer itemPerUserAmount = getItemPerUserAmount(parameterSetting);
        Double ratingSparsity = getRatingSparsity(parameterSetting);
        String distributionFileName = getDistributionFileName(parameterSetting.getSelectedDistribution());
        Boolean isUsingCommonUser = getUsingCommonUser(parameterSetting);
        String datasetFileName = getDatasetFileName(parameterSetting);
        Integer numberOfCommonUser = getNumberOfCommonUser(parameterSetting);

        if (isUsingCommonUser) {
            Integer commonUserAmount = getCommonUserAmount(userAmount, numberOfCommonUser);
            dataGeneratedMap.putAll(getDataSampleFromCommonUser(datasetFileName, commonUserAmount));
            userAmount = userAmount - commonUserAmount;
        }

        IntStream.range(1, userAmount).forEach(userIndex -> {
            String userId = UserUtil.generateUserId(userIndex);
            dataGeneratedMap.putAll(generateItem(userId, itemAmount, itemPerUserAmount, distributionFileName, ratingSparsity));
        });
        return dataGeneratedMap;
    }

    private ConcurrentMap<DataGeneratedParamDto, ConcurrentMap<String, DataGeneratedModel>> generateAllData(String fileName) {
        ConcurrentMap<DataGeneratedParamDto, ConcurrentMap<String, DataGeneratedModel>> totalDataGeneratedMap = new ConcurrentHashMap<>();
        AtomicLong count = new AtomicLong(1);
        PARAM_CONFIG_USER_AMOUNT_LIST.stream().forEach(userAmount -> {
            PARAM_CONFIG_ITEM_AMOUNT_LIST.stream().forEach(itemAmount -> {
                PARAM_CONFIG_COMMON_USER_AMOUNT_LIST.stream().forEach(commonUser -> {
                    PARAM_CONFIG_DISTRIBUTION_FILE_PATH_MAP.forEach((distributionName, distributionFilePath) -> {
                        totalDataGeneratedMap.put(
                                buildDataGeneratedParam("sample " + count.getAndIncrement(), userAmount, itemAmount, commonUser, distributionName),
                                generateData(userAmount, itemAmount, commonUser, distributionFilePath, fileName));
                    });
                });
            });
        });

        return totalDataGeneratedMap;
    }

    private DataGeneratedParamDto buildDataGeneratedParam(
            String fileName, Integer userAmount, Integer itemAmount, Integer commonUser, String distribution) {
        return DataGeneratedParamDto.builder()
                .fileName(fileName)
                .userAmount(userAmount)
                .itemAmount(itemAmount)
                .commonUser(commonUser)
                .distribution(distribution)
                .sparsity(PARAM_CONFIG_SPARSITY)
                .ratingScale(PARAM_CONFIG_RATING_SCALE)
                .build();
    }

    private ConcurrentMap<String, DataGeneratedModel> generateData(
            Integer userAmount, Integer itemAmount, Integer commonUser, String distributionFilePath, String datasetFileName) {

        ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap = new ConcurrentHashMap<>();

        Integer commonUserAmount = getCommonUserAmount(userAmount, commonUser);
        dataGeneratedMap.putAll(getDataSampleFromCommonUser(datasetFileName, commonUserAmount));
        userAmount = userAmount - commonUserAmount;

        IntStream.range(1, userAmount).forEach(userIndex -> {
            String userId = UserUtil.generateUserId(userIndex);
            dataGeneratedMap.putAll(generateItem(userId, itemAmount, PARAM_CONFIG_RATING_SCALE, distributionFilePath, PARAM_CONFIG_SPARSITY));
        });
        return dataGeneratedMap;
    }

    private Integer getUserAmount(ParameterSettingDto parameterSetting) {
        Integer userAmount = parameterSetting.getUserAmount();
        if (Objects.isNull(userAmount)) {
            return CommonConstant.DEFAULT_PATIENT_NUMBER;
        } else {
            return userAmount;
        }
    }

    private Integer getItemAmount(ParameterSettingDto parameterSetting) {
        Integer itemAmount = parameterSetting.getItemAmount();
        if (Objects.isNull(itemAmount)) {
            return CommonConstant.DEFAULT_ITEM_AMOUNT;
        } else {
            return itemAmount;
        }
    }

    private Integer getItemPerUserAmount(ParameterSettingDto parameterSetting) {
        Integer itemPerUserAmount = parameterSetting.getItemPerUserAmount();
        if (Objects.isNull(itemPerUserAmount)) {
            return CommonConstant.DEFAULT_ITEM_PER_USER_AMOUNT;
        } else {
            return itemPerUserAmount;
        }
    }

    private Double getRatingSparsity(ParameterSettingDto parameterSetting) {
        Double ratingSparsity = parameterSetting.getRatingSparsity();
        if (Objects.isNull(ratingSparsity)) {
            ratingSparsity = DataGeneratorConstant.NULL_RATING_PERCENT;
        }
        return (100 - ratingSparsity);
    }

    private String getDistributionFileName(String selectedDistribution) {
        DistributionTypeEntity distributionTypeEntity = distributionTypeRepository.findByName(selectedDistribution).orElse(null);
        if (Objects.nonNull(distributionTypeEntity)) {
            return distributionTypeEntity.getDescription();
        } else {
            return DataGeneratorConstant.RATING_FILE_NAME;
        }
    }

    private Boolean getUsingCommonUser(ParameterSettingDto parameterSetting) {
        Boolean isUsingCommonUser = parameterSetting.getIsUsingUserCommon();
        if (Objects.isNull(isUsingCommonUser)) {
            return false;
        } else {
            return isUsingCommonUser;
        }
    }

    private String getDatasetFileName(ParameterSettingDto parameterSettingDto) {
        String datasetFileName = parameterSettingDto.getSelectedFileName();
        if (Objects.isNull(datasetFileName)) {
            return DataGeneratorConstant.DEFAULT_DATASET_FILE_NAME;
        } else {
            return datasetFileName;
        }
    }

    private Integer getNumberOfCommonUser(ParameterSettingDto parameterSetting) {
        Integer numberOfCommonUser = parameterSetting.getNumberOfCommonUser();
        if (Objects.isNull(numberOfCommonUser)) {
            return DataGeneratorConstant.DEFAULT_NUMBER_OF_COMMON_USER;
        } else {
            return numberOfCommonUser;
        }
    }

    private ConcurrentMap<String, DataGeneratedModel> generateItem(String userId, int itemAmount, int itemPerUserAmount,
                                                String distribution, Double ratingSparsity) {
        ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap = new ConcurrentHashMap<>();
        IntStream.range(1, itemPerUserAmount).forEach(itemIndex -> {
            String itemId = ItemUtil.generateItemIdByRange(itemAmount);
            dataGeneratedMap.putAll(generateRating(userId, itemId, distribution, ratingSparsity));
        });
        return dataGeneratedMap;
    }

    private ConcurrentMap<String, DataGeneratedModel> generateRating(
            String userId, String itemId, String distribution, Double ratingSparsity) {
        String rating = RatingUtil.generateRatingWithNullValue(distribution, ratingSparsity);
        DataGeneratedModel dataGeneratedModel = DataGeneratedModel.builder()
                .userId(userId)
                .itemId(itemId)
                .rating(rating)
                .build();
        ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap = new ConcurrentHashMap<>();
        dataGeneratedMap.put(UUID.randomUUID().toString(), dataGeneratedModel);
        return dataGeneratedMap;
    }

    private ConcurrentMap<String, DataGeneratedModel> generateDataUsingDefaultData() {
        NaaccrXmlDataGenerator naaccrXmlDataGenerator = new NaaccrXmlDataGenerator(LayoutFactory.LAYOUT_ID_NAACCR_XML_18_ABSTRACT);
        ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap = new ConcurrentHashMap<>();
        IntStream.range(1, CommonConstant.DEFAULT_PATIENT_NUMBER).forEach(value -> {
            int tumorNumber = RandomUtil.randomTumorNumber();
            Patient patient = naaccrXmlDataGenerator.generatePatient(tumorNumber);
            ConcurrentMap<String, DataGeneratedModel> patientDataGeneratedMap = DataGeneratorConverter.patientToDataSample(patient);
            dataGeneratedMap.putAll(patientDataGeneratedMap);
        });
        return dataGeneratedMap;
    }

    private ConcurrentMap<String, DataGeneratedModel> getDataSampleFromCommonUser(String datasetFileName, Integer commonUserAmount) {
        Optional<FileNameEntity> fileNameEntityOptional = fileNameRepository.findFirstByFileName(datasetFileName);
        if (!fileNameEntityOptional.isPresent()) {
            return new ConcurrentHashMap<>();
        }

        List<DataFromFileEntity> dataFromFileEntities = defaultDataRepository.findAllByFileNameIdAndAmount(
                fileNameEntityOptional.get().getId(), commonUserAmount);
        return DataGeneratedConverter.convertToDataSampleEntities(dataFromFileEntities);
    }

    private Integer getCommonUserAmount(Integer userAmount, Integer numberOfCommonUser) {
        Double commonUserAmount = userAmount * (numberOfCommonUser * 1.0 / 100);
        return commonUserAmount.intValue();
    }

    private List<String> getCommonUsingItem(List<DataSampleEntity> dataSampleEntities) {
        if (CollectionUtils.isEmpty(dataSampleEntities)) {
            return Collections.emptyList();
        }
        return dataSampleEntities.stream().map(DataSampleEntity :: getItemId).distinct().collect(Collectors.toList());
    }

    private Double reCalculateRatingSparsity(Double ratingSparsity, Integer userAmount, Integer itemAmount, Integer numberOfCommonUser) {
        Long totalRatingCase = Long.valueOf(userAmount) * Long.valueOf(itemAmount);
        Integer commonUserAmount = getCommonUserAmount(userAmount, numberOfCommonUser);
        Double commonSparsity = (commonUserAmount * 1.0 / totalRatingCase) * 100;
        Double totalSparsity = ratingSparsity - commonSparsity;
        if (totalSparsity <= 0) {
            return 0d;
        }
        return totalSparsity;
    }

    @Override
    public ByteArrayInputStream exportDataSample() {
        ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap = dataGeneratedCache.getDataGeneratedMap();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        dataGeneratedMap.values().forEach(dataGeneratedModel -> writeDataToOutputStream(out, dataGeneratedModel));
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void writeDataToOutputStream(ByteArrayOutputStream out, DataGeneratedModel dataGeneratedModel) {
        try {
            out.write(dataGeneratedModel.getItemId().getBytes());
            out.write("\t".getBytes());
            out.write(dataGeneratedModel.getUserId().getBytes());
            out.write("\t".getBytes());
            out.write(dataGeneratedModel.getRating().getBytes());
            out.write("\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
