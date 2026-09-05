package vn.iotstar.filter;

import java.io.IOException;
import java.util.Objects;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.entity.*;
import vn.iotstar.util.Constant;

@WebFilter(urlPatterns = {"/admin/*", "/profile", "/member/*", "/waiting"})
public class AccountSessionFilter implements Filter {
    public void doFilter(ServletRequest input, ServletResponse output, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) input;
        HttpServletResponse resp = (HttpServletResponse) output;
        HttpSession session = req.getSession(false);
        boolean valid = false;
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) instanceof User account) {
            var em = JPAConfig.getEntityManager();
            try {
                User current = em.find(User.class, account.getId());
                AccountSecurity security = em.find(AccountSecurity.class, account.getId());
                valid = current != null && (security == null || security.activated)
                        && Objects.equals(current.getPassWord(), account.getPassWord());
            } finally { em.close(); }
        }
        if (!valid) {
            if (session != null) session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login"); return;
        }
        chain.doFilter(req, resp);
    }
}
