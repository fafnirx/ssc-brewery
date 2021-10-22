package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (authorityRepository.count() == 0) {
            loadSecurities();
        }
    }

    private void loadSecurities() {
        // beer permissions
        Authority beerCreateAuth = authorityRepository.save(Authority.builder().permission("beer.create").build());
        Authority beerReadAuth = authorityRepository.save(Authority.builder().permission("beer.read").build());
        Authority beerUpdateAuth = authorityRepository.save(Authority.builder().permission("beer.update").build());
        Authority beerDeleteAuth = authorityRepository.save(Authority.builder().permission("beer.delete").build());

        Authority customerCreateAuth = authorityRepository.save(Authority.builder().permission("customer.create").build());
        Authority customerReadAuth = authorityRepository.save(Authority.builder().permission("customer.read").build());
        Authority customerUpdateAuth = authorityRepository.save(Authority.builder().permission("customer.update").build());
        Authority customerDeleteAuth = authorityRepository.save(Authority.builder().permission("customer.delete").build());

        Authority breweryCreateAuth = authorityRepository.save(Authority.builder().permission("brewery.create").build());
        Authority breweryReadAuth = authorityRepository.save(Authority.builder().permission("brewery.read").build());
        Authority breweryUpdateAuth = authorityRepository.save(Authority.builder().permission("brewery.update").build());
        Authority breweryDeleteAuth = authorityRepository.save(Authority.builder().permission("brewery.delete").build());

        log.debug("Number of Authorities loaded: {}", authorityRepository.count());


        Role adminRole = roleRepository.save(Role.builder().roleName("ADMIN").build());
        adminRole.setAuthorities(
                new HashSet<>(Set.of(
                        beerCreateAuth, beerReadAuth, beerUpdateAuth, beerDeleteAuth,
                        customerCreateAuth, customerReadAuth, customerUpdateAuth, customerDeleteAuth,
                        breweryCreateAuth, breweryReadAuth, breweryUpdateAuth, breweryDeleteAuth))
        );

        Role customerRole = roleRepository.save(Role.builder().roleName("CUSTOMER").build());
        customerRole.setAuthorities(new HashSet<>(Set.of(beerReadAuth, customerReadAuth, breweryReadAuth)));

        Role userRole = roleRepository.save(Role.builder().roleName("USER").build());
        userRole.setAuthorities(new HashSet<>(Set.of(beerReadAuth)));
        roleRepository.saveAll(Arrays.asList(adminRole, userRole, customerRole));
        log.debug("Number of Roles loaded: {}", roleRepository.count());

        userRepository.save(User.builder()
                .userName("spring")
                .passWord(passwordEncoder.encode("guru"))
                .role(adminRole)
                .build());
        userRepository.save(User.builder()
                .userName("user")
                .passWord(passwordEncoder.encode("password"))
                .role(userRole)
                .build());
        userRepository.save(User.builder()
                .userName("scott")
                .passWord(passwordEncoder.encode("tiger"))
                .role(customerRole)
                .build());
        log.debug("Number of Users loaded: {}", userRepository.count());
    }
}
