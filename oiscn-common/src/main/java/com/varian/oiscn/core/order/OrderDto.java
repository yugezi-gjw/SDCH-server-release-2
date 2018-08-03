package com.varian.oiscn.core.order;

import com.varian.oiscn.core.participant.ParticipantDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Created by fmk9441 on 2017-02-17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String orderId;
    private String orderType;
    private String orderGroup;
    private String orderStatus;
    private String ownerId;
    private Date dueDate;
    private Date createdDT;
    private Date lastModifiedDT;
    private List<ParticipantDto> participants;
}
