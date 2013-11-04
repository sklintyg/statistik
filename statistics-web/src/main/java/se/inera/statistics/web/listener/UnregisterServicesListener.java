package se.inera.statistics.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class UnregisterServicesListener implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(UnregisterServicesListener.class);

    // Public constructor is required by servlet spec
    public UnregisterServicesListener() {
    }

    public void contextInitialized(ServletContextEvent sce) {
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks with respect to this class
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                LOG.error(String.format("deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
                LOG.error(String.format("Error deregistering driver %s", driver), e);
            }

        }
    }

}
