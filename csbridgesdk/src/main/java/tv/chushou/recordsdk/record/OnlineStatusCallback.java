package tv.chushou.recordsdk.record;

/**
 * Created by qfsong on 16/3/30.
 */
public interface OnlineStatusCallback {
    void onSuccess();
    void onFailure(String errorMsg);
    void offline();
}
