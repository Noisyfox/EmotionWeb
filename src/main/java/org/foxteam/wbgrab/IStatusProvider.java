package org.foxteam.wbgrab;

/**
 * Created by YeahO_O on 3/8/14.
 */
public interface IStatusProvider {

    int getTopicTag(String topic);

    Iterable<IStatus> getStatus(int topic_tag, boolean unAnalyzedOnly);
}
