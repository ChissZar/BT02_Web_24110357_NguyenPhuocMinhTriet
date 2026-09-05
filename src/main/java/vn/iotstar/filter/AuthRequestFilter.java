package vn.iotstar.filter;

import java.io.IOException;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

@WebFilter(urlPatterns = {"/login", "/register", "/activate", "/forgot-password", "/reset-password"})
public class AuthRequestFilter implements Filter {
    private final Map<String, long[]> requests = new HashMap<>();

    private synchronized boolean allow(String address) {
        long now = System.currentTimeMillis();
        requests.entrySet().removeIf(e -> now - e.getValue()[0] >= 900000);
        if (!requests.containsKey(address) && requests.size() >= 10000) return false;
        long[] window = requests.computeIfAbsent(address, key -> new long[]{now, 0});
        return ++window[1] <= 20;
    }

    public void doFilter(ServletRequest input, ServletResponse output, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) input;
        HttpServletResponse resp = (HttpServletResponse) output;
        req.setCharacterEncoding("UTF-8"); resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.setHeader("Referrer-Policy", "no-referrer");
        HttpSession session = req.getSession();
        if (session.getAttribute("authCsrf") == null) session.setAttribute("authCsrf", UUID.randomUUID().toString());
        if ("POST".equals(req.getMethod())) {
            if (!session.getAttribute("authCsrf").equals(req.getParameter("csrf"))) { resp.sendError(403); return; }
            if (!allow(req.getRemoteAddr())) {
                resp.setHeader("Retry-After", "900");
                resp.sendError(429, "Quá nhiều yêu cầu. Vui lòng thử lại sau 15 phút."); return;
            }
        }
        chain.doFilter(req, resp);
    }
}
