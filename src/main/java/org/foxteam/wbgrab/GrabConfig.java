package org.foxteam.wbgrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by YeahO_O on 3/7/14.
 */
public class GrabConfig {
    private static Properties props = new Properties();

    static {
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("WeiboGrabber.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key) {
        return props.getProperty(key);
    }

    public static void updateProperties(String key, String value) {
        props.setProperty(key, value);
    }
}
