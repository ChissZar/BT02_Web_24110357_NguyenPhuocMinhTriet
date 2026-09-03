package vn.iotstar.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import vn.iotstar.config.JPAConfig;

@WebListener
public class JpaContextListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        JPAConfig.close();
    }
}
