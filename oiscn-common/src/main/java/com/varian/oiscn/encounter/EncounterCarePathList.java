package com.varian.oiscn.encounter;

import com.varian.oiscn.core.encounter.EncounterCarePath;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bhp9696 on 2017/11/9.
 * encounterId对应的CarePath instanceId List
 */
@Data
public class EncounterCarePathList implements Serializable {
    private Long encounterId;
    /**  CarePath instanceId */
    private List<EncounterCarePath> encounterCarePathList = new ArrayList<>();

    /**
     * 返回主的CarePathInstanceId
     * @return
     */
    public Long getMasterCarePathInstanceId(){
        EncounterCarePath carePath = null;
        for(EncounterCarePath encounterCarePath:encounterCarePathList){
            if(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY.equals(encounterCarePath.getCategory())){
                if(carePath == null){
                    carePath = encounterCarePath;
                }else{
                    if(encounterCarePath.getCrtTime().compareTo(carePath.getCrtTime()) >0){
                        carePath = encounterCarePath;
                    }
                }
            }
        }
        if(carePath == null){
            return 0L;
        }
        return carePath.getCpInstanceId();
    }

    /**
     * 返可选的CarePathInstanceId，并且按照id逆序排序
     * @return
     */
    public List<Long> getOptionalCarePathInstanceId(){
        List<Long> idList = new ArrayList<>();
        for(EncounterCarePath encounterCarePath:encounterCarePathList){
            if(EncounterCarePath.EncounterCarePathCategoryEnum.OPTIONAL.equals(encounterCarePath.getCategory())){
               idList.add(encounterCarePath.getCpInstanceId());
            }
        }
        Collections.sort(idList);
        Collections.reverse(idList);
        return idList;
    }
}
