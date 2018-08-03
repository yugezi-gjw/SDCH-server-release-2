package com.varian.oiscn.connection;

import com.varian.oiscn.config.Configuration;

/**
 * Created by gbt1220 on 3/27/2017.
 * Modified by bhp9696 on 5/7/2017.
 */

public class ConnectionParam {
    private static String USER = null;
    private static String PASSWORD = null;
    private static String URL = null;
    private static String DRIVER = null;
    private static String DATABASE = null;
    private static String IP = null;
    private static String PORT = null;

    private static String MAX_TOTAL = null;
    private static String INITIAL_SIZE = null;
    private static String MAX_IDLE = null;
    private static String MAX_WAIT_MILLIS = null;
    private static String MIN_IDLE = null;
    private static String TIME_BETWEEN_EVICTION_RUNS_MILLIS = null;
    private static String MIN_EVICTABLE_IDLE_TIME_MILLIS = null;

    private ConnectionParam(){

    }

    public static void initParam(Configuration configuration) {
        DRIVER = configuration.getDatabase().getDriver();
        USER = configuration.getDatabase().getUsername();
        PASSWORD = configuration.getDatabase().getPassword();
        IP = configuration.getDatabase().getDatabaseServer();
        PORT = configuration.getDatabase().getPort();
        DATABASE = configuration.getDatabase().getDatabase();
        URL = "jdbc:sqlserver://" + IP + ":" + PORT + ";databaseName=" + DATABASE;
        MAX_TOTAL = configuration.getDatabase().getMaxTotal();
        INITIAL_SIZE = configuration.getDatabase().getInitialSize();
        MAX_WAIT_MILLIS = configuration.getDatabase().getMaxWaitMillis();
        MAX_IDLE = configuration.getDatabase().getMaxIdle();
        MIN_IDLE = configuration.getDatabase().getMinIdle();
        TIME_BETWEEN_EVICTION_RUNS_MILLIS = configuration.getDatabase().getTimeBetweenEvictionRunsMillis();
        MIN_EVICTABLE_IDLE_TIME_MILLIS = configuration.getDatabase().getMinEvictableIdleTimeMillis();
    }

    public static String getUSER() {
        return USER;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static String getURL() {
        return URL;
    }

    public static String getMAXTOTAL() {
        return MAX_TOTAL;
    }

    public static String getINITIALSIZE() {
        return INITIAL_SIZE;
    }

    public static String getMAXWAITMILLIS() {
        return MAX_WAIT_MILLIS;
    }

    public static String getMAXIDLE() {
        return MAX_IDLE;
    }


    public static String getDRIVER() {
        return DRIVER;
    }

    public static String getMINIDLE() {
        return MIN_IDLE;
    }

    public static String getTIMEBETWEENEVICTIONRUNSMILLIS() {
        return TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    }

    public static String getMINEVICTABLEIDLETIMEMILLIS() {
        return MIN_EVICTABLE_IDLE_TIME_MILLIS;
    }


}

