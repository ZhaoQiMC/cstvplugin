package tv.chushou.playsdklib.constants;

import org.json.JSONObject;


public interface OkHttpHandler {

    /**
     * 访问开始的回调
     */
    void onStart();

    /**
     * 访问失败的回调
     * @param code 错误码
     * @param message 错误提示
     */
    void onFailure(int code, String message);

    /**
     * 访问成功的回调
     * @param object
     */
    void onSuccess(JSONObject object);



}
