package com.varian.oiscn.encounter.setupphoto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by gbt1220 on 1/5/2018.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupPhotoDetailDTO {
    private String photoId;
    /**
     * photo base64 with image header
     */
    private String photo;
}
