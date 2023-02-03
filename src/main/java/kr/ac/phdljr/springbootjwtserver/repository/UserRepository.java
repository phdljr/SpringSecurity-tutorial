package kr.ac.phdljr.springbootjwtserver.repository;

import kr.ac.phdljr.springbootjwtserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
