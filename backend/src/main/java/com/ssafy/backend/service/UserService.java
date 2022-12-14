package com.ssafy.backend.service;

import com.ssafy.backend.dto.UserLoginDto;
import com.ssafy.backend.entity.Authority;
import com.ssafy.backend.entity.User;
import com.ssafy.backend.entity.UserInfo;
import com.ssafy.backend.repository.UserInfoRepository;
import com.ssafy.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String signup(UserLoginDto userLoginDto){
        if(userRepository.findUserByUserEmail(userLoginDto.getUserEmail()) != null){
//            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
            return "email";
        }

        //비밀번호 같으면 같지 않으면 에러 처리
        if(!userLoginDto.getUserPassword().equals(userLoginDto.getUserPassword2())){
            return "password";
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        // user 저장
        User user = User.builder()
                .userEmail(userLoginDto.getUserEmail())
                .userPassword(passwordEncoder.encode(userLoginDto.getUserPassword()))
                .authorities(Collections.singleton(authority))
                .build();
        userRepository.save(user);

        //        # 키 -0.01699 * x^2 + 6.305 * x - 565.1   x에다가 키나 몸무게 넣음녀 됨
//        # 몸무게 0.004596 * x^2 - 0.157 * x + 60.08   -> 이거 두개 더한게 최종 피지컬 스코어
        float userHeight = userLoginDto.getUserHeight();
        float userWeight = userLoginDto.getUserWeight();
        double heightScore = -0.01699 * userHeight*userHeight + 6.305*userHeight -565.1;
        double weightScore = 0.004596*userWeight*userWeight -0.157*userWeight +60.08;

        //user Info 저장
        UserInfo userInfo = UserInfo.builder()
                .userName(userLoginDto.getUserName())
                .userAge(userLoginDto.getUserAge())
                .userHeight(userHeight)
                .userWeight(userWeight)
                .userPhysical((int)Math.floor(heightScore+weightScore))
                .userPosition(userLoginDto.getUserPosition())
                .user(user)
                .build();

        userInfoRepository.save(userInfo);

        return "ok";
    }

    public boolean emailCheck(String email){
        //이미 가입된 이메일 체크 -> 있다면 false
        if(userRepository.findOneWithAuthoritiesByUserEmail(email).orElse(null) != null){
            return false;
        }
        return true;
    }

    public User findById(long pk){
        User user = userRepository.findUserByUserId(pk);

        if(user != null){
            return user;
        }else{
            return null;
        }
    }

    public User findByEmail(String email){
        User user = userRepository.findUserByUserEmail(email);

        if(user != null){
            return user;
        }else{
            return null;
        }
    }
}