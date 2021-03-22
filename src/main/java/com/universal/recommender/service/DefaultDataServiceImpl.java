package com.universal.recommender.service;

import com.universal.recommender.constant.CommonConstant;
import com.universal.recommender.model.DataFromFileEntity;
import com.universal.recommender.model.FileNameEntity;
import com.universal.recommender.repository.DefaultDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DefaultDataServiceImpl implements  DefaultDataService {

    private final DefaultDataRepository defaultDataRepository;

    private final FileNameService fileNameService;

    public DefaultDataServiceImpl(DefaultDataRepository defaultDataRepository,
                                  FileNameService fileNameService) {
        this.defaultDataRepository = defaultDataRepository;
        this.fileNameService = fileNameService;
    }

    @Override
    @Transactional
    public void saveDefaultData(MultipartFile multipartFile) {
        List<FileNameEntity> existingFileName = fileNameService.getAllFileName();
        String fileName = multipartFile.getOriginalFilename();
        if (!validateFileName(existingFileName, fileName)) {
            return;
        }
        FileNameEntity newFileNameEntity = fileNameService.saveFileName(FileNameEntity.builder().fileName(fileName).build());

        BufferedReader br;
        List<DataFromFileEntity> defaultDatas = new ArrayList<>();
        try {
            InputStream is = multipartFile.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            defaultDatas = br.lines().map(line -> {
                String[] items = line.split(CommonConstant.COMMA);
                return DataFromFileEntity.builder()
                        .fileNameEntity(newFileNameEntity)
                        .userId(items[1])
                        .itemId(items[0])
                        .rating(Double.valueOf(items[2]))
                        .build();
            }).collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        if (!CollectionUtils.isEmpty(defaultDatas)) {
            defaultDataRepository.saveAll(defaultDatas.subList(0, 9999));
        }
    }

    private boolean validateFileName(List<FileNameEntity> existingFileNames, String newFileName) {
        if (CollectionUtils.isEmpty(existingFileNames)) {
            return true;
        }
        return CollectionUtils.isEmpty(existingFileNames.stream().filter(fileNameEntity ->
                Objects.equals(fileNameEntity.getFileName(), newFileName)).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public List<DataFromFileEntity> getByFileName(FileNameEntity fileName) {
        return defaultDataRepository.findAllByFileNameEntity(fileName);
    }

    @Override
    public List<String> getAllFileNames() {
        return defaultDataRepository.findAllFileNames();
    }
}
