package org.zero.userservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zero.userservice.exception.AuthException;
import org.zero.userservice.model.*;
import org.zero.userservice.repository.UserRepository;
import org.zero.userservice.service.UserService;
import org.zero.userservice.utils.JWTModule;
import org.zero.userservice.utils.UUIDProvider;


@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final UUIDProvider uuid;
    private final JWTModule jwtModule;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping
    public Tokens login(@RequestBody AuthRequest requestBody) {
        return userService.loginUser(requestBody);
    }

    @PutMapping
    public void register(@RequestBody RegisterRequest userData) {
        userService.register(userData);
    }

    @GetMapping("/{token}")
    public String restoreSession(@PathVariable String token) {
        var decodedRefreshToken = jwtModule.decodeRefresh(token);
        var userId = decodedRefreshToken.getSubject();
        var user = userRepository.findById(uuid.get(userId));
        if (user.isEmpty()) throw new AuthException("User not founded");
        return jwtModule.issueSession(userId, true, user.get().getRoles());
    }

}
