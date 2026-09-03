# Bài tập 02 - CRUD Category bằng JPA

Project thực hiện đầy đủ các nội dung của Bài tập 02:

1. Login với Cookie và Session theo mô hình MVC, truy xuất User bằng JPA.
2. Cấu hình và kiểm tra JPA với Hibernate, SQL Server.
3. CRUD Category theo mô hình MVC và kiến trúc 3 tầng: Controller - Service - DAO.

## Công nghệ sử dụng

- JDK 17 trở lên
- Maven
- Tomcat 11
- Jakarta Servlet 6.1, JSP 4.0, JSTL 3
- Jakarta Persistence API qua Hibernate ORM 7.4.6.Final
- SQL Server JDBC 12.10.0

## Cấu trúc chính

```text
src/main/java/vn/iotstar
├── config       JPAConfig và TestJpa
├── controller   Servlet Login, Logout, CRUD và hiển thị ảnh
├── dao          Interface DAO
├── dao/impl     Các thao tác JPA với EntityManager
├── entity       User, Category và Video
├── listener     Đóng EntityManagerFactory khi dừng ứng dụng
├── service      Interface Service
├── service/impl Xử lý nghiệp vụ Category
└── util          Thư mục upload
```

## 1. Tạo database

Mở SQL Server Management Studio và chạy file:

```text
database/create_database.sql
```

Hibernate sẽ tự tạo hoặc cập nhật ba bảng `users`, `categories` và `videos` nhờ cấu hình:

```xml
<property name="hibernate.hbm2ddl.auto" value="update"/>
```

## 2. Cấu hình kết nối SQL Server

Mở file:

```text
src/main/resources/META-INF/persistence.xml
```

Kiểm tra lại bốn giá trị sau theo SQL Server trên máy:

```text
Server:   localhost:1433
Database: jakartaJPA
User:     sa
Password: 1234567@a$
```

Nếu SQL Server dùng mật khẩu khác, sửa thuộc tính `jakarta.persistence.jdbc.password`.

## 3. Test cấu hình JPA

Trong IDE, chạy hàm `main` của lớp:

```text
vn.iotstar.config.TestJpa
```

Hoặc chạy bằng Maven:

```bash
mvn clean compile exec:java
```

Khi thành công, Console hiển thị SQL tạo/thêm dữ liệu và ba dòng:

```text
Cấu hình JPA thành công.
Category vừa tạo có ID: ...
Tổng số Category: ...
Tài khoản đăng nhập: trungnh / 123
```

## 4. Chạy CRUD Category

1. Import project dưới dạng Maven project.
2. Chọn JDK 17 trở lên.
3. Cấu hình Tomcat 11.
4. Chạy project và mở:

```text
http://localhost:8080/BT02_JPA_Category/login
```

Đăng nhập bằng tài khoản được tạo bởi `TestJpa`:

```text
Username: trungnh
Password: 123
```

Các chức năng đã có:

- Hiển thị và tìm kiếm Category.
- Thêm Category bằng link ảnh hoặc file upload.
- Cập nhật tên, ảnh và trạng thái.
- Xóa Category.
- Hiển thị ảnh upload qua servlet `/image`.
- Lưu User vào Session sau khi đăng nhập.
- Lưu username vào Cookie trong 30 phút khi chọn “Nhớ tài khoản”.
- Hủy Session và Cookie khi đăng xuất.

Thư mục upload mặc định là `uploads` trong thư mục người dùng. Có thể đổi khi chạy Tomcat bằng biến môi trường `UPLOAD_DIR` hoặc Java VM option:

```text
-Dupload.dir=D:\upload
```

## 5. Build file WAR

```bash
mvn clean package
```

File tạo ra:

```text
target/BT02_JPA_Category.war
```

## 6. Đưa source code lên GitHub

Tạo repository trống trên GitHub, mở Terminal tại thư mục project và chạy:

```bash
git init
git add .
git commit -m "Hoan thanh bai tap 02 CRUD Category bang JPA"
git branch -M main
git remote add origin https://github.com/TEN_TAI_KHOAN/TEN_REPOSITORY.git
git push -u origin main
```

Sau khi push thành công, nộp đường dẫn repository trên UTExLMS.

## Minh chứng nên chụp

1. File `persistence.xml` và Console báo cấu hình JPA thành công.
2. Ba bảng `users`, `categories`, `videos` trong SQL Server.
3. Trang Login, Session và Cookie `username` trên trình duyệt.
4. Trang danh sách Category.
5. Kết quả thêm, sửa và xóa Category.
6. Repository GitHub hiển thị đầy đủ source code.
