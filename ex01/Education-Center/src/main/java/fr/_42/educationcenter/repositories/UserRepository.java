package fr._42.educationcenter.repositories;

import fr._42.educationcenter.models.User;
import fr._42.educationcenter.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    List<User> findAllByRole(UserRole role);
}
