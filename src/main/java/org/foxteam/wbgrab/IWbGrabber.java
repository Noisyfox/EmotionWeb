package org.foxteam.wbgrab;

/**
 * Created by Noisyfox on 14-3-9.
 */
public interface IWbGrabber {
    void Init() throws GrabberException;

    void resetDatabase() throws GrabberException;

    void clearDatabase();
}
