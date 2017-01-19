package tv.chushou.recordsdk;

import android.content.Intent;

import com.unity3d.player.UnityPlayerActivity;

import tv.chushou.bridge.record.ChuShouRecord;

/**
 * Created by zhusong on 16/6/23.
 */
public class RecUnityPlayerActivity extends UnityPlayerActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ChuShouRecord.instance().onActivityForResult(requestCode, resultCode, data);
    }

}
