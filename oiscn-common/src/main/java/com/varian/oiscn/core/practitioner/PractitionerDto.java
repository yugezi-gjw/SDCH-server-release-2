package com.varian.oiscn.core.practitioner;

import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by gbt1220 on 2/8/2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PractitionerDto {
    private String id;
    private String name;
    private Enum<ParticipantTypeEnum> participantType;
}
