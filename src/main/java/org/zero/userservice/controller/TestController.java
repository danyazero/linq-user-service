package org.zero.userservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.zero.userservice.exception.RequestException;
import org.zero.userservice.model.UserData;
import org.zero.userservice.repository.ContactPersonRepository;
import org.zero.userservice.service.UserService;
import org.zero.userservice.utils.Base64Encoder;
import org.zero.userservice.utils.IdempotencyValueProvider;
import org.zero.userservice.utils.SHAEncoder;
import org.zero.userservice.utils.UUIDProvider;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {
    private final UUIDProvider uuid;

    @GetMapping("/id/{value}")
    public String compileId(@PathVariable Integer value) {
        return uuid.generate(value);
    }

    @PutMapping("/hash")
    public String compiledUserHash(@RequestBody UserData userData) {
        var data = IdempotencyValueProvider.generate(userData.firstName(), userData.middleName(), userData.lastName(), userData.phone());
        return SHAEncoder.apply(data, SHAEncoder.Encryption.SHA1);
    }

}
