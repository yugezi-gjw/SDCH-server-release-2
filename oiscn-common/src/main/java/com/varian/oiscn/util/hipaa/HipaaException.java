package com.varian.oiscn.util.hipaa;

public class HipaaException extends Exception {
    private HipaaLogMessage message;

    public HipaaException(HipaaLogMessage message, Exception e){
        super(e);
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("HipaaMessage: %s\r\nCause: %s", message, getCause());
    }
}
