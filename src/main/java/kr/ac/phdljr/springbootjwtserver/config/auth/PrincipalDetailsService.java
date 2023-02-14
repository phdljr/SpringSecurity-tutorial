package kr.ac.phdljr.springbootjwtserver.config.auth;

import kr.ac.phdljr.springbootjwtserver.model.User;
import kr.ac.phdljr.springbootjwtserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
    formLogin을 안쓰기 때문에, http://localhost:8080/login 여기서 동작을 안한다.
 */
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("PrincipalDetailsService : 진입");
        User userEntity = userRepository.findByUsername(username);
        return new PrincipalDetails(userEntity);
    }
}
