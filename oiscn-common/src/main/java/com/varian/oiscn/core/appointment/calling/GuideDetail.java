package com.varian.oiscn.core.appointment.calling;

import lombok.Data;

import java.util.List;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 10/25/2017
 * @Modified By:
 */
@Data
public class GuideDetail {

    private List<String> texts;
    private List<String> imageUrls;
    private List<String> videoUrls;

}
