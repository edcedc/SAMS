package com.yyc.smas;

import static com.blankj.utilcode.util.StringUtils.getString;

import android.content.res.AssetManager;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.blankj.utilcode.util.LogUtils;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author
 * @Date 2023/8/9 15:48
 * @Description
 */
public class a {

    public void aa() {
        JSONArray ja = new JSONArray();
        for (int i = 0; i < ja.length(); i++) {

        }

        StringBuffer sb = new StringBuffer();
        String[] split = sb.toString().split(",");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
        }


    }

    public static void b(){
        for (Field field : R.string.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers()) && field.getType().equals(int.class)) {
                try {
                    int resId = field.getInt(null);
                    String value = getString(resId); // 获取字符串值

                    LogUtils.e(field.getName() + ": " + value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void c(){

    }
    // 辅助方法：解析并打印字符串数据
    public static void printStringData(InputStream inputStream, String folder) {
        List list = new ArrayList();
        for (int i = 0;i<list.size();i++){

        }
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "string".equals(parser.getName())) {
                    String name = parser.getAttributeValue(null, "name");
                    String value = parser.nextText();

                    LogUtils.e(folder + " - " + name + ": " + value);
                }

                eventType = parser.next();
            }

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
