package com.wave.wavaview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fengxuchao on 2016/12/13.
 */

public class WaveView extends View {


    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initWaveInfo(1f);
    }

    private void initWaveInfo(float a) {

    }
}
