package com.varian.oiscn.encounter.targetvolume;


import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.targetvolume.TargetVolume;
import com.varian.oiscn.core.targetvolume.TargetVolumeGroupVO;
import com.varian.oiscn.core.targetvolume.TargetVolumeItem;
import com.varian.oiscn.core.targetvolume.TargetVolumeVO;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by BHP9696 on 2017/7/25.
 */
@Slf4j
public class TargetVolumeServiceImp {

    private TargetVolumeDAO targetVolumeDAO;
    private EncounterDAO encounterDAO;

    public TargetVolumeServiceImp(UserContext userContext) {
        targetVolumeDAO = new TargetVolumeDAO(userContext);
        encounterDAO = new EncounterDAO(userContext);
    }

    public boolean saveTargetVolume(TargetVolumeGroupVO targetVolumeGroupVO) {
        boolean ok = true;
        Connection conn = null;
        try {
        	conn = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(conn,false);
//          First Query TargetVolume,Then delete these
            boolean delOk = true;
            List<TargetVolume> list = targetVolumeDAO.selectTargetVolumeExceptItemByPatientSer(conn,
                    targetVolumeGroupVO.getPatientSer(),new Long(targetVolumeGroupVO.getEncounterId()));
            if(list != null && !list.isEmpty()){
                delOk = targetVolumeDAO.batchDelete(conn,list.stream().map(TargetVolume :: getId).collect(Collectors.toList()));
            }
            if(delOk) {
                List<TargetVolumeVO> targetVolumeVOList = targetVolumeGroupVO.getTargetVolumeList();
                List<TargetVolume> targetVolumeList = new ArrayList<>();
                targetVolumeVOList.forEach(targetVolumeVO -> {
                    TargetVolume tv = new TargetVolume();
                    tv.setHisId(targetVolumeGroupVO.getHisId());
                    tv.setEncounterId(targetVolumeGroupVO.getEncounterId());
                    tv.setPatientSer(targetVolumeGroupVO.getPatientSer());
                    tv.setName(targetVolumeVO.getName());
                    tv.setMemo(targetVolumeVO.getMemo());
                    targetVolumeList.add(tv);

                    List<LinkedHashMap<String, String>> itemVOListList = targetVolumeVO.getTargetVolumeItemList();
                    List<TargetVolumeItem> targetVolumeItemList = new ArrayList<>();
                    tv.setTargetVolumeItemList(targetVolumeItemList);
                    for (int i = 0; i < itemVOListList.size(); i++) {
                        Map<String, String> itemVoMap = itemVOListList.get(i);
                        Map.Entry<String, String> entry;
                        Iterator<Map.Entry<String, String>> it = itemVoMap.entrySet().iterator();
                        int j = 0;
                        while (it.hasNext()) {
                            entry = it.next();
                            TargetVolumeItem targetVolumeItem = new TargetVolumeItem();
                            targetVolumeItem.setFieldId(entry.getKey());
                            targetVolumeItem.setFieldValue(entry.getValue());
                            targetVolumeItem.setRNum(i + 1);
                            targetVolumeItem.setSeq(++j);
                            targetVolumeItemList.add(targetVolumeItem);
                        }
                    }
                });
                ok = targetVolumeDAO.create(conn, targetVolumeList);
                if (ok) {
                    conn.commit();
                }
            }
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(conn);
            ok = false;
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return ok;
    }

    public TargetVolumeGroupVO queryTargetVolumeGroupByPatientSer(Long patientSer,Long encounterId) {
        Connection conn = null;
        TargetVolumeGroupVO targetVolumeGroupVO = new TargetVolumeGroupVO();
        try {
        	conn = ConnectionPool.getConnection();
            List<TargetVolume> targetVolumeList = targetVolumeDAO.selectTargetVolumeByPatientSer(conn,patientSer,encounterId);
            if(!targetVolumeList.isEmpty()) {
                targetVolumeGroupVO.setPatientSer(patientSer);
                targetVolumeGroupVO.setEncounterId(targetVolumeList.get(0).getEncounterId());
                List<TargetVolumeVO> targetVolumeVOList = new ArrayList<>();
                targetVolumeGroupVO.setTargetVolumeList(targetVolumeVOList);
                targetVolumeList.forEach(targetVolume -> {
//                    targetVolume 对象转换成targetVolumeVO对象
                    TargetVolumeVO targetVolumeVO = new TargetVolumeVO();
                    targetVolumeVO.setId(targetVolume.getId());
                    targetVolumeVO.setName(targetVolume.getName());
                    targetVolumeVO.setMemo(targetVolume.getMemo());
                    targetVolumeVOList.add(targetVolumeVO);

                    List<LinkedHashMap<String,String>> itemVOList = new ArrayList<>();
                    targetVolumeVO.setTargetVolumeItemList(itemVOList);
                    List<TargetVolumeItem> itemList = targetVolume.getTargetVolumeItemList();
//                  将targetVolume中的元素组织成前端需要的格式
                    Map<Integer,Map<String,String>> tmpMap = new LinkedHashMap<>();
                    itemList.forEach(targetVolumeItem -> {
                        Map<String,String> tmap = tmpMap.get(targetVolumeItem.getRNum());
                        if(tmap == null){
                            tmap = new LinkedHashMap<>();
                            tmpMap.put(targetVolumeItem.getRNum(),tmap);
                        }
                        tmap.put(targetVolumeItem.getFieldId(),targetVolumeItem.getFieldValue());
                    });
                    tmpMap.entrySet().forEach(entry->{
                        itemVOList.add((LinkedHashMap<String, String>) entry.getValue());
                    });
                });
            }
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return targetVolumeGroupVO;
    }

    public TargetVolumeGroupVO queryTargetVolumeGroupOnlyTargetVolumeExceptItemByPatientSer(Long patientSer,Long encounterId) {
        Connection conn = null;
        TargetVolumeGroupVO targetVolumeGroupVO = new TargetVolumeGroupVO(){{
            setTargetVolumeList(new ArrayList<>());
            setPatientSer(patientSer);
        }};
        try {
            conn = ConnectionPool.getConnection();
            List<TargetVolume> targetVolumeList = targetVolumeDAO.selectTargetVolumeExceptItemByPatientSer(conn,patientSer,encounterId);
            if(!targetVolumeList.isEmpty()) {
                targetVolumeGroupVO.setEncounterId(targetVolumeList.get(0).getEncounterId());
                targetVolumeList.forEach(targetVolume -> {
//                    targetVolume 对象转换成targetVolumeVO对象
                    TargetVolumeVO targetVolumeVO = new TargetVolumeVO();
                    targetVolumeVO.setId(targetVolume.getId());
                    targetVolumeVO.setName(targetVolume.getName());
                    targetVolumeVO.setMemo(targetVolume.getMemo());
                    targetVolumeGroupVO.getTargetVolumeList().add(targetVolumeVO);
                });
            }
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return targetVolumeGroupVO;
    }
}
