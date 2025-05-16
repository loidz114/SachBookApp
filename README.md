# SachBook - Ứng dụng Đọc Sách

## Giới thiệu
SackBook là một ứng dụng Android được thiết kế để cung cấp trải nghiệm đọc sách trực tuyến tốt nhất cho người dùng. Ứng dụng được xây dựng với kiến trúc hiện đại và tuân thủ các nguyên tắc thiết kế Material Design. Với giao diện thân thiện và tính năng phong phú, SackBook mang đến cho người dùng một không gian đọc sách trực tuyến hoàn hảo.

## Tính năng chính

### Đọc sách trực tuyến
- Hỗ trợ nhiều định dạng sách (PDF, EPUB)
- Tùy chỉnh font chữ, kích thước và màu sắc
- Chế độ đọc ban đêm
- Đánh dấu trang và ghi chú

### Quản lý thư viện sách cá nhân
- Tổ chức sách theo danh mục
- Tạo danh sách yêu thích
- Lưu trữ lịch sử đọc
- Đồng bộ hóa dữ liệu đọc

### Tìm kiếm và khám phá
- Tìm kiếm sách theo tên, tác giả, thể loại
- Gợi ý sách dựa trên sở thích
- Xem bảng xếp hạng sách phổ biến
- Đọc đánh giá và bình luận

### Theo dõi tiến độ
- Thống kê thời gian đọc
- Mục tiêu đọc hàng ngày
- Nhắc nhở đọc sách
- Chia sẻ tiến độ đọc

## Công nghệ sử dụng

### Ngôn ngữ và Framework
- Kotlin - Ngôn ngữ lập trình chính
- Android Jetpack - Bộ thư viện Android hiện đại
- Material Design 3 - Thiết kế giao diện người dùng
- Coroutines - Xử lý bất đồng bộ
- Flow - Reactive programming

### Kiến trúc và Pattern
- MVVM (Model-View-ViewModel) - Kiến trúc ứng dụng
- Clean Architecture - Tổ chức code
- Repository Pattern - Quản lý dữ liệu
- Dependency Injection (Hilt) - Quản lý dependencies

### Thư viện chính
- Retrofit - Gọi API
- Room - Cơ sở dữ liệu local
- Glide - Tải và cache hình ảnh
- Navigation Component - Điều hướng
- ViewBinding - Binding views
- DataStore - Lưu trữ preferences

## Cấu trúc dự án
```
app/src/main/
├── java/com/example/sachbook/
│   ├── data/
│   │   ├── local/          # Room database, SharedPreferences
│   │   ├── remote/         # API services, Retrofit
│   │   ├── repository/     # Repository implementations
│   │   └── model/          # Data models
│   ├── di/                 # Dependency Injection modules
│   ├── ui/
│   │   ├── activity/       # Activities
│   │   ├── fragment/       # Fragments
│   │   ├── adapter/        # RecyclerView adapters
│   │   └── viewmodel/      # ViewModels
│   └── util/               # Utility classes
├── res/
│   ├── layout/            # XML layouts
│   ├── drawable/          # Images and icons
│   ├── values/            # Resources (strings, colors, themes)
│   └── navigation/        # Navigation graphs
└── AndroidManifest.xml
```

## Yêu cầu hệ thống
- Android 5.0 (API level 21) trở lên
- RAM: Tối thiểu 2GB
- Bộ nhớ trống: Tối thiểu 100MB
- Kết nối internet để đọc sách trực tuyến
- Google Play Services (để đăng nhập và đồng bộ)

## Cài đặt và Chạy

### Yêu cầu phát triển
- Android Studio Arctic Fox (2020.3.1) trở lên
- JDK 11 trở lên
- Android SDK 21 trở lên
- Gradle 7.0 trở lên

### Các bước cài đặt
1. Clone repository:
```bash
git clone https://github.com/loidz114/SachBookAPI.git
cd sachbook
```

2. Mở project trong Android Studio

3. Cấu hình:
   - Tạo file `local.properties` và thêm đường dẫn SDK
   - Cấu hình API keys trong `gradle.properties`

4. Build project:
```bash
./gradlew build
```

5. Chạy ứng dụng:
   - Chọn thiết bị hoặc emulator
   - Nhấn Run 'app'

## Quy trình phát triển
1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit thay đổi (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## Đóng góp
Mọi đóng góp đều được hoan nghênh! Vui lòng:
1. Fork dự án
2. Tạo branch cho tính năng mới
3. Commit thay đổi
4. Push lên branch
5. Tạo Pull Request

## Cảm ơn
Cảm ơn bạn đã quan tâm đến dự án SackBook. Chúng tôi rất mong nhận được phản hồi và đóng góp từ cộng đồng! 
