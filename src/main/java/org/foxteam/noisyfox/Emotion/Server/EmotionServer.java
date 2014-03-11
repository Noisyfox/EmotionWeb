package org.foxteam.noisyfox.Emotion.Server;

import org.foxteam.noisyfox.Emotion.Core.IAnalyzer;
import org.foxteam.noisyfox.Emotion.EmotionDict.EmotionDict;
import org.foxteam.noisyfox.Emotion.WeiboAnalyzer.Analyzer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

/**
 * Created by Noisyfox on 14-3-9.
 * Servlet implementation class EmotionServer
 */
@WebServlet("/EmotionServer")
public class EmotionServer extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        EmotionDict dict = new EmotionDict();
        InputStream is = getClass().getResourceAsStream("/emotiondict.properties");
        dict.loadDictFromPropStream(is);
        is.close();
        //this.getClass().getResource("emotiondict.properties").toString();

        //IAnalyzer analyzer = new Analyzer();
        //analyzer.setEmotionDict(dict);

        //File f = new File("emotiondict.properties");

        PrintWriter w = response.getWriter();

        Map<String, String> envs = System.getenv();

        //w.write("test!\n");
        w.write(dict.getWordsString().size() + "words loaded!");
        for(Map.Entry<String, String> e : envs.entrySet()){
            w.write(e.getKey() + ":" + e.getValue() + "\n");
        }
        //w.write(f.getAbsolutePath());
        w.flush();
        w.close();
    }
}
