package com.varian.oiscn.core.user;

import lombok.Data;

import java.util.Date;

/**
 * Created by gbt1220 on 7/20/2017.
 */
@Data
public class OspLogin {
    private String name;
    private String displayName;
    private String username;
    private String userCUID;
    private String token;
    private Date lastModifiedDt;
}
