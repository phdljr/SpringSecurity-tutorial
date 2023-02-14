package kr.ac.phdljr.springbootjwtserver.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.phdljr.springbootjwtserver.config.auth.PrincipalDetails;
import kr.ac.phdljr.springbootjwtserver.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

/**
 * 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있음.
 * /login 요청해서 username, password POST 요청하면
 * UsernamePasswordAuthenticationFilter 가 동작을 함
 * 하지만, formLogin().disable()을 해두면 동작을 안함
 *
 * 그래서 해당 클래스를 스프링 시큐리티에 등록해주면 됨
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    // login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter: 로그인 시도중");

        // 1. username, password 받아서

        // 2. 정상인지 로그인 시도를 해봄
        // authenticationManager로 로그인 시도를 하면
        // PrincipalDetailsService가 호출 loadUserByUsername 함수 실행됨

        // 3. PrincipalDetails 를 세션에 담고
        // -> 권한 관리를 해주기 위해 시큐리티 세션에 담아줘야 함

        // 4. JWT 토큰을 만들어서 응답해주면 됨
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(request.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // PrincipalDetailsService의 loadUserByUsername 함수 실행된 후
            // 정상이면 authentication 이 리턴
            // 즉, DB에 있는 username 과 password 가 일치한다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Authentication 객체가 session 영역에 저장됨 => 로그인이 되었다는 뜻
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료: "+principalDetails.getUsername()+principalDetails.getPassword());

            // session 영역에 저장을 해야하고 그 방법이 return을 해주면 됨(권환 관리를 해주기 위해)
            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 해당 함수가 실행
    // 여기서 JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토근을 response해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨: 인증이 완료됐다는 뜻");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject(principalDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", principalDetails.getId())
                .withClaim("username", principalDetails.getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }
}
