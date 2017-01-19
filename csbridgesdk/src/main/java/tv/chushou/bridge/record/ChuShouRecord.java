package tv.chushou.bridge.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.reflect.Method;

import cstv.plugin.CstvPluginManager;
import tv.chushou.bridge.record.constants.CSRConstants;
import tv.chushou.common.CPUUtils;
import tv.chushou.common.CrashHandler;
import tv.chushou.recordsdk.ChuShouRecConfig;
import tv.chushou.recordsdk.datastruct.GameUserInfo;
import tv.chushou.recordsdk.record.LocalCallback;
import tv.chushou.recordsdk.record.OnlineStatusCallback;
import tv.chushou.recordsdk.record.ShareCallback;

/**
 * Created by qfsong on 16/2/25.
 */
public final class ChuShouRecord {
    private final String TAG = "ChuShouRecord";
    public static int RESOLUTION_SD = 0;
    public static int RESOLUTION_HD = 1;
    public static int RESOLUTION_UHD = 2;

    public static int DEVICE_COMPATIBILITY_SUPPORT = 0;
    public static int DEVICE_COMPATIBILITY_CPU_NOT_SUPPORT = 1;
    public static int DEVICE_COMPATIBILITY_OS_VERSION_NOT_SUPPORT = 2;


    public static int ORIENTATION_PORTRAIT = 0;
    public static int ORIENTATION_HORIZONTAL = 1;


    private OnlineStatusCallback mOnlineCallbackRef = null;
    private ShareCallback mShareCallback;

    private LocalCallback mLocalCallbackRef = null;
    private ChuShouRecConfig mRecConfig = null;

    private CSInitCallback mInitCallback = null;

    public static ChuShouRecord sInstance = null;

    private GameUserInfo gameInfo;
    private boolean mInitSuccess = false;

    private Activity activity;

    public static ChuShouRecord instance() {
        if (sInstance == null) {
            sInstance = new ChuShouRecord();
        }
        return sInstance;
    }

    private ChuShouRecord() {}

    public Activity getActivity() {
        return activity;
    }

    public OnlineStatusCallback getOnlineCallback() {
        return mOnlineCallbackRef;
    }

    public void setShareCallback(ShareCallback callback) {
        mShareCallback = callback;
    }

    public ShareCallback getShareCallback() {
        return mShareCallback;
    }

    public LocalCallback getLocalCallback() {
        return mLocalCallbackRef;
    }

    public CSInitCallback getInitCallback() {
        return mInitCallback;
    }

    public ChuShouRecConfig getRecConfig() {
        return mRecConfig;
    }

    public GameUserInfo getGameInfo() {
        return gameInfo;
    }


    public void initialize(final Activity context, final ChuShouRecConfig config, final CSInitCallback callback) {
        Thread initThread = new Thread(new Runnable() {
            @Override
            public void run() {
                initialize_(context, config, callback);
            }
        });
        initThread.start();
    }


    private void initialize_(Activity context, ChuShouRecConfig config, CSInitCallback callback) {
        if (context == null || config == null || TextUtils.isEmpty(config.getAppKey()) || TextUtils.isEmpty(config.getAppSecret())) {
            throw new IllegalStateException("ChuShouRecord.java, please config valid parameters for ChuShouRec SDK");
        }
        if (callback == null) {
            throw new IllegalStateException("init callback is null");
        }
        if (activity == context)
            return;

        CrashHandler.getInstance().init(context.getApplicationContext());

        CstvPluginManager.init(context.getApplication());
        CstvPluginManager.initApk(CSRConstants.PLUGIN_NAME);
        CstvPluginManager.loadLastVersionPlugin(CSRConstants.PLUGIN_NAME);


        mRecConfig = config;
        activity = context;
        mInitCallback = callback;

        Class cl = null;
        try {
            cl = CstvPluginManager.mNowClassLoader.loadClass(CstvPluginManager.getPlugin(CSRConstants.PLUGIN_NAME).getPluginMeta().mainClass);
            Intent intent = new Intent(activity, cl);
            intent.putExtra(CSRConstants.REC_TAG, CSRConstants.TAG_INI);
            intent.putExtra(CSRConstants.REC_INFO_COFIG, config);

            activity.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open Video Manager
     *
     * @param activity
     * @param userInfo
     * @param gameName
     */
    public void openVideoManager(final Activity activity, GameUserInfo userInfo, String gameName) {
        this.gameInfo = userInfo;
        this.activity = activity;
        if (null == activity && !mInitSuccess) {
            return;
        }

        Class cl = null;
        try {
            cl = CstvPluginManager.mNowClassLoader.loadClass(CstvPluginManager.getPlugin(CSRConstants.PLUGIN_NAME).getPluginMeta().mainClass);
            Intent intent = new Intent(activity, cl);
            intent.putExtra(CSRConstants.REC_TAG, CSRConstants.TAG_VIDEO);
            intent.putExtra(CSRConstants.REC_INFO_GAMEUSERINFO, userInfo);
            intent.putExtra(CSRConstants.REC_INFO_GAMENAME, gameName);
            activity.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public boolean isRecorderRunning(Activity activity) {
        try {
            Class cl = CstvPluginManager.mNowClassLoader.loadClass("tv.chushou.recordsdk.utils.AppUtils");
            Method method = cl.getMethod("serviceIsRunning", new Class[]{Context.class, String.class});
            boolean isRunningLive = (boolean) method.invoke(cl, activity, "tv.chushou.recordsdk.record.ScreenRecorderService");
            boolean isRunningRecord = (boolean) method.invoke(cl, activity, "tv.chushou.recordsdk.record.RecordToFileService");
            return isRunningLive || isRunningRecord;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * start online live to record game's screen
     *
     * @param activity
     * @param gameName
     * @param title          title for online live
     * @param orientation
     * @param resolution
     * @param onlineCallback online callback {@link OnlineStatusCallback}
     */
    public void startOnlineRecord(Activity activity, GameUserInfo userInfo,
                                  String gameName, String title,
                                  int orientation,
                                  int resolution,
                                  OnlineStatusCallback onlineCallback) {
        if (null == activity && !mInitSuccess)
            return;

        if (isRecorderRunning(activity)) {
            Toast.makeText(activity, "录制或直播服务已在进行", Toast.LENGTH_SHORT).show();
            return;
        }

        if (null != onlineCallback)
            mOnlineCallbackRef = onlineCallback;

        this.gameInfo = userInfo;
        this.activity = activity;

        Class cl = null;
        try {
            cl = CstvPluginManager.mNowClassLoader.loadClass(CstvPluginManager.getPlugin(CSRConstants.PLUGIN_NAME).getPluginMeta().mainClass);
            Intent intent = new Intent(activity, cl);
            intent.putExtra(CSRConstants.REC_TAG, CSRConstants.TAG_LIVE);
            intent.putExtra(CSRConstants.REC_INFO_GAMEUSERINFO, userInfo);
            intent.putExtra(CSRConstants.REC_INFO_GAMENAME, gameName);
            intent.putExtra(CSRConstants.REC_INFO_TITLE, title);
            intent.putExtra(CSRConstants.REC_INFO_ORIENTATION, orientation);
            intent.putExtra(CSRConstants.REC_INFO_RESOLUTION, resolution);
            activity.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startLocalRecord(Activity activity, GameUserInfo userInfo,
                                 String gameName,
                                 int orientation,
                                 int resolution, LocalCallback callback) {
        if (null == activity && !mInitSuccess)
            return;

        if (isRecorderRunning(activity)) {
            Toast.makeText(activity, "录制或直播服务已在进行", Toast.LENGTH_SHORT).show();
            return;
        }

        if (null != mLocalCallbackRef)
            mLocalCallbackRef = callback;

        this.gameInfo = userInfo;
        this.activity = activity;

        Class cl = null;
        try {
            cl = CstvPluginManager.mNowClassLoader.loadClass(CstvPluginManager.getPlugin(CSRConstants.PLUGIN_NAME).getPluginMeta().mainClass);
            Intent intent = new Intent(activity, cl);
            intent.putExtra(CSRConstants.REC_TAG, CSRConstants.TAG_REC);
            intent.putExtra(CSRConstants.REC_INFO_GAMEUSERINFO, userInfo);
            intent.putExtra(CSRConstants.REC_INFO_GAMENAME, gameName);
            intent.putExtra(CSRConstants.REC_INFO_ORIENTATION, orientation);
            intent.putExtra(CSRConstants.REC_INFO_RESOLUTION, resolution);
            activity.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public boolean onActivityForResult(int requestCode, int resultCode, Intent data) {
        Class cl = null;
        try {
            cl = CstvPluginManager.mNowClassLoader.loadClass(CstvPluginManager.getPlugin(CSRConstants.PLUGIN_NAME).getPluginMeta().mainClass);
            Intent intent = new Intent(activity, cl);
            intent.putExtra(CSRConstants.REC_TAG, CSRConstants.TAG_RES);
            intent.putExtra(CSRConstants.REC_CODE_REQ, requestCode);
            intent.putExtra(CSRConstants.REC_CODE_RES, resultCode);
            intent.putExtra(CSRConstants.REC_DATA, data);
            activity.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int supportRunningOnCurrDevice(Context context) {
        if (!CPUUtils.checkAPKArc(context).equals("32")) {
            return DEVICE_COMPATIBILITY_CPU_NOT_SUPPORT;
        } else if (android.os.Build.VERSION.SDK_INT < 21) {
            return DEVICE_COMPATIBILITY_OS_VERSION_NOT_SUPPORT;
        } else {
            return DEVICE_COMPATIBILITY_SUPPORT;
        }
    }

}
