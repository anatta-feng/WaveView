package com.wave.wavaview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengxuchao on 2016/12/13.
 */

public class WaveView extends View {

    /**
     * 多少毫秒刷新一帧
     */
    private long time = 25;
    /**
     * View 中心坐标
     */
    private float mViewX;
    private float mViewY;
    /**
     * 中心圆形半径
     */
    private float centerRadius = 20f;
    /**
     * 水波间距
     */
    private float mWaveDistance;
    /**
     * 水波起始与结束的宽度
     */
    private float mWaveStartWidth;
    private float mWaveEndWidth;
    /**
     * 波形速度
     */
    private float mWaveSpeed;
    /**
     * 水波纹透明插值器
     */
    private Interpolator interpolator = new CycleInterpolator(0.5f);
    /**
     * View 最大半径
     */
    private float viewMaxRadius;
    /**
     * 中心画笔
     */
    private Paint mCenterPaint = new Paint();
    {
        // 抗锯齿
        mCenterPaint.setAntiAlias(true);
        mCenterPaint.setStyle(Paint.Style.FILL);
    }
    /**
     * 波形画笔
     */
    private Paint mWavePaint = new Paint();
    {
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Paint.Style.STROKE);
    }
    /**
     * 波形集合
     */
    private List<Wave> mWaves = new ArrayList<>();

    /**
     * 波形颜色
     */
    private int mWaveColor;
    private boolean isFillAllView = false;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context) {
        super(context);
        init();
    }

    private void init() {
        initWaveInfo(0.8f, 30f, 8f, 5f, Color.WHITE);
    }

    /**
     * 初始化波形属性
     * @param mWaveSpeed            波形移动速度
     * @param mWaveDistance         波形间距
     * @param mWaveStartWidth       波形起始宽度
     * @param mWaveEndWidth         波形结束宽度
     * @param color                 波形颜色
     */
    private void initWaveInfo(float mWaveSpeed, float mWaveDistance, float mWaveStartWidth, float mWaveEndWidth, int color) {
        this.mWaveSpeed = mWaveSpeed;
        this.mWaveDistance = mWaveDistance;
        this.mWaveStartWidth = mWaveStartWidth;
        this.mWaveEndWidth = mWaveEndWidth;
        setWaveColor(color);
        // 每次初始化波形时候要重置波形
        resetWave();
    }

    /**
     * 清空 Wave 集合
     */
    private void resetWave() {
        mWaves.clear();
        postInvalidate();
    }

    private void setWaveColor(int color) {
        mWaveColor = color;
        mCenterPaint.setColor(color);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mViewX = getWidth() / 2;
        mViewY = getHeight() / 2;
        float waveAreaRadius;
        if (isFillAllView) {
            waveAreaRadius = (float) Math.sqrt(mViewX * mViewX + mViewY * mViewY);
        } else {
            waveAreaRadius = Math.min(mViewX, mViewY);
        }
        if (waveAreaRadius != viewMaxRadius) {
            viewMaxRadius = waveAreaRadius;
            resetWave();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getSwarming();
        for (Wave w: mWaves) {
            mWavePaint.setColor(w.color);
            mWavePaint.setStrokeWidth(w.width);
            canvas.drawCircle(mViewX, mViewY, w.radius, mWavePaint);
        }
        if (centerRadius > 0) {
            canvas.drawCircle(mViewX, mViewY, centerRadius, mCenterPaint);
        }
        postInvalidateDelayed(time);
    }

    private Wave mLastRemoveWave;

    /**
     * 计算波纹每一帧的属性变化
     */
    public void getSwarming() {
        Wave nearestWave = mWaves.isEmpty() ? null : mWaves.get(0);
        if (nearestWave == null || nearestWave.radius >= mWaveDistance) {
            Wave w;
            if (mLastRemoveWave != null) {
                w = mLastRemoveWave;
                mLastRemoveWave = null;
            } else {
                w = new Wave();
            }
            mWaves.add(0, w);
        }
        float waveWidthIncrease = mWaveEndWidth - mWaveStartWidth;
        int size = mWaves.size();
        for (int i = 0; i < size; i++) {
            Wave w = mWaves.get(i);
            w.radius += mWaveSpeed;
            float factor = w.radius / viewMaxRadius;
            w.width = mWaveStartWidth + factor * waveWidthIncrease;
            float colorFactor = interpolator.getInterpolation(factor);
            w.color = mWaveColor & 0x00FFFFFF | ((int) (255 * colorFactor) << 24);
        }
        Wave farthestWave = mWaves.get(size - 1);
        if (farthestWave.radius > viewMaxRadius) {
            mWaves.remove(farthestWave);
        }
    }

    class Wave {
        float radius;
        float width;
        int color;
        Wave() {
            reset();
        }

        private void reset() {
            radius = 0;
            width = mWaveStartWidth;
            color = mWaveColor;
        }
    }

    public void setCenterViewRadius(float radius) {
        centerRadius = radius;
    }

    public void setRefreshTime(long time) {
        this.time = time;
    }
}
