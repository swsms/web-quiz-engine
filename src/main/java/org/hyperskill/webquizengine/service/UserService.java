package org.hyperskill.webquizengine.service;

import org.hyperskill.webquizengine.exception.DuplicateEmailException;
import org.hyperskill.webquizengine.model.User;
import org.hyperskill.webquizengine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("No user %s found", username)));
    }

    public Long registerNewUser(String username, String password) {
        try {
            var encodedPassword = encoder.encode(password);
            var user = new User(username, encodedPassword);
            return userRepository.save(user).getId();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException();
        }
    }
}

