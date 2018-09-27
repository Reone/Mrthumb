package com.reone.simple.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.reone.simple.R;


/**
 * Created by wangxingsheng on 2018/5/17.
 * 自定义宽高比布局
 */
public class FiexedLayout extends FrameLayout {

    private int mDemoHeight = -1;
    private int mDemoWidth = -1;
    private String mStandard = "w";
    private Boolean standardH;

    public FiexedLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FiexedLayout, defStyle, 0);
        if(a!=null){
            mDemoHeight = a.getInteger(R.styleable.FiexedLayout_demoHeight,-1);
            mDemoWidth = a.getInteger(R.styleable.FiexedLayout_demoWidth,-1);
            String standard = a.getString(R.styleable.FiexedLayout_standard);
            if(!TextUtils.isEmpty(standard) && (standard.equals("w") || standard.equals("h"))){
                mStandard = standard;
            }
        }
    }

    public FiexedLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FiexedLayout(Context context) {
        this(context,null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        // Children are just made to fill our space.
        if(mStandard.length() > 0 && mDemoWidth != -1 && mDemoHeight != -1){
            boolean standardByWidth;
            if(standardH != null){
                standardByWidth = !standardH;
            }else {
                standardByWidth = mStandard.equals("w");
            }
            if(standardByWidth){//以宽为标准
                int childWidthSize = getMeasuredWidth();
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize-1, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize * mDemoHeight / mDemoWidth - 1, MeasureSpec.EXACTLY);
            }else{//以高为标准
                int childheightSize = getMeasuredHeight();
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(childheightSize+1, MeasureSpec.EXACTLY);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(childheightSize * mDemoWidth / mDemoHeight - 1, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setStandardH(Boolean standardH) {
        this.standardH = standardH;
    }
}