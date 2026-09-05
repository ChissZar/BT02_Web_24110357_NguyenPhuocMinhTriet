package vn.iotstar.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.imageio.ImageIO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

@WebServlet(urlPatterns = {"/profile", "/member/myaccount"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class ProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IUserService users;

    public ProfileController() {
        this(new UserServiceImpl());
    }

    ProfileController(IUserService users) {
        this.users = users;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        HttpSession session = req.getSession(false);
        if (session == null || !(session.getAttribute(Constant.SESSION_ACCOUNT) instanceof User account)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User current = users.findById(account.getId());
        if (current == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("profile", current);
        if (session.getAttribute("profileCsrf") == null)
            session.setAttribute("profileCsrf", UUID.randomUUID().toString());
        super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        show(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Path uploaded = null;
        boolean saved = false;
        try {
            if (!req.getSession().getAttribute("profileCsrf").equals(req.getParameter("csrf"))) {
                resp.sendError(403);
                return;
            }
            String name = req.getParameter("fullname");
            String phone = req.getParameter("phone");
            req.setAttribute("submitted", true);
            req.setAttribute("fullNameInput", name);
            req.setAttribute("phoneInput", phone);
            Part image = req.getPart("images");
            String avatar = null;
            if (image != null && image.getSize() > 0) {
                try (var input = image.getInputStream();
                     var stream = ImageIO.createImageInputStream(input)) {
                    var readers = ImageIO.getImageReaders(stream);
                    if (!readers.hasNext()) throw new IllegalArgumentException("Chỉ nhận ảnh JPEG hoặc PNG");
                    var reader = readers.next();
                    try {
                        reader.setInput(stream);
                        String format = reader.getFormatName().toLowerCase(java.util.Locale.ROOT);
                        if (!format.equals("jpeg") && !format.equals("png"))
                            throw new IllegalArgumentException("Chỉ nhận ảnh JPEG hoặc PNG");
                        if ((long) reader.getWidth(0) * reader.getHeight(0) > 16000000)
                            throw new IllegalArgumentException("Ảnh tối đa 16 triệu điểm ảnh");
                        var decoded = reader.read(0);
                        avatar = "profile-" + UUID.randomUUID() + "." + format;
                        Files.createDirectories(Constant.uploadDirectory());
                        uploaded = Constant.uploadDirectory().resolve(avatar);
                        if (!ImageIO.write(decoded, format, uploaded.toFile())) throw new IOException("Không thể lưu ảnh");
                    } finally {
                        reader.dispose();
                    }
                }
            }
            User current = (User) req.getAttribute("profile");
            User updated = users.updateProfile(current.getId(), name, phone, avatar);
            saved = true;
            req.getSession().setAttribute(Constant.SESSION_ACCOUNT, updated);
            req.getSession().setAttribute("profileSuccess", "Đã cập nhật hồ sơ");
            resp.sendRedirect(req.getContextPath() + "/profile");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            req.setAttribute("alert", exception instanceof IllegalStateException
                    ? "Ảnh tối đa 5 MB; tổng dữ liệu gửi tối đa 6 MB" : exception.getMessage());
            show(req, resp);
        } catch (RuntimeException | IOException exception) {
            log("Profile update failed", exception);
            req.setAttribute("alert", "Không thể lưu hồ sơ. Vui lòng thử lại.");
            show(req, resp);
        } finally {
            if (!saved && uploaded != null) {
                try { Files.deleteIfExists(uploaded); } catch (IOException exception) { log("Upload cleanup failed", exception); }
            }
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/WEB-INF/views/profile.jsp").include(req, resp);
    }
}
