package cstv.plugin;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

/**
 * 基础的Application
 * Created by huangjian on 2016/6/21.
 */
public class CstvBaseApplication extends Application {

    //---------------------插件相关的代码-----------------------start
    CstvHelper helper = new CstvHelper();
    private static Context appContext;


    @Override
    public Object getSystemService(String name) {
        return helper.getSystemService(this, super.getSystemService(name), name);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        this.appContext = this;
        CstvPluginManager.init(this);
    }



    @Override
    public Resources getResources() {//这里需要返回插件框架的resources
        return CstvPluginManager.getResources();
    }

    /**
     * 解决有时插件通过inflate找不到资源的问题
     *
     * @return Resources.Theme
     */
    public Resources.Theme getTheme() {
        return helper.getTheme(super.getTheme());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //支持切换语言
        CstvHelper.onConfigurationChanged();
    }

    public static Context getAppContext(){
        return appContext;
    }
    //---------------------插件相关的代码-----------------------end
}
