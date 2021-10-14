package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (authorityRepository.count()==0) {
            loadSecurities();
        }
    }

    private void loadSecurities() {
        Authority adminAuth = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
        Authority userAuth = authorityRepository.save(Authority.builder().role("ROLE_USER").build());
        Authority customerAuth = authorityRepository.save(Authority.builder().role("ROLE_CUSTOMER").build());

        log.debug("Number of Authorities loaded: {}", authorityRepository.count());

        userRepository.save(User.builder()
                .userName("spring")
                .passWord(passwordEncoder.encode("guru"))
                .authority(adminAuth)
                .build());
        userRepository.save(User.builder()
                .userName("user")
                .passWord(passwordEncoder.encode("password"))
                .authority(userAuth)
                .build());
        userRepository.save(User.builder()
                .userName("scott")
                .passWord(passwordEncoder.encode("tiger"))
                .authority(customerAuth)
                .build());
        log.debug("Number of Users loaded: {}", userRepository.count());
    }
}
