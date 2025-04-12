package org.example.sachbookapi.Controller;

import lombok.RequiredArgsConstructor;
import org.example.sachbookapi.Dto.LoginRequest;
import org.example.sachbookapi.Dto.LoginResponse;
import org.example.sachbookapi.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}