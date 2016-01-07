package com.sven.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by sven on 2016/1/6.
 * ���ÿ�
 * �ĸ���͸�������һ���м�հ�����
 */
public class CropBorderView extends View {
    /**
     * ����
     */
    private Paint mPaint;
    /**
     * ��߾�
     */
    private int mHorizontalPadding;//default
    /**
     * �ϱ߾࣬Ĭ���������߱߾�һ��
     */
    private int mVerticalPadding;
    /**
     * ���߿���
     */
    private int mBoderWidth = 1;
    /**
     * ���ÿ����
     */
    private int mCropWidth;

    public CropBorderView(Context context) {
        this(context, null);
    }

    public CropBorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("CropBorderView", "mHorizontalPadding=" + mHorizontalPadding);
        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources().getDisplayMetrics());
        mBoderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBoderWidth, getResources().getDisplayMetrics());
        init();
    }

    private void init() {
        mPaint = new Paint();
        //��Ϊ�����Ǿ��ο����ÿ����
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mCropWidth = (getWidth() - 2 * mHorizontalPadding);
        mVerticalPadding = (getHeight() - mCropWidth) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //����͸������
        mPaint.setColor(Color.parseColor("#aa000000"));
        mPaint.setStyle(Paint.Style.FILL);
        //��߾���
        canvas.drawRect(0, 0, mHorizontalPadding, getHeight(), mPaint);
        //��������
        canvas.drawRect(mHorizontalPadding, 0, mHorizontalPadding + mCropWidth, mVerticalPadding, mPaint);
        //�ұ߾���
        canvas.drawRect(mHorizontalPadding + mCropWidth, 0, getWidth(), getHeight(), mPaint);
        //�ײ�����
        canvas.drawRect(mHorizontalPadding, mVerticalPadding + mCropWidth, mHorizontalPadding + mCropWidth, getHeight(), mPaint);
        //�м��������
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setStrokeWidth(mBoderWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mHorizontalPadding, mVerticalPadding, mHorizontalPadding + mCropWidth, mVerticalPadding + mCropWidth, mPaint);
    }

    /**
     * ������߾�
     *
     * @param padding
     */
    public void setHorizontalPadding(int padding) {
        this.mHorizontalPadding = padding;
    }

}
