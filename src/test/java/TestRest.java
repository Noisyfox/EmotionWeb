import com.alibaba.fastjson.JSONObject;
import org.foxteam.wbgrab.GrabberException;
import org.foxteam.wbgrab.WbGrabHost;
import org.foxteam.wbgrab.WbGrabWorker;
import org.foxteam.wbgrab.restapi.AccountLogin;
import org.foxteam.wbgrab.restapi.RestException;
import org.foxteam.wbgrab.restapi.SearchStatus;
import org.foxteam.wbgrab.restapi.WbLoginSession;
import org.junit.Test;

/**
 * Created by YeahO_O on 3/7/14.
 */
public class TestRest {
    //@Test
    public void TestLogin() throws RestException {
        AccountLogin accountLogin = new AccountLogin();
        WbLoginSession session = accountLogin.login("duckoffice@sina.com", "weibopassword");
        assert session != null;
        String uid = session.getUid();
        assert uid != null;
        assert uid.equals("3912879170");

        SearchStatus ss = new SearchStatus();
        ss.setSession(session);
        JSONObject json = ss.searchStatus("2014", 5, 20);
        System.out.println(json.getLong("total_number"));
    }

    @Test
    public void TestGrab() throws GrabberException, InterruptedException {
        WbGrabHost host = new WbGrabHost();
        host.Init();
        host.clearDatabase();
        host.newAccount("duckoffice@sina.com", "weibopassword");
        WbGrabWorker worker = new WbGrabWorker(host, "2012", 80);
        worker.run();
        worker.join();
    }
}
