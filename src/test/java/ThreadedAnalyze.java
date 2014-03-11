import com.mongodb.BasicDBObject;
import org.foxteam.wbgrab.GrabberException;
import org.foxteam.wbgrab.IStatus;
import org.foxteam.wbgrab.WbGrabHost;

/**
 * Created by YeahO_O on 3/8/14.
 */
public class ThreadedAnalyze extends Thread {
    @Override
    public void run() {
        WbGrabHost grabHost = new WbGrabHost();
        try {
            grabHost.Init();
            Iterable<IStatus> statusIterator = grabHost.getStatus(grabHost.getTopicTag("2012"), true);
            for (IStatus IStatus : statusIterator) {
                System.out.println(IStatus.getText());
                BasicDBObject analyzeResult = new BasicDBObject("key", "value");
                analyzeResult.append("key2", 1234);
                analyzeResult.append("key3", new BasicDBObject("subKeys", 2.0));
                IStatus.setAnalyzeResult(analyzeResult);
                IStatus.save();
                Thread.sleep(50);
            }
        } catch (GrabberException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
