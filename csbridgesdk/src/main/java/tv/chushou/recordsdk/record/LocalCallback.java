package tv.chushou.recordsdk.record;

/**
 * Created by zhusong on 16/4/7.
 */
public interface LocalCallback {
    void onFailure(String errTip);
    void onRecordStart(/*String dstPath*/);
    void onRecordFinish(/*String dstPath*/);
}
