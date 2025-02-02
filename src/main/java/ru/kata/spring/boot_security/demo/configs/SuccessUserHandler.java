package ru.kata.spring.boot_security.demo.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;

import java.io.IOException;
import java.util.Set;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {
    UserDao userDao;

    public SuccessUserHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    // Spring Security использует объект Authentication, пользователя авторизованной сессии.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userDao.findByUsername(userDetails.getUsername()).orElse(null);
        if (roles.contains("ROLE_ADMIN")) {
            httpServletResponse.sendRedirect("/admin/allUsers");
        } else if (roles.contains("ROLE_USER")) {
            assert user != null;
            httpServletResponse.sendRedirect("/user/" + user.getId());
        }
    }
} 