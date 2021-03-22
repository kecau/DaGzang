package com.universal.recommender.service;

import com.universal.recommender.model.FileNameEntity;
import com.universal.recommender.repository.FileNameRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class FileNameServiceImpl implements FileNameService {

    private final FileNameRepository fileNameRepository;

    public FileNameServiceImpl(FileNameRepository fileNameRepository) {
        this.fileNameRepository = fileNameRepository;
    }

    @Override
    @Transactional
    public List<FileNameEntity> getAllFileName() {
        return fileNameRepository.findAll();
    }

    @Override
    @Transactional
    public FileNameEntity saveFileName(FileNameEntity fileNameEntity) {
        return fileNameRepository.save(fileNameEntity);
    }

    @Override
    public FileNameEntity getFileNameByName(String name) {
        return fileNameRepository.findFirstByFileName(name).orElse(null);
    }
}
