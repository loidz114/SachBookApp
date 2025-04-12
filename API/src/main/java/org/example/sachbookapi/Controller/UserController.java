package org.example.sachbookapi.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sachbookapi.Dto.RegisterRequest;
import org.example.sachbookapi.Dto.UpdateUserRequest;
import org.example.sachbookapi.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request) {
        String result = userService.registerUser(request);

        if (result.equals("User registered successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        String result = userService.updateUser(id, request);

        if (result.equals("User updated successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}

