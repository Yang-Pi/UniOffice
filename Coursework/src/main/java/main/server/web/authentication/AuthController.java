package main.server.web.authentication;

import main.server.entity.user.User;
import main.server.entity.user.notentity.Role;
import main.server.repository.UserRepository;
import main.server.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    private static String token;

    public static String getToken() {
        return token;
    }

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody AuthRequest request) {
        String userName = request.getUserName();
        String password = request.getPassword();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashPassword = encoder.encode(password);

        List<Role> roles = new ArrayList<>();
        Role role = request.getRole();

        if (role == Role.MANAGER) {
            roles.add(Role.STUDENT);
            roles.add(Role.PROFESSOR);
        }
        else if (role == Role.PROFESSOR) {
            roles.add(Role.STUDENT);
        }
        roles.add(role);

        User newUser = new User(userName, hashPassword, roles);
        userRepository.save(newUser);

        Map<Object, Object> model = new HashMap<>();
        model.put("userName", userName);
        model.put("roles", newUser.getRoles());

        return ResponseEntity.ok(model);
    }

    @PostMapping("/signin")
    public ResponseEntity singIn(@RequestBody AuthRequest request) {
        try {
            String userName = request.getUserName();
            String token = jwtTokenProvider.createToken(
                    userName,
                    userRepository.findUserByUserName(userName)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found")).getRoles()
            );

            String enteredPw = request.getPassword();
            String userPw = userRepository.findUserByUserName(userName).get().getPassword();
            Map<Object, Object> model = new HashMap<>();

            if (BCrypt.checkpw(enteredPw, userPw)) {
                this.token = token;

                model.put("userName", userName);
                model.put("token", token);

                return ResponseEntity.ok(model);
            }
            else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @GetMapping("/checkUsername/{username}")
    public ResponseEntity checkUsername(@PathVariable("username") String username) {
        Optional<User> user = userRepository.findUserByUserName(username);
        if (user.isPresent()) {
            return new ResponseEntity(user.get().getRoles(), HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/isValidToken/{token}")
    public ResponseEntity isValidToken(@PathVariable("token") String token) {
        if (jwtTokenProvider.validateToken(token)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        else {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
