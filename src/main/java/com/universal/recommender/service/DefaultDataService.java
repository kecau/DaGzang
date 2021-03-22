package com.universal.recommender.service;

import com.universal.recommender.model.DataFromFileEntity;
import com.universal.recommender.model.FileNameEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DefaultDataService {

    void saveDefaultData(MultipartFile multipartFile);

    List<DataFromFileEntity> getByFileName(FileNameEntity fileName);

    List<String> getAllFileNames();
}
