package com.varian.oiscn.encounter.treatmentworkload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by BHP9696 on 2017/11/1.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkloadSignature implements Serializable {
    private long workloadId;
    private String userName;
    private String resourceName;
    private Long resourceSer;
    private Date signDate;
    private SignatureTypeEnum signType;

    public enum SignatureTypeEnum{
        //      医师
        PHYSICIAN,
        //      物理师
        PHYSICIST,
        //      操作者A
        OPERATORA,
        //      操作者B
        OPERATORB,
    }

}
