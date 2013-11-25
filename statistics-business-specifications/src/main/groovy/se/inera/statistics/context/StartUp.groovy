package se.inera.statistics.context

import org.springframework.context.support.ClassPathXmlApplicationContext

class StartUp {
    static ClassPathXmlApplicationContext context

    boolean startContext(String contextFile) {
        println("About to read context")
        context = new ClassPathXmlApplicationContext(contextFile)
        true
    }

    boolean stopContext() {
        context.stop()
        context = null
        Thread.sleep(10000)
        true
    }

    static ClassPathXmlApplicationContext getContext() {
        return context
    }
}
