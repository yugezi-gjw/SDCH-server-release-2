package com.varian.oiscn.util;

import lombok.Setter;
import org.slf4j.Logger;

/**
 * Logging Utility.<br>
 */
@Setter
public class LogUtil {

    protected static boolean performanceLogging = false;
    
    private LogUtil(){
    }

    public static void performanceLogging(Logger log, String msg, long elapse) {
        if(log != null && performanceLogging) {
            log.warn("{} - {} ms ", msg, elapse);
        }
    }

    public static void setPerformanceLogging(boolean b) {
        performanceLogging = b;
    }
}
