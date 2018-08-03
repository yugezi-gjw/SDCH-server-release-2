package com.varian.oiscn.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DbConfiguration {

    @JsonProperty
    private String databaseServer;

    @JsonProperty
    private String database;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonProperty
    private String port;

    @JsonProperty
    private String driver;

    @JsonProperty
    private String maxTotal;

    @JsonProperty
    private String initialSize;

    @JsonProperty
    private String maxWaitMillis;

    @JsonProperty
    private String maxIdle;

    @JsonProperty
    private String minIdle;

    @JsonProperty
    private String timeBetweenEvictionRunsMillis;

    @JsonProperty
    private String minEvictableIdleTimeMillis;

}
