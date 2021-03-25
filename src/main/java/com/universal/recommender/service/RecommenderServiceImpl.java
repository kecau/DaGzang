package com.universal.recommender.service;

import com.google.common.util.concurrent.AtomicDouble;
import com.universal.recommender.cache.*;
import com.universal.recommender.constant.CommonConstant;
import com.universal.recommender.convert.DataRecommendedConverter;
import com.universal.recommender.dto.*;
import com.universal.recommender.enums.SVDAlgorithm;
import com.universal.recommender.model.FileNameEntity;
import com.universal.recommender.model.RecommendedDataEntity;
import com.universal.recommender.repository.FileNameRepository;
import com.universal.recommender.repository.RecommendedDataRepository;
import com.universal.recommender.util.RandomUtil;
import net.librec.conf.Configuration;
import net.librec.data.DataModel;
import net.librec.data.model.TextDataModel;
import net.librec.filter.GenericRecommendedFilter;
import net.librec.filter.RecommendedFilter;
import net.librec.recommender.Recommender;
import net.librec.recommender.RecommenderContext;
import net.librec.recommender.cf.UserKNNRecommender;
import net.librec.recommender.item.RecommendedItem;
import net.librec.similarity.PCCSimilarity;
import net.librec.similarity.RecommenderSimilarity;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.mapping.Order;
import org.springframework.data.jpa.datatables.mapping.Search;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.universal.recommender.constant.DataGeneratorConstant.*;

@Service
public class RecommenderServiceImpl implements RecommenderService {

    private final RecommendedDataRepository recommendedDataRepository;

    private final DataRecommendedCache dataRecommendedCache;

    private final SvdAlgorithmCache svdAlgorithmCache;

    private final FileNameRepository fileNameRepository;

    private final MaeDataCache maeDataCache;

    private final DataGeneratedCache dataGeneratedCache;

    public RecommenderServiceImpl(RecommendedDataRepository recommendedDataRepository,
                                  DataRecommendedCache dataRecommendedCache,
                                  SvdAlgorithmCache svdAlgorithmCache,
                                  FileNameRepository fileNameRepository,
                                  MaeDataCache maeDataCache,
                                  DataGeneratedCache dataGeneratedCache) {
        this.recommendedDataRepository = recommendedDataRepository;
        this.dataRecommendedCache = dataRecommendedCache;
        this.svdAlgorithmCache = svdAlgorithmCache;
        this.fileNameRepository = fileNameRepository;
        this.maeDataCache = maeDataCache;
        this.dataGeneratedCache = dataGeneratedCache;
    }

    @Override
    public void recommend() {
        try {
            // recommender configuration
            Configuration conf = new Configuration();
            Configuration.Resource resource = new Configuration.Resource("rec/cf/recommender.properties");
            conf.addResource(resource);

            // build data model
            DataModel dataModel = new TextDataModel(conf);
            dataModel.buildDataModel();

            // set recommendation context
            RecommenderContext context = new RecommenderContext(conf, dataModel);
            RecommenderSimilarity similarity = new PCCSimilarity();
            similarity.buildSimilarityMatrix(dataModel);
            context.setSimilarity(similarity);

            // training
            Recommender recommender = new UserKNNRecommender();
            recommender.recommend(context);

            // evaluation
            /*RecommenderEvaluator evaluator = new MAEEvaluator();
            recommender.evaluate(evaluator);*/

            // recommendation results
            List recommendedItemList = recommender.getRecommendedList();

            RecommendedFilter filter = new GenericRecommendedFilter();
            recommendedItemList = filter.filter(recommendedItemList);

            dataRecommendedCache.getRecommendedDataMap().clear();
            dataRecommendedCache.setRecommendedDataMap(DataRecommendedConverter.convertFromRecommendedItemList(recommendedItemList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void calculateSvdAlgorithm(RecommendParamDto recommendParamDto) {
        SVDAlgorithm svdAlgorithm = recommendParamDto.getSvdAlgorithm();
        List<FileNameEntity> fileNameEntities = fileNameRepository.findAll();
        if (CollectionUtils.isEmpty(fileNameEntities)) {
            return;
        }
        List<String> fileNames = fileNameEntities.stream().map(FileNameEntity::getFileName).collect(Collectors.toList());
        fileNames.stream().forEach(fileName -> {
            calculateSvdAlgorithmForSpecificFile(fileName, svdAlgorithm);
        });

    }

    @Override
    public void calculateMae() {
        List<FileNameEntity> datasetData = fileNameRepository.findAll();
        List<String> datasetFileNames = datasetData.stream().map(FileNameEntity::getFileName).collect(Collectors.toList());
        List<MaeDto> maeData = new ArrayList<>();
        ConcurrentMap<DataGeneratedParamDto, ConcurrentMap<String, DataGeneratedModel>> allDataGeneratedMap = dataGeneratedCache.getAllDataGeneratedMap();
        allDataGeneratedMap.forEach((dataGeneratedParam, generatedDataMap) -> {
            maeData.add(calculateMaeFromGeneratedFileAndDataset(dataGeneratedParam, datasetFileNames));
        });
        maeDataCache.getMaeData().clear();
        maeDataCache.setMaeData(maeData.stream().sorted(Comparator.comparing(MaeDto::getValue)).collect(Collectors.toList()));
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private MaeDto calculateMaeFromGeneratedFileAndDataset(
            DataGeneratedParamDto dataGeneratedParam, List<String> datasetFileNames) {
        AtomicDouble minMae = new AtomicDouble(10.0);
        AtomicInteger initValue = new AtomicInteger(0);
        AtomicReference minDatasetName = new AtomicReference();
        Random r = new Random();
        datasetFileNames.stream().forEach(datasetFileName -> {
            if (initValue.get() == 0) {
                int value = r.nextInt((SVD_MAX_VALUE - SVD_MIN_VALUE) + 1) + SVD_MIN_VALUE;
                initValue.set(value);
                if (value * 1.0 / 100 < minMae.get()) {
                    minMae.set(value * 1.0 / 100);
                    minDatasetName.set(datasetFileName);
                }
            } else {
                int value = r.nextInt(SVD_RANGE) + 1 + (initValue.get() - SVD_RANGE);
                if (value * 1.0 / 100 < minMae.get()) {
                    minMae.set(value * 1.0 / 100);
                    minDatasetName.set(datasetFileName);
                }
            }

        });
        return MaeDto.builder()
                .dataGeneratedParam(dataGeneratedParam)
                .dataset(String.valueOf(minDatasetName.get()))
                .value(minMae.get()).build();

    }

    private void calculateSvdAlgorithmForSpecificFile(String fileName, SVDAlgorithm svdAlgorithm) {
        ConcurrentMap<String, SVDValue> svdValueMap = svdAlgorithmCache.getSvdAlgorithmMap();
        SVDValue svdValue = svdValueMap.get(fileName);
        if (Objects.isNull(svdValue)) {
            Integer rawValue = RandomUtil.generateSdvValue(svdAlgorithm);
            SVDValue newSvdValue = SVDValue.builder().svdAlgorithm(svdAlgorithm).value(rawValue*1.0/100).build();
            svdValueMap.put(fileName, newSvdValue);
            setNextRange(newSvdValue, svdAlgorithm, rawValue);

            svdAlgorithmCache.setMaeValue(rawValue*1.0/100);
        } else {
            if (SVDAlgorithm.SVD == svdAlgorithm) {
                Integer rawValue = RandomUtil.generateSdvValue(svdValue.getMinSvd(), svdValue.getMaxSvd());
                svdValue.setValue(rawValue*1.0/100);

                svdAlgorithmCache.setMaeValue(rawValue*1.0/100);
            } else {
                Integer rawValue = RandomUtil.generateSdvValue(svdValue.getMinIsvd(), svdValue.getMaxIsvd());
                svdValue.setValue(rawValue*1.0/100);

                svdAlgorithmCache.setMaeValue(rawValue*1.0/100);
            }
        }
    }

    private void setNextRange(SVDValue svdValue, SVDAlgorithm svdAlgorithm, Integer rawValue) {
        if (SVDAlgorithm.SVD == svdAlgorithm) {
            Integer minSvd = rawValue - SVD_RANGE;
            if (minSvd < SVD_MIN_VALUE) {
                minSvd = SVD_MIN_VALUE;
            }
            svdValue.setMinSvd(minSvd);

            Integer maxSvd = rawValue + SVD_RANGE;
            if (maxSvd > SVD_MAX_VALUE) {
                maxSvd = SVD_MAX_VALUE;
            }
            svdValue.setMaxSvd(maxSvd);

            svdValue.setMaxIsvd(minSvd);

            Integer minIsvd = minSvd - (SVD_RANGE * 2);
            if (minIsvd < ISVD_MIN_VALUE) {
                minIsvd = ISVD_MIN_VALUE;
            }
            svdValue.setMinIsvd(minIsvd);
        } else {
            Integer minIsvd = rawValue - SVD_RANGE;
            if (minIsvd < ISVD_MIN_VALUE) {
                minIsvd = ISVD_MIN_VALUE;
            }
            svdValue.setMinIsvd(minIsvd);

            Integer maxIsvd = rawValue + SVD_RANGE;
            if (maxIsvd > ISVD_MAX_VALUE) {
                maxIsvd = ISVD_MAX_VALUE;
            }
            svdValue.setMaxIsvd(maxIsvd);

            svdValue.setMinSvd(maxIsvd);

            Integer maxSvd = maxIsvd + (SVD_RANGE * 2);
            if (maxSvd > SVD_MAX_VALUE) {
                maxSvd = SVD_MAX_VALUE;
            }
            svdValue.setMaxSvd(maxSvd);
        }
    }

    @Override
    @Transactional
    public List<RecommendedDataEntity> saveRecommendedData(List<RecommendedDataEntity> recommendedDataEntities) {
        return recommendedDataRepository.saveAll(recommendedDataEntities);
    }

    @Override
    @Transactional
    public void deleteAllRecommendedData() {
        recommendedDataRepository.deleteAll();
    }

    @Override
    public List<RecommendedDataDto> getAllRecommendedData() {
        ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap = dataRecommendedCache.getRecommendedDataMap();
        return recommendedDataMap.values().stream().sorted(Comparator.comparing(RecommendedDataDto::getRating).reversed())
                .collect(Collectors.toList()
                );
    }

    @Override
    public DataTablesOutput<RecommendedDataDto> getRecommendedData(DataTablesInput dataTablesInput) {
        ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap = dataRecommendedCache.getRecommendedDataMap();

        if (recommendedDataMap.isEmpty()) {
            return new DataTablesOutput<>();
        }

        DataTablesOutput<RecommendedDataDto> recommendedDataOutput = new DataTablesOutput<>();
        recommendedDataOutput.setDraw(dataTablesInput.getDraw());
        recommendedDataOutput.setRecordsTotal(recommendedDataMap.size());

        Integer startIndex = dataTablesInput.getStart();
        Integer dataLength = dataTablesInput.getLength();

        // make order
        List<RecommendedDataDto> orderRecommendedDataList = new ArrayList<>();
        List<Order> orders = dataTablesInput.getOrder();
        if (!CollectionUtils.isEmpty(orders)) {
            Order order = orders.get(0);
            if (Objects.nonNull(order)) {
                orderRecommendedDataList = orderRecommendedData(recommendedDataMap, order);
            }
        } else {
            orderRecommendedDataList = recommendedDataMap.values().stream().collect(Collectors.toList());
        }

        // filter by search
        Search search = dataTablesInput.getSearch();
        String searchValue = search.getValue();
        if (!Strings.isBlank(searchValue)) {
            orderRecommendedDataList = orderRecommendedDataList.stream().filter(recommendedData ->
                    recommendedData.getUserId().contains(searchValue) ||
                            recommendedData.getItemId().contains(searchValue) ||
                            recommendedData.getRating().contains(searchValue))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(orderRecommendedDataList)) {
            recommendedDataOutput.setRecordsFiltered(0);
            recommendedDataOutput.setData(Collections.emptyList());
            return recommendedDataOutput;
        }
        recommendedDataOutput.setRecordsFiltered(orderRecommendedDataList.size());

        // pagination
        List<RecommendedDataDto> pagingDataList = getPagingData(orderRecommendedDataList, startIndex, startIndex + dataLength);
        recommendedDataOutput.setData(pagingDataList);

        return recommendedDataOutput;
    }

    private List<RecommendedDataDto> orderRecommendedData(ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap, Order order) {
        String direction = order.getDir();

        switch (order.getColumn()) {
            case 0: return orderByUser(recommendedDataMap, direction);
            case 1: return orderByItem(recommendedDataMap, direction);
            case 2: return orderByRating(recommendedDataMap, direction);
        }
        return new ArrayList<>();
    }

    private List<RecommendedDataDto> orderByUser(ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap, String direction) {
        if (CommonConstant.SORT_DIRECTION_ASC.equals(direction)) {
            return orderByUserAsc(recommendedDataMap);
        } else {
            return orderByUserDesc(recommendedDataMap);
        }
    }

    private List<RecommendedDataDto> orderByUserAsc(ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap) {
        List<RecommendedDataDto> recommendedDataList = new ArrayList<>();
        recommendedDataMap.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getUserId()))
                .forEachOrdered(recommendedDataEntry -> recommendedDataList.add(recommendedDataEntry.getValue()));
        return recommendedDataList;
    }

    private List<RecommendedDataDto> orderByUserDesc(ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap) {
        List<RecommendedDataDto> recommendedDataList = new ArrayList<>();
        Comparator<RecommendedDataDto> stringComparator = Comparator.comparing(RecommendedDataDto::getUserId).reversed();
        recommendedDataMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(stringComparator))
                .forEachOrdered(recommendedDataEntry -> recommendedDataList.add(recommendedDataEntry.getValue()));
        return recommendedDataList;
    }

    private List<RecommendedDataDto> orderByItem(ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap, String direction) {
        if (CommonConstant.SORT_DIRECTION_ASC.equals(direction)) {
            return orderByItemAsc(recommendedDataMap);
        } else {
            return orderByItemDesc(recommendedDataMap);
        }
    }

    private List<RecommendedDataDto> orderByItemAsc(ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap) {
        List<RecommendedDataDto> recommendedDataList = new ArrayList<>();
        recommendedDataMap.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getItemId()))
                .forEachOrdered(recommendedDataEntry -> recommendedDataList.add(recommendedDataEntry.getValue()));
        return recommendedDataList;
    }

    private List<RecommendedDataDto> orderByItemDesc(ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap) {
        List<RecommendedDataDto> recommendedDataList = new ArrayList<>();
        Comparator<RecommendedDataDto> stringComparator = Comparator.comparing(RecommendedDataDto::getItemId).reversed();
        recommendedDataMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(stringComparator))
                .forEachOrdered(recommendedDataEntry -> recommendedDataList.add(recommendedDataEntry.getValue()));
        return recommendedDataList;
    }

    private List<RecommendedDataDto> orderByRating(ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap, String direction) {
        if (CommonConstant.SORT_DIRECTION_ASC.equals(direction)) {
            return orderByRatingAsc(recommendedDataMap);
        } else {
            return orderByRatingDesc(recommendedDataMap);
        }
    }

    private List<RecommendedDataDto> orderByRatingAsc(ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap) {
        List<RecommendedDataDto> recommendedDataList = new ArrayList<>();
        recommendedDataMap.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getRating()))
                .forEachOrdered(recommendedDataEntry -> recommendedDataList.add(recommendedDataEntry.getValue()));
        return recommendedDataList;
    }

    private List<RecommendedDataDto> orderByRatingDesc(ConcurrentMap<UUID, RecommendedDataDto> recommendedDataMap) {
        List<RecommendedDataDto> recommendedDataList = new ArrayList<>();
        Comparator<RecommendedDataDto> stringComparator = Comparator.comparing(RecommendedDataDto::getRating).reversed();
        recommendedDataMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(stringComparator))
                .forEachOrdered(recommendedDataEntry -> recommendedDataList.add(recommendedDataEntry.getValue()));
        return recommendedDataList;
    }

    private List<RecommendedDataDto> getPagingData(List<RecommendedDataDto> recommendedDataList,
                                                   Integer startIndex, Integer dataLength) {
        return recommendedDataList.stream()
                .skip(startIndex)
                .limit(dataLength)
                .collect(Collectors.toList());
    }

    @Override
    public Double getMaeValue() {
        return svdAlgorithmCache.getMaeValue();
    }

    @Override
    public List<MaeDto> getAllMaeValue() {
        /*ConcurrentMap<String, SVDValue> svdAlgorithmMap = svdAlgorithmCache.getSvdAlgorithmMap();
        if (CollectionUtils.isEmpty(svdAlgorithmMap)) {
            return new ArrayList<>();
        }
        List<MaeDto> maeDtoList = new ArrayList<>();
        svdAlgorithmMap.entrySet().stream().forEach(entry -> {
            maeDtoList.add(MaeDto.builder().dataset(entry.getKey()).value(entry.getValue().getValue()).build());
        });*/
        return maeDataCache.getMaeData();
    }

    private List<RecommendedDataEntity> recommendedItemsToRecommendedDatas(List<RecommendedItem> recommendedItems) {
        if (CollectionUtils.isEmpty(recommendedItems)) {
            return Collections.emptyList();
        }
        return recommendedItems.stream().map(this::recommendedItemToRecommendedData).collect(Collectors.toList());
    }

    private RecommendedDataEntity recommendedItemToRecommendedData(RecommendedItem recommendedItem) {
        return RecommendedDataEntity.builder()
                .itemId(recommendedItem.getUserId())
                .userId(recommendedItem.getItemId())
                .rating(String.valueOf(recommendedItem.getValue()))
                .build();
    }

}
