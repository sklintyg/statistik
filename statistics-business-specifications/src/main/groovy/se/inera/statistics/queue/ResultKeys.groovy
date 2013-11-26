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
