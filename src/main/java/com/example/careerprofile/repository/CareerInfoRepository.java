package com.example.careerprofile.repository;

import com.example.careerprofile.entity.CareerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerInfoRepository extends JpaRepository<CareerInfo, Long> {

    List<CareerInfo> findByProfileId(Long profileId);
}