package org.example.sachbookapi.Service;

import lombok.RequiredArgsConstructor;
import org.example.sachbookapi.Dto.RegisterRequest;
import org.example.sachbookapi.Dto.UpdateUserRequest;
import org.example.sachbookapi.Entity.UserModel;
import org.example.sachbookapi.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String registerUser(RegisterRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        // Tạo User mới
        UserModel user = new UserModel();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER"); // mặc định là USER
        user.setIsActive(true);
        user.setCreatedAt(new Date());

        userRepository.save(user);

        return "User registered successfully";
    }
    public String updateUser(Long id, UpdateUserRequest request) {
        Optional<UserModel> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return "User not found";
        }

        UserModel user = optionalUser.get();

        // Nếu người dùng thay đổi email, cần kiểm tra email mới đã được ai dùng chưa
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            return "Email already in use";
        }

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail()); // cập nhật email nếu hợp lệ

        userRepository.save(user);
        return "User updated successfully";
    }
}
