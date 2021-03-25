package com.universal.recommender.convert;

import com.imsweb.naaccrxml.entity.Item;
import com.imsweb.naaccrxml.entity.Patient;
import com.imsweb.naaccrxml.entity.Tumor;
import com.universal.recommender.cache.DataGeneratedModel;
import com.universal.recommender.constant.CommonConstant;
import com.universal.recommender.constant.DataGeneratorConstant;
import com.universal.recommender.enums.PatientRace;
import com.universal.recommender.util.RatingUtil;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DataGeneratorConverter {

    public static ConcurrentMap<String, DataGeneratedModel> patientToDataSample(Patient patient) {
        if (Objects.isNull(patient) || !CollectionUtils.isEmpty(patient.getAllValidationErrors())) {
            return new ConcurrentHashMap<>();
        }
        ConcurrentMap<String, DataGeneratedModel> dataGeneratedMap = new ConcurrentHashMap<>();
        String userId = getPatientItem(patient, DataGeneratorConstant.USER_ID);
        String rating = RatingUtil.generateRatingWithNullValue();
        List<Tumor> tumors = patient.getTumors();
        if (CollectionUtils.isEmpty(tumors)) {
            System.out.println("don't have any tumor");
            return new ConcurrentHashMap<>();
        }
        for (Tumor tumor : tumors) {
            String tumorId = getTumorItem(tumor, DataGeneratorConstant.ITEM_ID);
            String id = UUID.randomUUID().toString();
            dataGeneratedMap.put(
                    id,
                    DataGeneratedModel.builder()
                            .userId(userId)
                            .itemId(tumorId)
                            .rating(rating)
                            .build());

        }
        return dataGeneratedMap;
    }

    private static String getPatientRace(Patient patient) {
        for (PatientRace patientRace : PatientRace.values()) {
            String patientRaceValue = getPatientItem(patient, patientRace.getValue());
            if (!Objects.equals(patientRaceValue, CommonConstant.UNSELECTED_PATIENT_RACE)) {
                return patientRace.getRating();
            }
        }
        return "";
    }

    private static String getPatientItem(Patient patient, String itemId) {
        List<Item> items = patient.getItems();
        return getSpecifiedItem(items, itemId);
    }

    private static String getTumorItem(Tumor tumor, String itemId) {
        List<Item> items = tumor.getItems();
        return getSpecifiedItem(items, itemId);
    }

    private static String getSpecifiedItem(List<Item> items, String itemId) {
        Optional<Item> itemOptional = items.stream().filter(item -> Objects.equals(item.getNaaccrId(), itemId)).findFirst();
        if (itemOptional.isPresent()) {
            return itemOptional.get().getValue();
        } else {
            return "";
        }
    }
}
