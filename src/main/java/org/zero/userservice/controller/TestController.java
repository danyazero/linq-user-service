package org.zero.userservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.zero.userservice.utils.UUIDProvider;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {
    private final UUIDProvider uuid;

    @GetMapping("/id/{value}")
    public String compileId(@PathVariable Integer value) {
        return uuid.generate(value);
    }

/*
    @PutMapping("/hash")
    public String compiledUserHash(@RequestBody UserData userData) {
        var data = IdempotencyValueProvider.generate(userData.firstName(), userData.middleName(), userData.lastName(), userData.phone());
        return SHAEncoder.apply(data, SHAEncoder.Encryption.SHA1);
    }
*/

}
