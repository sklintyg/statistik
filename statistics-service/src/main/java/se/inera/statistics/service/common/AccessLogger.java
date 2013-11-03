package se.inera.statistics.service.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessLogger {
    private static final Logger LOG = LoggerFactory.getLogger(AccessLogger.class);

    public void logCall(ProceedingJoinPoint joinpoint) throws Throwable {

        try {
            LOG.info("Calling protected method: ");
            joinpoint.proceed();
        } catch (Throwable throwable) {
            LOG.error(throwable.getMessage());
            throw throwable;
        }

    }
}
