package com.universal.recommender.repository;

import com.universal.recommender.model.FileNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileNameRepository extends JpaRepository<FileNameEntity, Integer> {

    Optional<FileNameEntity> findFirstByFileName(String fileName);
}
