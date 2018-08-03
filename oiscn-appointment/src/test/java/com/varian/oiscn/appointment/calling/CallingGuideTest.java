package com.varian.oiscn.appointment.calling;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

/**
 * Created by bhp9696 on 2018/3/14.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CallingGuide.class)
public class CallingGuideTest {
    private CallingGuide callingGuide = new CallingGuide();
    @Test
    public void testMethod(){
        callingGuide.addText("text");
        Assert.assertTrue(callingGuide.getTexts().get(0).equals("text"));
        callingGuide.addTextList(Arrays.asList("text1","text2"));
        Assert.assertTrue(callingGuide.getTexts().get(2).equals("text2"));

        callingGuide.addImage("img");
        Assert.assertTrue("img".equals(callingGuide.getImages().get(0)));
        callingGuide.addImageList(Arrays.asList("img1","img2"));
        Assert.assertTrue("img2".equals(callingGuide.getImages().get(2)));

        callingGuide.addVideo("video");
        Assert.assertTrue("video".equals(callingGuide.getVideos().get(0)));
        callingGuide.addVideoList(Arrays.asList("video1","video2"));
        Assert.assertTrue("video2".equals(callingGuide.getVideos().get(2)));

       Assert.assertNotNull(callingGuide.toString());

    }

}
