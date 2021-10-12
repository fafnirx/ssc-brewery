package guru.sfg.brewery.config;

import guru.sfg.brewery.security.FafnirxDelegatingPasswordEncoder;
import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.RestUrlParamAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager( authManager);
        return filter;
    }

    public RestUrlParamAuthFilter restUrlParamAuthFilter(AuthenticationManager authManager) {
        RestUrlParamAuthFilter filter = new RestUrlParamAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authManager);
        return filter;
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return FafnirxDelegatingPasswordEncoder.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class).csrf().disable();
        http.addFilterBefore(restUrlParamAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class);
        http
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/",
                                "/webjars/**",
                                "/login", "/resources/**",
                                "/beers/find", "/beers*").permitAll()
                        .antMatchers("/h2-console/**bewlin").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll())
                .authorizeRequests()
                .anyRequest().authenticated().and()
                .formLogin().and()
                .httpBasic();
        //h2 console config
        http.headers().frameOptions().sameOrigin();
    }

/*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("{bcrypt}$2a$15$qiYgYo9CgL/Onv2.NTBiae6.nCngOf4v6JaavFcnG93iqqHLcIxxC")
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{sha256}304dd9a09f841be2670d8caa8ae1ade3d08ac88359a7019426d3ef9f9a14d750254380f13bbee08e")
                .roles("USER");
        auth.inMemoryAuthentication()
                .withUser("scott")
                .password("{bcrypt15}$2a$15$c0c6QGcVgjN1gBpURV70b.kdYByE0oLmTfCTb5jM/zfjhxeIbCE.6")
                .roles("CUSTOMER");
    }
*/

//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("spring")
//                .password("guru")
//                .roles("ADMIN")
//                .build();
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(admin, user);
//    }
}
