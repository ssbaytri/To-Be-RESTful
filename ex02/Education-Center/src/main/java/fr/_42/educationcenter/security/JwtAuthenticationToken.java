package fr._42.educationcenter.security;

import fr._42.educationcenter.models.UserRole;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long userId;
    private final String login;

    public JwtAuthenticationToken(Long userId, String login, UserRole role) {
        super(List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
        this.userId = userId;
        this.login = login;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return login;
    }

    public Long getUserId() {
        return userId;
    }
}
