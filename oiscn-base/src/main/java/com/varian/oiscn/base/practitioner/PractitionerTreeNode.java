package com.varian.oiscn.base.practitioner;

import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import lombok.Data;

@Data
public class PractitionerTreeNode {
    private String id;
    private String name;
    private Enum<ParticipantTypeEnum> participantType;

    public PractitionerTreeNode(String id, String name,Enum<ParticipantTypeEnum> participantType) {
        this.id = id;
        this.name = name;
        this.participantType = participantType;
    }
}
