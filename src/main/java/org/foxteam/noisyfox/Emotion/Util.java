package org.foxteam.noisyfox.Emotion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Noisyfox on 14-3-11.
 * 工具类
 */
public final class Util {

    private static final Pattern EnvPattern = Pattern.compile("\\{\\$(.*?)\\}");

    /**
     * 将输入字符串中以 {$ } 包围的字符串替换为对应的环境变量的值。
     * 如果不存在相应的环境变量则用 "null" 代替
     * @param input 待转换的字符串
     * @return 转换后的字符串
     */
    public static String replaceEnv(String input){
        if(input == null)return null;

        Matcher m = EnvPattern.matcher(input);

        StringBuilder sb = new StringBuilder();
        boolean result = m.find();
        int start = 0, end;
        while(result) {
            String envKey = m.group(1);
            String envValue = System.getenv(envKey);
            //由于 matcher.appendReplacement 会对输入进行转义，因此这里用自己的替换
            //m.appendReplacement(sb, String.valueOf(envValue));
            end = m.start();
            sb.append(input, start, end);
            sb.append(String.valueOf(envValue));
            start = m.end();
            result = m.find();
        }
        //m.appendTail(sb);
        sb.append(input, start, input.length());

        return sb.toString();
    }

}
