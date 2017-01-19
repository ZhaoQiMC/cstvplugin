package tv.chushou.bridge.chsuou;

import android.app.Activity;
import android.content.Intent;

import java.lang.reflect.Method;

import cstv.plugin.CstvPluginManager;
import tv.chushou.bridge.chsuou.constants.CSWConstants;
import tv.chushou.bridge.record.CSInitCallback;
import tv.chushou.common.CrashHandler;
import tv.chushou.playsdklib.constants.CSGlobalConfig;
import tv.chushou.playsdklib.constants.OkHttpHandler;


public class ChuShouTVSDK {

	private static final String TAG = "ChuShouTVSDK";


	private static ChuShouTVSDK mInstance;

	public String mCpCurrencyUnit = null;
	public boolean showTip = false;
    public final static int SEARCH_TYPE_ROOM = 1;
    public final static int SEARCH_TYPE_VIDEO = 2;
	private Activity mActivity = null;

	private CSGlobalConfig mConfig = null;

	private CSInitCallback mInitCallback = null;

	private boolean mInit = false;

	public CSInitCallback getInitCallback() {
		return mInitCallback;
	}

	public CSGlobalConfig getCSGlobalConfig() {
		return mConfig;
	}


	public void initialize(final Activity context, final CSGlobalConfig config, final CSInitCallback callback) {
		Thread initThread = new Thread(new Runnable() {
			@Override
			public void run() {
				initialize_(context, config, callback);
			}
		});
		initThread.start();
	}

	public void initialize_(Activity context, final CSGlobalConfig config, final CSInitCallback callback) {
		if (null == context || config == null)
			throw new IllegalArgumentException("context or config is null!");
		if(mActivity == context)
			return;
		mActivity = context;
		mConfig = config;
		mInitCallback = callback;

		CrashHandler.getInstance().init(context.getApplicationContext());

		CstvPluginManager.init(context.getApplication());
		CstvPluginManager.initApk(CSWConstants.PLUGIN_NAME);
		CstvPluginManager.loadLastVersionPlugin(CSWConstants.PLUGIN_NAME);

		Class cl = null;
		try {
			cl = CstvPluginManager.mNowClassLoader.loadClass(CstvPluginManager.getPlugin(CSWConstants.PLUGIN_NAME).getPluginMeta().mainClass);
			Intent intent = new Intent(mActivity, cl);
			intent.putExtra(CSWConstants.CSTV_TAG, CSWConstants.TAG_INI);

			mActivity.startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		mInit = true;
	}

	public static ChuShouTVSDK instance(){
		if(null == mInstance){
			mInstance = new ChuShouTVSDK();
		}
		return mInstance;
	}


	public ChuShouTVSDK(){

	}

	/**
	 *
	 * @param context
	 * @param roomid
	 * @param bPortrait
	 * @return
	 */
	public boolean playLiveRoom(final Activity context, final String roomid, final boolean bPortrait){
		if(!mInit)
			return false;
		mActivity = context;
		Class cl = null;
		try {
			cl = CstvPluginManager.mNowClassLoader.loadClass(CstvPluginManager.getPlugin(CSWConstants.PLUGIN_NAME).getPluginMeta().mainClass);
			Intent intent = new Intent(mActivity, cl);
			intent.putExtra(CSWConstants.CSTV_TAG, CSWConstants.TAG_PLAY_LIVE);
			intent.putExtra(CSWConstants.CSTV_ROOMID, roomid);
			intent.putExtra(CSWConstants.CSTV_POTRAIT, bPortrait);
			mActivity.startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}


	/**
	 *
	 * @param context
	 * @param videoid
	 * @param bPortrait
	 * @return
	 */
	public boolean playVideo(final Activity context, final String videoid, final boolean bPortrait){
		if(!mInit)
			return false;
		mActivity = context;
		Class cl = null;
		try {
			cl = CstvPluginManager.mNowClassLoader.loadClass(CstvPluginManager.getPlugin(CSWConstants.PLUGIN_NAME).getPluginMeta().mainClass);
			Intent intent = new Intent(mActivity, cl);
			intent.putExtra(CSWConstants.CSTV_TAG, CSWConstants.TAG_PLAY_VIDEO);
			intent.putExtra(CSWConstants.CSTV_VIDEOID, videoid);
			intent.putExtra(CSWConstants.CSTV_POTRAIT, bPortrait);
			mActivity.startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}


	public boolean getOnlineRoomListWithUI(final Activity context, final String gameid){
		if(!mInit)
			return false;
		mActivity = context;
		Class cl = null;
		try {
			cl = CstvPluginManager.mNowClassLoader.loadClass(CstvPluginManager.getPlugin(CSWConstants.PLUGIN_NAME).getPluginMeta().mainClass);
			Intent intent = new Intent(mActivity, cl);
			intent.putExtra(CSWConstants.CSTV_TAG, CSWConstants.TAG_LIVE);
			intent.putExtra(CSWConstants.CSTV_GAMEID, gameid);

			mActivity.startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean getGameVideoListWithUI(final Activity context, final String gameid){
		if(!mInit)
			return false;
		mActivity = context;
		Class cl = null;
		try {
			cl = CstvPluginManager.mNowClassLoader.loadClass(CstvPluginManager.getPlugin(CSWConstants.PLUGIN_NAME).getPluginMeta().mainClass);
			Intent intent = new Intent(mActivity, cl);
			intent.putExtra(CSWConstants.CSTV_TAG, CSWConstants.TAG_VIDEO);
			intent.putExtra(CSWConstants.CSTV_GAMEID, gameid);

			mActivity.startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

    public void getOnlineRoomList(final OkHttpHandler handler, final String gameid,
                                  final int pagesize, final String breakpoint){
        if(!mInit)
            return ;
        try {
            Class cl = CstvPluginManager.mNowClassLoader.loadClass("tv.chushou.playsdk.ChuShouTVSDK");
            Method instanceMethod = cl.getMethod("instance");
            Object instance = instanceMethod.invoke(cl);
            Method gMethod = cl.getMethod("getOnlineRoomList",
                    new Class[]{OkHttpHandler.class, String.class, int.class, String.class});
            gMethod.invoke(instance,handler, gameid, pagesize, breakpoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getGameVideoList(final OkHttpHandler handler,final String gameid,
                                 final int pagesize, final String breakpoint){
        if(!mInit)
            return ;
        try {
            Class cl = CstvPluginManager.mNowClassLoader.loadClass("tv.chushou.playsdk.ChuShouTVSDK");
            Method instanceMethod = cl.getMethod("instance");
            Object instance = instanceMethod.invoke(cl);
            Method gMethod = cl.getMethod("getGameVideoList",
                    new Class[]{OkHttpHandler.class, String.class, int.class, String.class});
            gMethod.invoke(instance,handler, gameid, pagesize, breakpoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getGameZoneList(final OkHttpHandler handler){
        if(!mInit)
            return ;
        try {
            Class cl = CstvPluginManager.mNowClassLoader.loadClass("tv.chushou.playsdk.ChuShouTVSDK");
            Method instanceMethod = cl.getMethod("instance");
            Object instance = instanceMethod.invoke(cl);
            Method gMethod = cl.getMethod("getGameZoneList",
                    new Class[]{OkHttpHandler.class});
            gMethod.invoke(instance,handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSearchCategoryData(final OkHttpHandler handler, final String keyword, final int type){
        if(!mInit)
            return ;
        try {
            Class cl = CstvPluginManager.mNowClassLoader.loadClass("tv.chushou.playsdk.ChuShouTVSDK");
            Method instanceMethod = cl.getMethod("instance");
            Object instance = instanceMethod.invoke(cl);
            Method gMethod = cl.getMethod("getSearchCategoryData",
                    new Class[]{OkHttpHandler.class, String.class, int.class});
            gMethod.invoke(instance,handler,keyword,type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSearchResultData(final OkHttpHandler handler, final String target, final int size, final String breakpoint){
        if(!mInit)
            return ;
        try {
            Class cl = CstvPluginManager.mNowClassLoader.loadClass("tv.chushou.playsdk.ChuShouTVSDK");
            Method instanceMethod = cl.getMethod("instance");
            Object instance = instanceMethod.invoke(cl);
            Method gMethod = cl.getMethod("getSearchResultData",
                    new Class[]{OkHttpHandler.class, String.class, int.class, String.class});
            gMethod.invoke(instance,handler,target,size,breakpoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void setCpCurrencyUnit(String unit){
		mCpCurrencyUnit = unit;
	}

}
