package com.innovation.tuil2;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/5/25.
 */
public class UIUtils {

    //过滤字符串中的数字并改变颜色返回
    public static SpannableString filterNumber(String content) {
        if (content == null) return null;
        return setKeyWordColors(content, content.split("\\D+"));
    }
    // 把数字改为蓝色
    public static SpannableString setKeyWordColors(String content, String[] keywords) {
        SpannableString s = new SpannableString(content);
        for(String key:keywords){
            if (key == null) continue;
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(Color.parseColor("#0089e1")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }
}
