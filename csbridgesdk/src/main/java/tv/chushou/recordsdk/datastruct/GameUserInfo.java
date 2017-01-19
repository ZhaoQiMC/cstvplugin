package tv.chushou.recordsdk.datastruct;

import java.io.Serializable;

/**
 * Created by zhusong on 16/4/22.
 */
public class GameUserInfo implements Serializable{
    public String mGameUid;
    public String phone;
    public String nickName;
    public String gameToken;
    public String avatar;
    public String gender;
    public int forcePhoneNumVerified;
    public int forceUpdateProfile;

    public String gameExtraData;

    public GameUserInfo(){}

    @Override
    public String toString() {
        return mGameUid+","+phone+","+nickName+","+gameToken+","+avatar+","+gender+","+gameExtraData;
    }
}
