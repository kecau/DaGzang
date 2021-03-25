package com.universal.recommender.service;

import com.universal.recommender.model.FileNameEntity;

import java.util.List;

public interface FileNameService {

    List<FileNameEntity> getAllFileName();

    FileNameEntity saveFileName(FileNameEntity fileNameEntity);

    FileNameEntity getFileNameByName(String name);
}
