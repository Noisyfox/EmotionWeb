package org.foxteam.noisyfox.Emotion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Noisyfox on 14-3-11.
 */
public final class Util {

    private static final Pattern EnvPattern = Pattern.compile("\\{\\$(.*?)\\}");

    /**
     * 将输入字符串中以 {$ } 包围的字符串替换为对应的环境变量的值。
     * 如果不存在相应的环境变量则用 "null" 代替
     * @param input
     * @return
     */
    public static String replaceEnv(String input){
        Matcher m = EnvPattern.matcher(input);

        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        while(result) {
            String envKey = m.group(1);
            String envValue = System.getenv(envKey);
            m.appendReplacement(sb, String.valueOf(envValue));
            result = m.find();
        }
        m.appendTail(sb);

        return sb.toString();
    }

}
