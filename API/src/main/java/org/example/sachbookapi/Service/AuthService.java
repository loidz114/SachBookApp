package org.example.sachbookapi.Service;

import lombok.RequiredArgsConstructor;
import org.example.sachbookapi.Dto.LoginRequest;
import org.example.sachbookapi.Dto.LoginResponse;
import org.example.sachbookapi.Entity.UserModel;
import org.example.sachbookapi.Repository.UserRepository;
import org.example.sachbookapi.Security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        // 1. Tìm user theo username
        UserModel user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3. Sinh token
        String token = jwtTokenProvider.generateToken(user.getUsername());

        // 4. Trả về response
        return new LoginResponse(token, user.getUsername(), user.getRole());
    }
}