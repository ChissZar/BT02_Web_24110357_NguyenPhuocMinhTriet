package vn.iotstar.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            response.sendRedirect(request.getContextPath() + "/waiting");
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constant.COOKIE_REMEMBER.equals(cookie.getName())) {
                    try {
                        request.setAttribute("rememberedUsername", URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));
                    } catch (IllegalArgumentException ignored) { }
                }
            }
        }

        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String username = normalize(request.getParameter("username"));
        String password = request.getParameter("password");
        boolean rememberMe = "on".equals(request.getParameter("remember"));

        if (username.isEmpty() || password == null || password.isEmpty() || password.length() > 128) {
            request.setAttribute("alert", "Tài khoản hoặc mật khẩu không được rỗng");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        User user = userService.login(username, password);
        if (user == null) {
            request.setAttribute("alert", "Tài khoản/mật khẩu không đúng hoặc tài khoản chưa kích hoạt.");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute(Constant.SESSION_ACCOUNT, user);
        session.setMaxInactiveInterval(30 * 60);

        if (rememberMe) {
            Cookie cookie = new Cookie(
                    Constant.COOKIE_REMEMBER,
                    URLEncoder.encode(username, StandardCharsets.UTF_8));
            cookie.setMaxAge(30 * 60);
            cookie.setHttpOnly(true);
            cookie.setSecure(request.isSecure());
            cookie.setAttribute("SameSite", "Lax");
            cookie.setPath(cookiePath(request));
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, "");
            cookie.setMaxAge(0);
            cookie.setPath(cookiePath(request));
            response.addCookie(cookie);
        }

        response.sendRedirect(request.getContextPath() + "/waiting");
    }

    private String cookiePath(HttpServletRequest request) {
        return request.getContextPath().isEmpty() ? "/" : request.getContextPath();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
