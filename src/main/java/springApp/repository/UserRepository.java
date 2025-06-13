package springApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springApp.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
