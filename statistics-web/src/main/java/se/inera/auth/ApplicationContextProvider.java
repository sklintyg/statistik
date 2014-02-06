package se.inera.auth;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext context = null;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public void setApplicationContext(ApplicationContext ctx) {
        setContext(ctx);
    }

    private static void setContext(ApplicationContext ctx) {
        context = ctx;
    }
}
