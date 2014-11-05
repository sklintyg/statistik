package se.inera.ifv.statistics.spi.authorization.impl;

@SuppressWarnings("serial")
public class HsaCommunicationException extends RuntimeException {

    public HsaCommunicationException(String message, Throwable ex) {
        super(message, ex);
    }

}
