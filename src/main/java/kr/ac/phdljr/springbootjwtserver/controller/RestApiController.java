package kr.ac.phdljr.springbootjwtserver.controller;

import kr.ac.phdljr.springbootjwtserver.config.auth.PrincipalDetails;
import kr.ac.phdljr.springbootjwtserver.model.User;
import kr.ac.phdljr.springbootjwtserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestApiController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/home")
    public String home(){
        return "<h1>home</h1>";
    }

    @PostMapping ("/token")
    public String token(){
        return "token";
    }

    @PostMapping("/join")
    public String join(@RequestBody User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles("ROLE_USER");
        userRepository.save(user);
        return "회원가입 완료";
    }

    // user, manager, admin 권한만 접근 가능
    @GetMapping("/api/v1/user")
    public String user(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principal : " + principal.getId());
        System.out.println("principal : " + principal.getUsername());
        System.out.println("principal : " + principal.getPassword());

        return "user";
    }

    // manager, admin 권한만 접근 가능
    @GetMapping("/api/v1/manager")
    public String manager(){
        return "manager";
    }

    // admin 권한만 접근 가능
    @GetMapping("/api/v1/admin")
    public String admin(){
        return "admin";
    }
}
