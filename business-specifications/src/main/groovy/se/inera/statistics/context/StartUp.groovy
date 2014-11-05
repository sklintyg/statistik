/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.context

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.context.support.GenericXmlApplicationContext

class StartUp {
    static GenericXmlApplicationContext context

    boolean startContext(String contextFile) {
        println("About to read context")
        context = new GenericXmlApplicationContext();
        context.getEnvironment().setActiveProfiles("dev");
        context.load(contextFile);
        context.refresh();
        true
    }

    boolean stopContext() {
        context.close()
        context = null
        true
    }

    static ApplicationContext getContext() {
        return context
    }
}
