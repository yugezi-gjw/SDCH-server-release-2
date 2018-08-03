package com.varian.oiscn.base.diagnosis;

import com.varian.oiscn.core.common.KeyValuePair;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 7/12/2017.
 */
@Data
public class StagingVO {
    private List<KeyValuePair> tcodes;
    private List<KeyValuePair> ncodes;
    private List<KeyValuePair> mcodes;

    public void addTCode(KeyValuePair tcode) {
        if (tcodes == null) {
            tcodes = new ArrayList<>();
        }
        tcodes.add(tcode);
    }

    public void addNCode(KeyValuePair ncode) {
        if (ncodes == null) {
            ncodes = new ArrayList<>();
        }
        ncodes.add(ncode);
    }

    public void addMCode(KeyValuePair mcode) {
        if (mcodes == null) {
            mcodes = new ArrayList<>();
        }
        mcodes.add(mcode);
    }
}
