/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

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
        context.close()
        context = null
        true
    }

    static ClassPathXmlApplicationContext getContext() {
        return context
    }
}
