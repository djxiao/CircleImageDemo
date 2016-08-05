package com.djxiao.circleimagedemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * @author djxiao
 * @create 2016/8/5 10:26
 * @DESC  绘制圆形或者圆角图片
 */
public class RoundImageView extends ImageView{

    private int type;//图片的类型
    private static final int CIRCLE_TYPE = 0;
    private static final int ROUND_TYPE = 1;

    private static final int ROUND_DEFAULT_RADIUS = 10;

    private int mCircleRadius;//圆的半径
    private int mRoundRadius;//圆角的弧度

    private Paint mBitmapPaint;
    private Matrix mMatirx;
    private BitmapShader mBitmapShader;

    private int mWidth;//view的宽度
    private RectF mBoundRect;


    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMatirx = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.RoundImageView);

        mRoundRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_borderRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,ROUND_DEFAULT_RADIUS,
                        getResources().getDisplayMetrics()));
        type = a.getInt(R.styleable.RoundImageView_type,CIRCLE_TYPE);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(type == CIRCLE_TYPE){ //如果是圆，根据view的大小来决定半径
            mWidth = Math.min(getMeasuredWidth(),getMeasuredHeight());
            mCircleRadius = mWidth/2;
            setMeasuredDimension(mWidth,mWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable() == null){
            return;
        }
        setupShader();
        if(type == CIRCLE_TYPE){
            canvas.drawCircle(mCircleRadius,mCircleRadius,mCircleRadius,mBitmapPaint);
        }else{
            canvas.drawRoundRect(mBoundRect,mRoundRadius,mRoundRadius,mBitmapPaint);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(type == ROUND_TYPE){
            mBoundRect = new RectF(0,0,getWidth(),getHeight());
        }
    }

    /**
     * 调整图片的缩放，设置shader
     */
    private void setupShader(){
        Drawable drawable = getDrawable();
        if(drawable == null){
            return;
        }

        Bitmap bitmap = drawableToBitmap(drawable);
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if(type == CIRCLE_TYPE){
            float bSize = Math.min(bitmap.getWidth(),bitmap.getHeight());
            scale = mWidth * 1.0f/bSize;
        }
        if(type == ROUND_TYPE){
            scale = Math.max(getWidth()/bitmap.getWidth(),getHeight()/bitmap.getHeight());
        }
        mMatirx.setScale(scale,scale);
        mBitmapShader.setLocalMatrix(mMatirx);
        mBitmapPaint.setShader(mBitmapShader);
    }

    /**
     * 将Drawable转化成bitmap
     * @param drawable
     * @return  bitmap
     */
    private Bitmap drawableToBitmap(Drawable drawable){

        if(drawable instanceof BitmapDrawable){
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,w,h);
        drawable.draw(canvas);
        return bitmap;
    }

    public void setRoundRadius(int radius){
        int px = dp2px(radius);
        if(this.mRoundRadius != px){
            this.mRoundRadius = px;
            invalidate();
        }
    }

    public void setType(int type){
        if(this.type != type){
            this.type = type;
            if(type != CIRCLE_TYPE && type != ROUND_TYPE){
                this.type = CIRCLE_TYPE;
            }
            requestLayout();
        }
    }

    private int dp2px(float dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getResources().getDisplayMetrics());
    }


}
