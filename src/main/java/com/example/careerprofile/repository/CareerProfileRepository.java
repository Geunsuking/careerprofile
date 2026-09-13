package com.example.careerprofile.repository;

import com.example.careerprofile.entity.CareerProfile;
import com.example.careerprofile.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CareerProfileRepository extends JpaRepository<CareerProfile, Long> {

    Optional<CareerProfile> findByUser(User user);

}