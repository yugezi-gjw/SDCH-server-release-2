package com.varian.oiscn.base.util;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BHP9696 on 2017/10/16.
 */
@Slf4j
public class PinyinUtil {


    /**
     * 将中文的姓名转换为相应的汉语拼音的首字母大写
     *
     * @param chineseName
     * @return
     */
    public static String chineseName2PinyinAcronyms(String chineseName) {
        List<String> list = new ArrayList<>();
        char[] chars = chineseName.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        String[] pinyinArray;
        try {
            for (int i = 0; i < chars.length; i++) {
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(chars[i], format);
                if (pinyinArray != null && pinyinArray.length > 0) {
                    list.add(pinyinArray[0].substring(0, 1));
                }
            }

        } catch (BadHanyuPinyinOutputFormatCombination e) {
            log.error("BadHanyuPinyinOutputFormatCombination: {}", e.getMessage());
        }
        return StringUtils.join(list.toArray(), "");
    }
}
