package com.example.careerprofile.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class CareerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String school;
    private String major;
    private String grade;

    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CareerInfo> careerInfos = new ArrayList<>();

    // 로그인 사용자와 프로필 연결
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public CareerProfile() {

    }
    //setter
    public void setName(String name) {
        this.name = name;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setUser(User user) {
        this.user = user;
    }

//getter
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSchool() {
        return school;
    }

    public String getMajor() {
        return major;
    }

    public String getGrade() {
        return grade;
    }
    public User getUser() {
        return user;
    }
}
