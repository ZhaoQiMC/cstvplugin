package tv.chushou.recordsdk;

import java.io.Serializable;

/**
 * Created by qfsong on 16/4/8.
 */
public class ChuShouRecConfig implements Serializable{

    private String mAppKey = null;

    private String mAppSecret = null;

    private String mAgentId = null;

    private boolean mEnableShare;

    private boolean mDebug = false;

    private boolean mAudioShare = false;

    // use ChushouRec UI
    private boolean mDefaultUI = true;

    public ChuShouRecConfig(Builder builder) {
        mAppSecret = builder.mAppSecret;
        mAppKey = builder.mAppKey;
        mAgentId = builder.mAgentId;
        mEnableShare = builder.mEnableShare;
        mDebug = builder.mDebug;
        mAudioShare = builder.mAudioShare;
        mDefaultUI = builder.mDefaultUI;
    }

    public ChuShouRecConfig(){}

    public String getAppKey(){
        return mAppKey;
    }

    public void setAppKey(String appKey) {
        this.mAppKey = appKey;
    }

    public String getAppSecret(){
        return mAppSecret;
    }
    //获取sdk版本号 sjk 后加请求参数
    public String getChushouSDKVersion(){
        //sjk版本号
        return "1.0.18.8488";
    }
    public void setAppSecret(String appSecret) {
        this.mAppSecret = appSecret;
    }

    public String getAgentId() {
        return mAgentId;
    }

    public void setAgentId(String agentId) {
        this.mAgentId = agentId;
    }

    public boolean isShareEnabled() {
        return this.mEnableShare;
    }

    public void enableShare(boolean enable) {
        this.mEnableShare = enable;
    }

    public void setDebug(boolean isDebug) {
        this.mDebug = isDebug;
    }

    public boolean isDebug() {
        return this.mDebug;
    }

    public boolean isAudioShare() {return this.mAudioShare; }

    public boolean isDefaultUI() {
        return this.mDefaultUI;
    }

    public void enableDefaultUI(boolean enable) { this.mDefaultUI = enable; }

    public void enableAudioShare(boolean enable) { this.mAudioShare = enable; }

    public static class Builder {

        public String mAppKey = null;
        public String mAppSecret = null;
        public String mAgentId = null;
        private boolean mEnableShare = false;
        private boolean mDebug = false;
        private boolean mAudioShare = false;
        private boolean mDefaultUI = true;
        
        public ChuShouRecConfig build() {

            return new ChuShouRecConfig(this);
        }

        public Builder configAppKey(String appKey) {
            mAppKey = appKey;
            return this;
        }

        public Builder configAppSecret(String appSecret) {
            mAppSecret = appSecret;
            return this;
        }

        public Builder configAgentId(String agentId) {
            mAgentId = agentId;
            return this;
        }

        public Builder configEnableShare(boolean enableShare) {
            this.mEnableShare = enableShare;
            return this;
        }

        public Builder configDebug(boolean isDebug) {
            mDebug = isDebug;
            return this;
        }

    }
}
