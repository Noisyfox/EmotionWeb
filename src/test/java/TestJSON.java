import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

/**
 * Created by YeahO_O on 3/7/14.
 */
public class TestJSON {
    @Test
    public void test() {
        JSONObject a = (JSONObject) JSON.parse("{\"a\":\"2\"}");
        System.out.println(a.getIntValue("a"));
        System.out.println(a.getIntValue("b"));
    }
}
