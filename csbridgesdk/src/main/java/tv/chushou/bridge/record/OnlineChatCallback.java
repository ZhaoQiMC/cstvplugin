package tv.chushou.bridge.record;

/**
 * Created by zhusong on 16/8/24.
 */
public interface OnlineChatCallback {

    void onNewMsg(String newMsgJson);

    void onPerNum(int count);
}
