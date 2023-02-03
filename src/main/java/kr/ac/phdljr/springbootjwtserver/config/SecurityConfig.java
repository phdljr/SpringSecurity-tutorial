package kr.ac.phdljr.springbootjwtserver.config;

import kr.ac.phdljr.springbootjwtserver.config.jwt.JwtAuthenticationFilter;
import kr.ac.phdljr.springbootjwtserver.filter.MyFilter3;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 참고로, 시큐리티 필터가 다른 필터에 비해 먼저 동작함
    // 만약 내가 만든 필터를 시큐리티 필터보다 먼저 실행시키고 싶다면, addFilterBefore를 사용해라.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 요청 다 허락해주는 필터 추가
                // @CrossOrigin (인증 X)
                // 시큐리티 필터에 등록 (인증 O)
                .addFilter(corsConfig.corsFilter())

                // 스프링 시큐리티엔 여러가지 필터가 등록돼있는 상태다.
                // 그 중에서, BasicAuthenticationFilter가 실행되기 전에 내가 등록한 필터를 실행시키고 싶을 때 설정
                // .addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class)
                .addFilterBefore(new MyFilter3(), SecurityContextHolderFilter.class)
                .csrf().disable()

                // 세션 사용 안함
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                // form 태그로 로그인을 하지 않는다
                .formLogin().disable()
                // Basic 방식이 아닌 Bearer 방식을 사용하기 위해 설정
                .httpBasic().disable()
                // AuthenticationManager 를 넘겨줘야 함.
                .addFilter(new JwtAuthenticationFilter(http.getSharedObject(AuthenticationManager.class)))
                .authorizeRequests()
                .antMatchers("/api/v1/user/**").access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**").access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/resources/**");
    }
}
