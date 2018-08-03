/**
 * 
 */
package com.varian.oiscn.encounter.history;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encounter Title Item for history list.<br>
 *
 */
@Data
@NoArgsConstructor
public class EncounterTitleItem {
    /** Encounter Id */
    protected String id;
    protected String status;
    protected Date diagnoseDate;
    protected Date createdDate;
    protected String urgent;
}
