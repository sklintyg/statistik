/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.web.spec;

public class Browser {

    public void stäng() {
        geb.Browser.drive {
        }.quit()
    }
}
