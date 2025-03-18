package vasu.easyproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vasu.easyproject.model.User;
import vasu.easyproject.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    
    public UserService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Boolean validateCredentials(String password, String requestPassword) {
        if (passwordEncoder.matches(requestPassword, password)) {
            return true;
        }
        return false;
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
