package se.inera.statistics.service.queue;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Aspect
@Component
public class QueueAspect {

    private CountDownLatch countDownLatch;

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Pointcut("execution(* se.inera.statistics.service.queue.JmsReceiver.onMessage(..))")
    public void aopPointcut() {
    }

    @After("aopPointcut()")
    public void afterOnMessage(JoinPoint jp) {
        if(countDownLatch != null) {
            countDownLatch.countDown();
        }
    }
}
