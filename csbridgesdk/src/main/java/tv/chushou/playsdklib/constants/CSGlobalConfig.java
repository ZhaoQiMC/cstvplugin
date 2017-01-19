package tv.chushou.playsdklib.constants;

import android.content.Context;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;

public class CSGlobalConfig implements Serializable{

    public final static String KEY_OPENCLOSE_LOG = "log";
    public final static String KEY_OPENCLOSE_GIFT = "gift";
    public final static String KEY_OPENCLOSE_SEARCH = "search";
    public final static String KEY_OPENCLOSE_CP_CURRENCY = "cp_currency";
    public final static String KEY_OPENCLOSE_PLAYER_SCREEN_ROTATION = "player_screen_rotation";
    public final static String KEY_OPENCLOSE_SHARE = "share";

    public final static String APPKEY_TAG = "_appkey";
    public final static String XAPPKEY_TAG = "_xappkey";
    public final static String SIGN_TAG = "_sign";
    public final static String TIME_TAG = "_t";
    public final static String SDKVERSION_TAG = "_sdkVersion";
    public final static String OPEN_UID = "_openUid";
    public final static String ACCESS_TOKEN = "_accessToken";

    public static final String KEY_MODEL_NAME = "_modelName";
    public  static  final String KEY_APPVERSION = "_appVersion";


    public Context mContext;

    public String mAppkey;

    public String mAppSecret;

    public String mAgentId;     // optional

    public Boolean mDebug = false;

    public CSConfigCallback mConfigCallback;

    private HashMap<String, Boolean> mOpenCloseParam;

    public CSGlobalConfig(CSConfigCallback callback){
        mConfigCallback = callback;

        setOption(KEY_OPENCLOSE_PLAYER_SCREEN_ROTATION, true);
    }

    public void setOption(String key, Object value){
       if(TextUtils.isEmpty(key))
           return;

        if(null == mOpenCloseParam){
            mOpenCloseParam = new HashMap<>();
        }
        if(key.equals(KEY_OPENCLOSE_GIFT) ||
                key.equals(KEY_OPENCLOSE_LOG) ||
                key.equals(KEY_OPENCLOSE_SEARCH) ||
                key.equals(KEY_OPENCLOSE_CP_CURRENCY) ||
                key.equals(KEY_OPENCLOSE_PLAYER_SCREEN_ROTATION) ||
                key.equals(KEY_OPENCLOSE_SHARE)
                ) {
            if(null != value && !(value instanceof Boolean)){
                return;
            }
            mOpenCloseParam.put(key, (Boolean)value);
        }else{
        }
    }

    public Object getOption(String key){
        if(TextUtils.isEmpty(key))
            return null;

        if(null != mOpenCloseParam && mOpenCloseParam.containsKey(key)){
            return mOpenCloseParam.get(key);
        }
        return null;
    }
}