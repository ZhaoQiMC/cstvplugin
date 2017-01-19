package tv.chushou.playsdklib.constants;

/**
 * Created by klxytx on 16/7/1.
 */
public interface CSPayCallback {
    void paySuccess();
    void payCancel();
    void payError(String errMsg);
}
