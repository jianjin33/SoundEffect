package com.jianjin33.soundeffect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * 频谱动画控件
 */
public class SpectrumDrawView extends View {

    private boolean mIsStartAnmi = false;
    private static final int MSG_UPDATE_ENERGY = 120;
    private int mSpectrumCount = 3; // 频谱条列数
    private ArrayList<DrawSpectrumRect> mSpectrumList = new ArrayList<DrawSpectrumRect>();
    private int mBgColor = Color.TRANSPARENT; // 圆形背景颜色
    private int mSpectrumColor = Color.WHITE; // 频谱条颜色
    // 创建画笔
    private Paint paint = new Paint();

    private  float mMaxWH= 50;

    public SpectrumDrawView(Context context) {
        super(context);
        initView();
    }

    public SpectrumDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SpectrumDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setSpectrumCount(3);
    }

    public void setmMaxWH(float maxWH) {
        this.mMaxWH = maxWH;
    }
    /**
     * 开始动画
     */
    public void startAnmi() {
        mIsStartAnmi = true;
        mUpdateEnergyHandler.removeMessages(MSG_UPDATE_ENERGY);
        mUpdateEnergyHandler.sendEmptyMessage(MSG_UPDATE_ENERGY);
    }

    /**
     * 停止动画
     */
    public void stopAnmi() {
        mIsStartAnmi = false;
        mUpdateEnergyHandler.removeMessages(MSG_UPDATE_ENERGY);
    }

    public void destroyView() {
        stopAnmi();
        mSpectrumList.clear();
    }

    /**
     * 设置频谱柱状条个数
     *
     */
    public void setSpectrumCount(int spectrumCount) {
        if (spectrumCount < 1) {
            return;
        }

        this.mSpectrumCount = spectrumCount;
        mSpectrumList.clear();

        paint.setAntiAlias(true);

        ArrayList<Float> randList = new ArrayList<Float>();
        int nRandCount = this.mSpectrumCount;
        for (int i = 0; i < nRandCount; i++) {
            randList.add(1.0f * i / nRandCount);
        }

        for (int i = 0; i < mSpectrumCount; i++) {
            mSpectrumList.add(new DrawSpectrumRect(i, nextFloatNotRe(randList)));
        }
    }

    private float nextFloatNotRe(ArrayList<Float> randList) {
        Random rand = new Random(System.currentTimeMillis());
        if (randList == null || randList.size() == 0) {
            return rand.nextFloat();
        }

        int nId = Math.abs(rand.nextInt(Integer.MAX_VALUE)) % randList.size();

        if (nId >= 0 && nId < randList.size()) {
            float randNum = randList.get(nId);
            randList.remove(nId);
            return randNum;
        }
        return rand.nextFloat();
    }

    /**
     * 设置动画单方向缓动总时间（柱形条高度单次从0->maxHeight渐变的时间）
     *
     */
    public void setTweenTime(int time) {
        this.mTweenTime = time;
        computerRefeshFrameTime();
    }

    /**
     * 设置圆形背景颜色
     * 
     * @param color 颜色
     */
    public void setBgColor(int color) {
        this.mBgColor = color;
    }

    /**
     * 设置频谱柱状条颜色
     *
     */
    public void setSpectrumColor(int color) {
        this.mSpectrumColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // drawTest(canvas);
        drawSpectrum(canvas);
    }

    private static final float PI = 3.14159265f;

    class DrawSpectrumRect {
        int index;
        float deltT;

        public DrawSpectrumRect(int index, float t) {
            this.index = index;
            // this.h=mMaxH*mRand.nextFloat();
            this.deltT = t;
        }

        private float nextFloat() {
            float h = 0.0f;
            h = tweenInOut(deltT);

            if (h <= 0.0f) {
                h = 0.0f;
            } else if (h >= 1.0f) {
                h = 1.0f;
            }
            return h;
        }

        boolean bOut = true;

        float tweenIn(float t) {
            return (float) -Math.cos(t * (PI / 2)) + 1;
        }

        private float tweenOut(float t) {
            return (float) Math.sin(t * (PI / 2));
        }

        private float tweenInOut(float t) {
            // return -0.5f * ((float) Math.cos(PI*t) - 1);
            return bOut ? tweenOut(t) : tweenIn(1 - t);
        }

        public void deltT(float t) {
            deltT += t;
            if (deltT >= 1.0f) {
                deltT -= 1.0f;
                bOut = !bOut;
            }
        }
    }

    private final int borderCount = 0;

    private void drawSpectrum(Canvas canvas) {
        // 这里检测一下mSpectrumCount和mSpectrumList数量上是否一致,不一致的话进行纠错
        // 因为在2.3手机上会莫名其妙走detach,把mSpectrumList清空,导致画不出东西
        if (mSpectrumList != null && mSpectrumCount != mSpectrumList.size()) {
            setSpectrumCount(mSpectrumCount);
        }

        // Rect region=new Rect(0,0,this.getMeasuredWidth(),this.getMeasuredHeight());
        Point center = new Point(this.getMeasuredWidth() / 2, this.getMeasuredHeight() / 2);
        float radis = this.getMeasuredWidth() / 2;
        if (radis <= 0) {
            return;
        }
        paint.setColor(mBgColor);
        canvas.drawCircle(center.x, center.y, radis, paint); // 大圆



        float arc = 30.0f;
//        float maxW = (float) (2 * radis * Math.abs(Math.cos(arc * PI / 180)));
        float maxW = mMaxWH;
        // float maxH=(float) (2*radis*Math.abs(Math.sin(arc*PI/180)));
        final int spaceCount = mSpectrumCount - 1 + borderCount;
        float space = maxW / (mSpectrumCount + spaceCount); // 间隔与频谱条等宽
        space=space*1.5f;//产品需求，柱子细一点，所以把间隔变大1.5倍
        maxW -= space * borderCount;
        float maxH = maxW; // 宽高相等
        float w = (maxW - space * (mSpectrumCount - 1)) / mSpectrumCount;


        float startX = center.x - maxW / 2;
        // float startY=center.y-maxH/2;
        paint.setColor(mSpectrumColor);
        for (DrawSpectrumRect rct : mSpectrumList) {
            float left = startX + rct.index * (w + space);
            float bottom = center.y + maxH / 2;
            canvas.drawRect(left, bottom - maxH * rct.nextFloat(), left + w, bottom, paint);
        }
    }

    private int mTweenTime = 1000;
    private static final int UPDATE_ENERGY_FRAME = 10;
    private int mUpdateEnergyTime = 100;

    private void computerRefeshFrameTime() {
        mUpdateEnergyTime = mTweenTime / UPDATE_ENERGY_FRAME;
        if (mUpdateEnergyTime < 30) {
            mUpdateEnergyTime = 30;
        }

        if (mUpdateEnergyTime > 330) {
            mUpdateEnergyTime = 330;
        }
    }

    Handler mUpdateEnergyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE_ENERGY) {
                mUpdateEnergyHandler.removeMessages(MSG_UPDATE_ENERGY);
                if (mIsStartAnmi) {
                    for (DrawSpectrumRect rct : mSpectrumList) {
                        rct.deltT(mUpdateEnergyTime * 1.0f / mTweenTime);
                    }
                    invalidate();
                    mUpdateEnergyHandler.sendEmptyMessageDelayed(MSG_UPDATE_ENERGY, mUpdateEnergyTime);
                }
            }
            // synchronized(mProgress)
            // {
            // progressBar1.setProgress(mProgress);
            // }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // startAnmi();
        if (mSpectrumList.size() == 0 && this.mSpectrumCount > 0) {
            setSpectrumCount(this.mSpectrumCount);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroyView();
    }
}
