package com.varian.oiscn.core.encounter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bhp9696 on 2018/4/9.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EncounterCarePath implements Serializable{
    private Long encounterId;
    private Long cpInstanceId;
    private EncounterCarePathCategoryEnum category;
    private String crtUser;
    private Date crtTime;



    public enum EncounterCarePathCategoryEnum{
        PRIMARY,OPTIONAL;

        public static EncounterCarePathCategoryEnum fromCode(String code){
            EncounterCarePathCategoryEnum category;
            switch (code.toUpperCase()){
                case "PRIMARY":
                    category = PRIMARY;
                    break;
                case "OPTIONAL":
                    category = OPTIONAL;
                    break;
                default:
                    category = PRIMARY;

            }
            return category;
        }
    }


}
