# Dự án học phần Lập trình web

## Thông tin học phần

| Nội dung | Thông tin |
| --- | --- |
| Sinh viên | Nguyễn Phước Minh Triết |
| MSSV | 24110357 |
| GVHD | ThS. Nguyễn Hữu Trung |
| Mã lớp học phần | 261WEPR330479_09 |
| Trường | Trường ĐH Công Nghệ Kỹ Thuật Tp.HCM |

## Giới thiệu

Ứng dụng web được xây dựng và phát triển xuyên suốt học phần Lập trình web. Các chức năng được bổ sung theo từng bài thực hành, trên cùng cấu trúc dự án MVC và kiến trúc Controller – Service – DAO.

Dự án hiện sử dụng Java Servlet, JSP và JPA để quản lý tài khoản, hồ sơ người dùng và danh mục. README được cập nhật cùng các yêu cầu mới của học phần.

## Chức năng hiện tại

- Đăng ký tài khoản và kích hoạt bằng OTP gửi qua email.
- Đăng nhập, đăng xuất và quản lý phiên bằng Session.
- Ghi nhớ tên đăng nhập bằng Cookie trong 30 phút; vẫn yêu cầu mật khẩu khi đăng nhập.
- Quên mật khẩu và đặt lại mật khẩu bằng OTP email.
- Xem, tìm kiếm, thêm, sửa và xóa Category.
- Cập nhật họ tên, số điện thoại và ảnh đại diện bằng JPA.
- Upload ảnh qua multipart và hiển thị ảnh qua servlet.
- Sử dụng SiteMesh để quản lý bố cục trang hồ sơ.
- Cấu hình và kiểm tra JPA với các entity User, Category và Video.

## Công nghệ

- Java 17 trở lên, Maven, Apache Tomcat 11.
- Jakarta Servlet 6.1, JSP 4.0, JSTL 3.
- JPA với Hibernate ORM, SQL Server và JDBC.
- SiteMesh 3.3.0-RC1.
- Jakarta Mail với Eclipse Angus để gửi email qua SMTP.
- H2 cho kiểm thử nghiệp vụ tài khoản trong bộ nhớ.

## Cấu trúc dự án

```text
database/                       Script khởi tạo database và bảng OTP
src/main/java/vn/iotstar/
├── config/                     Cấu hình và kiểm tra JPA
├── controller/                 Servlet xử lý yêu cầu
├── dao/                        Truy xuất dữ liệu bằng JPA
├── entity/                     Ánh xạ các bảng dữ liệu
├── filter/                     Kiểm tra phiên, CSRF và giới hạn yêu cầu
├── listener/                   Quản lý vòng đời kết nối JPA
├── service/                    Xử lý nghiệp vụ và gửi OTP
└── util/                       Tiện ích mật khẩu và upload
src/main/resources/META-INF/    Cấu hình persistence
src/main/webapp/
├── assets/                     CSS và ảnh mặc định
├── views/                      Trang đăng nhập và CRUD Category
└── WEB-INF/                    Cấu hình web, SiteMesh và các JSP nội bộ
src/test/                       Mã và cấu hình kiểm thử
pom.xml                         Dependency và cấu hình Maven
```

## Cài đặt và chạy

### Database

Chạy `database/create_database.sql` trên SQL Server để tạo database `jakartaJPA` và các bảng `users`, `categories`, `videos`.

Cấu hình kết nối trong `src/main/resources/META-INF/persistence.xml` theo SQL Server trên máy:

- `jakarta.persistence.jdbc.url`: server, cổng và tên database.
- `jakarta.persistence.jdbc.user`: tài khoản SQL Server.
- `jakarta.persistence.jdbc.password`: mật khẩu SQL Server.

Dự án đang dùng `hibernate.hbm2ddl.auto=update` để tạo hoặc cập nhật bảng theo entity. Hai bảng `account_security` và `registration_keys` phục vụ kích hoạt tài khoản và OTP. Có thể tạo chúng bằng `database/auth-otp.sql` sau khi tạo bảng `users`.

### Email gửi OTP

Trong Eclipse/STS, mở server Tomcat → **Open launch configuration → Environment** và khai báo:

| Biến | Giá trị |
| --- | --- |
| `SMTP_HOST` | Máy chủ SMTP, ví dụ `smtp.gmail.com` |
| `SMTP_PORT` | `587` mặc định, sử dụng STARTTLS |
| `SMTP_USER` | Địa chỉ email dùng để gửi OTP |
| `SMTP_PASSWORD` | Mật khẩu ứng dụng của email gửi |
| `SMTP_FROM` | Địa chỉ gửi, thường giống `SMTP_USER` |

Thông tin SMTP được đọc từ biến môi trường và được dùng chung cho ứng dụng. Email nhận OTP là email người dùng nhập khi đăng ký. Với Gmail, dùng mật khẩu ứng dụng của tài khoản gửi; nhập liền 16 ký tự, bỏ khoảng trắng phân nhóm. Khởi động lại Tomcat sau khi thay đổi cấu hình.

### Upload ảnh

Thư mục mặc định là `uploads` trong thư mục người dùng hệ điều hành. Có thể thay đổi bằng biến môi trường `UPLOAD_DIR` hoặc VM option:

```text
-Dupload.dir=D:\upload
```

Tài khoản chạy Tomcat cần quyền đọc và ghi thư mục này. Database lưu tên file ảnh; file ảnh được lưu ngoài thư mục triển khai web.

### Chạy trên Tomcat

1. Import bằng **Existing Maven Projects** trong Eclipse/STS.
2. Chọn JDK 17 trở lên và **Maven → Update Project**.
3. Cấu hình Tomcat 11, database và SMTP.
4. Chọn **Run As → Run on Server**.

Ví dụ với Tomcat chạy cổng 8081:

```text
http://localhost:8081/BT02_JPA_Category/login
```

Cổng và context path phụ thuộc cấu hình triển khai. Tên WAR hiện giữ là `BT02_JPA_Category` để tương thích với cấu hình server đã sử dụng.

### Build

```bash
mvn package
```

Kết quả: `target/BT02_JPA_Category.war`. Thư mục `target` là đầu ra Maven và được loại khỏi Git.

## Các đường dẫn chính

| Đường dẫn | Chức năng |
| --- | --- |
| `/register` | Đăng ký |
| `/activate` | Kích hoạt và gửi lại OTP |
| `/login`, `/logout` | Đăng nhập, đăng xuất |
| `/forgot-password` | Yêu cầu OTP đặt lại mật khẩu |
| `/reset-password` | Xác nhận OTP và đổi mật khẩu |
| `/admin/categories` | Quản lý Category |
| `/profile`, `/member/myaccount` | Hồ sơ cá nhân |
| `/image` | Hiển thị ảnh upload |

## Quy tắc xử lý

- Tài khoản đăng ký mới cần kích hoạt email trước khi đăng nhập.
- OTP gồm 8 chữ số, có hiệu lực 10 phút, sử dụng một lần và gắn với mục đích kích hoạt hoặc đặt lại mật khẩu.
- Gửi lại OTP cách nhau ít nhất 60 giây. Tối đa 5 lần thử sai trong cửa sổ hiệu lực; gửi lại không xóa số lần thử.
- Khi gửi email thất bại, tài khoản có thể đã được lưu ở trạng thái chưa kích hoạt. Chức năng gửi lại OTP được dùng sau khi cấu hình email hoạt động.
- Mật khẩu được băm bằng PBKDF2-HMAC-SHA256. Chính sách hiện tại phục vụ thực hành: không rỗng, tối đa 128 ký tự và xác nhận trùng khớp.
- Tài khoản cũ chưa có bản ghi bảo mật được coi là đã kích hoạt; mật khẩu dạng cũ được chuyển sang dạng băm khi đăng nhập thành công.
- Đặt lại mật khẩu yêu cầu đăng nhập lại; phiên cũ bị từ chối khi truy cập trang được bảo vệ.
- Ảnh đại diện nhận JPEG/PNG tối đa 5 MB và 16 triệu điểm ảnh. Không chọn ảnh mới sẽ giữ ảnh hiện tại.
- SiteMesh chỉ áp dụng cho hai đường dẫn hồ sơ, dùng dispatcher `include` cho Tomcat 11.
- Các POST xác thực có kiểm tra CSRF và giới hạn 20 yêu cầu/15 phút/IP trong một tiến trình.

## Kiểm thử

Các lớp sau có hàm `main`, chạy bằng **Run As → Java Application**:

| Lớp | Nội dung |
| --- | --- |
| `TestJpa` | Kiểm tra kết nối JPA và thêm dữ liệu mẫu vào database đang cấu hình |
| `PasswordsCheck` | Băm mật khẩu, salt, Unicode và kiểm tra độ dài |
| `OtpPolicyCheck` | Hạn OTP, gửi lại, giới hạn thử và sử dụng một lần |
| `AccountServiceCheck` | Luồng tài khoản với H2 và hộp thư giả |
| `ProfileControllerCheck` | Kiểm tra logic cập nhật hồ sơ |

Các lớp `*Check` là chương trình kiểm tra độc lập, không tự chạy qua `mvn test`. `AccountServiceCheck` dùng persistence unit `accounts-test` với database trong bộ nhớ. `TestJpa` có ghi dữ liệu mẫu, còn việc gửi email thực tế được kiểm tra qua ứng dụng trên Tomcat.

Các luồng kiểm tra trên web gồm đăng ký → kích hoạt → đăng nhập; quên mật khẩu → OTP → đổi mật khẩu; cập nhật hồ sơ và ảnh; CRUD Category.

## Phát triển tiếp

Các yêu cầu mới của học phần được bổ sung vào các tầng hiện có. Mỗi lần cập nhật chức năng sẽ cập nhật entity/script dữ liệu, giao diện, kiểm thử và phần hướng dẫn tương ứng trong README.

## Tài liệu tham khảo

- [Jakarta Persistence](https://jakarta.ee/specifications/persistence/)
- [SiteMesh](https://github.com/sitemesh/sitemesh3)
- [Eclipse Angus Mail](https://eclipse-ee4j.github.io/angus-mail/)
