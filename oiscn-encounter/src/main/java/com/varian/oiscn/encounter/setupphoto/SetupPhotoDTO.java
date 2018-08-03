package com.varian.oiscn.encounter.setupphoto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 1/5/2018.
 */
@Data
public class SetupPhotoDTO {
    private String id;
    private String deviceId;
    private Long patientSer;

    private List<SetupPhotoDetailDTO> photos;

    /**
     * Add a new Photo.<br>
     *
     * @param photoId Photo Id
     * @param photo   Photo Data
     * @return self
     */
    public SetupPhotoDTO addPhoto(String photoId, String photo) {
        if (photos == null) {
            photos = new ArrayList<>();
        }
        photos.add(new SetupPhotoDetailDTO(photoId, photo));
        return this;
    }

    /**
     * Return Photo by specified id.<br>
     *
     * @param photoId Photo Id
     * @return Photo Data
     */
    public String getPhoto(String photoId) {
        String photo = null;
        if (photos != null && photoId != null) {
            for (SetupPhotoDetailDTO dto : photos) {
                if (photoId.equals(dto.getPhotoId())) {
                    photo = dto.getPhoto();
                    break;
                }
            }
        }
        return photo;
    }
}
