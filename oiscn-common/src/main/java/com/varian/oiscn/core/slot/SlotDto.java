package com.varian.oiscn.core.slot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by fmk9441 on 2017-02-21.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDto {
    private String start;
    private String end;
    private String status;
}