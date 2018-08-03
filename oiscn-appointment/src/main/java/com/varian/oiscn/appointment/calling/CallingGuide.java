package com.varian.oiscn.appointment.calling;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Call Guide.<br>
 */
@Getter
public class CallingGuide {

    protected List<String> texts = new ArrayList<>();
    /**
     * images urls
     */
    protected List<String> images = new ArrayList<>();
    /**
     * videos urls
     */
    protected List<String> videos = new ArrayList<>();

    public CallingGuide addText(String text) {
        texts.add(text);
        return this;
    }

    public CallingGuide addTextList(List<String> textList) {
        texts.addAll(textList);
        return this;
    }

    public CallingGuide addImage(String url) {
        images.add(url);
        return this;
    }

    public CallingGuide addImageList(List<String> urlList) {
        images.addAll(urlList);
        return this;
    }

    public CallingGuide addVideo(String url) {
        videos.add(url);
        return this;
    }

    public CallingGuide addVideoList(List<String> urlList) {
        videos.addAll(urlList);
        return this;
    }

    @Override
    public String toString() {
        return "CallingGuide [texts=" + texts + ", images=" + images + ", videos=" + videos + "]";
    }
}
