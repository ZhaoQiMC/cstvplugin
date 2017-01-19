package tv.chushou.playsdklib.constants;


import android.content.Context;

public interface CSConfigCallback {
    CSAppUserInfo getUserInfo(Context context);

    void notifyLoginResult(Context context, int code, String message);

    void startPay(CSGoodsInfo goodsInfo, CSPayCallback callBack);

    CPAccountBalance queryAccountBalance();

    void notifyGiftResult(int code, String message, int accountBalance);

    void onShare(CSShareInfo shareInfo);
}
