/*
 * Copyright (C) 2013 - 2014 by Inera AB. All rights reserved.
 * Released under the terms of the CPL Common Public License version 1.0.
 */

package se.inera.statistics.queue

class ResultKeys {

    String listOfKeys() {
        StringBuilder sb = new StringBuilder()
        for( String key : IntygSender.testResult.keySet().sort()) {
            sb.append(key).append('\n')
        }
        sb.toString()
    }
}
