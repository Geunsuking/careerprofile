package com.example.careerprofile.controller;

import com.example.careerprofile.entity.CareerProfile;
import com.example.careerprofile.repository.CareerProfileRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.careerprofile.repository.CareerInfoRepository;
import java.util.List;
import com.example.careerprofile.entity.CareerInfo;
import com.example.careerprofile.entity.User;
import com.example.careerprofile.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class HomeController {

    private final CareerProfileRepository careerProfileRepository;
    private final UserRepository userRepository;
    private final CareerInfoRepository careerInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public HomeController(
            CareerProfileRepository careerProfileRepository,
            UserRepository userRepository,
            CareerInfoRepository careerInfoRepository,
            PasswordEncoder passwordEncoder) {

        this.careerProfileRepository = careerProfileRepository;
        this.userRepository = userRepository;
        this.careerInfoRepository = careerInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // 홈
    @GetMapping("/")
    public String home(
            Model model,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        // 로그인하지 않은 경우
        if (userDetails == null) {
            return "home";
        }

        // 로그인한 사용자 이름 전달
        String username = userDetails.getUsername();
        model.addAttribute("username", username);

        // 현재 로그인한 사용자 찾기
        User user = userRepository.findByUsername(username)
                .orElseThrow();

        // 현재 로그인한 사용자의 프로필 찾기
        java.util.Optional<CareerProfile> profileOptional =
                careerProfileRepository.findByUser(user);

        // 프로필이 존재하는 경우
        if (profileOptional.isPresent()) {

            CareerProfile profile = profileOptional.get();

            // 메인 화면에 프로필 전달
            model.addAttribute("profile", profile);
        }

        return "home";
    }
    // 회원가입 페이지
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
    // 회원가입
    @PostMapping("/signup")
    public String saveUser(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        // 이미 사용 중인 아이디인지 확인
        if (userRepository.findByUsername(username).isPresent()) {

            model.addAttribute("error", "이미 사용 중인 아이디입니다.");

            return "signup";
        }

        User user = new User();

        user.setUsername(username);

        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        userRepository.save(user);

        return "redirect:/login";
    }
    // 로그인 페이지
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 정보 추가
    @GetMapping("/profile/add")
    public String addProfile() {
        return "add";
    }
    // 정보 추가2
    @GetMapping("/profile/info/add")
    public String addCareerInfo() {
        return "career-info-add";
    }

    // 프로필 목록
    @GetMapping("/profile/list")
    public String profileList(
            Model model,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        // 현재 로그인한 사용자 찾기
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        // 현재 로그인한 사용자의 프로필만 가져오기
        java.util.Optional<CareerProfile> profileOptional =
                careerProfileRepository.findByUser(user);

        if (profileOptional.isPresent()) {

            CareerProfile profile = profileOptional.get();

            model.addAttribute("profiles", List.of(profile));

            // 해당 프로필의 커리어 정보만 가져오기
            List<CareerInfo> careerInfos =
                    careerInfoRepository.findByProfileId(profile.getId());

            model.addAttribute("careerInfos", careerInfos);

        } else {

            // 아직 프로필이 없는 경우
            model.addAttribute("profiles", List.of());
            model.addAttribute("careerInfos", List.of());
        }

        return "list";
    }

    // 프로필 저장
    @PostMapping("/profile")
    public String saveProfile(
            @RequestParam String name,
            @RequestParam String school,
            @RequestParam String major,
            @RequestParam String grade,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        // 현재 로그인한 사용자 찾기
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        // 프로필 생성
        CareerProfile profile = new CareerProfile();

        profile.setName(name);
        profile.setSchool(school);
        profile.setMajor(major);
        profile.setGrade(grade);

        // 현재 로그인한 User와 프로필 연결
        profile.setUser(user);

        // DB 저장
        careerProfileRepository.save(profile);

        return "redirect:/profile/list";
    }

    @PostMapping("/profile/info")
    public String saveCareerInfo(
            @RequestParam String itemName,
            @RequestParam String itemValue,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        // 현재 로그인한 사용자 찾기
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        // 현재 로그인한 사용자의 프로필 찾기
        CareerProfile profile = careerProfileRepository.findByUser(user)
                .orElseThrow();

        // 새로운 정보 생성
        CareerInfo careerInfo = new CareerInfo();

        careerInfo.setItemName(itemName);
        careerInfo.setItemValue(itemValue);
        careerInfo.setProfile(profile);

        // DB 저장
        careerInfoRepository.save(careerInfo);

        return "redirect:/profile/list";
    }

    // 수정 페이지
    @GetMapping("/profile/edit")
    public String editProfile(
            @RequestParam Long id,
            Model model,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        // 현재 로그인한 사용자 찾기
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        // 프로필 찾기
        CareerProfile profile = careerProfileRepository.findById(id)
                .orElseThrow();

        // 내 프로필인지 확인
        if (!profile.getUser().getId().equals(user.getId())) {
            return "redirect:/profile/list";
        }

        model.addAttribute("profile", profile);

        return "edit";
    }

    // 프로필 수정
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String school,
            @RequestParam String major,
            @RequestParam String grade,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        // 현재 로그인한 사용자 찾기
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        // 프로필 찾기
        CareerProfile profile = careerProfileRepository.findById(id)
                .orElseThrow();

        // 내 프로필인지 확인
        if (!profile.getUser().getId().equals(user.getId())) {
            return "redirect:/";
        }

        profile.setName(name);
        profile.setSchool(school);
        profile.setMajor(major);
        profile.setGrade(grade);

        careerProfileRepository.save(profile);

        return "redirect:/profile/list";
    }

    // 프로필 삭제
    @PostMapping("/profile/delete")
    public String deleteProfile(
            @RequestParam Long id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        // 현재 로그인한 사용자 찾기
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        // 프로필 찾기
        CareerProfile profile = careerProfileRepository.findById(id)
                .orElseThrow();

        // 내 프로필인지 확인
        if (!profile.getUser().getId().equals(user.getId())) {
            return "redirect:/profile/list";
        }

        // 내 프로필이면 삭제
        careerProfileRepository.delete(profile);

        return "redirect:/profile/list";
    }

    @PostMapping("/profile/info/delete")
    public String deleteCareerInfo(
            @RequestParam Long id,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        // 현재 로그인한 사용자 찾기
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        // CareerInfo 찾기
        CareerInfo careerInfo = careerInfoRepository.findById(id)
                .orElseThrow();

        // CareerInfo가 연결된 프로필 가져오기
        CareerProfile profile = careerInfo.getProfile();

        // 현재 로그인한 사용자의 프로필인지 확인
        if (!profile.getUser().getId().equals(user.getId())) {
            return "redirect:/profile/list";
        }

        // 내 정보라면 삭제
        careerInfoRepository.delete(careerInfo);

        return "redirect:/profile/list";
    }

    @GetMapping("/profile/info/edit")
    public String editCareerInfo(
            @RequestParam Long id,
            Model model,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        CareerInfo careerInfo = careerInfoRepository.findById(id)
                .orElseThrow();

        CareerProfile profile = careerInfo.getProfile();

        if (!profile.getUser().getId().equals(user.getId())) {
            return "redirect:/profile/list";
        }

        model.addAttribute("careerInfo", careerInfo);

        return "career-info-edit";
    }

    @PostMapping("/profile/info/update")
    public String updateCareerInfo(
            @RequestParam Long id,
            @RequestParam String itemName,
            @RequestParam String itemValue,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            org.springframework.security.core.userdetails.UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        CareerInfo careerInfo = careerInfoRepository.findById(id)
                .orElseThrow();

        CareerProfile profile = careerInfo.getProfile();

        if (!profile.getUser().getId().equals(user.getId())) {
            return "redirect:/profile/list";
        }

        careerInfo.setItemName(itemName);
        careerInfo.setItemValue(itemValue);

        careerInfoRepository.save(careerInfo);

        return "redirect:/profile/list";
    }
}