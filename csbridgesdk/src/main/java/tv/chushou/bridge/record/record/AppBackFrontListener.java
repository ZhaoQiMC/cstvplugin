package tv.chushou.bridge.record.record;

/**
 * Created by qfsong on 16/4/7.
 */
public interface AppBackFrontListener {
    /**
     * current process move to background
     * */
    void onBackground();

    /**
     * current process move to front
     * */
    void onFront();
}
