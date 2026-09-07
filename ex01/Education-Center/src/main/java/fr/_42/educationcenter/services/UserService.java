package fr._42.educationcenter.services;

import fr._42.educationcenter.dto.UserRequest;
import fr._42.educationcenter.dto.UserResponse;
import fr._42.educationcenter.exceptions.BadRequestException;
import fr._42.educationcenter.exceptions.NotFoundException;
import fr._42.educationcenter.models.User;
import fr._42.educationcenter.models.UserRole;
import fr._42.educationcenter.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserService::toResponse);
    }

    public UserResponse getUser(Long id) {
        return toResponse(findUser(id));
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByLogin(request.login()).isPresent()) {
            throw new BadRequestException("Login already exists");
        }
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(parseRole(request.role()));
        user.setLogin(request.login());
        user.setPassword(request.password());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findUser(id);
        if (!user.getLogin().equals(request.login())
                && userRepository.findByLogin(request.login()).isPresent()) {
            throw new BadRequestException("Login already exists");
        }
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(parseRole(request.role()));
        user.setLogin(request.login());
        user.setPassword(request.password());
        return toResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUser(id);
        userRepository.delete(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private UserRole parseRole(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BadRequestException("Incorrect role");
        }
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getLogin()
        );
    }
}
