package vn.iotstar.controller;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

public class ProfileControllerCheck {
    interface Call { Object invoke(String name, Object[] args) throws Throwable; }

    @SuppressWarnings("unchecked")
    static <T> T proxy(Class<T> type, Call call) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (object, method, args) -> call.invoke(method.getName(), args));
    }

    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> sessionData = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();
        Map<String, Object> results = new HashMap<>();
        User user = new User();
        user.setId(7);
        user.setAvatar("old.png");
        int[] updates = {0};
        IUserService users = new IUserService() {
            public User findById(int id) { check(id == 7, "Use session user ID"); return user; }
            public User get(String username) { return user; }
            public User login(String username, String password) { return user; }
            public User updateProfile(int id, String name, String phone, String avatar) {
                check(id == 7, "Cannot update another user");
                check(avatar == null, "Keep avatar when no file is selected");
                user.setFullName(name);
                user.setPhone(phone);
                updates[0]++;
                return user;
            }
        };
        HttpSession session = proxy(HttpSession.class, (name, values) -> {
            if (name.equals("getAttribute")) return sessionData.get(values[0]);
            if (name.equals("setAttribute")) sessionData.put((String) values[0], values[1]);
            return null;
        });
        RequestDispatcher dispatcher = proxy(RequestDispatcher.class, (name, values) -> {
            results.put("view", name); return null;
        });
        Part[] part = {null};
        HttpServletRequest request = proxy(HttpServletRequest.class, (name, values) -> switch (name) {
            case "getSession" -> session;
            case "getContextPath" -> "/app";
            case "getMethod" -> "POST";
            case "getParameter" -> parameters.get(values[0]);
            case "getAttribute" -> attributes.get(values[0]);
            case "setAttribute" -> { attributes.put((String) values[0], values[1]); yield null; }
            case "getPart" -> part[0];
            case "getRequestDispatcher" -> dispatcher;
            default -> null;
        });
        HttpServletResponse response = proxy(HttpServletResponse.class, (name, values) -> {
            if (name.equals("sendRedirect") || name.equals("sendError")) results.put(name, values[0]);
            return null;
        });
        ProfileController controller = new ProfileController(users);
        controller.service(request, response);
        check("/app/login".equals(results.get("sendRedirect")), "Guest must log in");
        sessionData.put(Constant.SESSION_ACCOUNT, user);
        controller.service(request, response);
        check(Integer.valueOf(403).equals(results.get("sendError")), "Reject missing CSRF");
        check(updates[0] == 0, "No update on rejected request");
        parameters.put("csrf", (String) sessionData.get("profileCsrf"));
        parameters.put("fullname", "Nguyễn Văn An");
        parameters.put("phone", "0901234567");
        parameters.put("id", "999");
        controller.service(request, response);
        check(updates[0] == 1, "Profile updated once");
        check("/app/profile".equals(results.get("sendRedirect")), "Redirect after save");
        check("old.png".equals(user.getAvatar()), "Existing image retained");
        check(sessionData.get(Constant.SESSION_ACCOUNT) == user, "Refresh session account");
        part[0] = proxy(Part.class, (name, values) -> switch (name) {
            case "getSize" -> 5L;
            case "getInputStream" -> new ByteArrayInputStream("hello".getBytes());
            default -> null;
        });
        controller.service(request, response);
        check(updates[0] == 1, "Reject non-image file without database update");
        check(attributes.get("alert") != null, "Display upload error");
        check("include".equals(results.get("view")), "Render view through include");
        UserServiceImpl service = new UserServiceImpl();
        String[][] invalid = {{"", ""}, {" ", ""}, {"x".repeat(101), ""},
                {"An", "abc"}, {"An", "1".repeat(21)}};
        for (String[] input : invalid) {
            try {
                service.updateProfile(7, input[0], input[1], null);
                throw new AssertionError("Invalid profile accepted");
            } catch (IllegalArgumentException expected) { }
        }
        System.out.println("PASS: authentication, CSRF, profile update, session refresh, avatar retention, invalid upload, validation");
    }
}
