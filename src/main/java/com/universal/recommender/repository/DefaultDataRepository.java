package com.universal.recommender.repository;

import com.universal.recommender.model.DataFromFileEntity;
import com.universal.recommender.model.FileNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DefaultDataRepository extends JpaRepository<DataFromFileEntity, Long> {

    List<DataFromFileEntity> findAllByFileNameEntity(FileNameEntity fileName);

    @Query(value = "SELECT df.* FROM data_from_file df WHERE df.file_name_id = ?1 LIMIT ?2", nativeQuery = true)
    List<DataFromFileEntity> findAllByFileNameIdAndAmount(Integer fileNameId, Integer amount);


    @Query("SELECT DISTINCT id FROM DataFromFileEntity")
    List<String> findAllFileNames();
}
