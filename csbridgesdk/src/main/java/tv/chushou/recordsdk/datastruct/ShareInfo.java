package tv.chushou.recordsdk.datastruct;

/**
 * Created by qfsong on 16/4/25.
 */
public class ShareInfo {
    /**get share info from server, use this type to share room info*/
    public final static String ROOM_TYPE = "1";

    public final static String VIDEO_TYPE = "4";

    public final static String SCREEN_CAP_TYPE = "6";

    public String mThumbnail = null;
    public String mContent = null;
    public String mShareUrl = null;
    public String mTitle = null;

    @Override
    public String toString() {
        return mTitle+","+mContent+","+mShareUrl+","+mThumbnail;
    }
}
