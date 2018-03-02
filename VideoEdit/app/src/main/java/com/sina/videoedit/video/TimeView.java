package com.sina.videoedit.video;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.sina.videoedit.util.DisplayUtils;


/**
 * 拍摄进度条
 *
 * @author weiwei42
 */
public class TimeView extends View {

    /**
     * 字体高度偏移值
     */
    private final static int TEXT_OFFSET = 4;

    /**
     * 字体画笔
     **/
    private Paint mTextPaint;

    /**
     * 字体背景画笔
     **/
    private Paint mTextBgPaint;

    /**
     * 进度条画笔
     **/
    private Paint mProgressPaint;

    /**
     * 进度条高度，默认为6px
     **/
    private int mProgressHeight = 6;

    /**
     * 进度条背景颜色值
     **/
    private int mColorProBg = Color.parseColor("#1d1d1d");

    /**
     * 进度条进度颜色值
     **/
    private int mColorPro = Color.parseColor("#ff7200");

    /**
     * 当前进度值
     **/
    private int mProgress = 0;

    /**
     * 最大进度值
     **/
    private int mMaxProgress = 1;

    /**
     * 当前要显示的字体
     **/
    private String mTextString = "00:00";

    private Rect mRect;//进度条区域

    public TimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initAttributes(context, attrs);
        initData();
    }

    public TimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeView(Context context) {
        this(context, null);
    }

    /**
     * 初始化自定义属性
     */
    private void initAttributes(Context context, AttributeSet attrs) {
        //TODO 自定义属性后续扩展

    }

    /**
     * 初始化数据
     */
    private void initData() {

        mProgressHeight = DisplayUtils.dpToPx(getContext(), 6);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);

        mTextBgPaint = new Paint();
        mTextBgPaint.setAntiAlias(true);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画进度条背景
        mProgressPaint.setColor(mColorProBg);
        canvas.drawRect(setRect(0, 0, getWidth(), mProgressHeight),
                mProgressPaint);

        // 画进度条进度
        int proWidth = getRealyProgressWidth();
        mProgressPaint.setColor(mColorPro);
        canvas.drawRect(setRect(0, 0, proWidth, mProgressHeight),
                mProgressPaint);

        // 画字体
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        mTextPaint.setColor(Color.WHITE); // 字体颜色
        mTextPaint.setTextSize(DisplayUtils.dpToPx(getContext(), 10)); // 字体大小
        float textWidth = mTextPaint.measureText(mTextString);
        FontMetrics fm = mTextPaint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;

        int proWidth = getRealyProgressWidth();//当前进度条宽度

        Path path = new Path();
        if (proWidth + textWidth + 12 - 6 <= getWidth()) {
            path.moveTo(proWidth, mProgressHeight);
            path.lineTo(proWidth + 6, mProgressHeight + 6);//三角角度为45度
            path.lineTo(proWidth + textWidth + 12, mProgressHeight + 6);
            path.lineTo(proWidth + textWidth + 12, mProgressHeight + 6 + 8
                    + textHeight);
            path.lineTo(proWidth, mProgressHeight + 6 + 8 + textHeight);
        } else {
            float bk_width = textWidth + 12;
            path.moveTo(getWidth(), mProgressHeight + 6);
            path.lineTo(proWidth, mProgressHeight + 6);
            path.lineTo(proWidth, mProgressHeight);
            path.lineTo(proWidth - 6, mProgressHeight + 6);
            path.lineTo(getWidth() - bk_width, mProgressHeight + 6);
            path.lineTo(getWidth() - bk_width, mProgressHeight + 6 + 8 + textHeight);
            path.lineTo(getWidth(), mProgressHeight + 6 + 8 + textHeight);

//            path.lineTo(proWidth - 6, mProgressHeight + 6);
//            path.lineTo(proWidth - textWidth - 12, mProgressHeight + 6);
//            path.lineTo(proWidth - textWidth - 12, mProgressHeight + 6 + 8
//                    + textHeight);
//            path.lineTo(proWidth, mProgressHeight + 6 + 8 + textHeight);
        }
        path.close();

        // 画背景
        mTextBgPaint.setColor(Color.BLACK);
        mTextBgPaint.setAlpha(51);
        mTextBgPaint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawPath(path, mTextBgPaint);

        // 画字体
        float posx = proWidth + 6;
        float posy = mProgressHeight + 6 + 4 + textHeight - TEXT_OFFSET;
        if (proWidth + textWidth + 12 > getWidth()) {
//            posx = proWidth - textWidth - 6;
            posx = getWidth() - textWidth - 6;
        }
        canvas.drawText(mTextString, posx, posy, mTextPaint);
    }

    /**
     * 得到进度条的实际宽度
     *
     * @return 进度条的宽度值
     */
    private int getRealyProgressWidth() {
        if (mProgress > mMaxProgress) {
            mProgress = mMaxProgress;
        }
        int progress = mProgress * getWidth() / mMaxProgress;

        return progress;
    }

    private Rect setRect(int left, int top, int right, int bottom) {
        if (mRect == null) {
            mRect = new Rect();
        }

        mRect.left = left;
        mRect.top = top;
        mRect.right = right;
        mRect.bottom = bottom;

        return mRect;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        }
        if (progress > mMaxProgress) {
            progress = mMaxProgress;
        }
        this.mProgress = progress;

        postInvalidate();
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;

        postInvalidate();
    }

    public String getTextString() {
        return mTextString;
    }

    public void setTextString(String textString) {
        this.mTextString = textString;
    }

}
