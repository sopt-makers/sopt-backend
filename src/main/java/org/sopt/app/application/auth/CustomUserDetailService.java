package org.sopt.app.application.auth;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.ResponseCode;
import org.sopt.app.common.exception.EntityNotFoundException;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findUserById(Long.parseLong(username))
                .orElseThrow(() -> new EntityNotFoundException(ResponseCode.ENTITY_NOT_FOUND));
    }
}
