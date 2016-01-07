package com.sven.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by sven on 2016/1/6.
 * ͼƬ�����ţ���ק�Զ���ؼ�
 */
public class ImageScaleView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {
    /**
     * first visible layout
     */
    private boolean once = true;
    /**
     * ͼƬ�ľ���
     */
    private Matrix mMatrx;
    /**
     * ͼƬ��ʱ״̬�ľ���
     */
    private Matrix mTempMatrix = new Matrix();
    /**
     * ��ָ���µĵ�
     */
    private PointF mStartPoint = new PointF();
    /**
     * ��������ĵ�
     */
    private PointF mMidPoint = new PointF();
    /**
     * ��ָ���µľ���
     */
    private float oldDis = 1f;
    /**
     * ��ͼ����߾�
     */
    private int mLeftPadding;
    /**
     * ��ͼ���ϱ߾�
     */
    private int mTopPadding;
    /**
     * ״̬
     */
    private int state = NONE;
    /**
     * Ĭ��״̬
     */
    private static final int NONE = 0;
    /**
     * ��ק״̬
     */
    private static final int DRAG = 1;
    /**
     * ����״̬
     */
    private static final int ZOOM = 2;

    public ImageScaleView(Context context) {
        this(context, null);
    }

    public ImageScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //����imageview����������Ϊ�������ţ����ܹؼ�
        super.setScaleType(ScaleType.MATRIX);
        this.mLeftPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mLeftPadding, getResources().getDisplayMetrics());
        init();
    }

    private void init() {
        mMatrx = new Matrix();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        //��һ��layout
        if (once) {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            //ͼƬ�Ŀ��
            int dw = drawable.getIntrinsicWidth();
            int dh = drawable.getIntrinsicHeight();
            //�ؼ��Ŀ��
            int w = getWidth();
            int h = getHeight();
            float scale = 1.0f;
            if (w >= dw && h >= dh) {
                //nothing to do
            }
            if (w >= dw && h < dh) {
                scale = (h * 1.0f) / dh;
            }
            if (w < dw && h >= dh) {
                scale = (w * 1.0f) / dw;
            }
            if (w < dw && h < dh) {
                scale = Math.min((w * 1.0f) / dw, (h * 1.0f) / dh);
            }

            //ƽ�Ƶ��ؼ��м�
            mMatrx.postTranslate((w - dw) / 2, (h - dh) / 2);
            //����
            mMatrx.postScale(scale, scale, w / 2, h / 2);
            setImageMatrix(mMatrx);
            once = false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mTempMatrix.set(mMatrx);
                mStartPoint.set(event.getX(), event.getY());
                state = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDis = getDistance(event);
                if (oldDis > 10f) {
                    mTempMatrix.set(mMatrx);
                    getMidPoint(mMidPoint, event);
                    state = ZOOM;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (state == DRAG) {
                    mMatrx.set(mTempMatrix);
                    mMatrx.postTranslate(event.getX() - mStartPoint.x, event.getY() - mStartPoint.y);
                } else if (state == ZOOM) {
                    float newDis = getDistance(event);
                    if (newDis > 10f) {
                        mMatrx.set(mTempMatrix);
                        float scale = newDis / oldDis;
                        mMatrx.postScale(scale, scale, mMidPoint.x, mMidPoint.y);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                state = NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                state = NONE;
                break;
        }
        setImageMatrix(mMatrx);
        return true;
//        return super.onTouchEvent(event);
    }

    /**
     * �����������
     *
     * @param event
     * @return
     */
    private float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * �������ĵ�
     *
     * @param point
     * @param event
     */
    private void getMidPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * ������߾�
     *
     * @param padding
     */
    public void setLeftPadding(int padding) {
        this.mLeftPadding = padding;
    }

    /**
     * ��ȡ�����õ�ͼƬ
     *
     * @return
     */
    public Bitmap getCropImage() {
        this.mTopPadding = (getHeight() - (getWidth() - 2 * mLeftPadding)) / 2;
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        bitmap = Bitmap.createBitmap(bitmap, mLeftPadding, mTopPadding, getWidth() - 2 * mLeftPadding, getHeight() - 2 * mTopPadding);
        return bitmap;
    }
}
