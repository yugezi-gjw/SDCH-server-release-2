package com.varian.oiscn.core.participant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by fmk9441 on 2017-02-15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
    private ParticipantTypeEnum type;
    private String participantId;
}
