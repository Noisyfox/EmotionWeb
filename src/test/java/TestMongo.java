import com.alibaba.fastjson.JSONObject;
import org.foxteam.wbgrab.GrabberException;
import org.foxteam.wbgrab.WbGrabHost;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by YeahO_O on 3/8/14.
 */
public class TestMongo {
    private WbGrabHost grabHost;

    @Before
    public void setup() throws GrabberException {
        grabHost = new WbGrabHost();
        grabHost.Init();
    }

    @Test
    public void Test() throws IOException, GrabberException, InterruptedException {
        grabHost.clearDatabase();
        InputStream jis = Thread.currentThread().getContextClassLoader().getResourceAsStream("sample.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(jis));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = br.readLine();
            if (null == line) break;
            sb.append(line);
        }
        br.close();
        jis.close();
        JSONObject json = JSONObject.parseObject(sb.toString());
        grabHost.insertStatuses(json, grabHost.getTopicTag("2012"));

        ThreadedAnalyze ta = new ThreadedAnalyze();
        ThreadedAnalyze tb = new ThreadedAnalyze();
        ta.start();
        tb.start();
        ta.join();
        tb.join();
    }
}
