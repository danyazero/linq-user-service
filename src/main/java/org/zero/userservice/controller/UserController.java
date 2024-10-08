package org.zero.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.zero.userservice.exception.RequestException;
import org.zero.userservice.model.IUserService;
import org.zero.userservice.model.UserDataRequest;
import org.zero.userservice.model.UserDataResponse;
import org.zero.userservice.repository.ContactPersonRepository;
import org.zero.userservice.utils.RequestMetadataStore;
import org.zero.userservice.utils.UUIDProvider;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UUIDProvider uuid;
    private final List<IUserService> userServiceList;
    private final RequestMetadataStore requestMetadataStore;
    private final ContactPersonRepository contactPersonRepository;

    private IUserService userService;

    @PostConstruct
    private void init() {
        log.info("Initialize user service -> {}", userServiceList.size());
         userService = userServiceList.stream()
                .limit(userServiceList.size() - 1)
                .reduce(
                        userServiceList.getLast(),
                        (result, current) -> (IUserService) current.next(result)
                );
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Create ContactPerson", description = "Create a ContactPerson for the user whose ID is contained in the “X-Issuer-ID” header.")
    public UserDataResponse createUser(
            @RequestBody UserDataRequest userData,
            @Parameter(description = "Request unique id") @RequestHeader("X-Request-ID") String requestId
    ) {
        requestMetadataStore.setRequestId(requestId);
        log.info("({}) Create ContactPerson -> {}", requestId, userData);
        return userService.createContactPerson(userData);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user ContactPerson")
    public String deleteContactPerson(
            @PathVariable String userId,
            @Parameter(description = "Issuer user id") @RequestHeader("X-Issuer-ID") String issuer,
            @Parameter(description = "Request unique id") @RequestHeader("X-Request-ID") String requestId
    ) {
        requestMetadataStore.setRequestId(requestId);
        log.info("({}) Delete ContactPerson with: id -> {}, issuer -> {}", requestId, userId, issuer);
        return userService.deleteContactPerson(userId, issuer);
    }


    @SneakyThrows
    @GetMapping("/{userId}")
    @Operation(summary = "Get ContactPerson by it ID")
    public UserDataResponse getUser(
            @Parameter(description = "ContactPerson ID") @PathVariable String userId
    ) {
        var compiledUserId = uuid.get(userId);
        var user = contactPersonRepository.getUserByIdAndIssuerIdfExists(compiledUserId);
        if (user.isEmpty()) throw new RequestException("Contact person not found");

        return new UserDataResponse(
                user.get().getFirstName(),
                user.get().getLastName(),
                user.get().getMiddleName(),
                user.get().getPhone(),
                userId);
    }

    @GetMapping("/phone")
    @Operation(summary = "Get ContactPerson List", description = "Get the list of contacts saved for the user with the identifier in the “X-Issuer-ID” header by phone number.")
    public List<UserDataResponse> getByPhone(
            @Parameter(description = "Phone number") @RequestParam String phone,
            @Parameter(description = "Issuer user id") @RequestHeader("X-Issuer-ID") String issuerId
    ) {
        var user = contactPersonRepository.getUserByPhoneAndIssuerId(phone, uuid.get(issuerId));
        if (user.isEmpty())
            throw new RequestException("Contact person not founded");

        return user.stream()
                .map(element -> new UserDataResponse(
                        element.getFirstName(),
                        element.getLastName(),
                        element.getMiddleName(),
                        element.getPhone(),
                        issuerId)
                ).toList();
    }
}
