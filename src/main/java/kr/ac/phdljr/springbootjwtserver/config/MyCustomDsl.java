package kr.ac.phdljr.springbootjwtserver.config;

import kr.ac.phdljr.springbootjwtserver.config.jwt.JwtAuthenticationFilter;
import kr.ac.phdljr.springbootjwtserver.config.jwt.JwtAuthorizationFilter;
import kr.ac.phdljr.springbootjwtserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@RequiredArgsConstructor
public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {

    private final CorsConfig corsConfig;
    private final UserRepository userRepository;

    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http
                // CORS 요청 다 허락해주는 필터 추가
                // @CrossOrigin (인증 X)
                // 시큐리티 필터에 등록 (인증 O)
                .addFilter(corsConfig.corsFilter())
                .addFilter(new JwtAuthenticationFilter(authenticationManager))
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
    }
}
