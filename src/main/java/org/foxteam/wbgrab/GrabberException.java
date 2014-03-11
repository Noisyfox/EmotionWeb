package org.foxteam.wbgrab;

/**
 * Created by YeahO_O on 3/8/14.
 */
public class GrabberException extends Exception {

    public GrabberException(String msg) {
        super(msg);
    }

    public GrabberException(Exception cause) {
        super(cause);
    }

    public GrabberException(String msg, Exception cause) {
        super(msg, cause);
    }
}