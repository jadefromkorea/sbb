package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

/*
User의 비밀번호는 보안을 위해 반드시 암호화하여 저장해야 한다. 그러므로 스프링 시큐리티의 BCryptPasswordEncoder 클래스를 사용하여 암호화하여 비밀번호를 저장했다.
BCryptPasswordEncoder 클래스는 비크립트(BCrypt) 해시 함수를 사용하는데, 비크립트는 해시 함수의 하나로 주로 비밀번호와 같은 보안 정보를 안전하게 저장하고 검증할 때 사용하는 암호화 기술이다.

하지만 이렇게 BCryptPasswordEncoder 객체를 직접 new로 생성하는 방식보다는 PasswordEncoder 객체를 빈으로 등록해서 사용하는 것이 좋다.
왜냐하면 암호화 방식을 변경하면 BCryptPasswordEncoder를 사용한 모든 프로그램을 일일이 찾아다니며 수정해야 하기 때문이다.
 */
    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);

//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        user.setPassword(passwordEncoder.encode(password));
        user.setPassword(passwordEncoder.encode(password));

        log.info(">>>>> user: {}", user);

        this.userRepository.save(user);

        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
}
