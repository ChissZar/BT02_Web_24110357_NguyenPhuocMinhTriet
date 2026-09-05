package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.AccountService;

@WebServlet(urlPatterns = {"/register", "/activate", "/forgot-password", "/reset-password"})
public class AccountController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final AccountService accounts = new AccountService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        show(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String route = req.getServletPath();
        try {
            String email = req.getParameter("email");
            switch (route) {
                case "/register" -> {
                    confirmPassword(req);
                    accounts.register(req.getParameter("username"), req.getParameter("fullname"), email, req.getParameter("password"));
                    resp.sendRedirect(req.getContextPath() + "/activate"); return;
                }
                case "/forgot-password" -> {
                    accounts.requestOtp(email, "RESET");
                    req.setAttribute("notice", "Nếu email thuộc tài khoản đã kích hoạt và đủ thời gian gửi lại, mã OTP sẽ được gửi. Chọn Đặt lại mật khẩu để nhập mã.");
                }
                case "/activate" -> {
                    if ("resend".equals(req.getParameter("action"))) {
                        accounts.requestOtp(email, "ACTIVATE");
                        req.setAttribute("notice", "Nếu tài khoản đang chờ kích hoạt và đủ thời gian gửi lại, mã OTP sẽ được gửi. Kiểm tra cả thư mục Spam.");
                    } else {
                        if (!accounts.confirm(email, "ACTIVATE", req.getParameter("otp"), null))
                            throw new IllegalArgumentException("OTP không hợp lệ, hết hạn hoặc đã vượt số lần thử.");
                        req.getSession().setAttribute("authNotice", "Kích hoạt thành công. Bạn có thể đăng nhập.");
                        resp.sendRedirect(req.getContextPath() + "/login"); return;
                    }
                }
                case "/reset-password" -> {
                    confirmPassword(req);
                    if (!accounts.confirm(email, "RESET", req.getParameter("otp"), req.getParameter("password")))
                        throw new IllegalArgumentException("OTP không hợp lệ, hết hạn hoặc đã vượt số lần thử.");
                    req.getSession().invalidate();
                    req.getSession(true).setAttribute("authNotice", "Đã đổi mật khẩu. Hãy đăng nhập bằng mật khẩu mới.");
                    resp.sendRedirect(req.getContextPath() + "/login"); return;
                }
                default -> { resp.sendError(404); return; }
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            req.setAttribute("alert", exception.getMessage());
        } catch (RuntimeException exception) {
            getServletContext().log("Account operation failed: " + exception.getClass().getSimpleName());
            req.setAttribute("alert", "Không xử lý được yêu cầu. Kiểm tra lại thông tin hoặc thử lại sau.");
        }
        show(req, resp);
    }

    private void confirmPassword(HttpServletRequest req) {
        String password = req.getParameter("password");
        if (password == null || !password.equals(req.getParameter("confirmPassword")))
            throw new IllegalArgumentException("Hai mật khẩu không khớp.");
    }

    private void show(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("mode", req.getServletPath().substring(1));
        req.getRequestDispatcher("/WEB-INF/views/account-auth.jsp").forward(req, resp);
    }
}
