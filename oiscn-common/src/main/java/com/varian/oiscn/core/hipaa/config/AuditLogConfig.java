package com.varian.oiscn.core.hipaa.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 3/2/2018
 * @Modified By:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogConfig {

    private String hostName;
    private int port;
    private int timeoutInMs;
    private int logThreadCount; //audit log transfer thread数量
    private int logBatchSize; //从queue中取得log批量写入数量

}
