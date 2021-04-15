package com.yfz.password;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 简介：自定义验证码输入框 (组合+自绘)
 * 作者：游丰泽
 * 主要功能: （以下功能涉及到盒子样式的改变，均可单独控制功能的盒子样式,默认为线，可自定设置backgroundDrawable替代）
 *           password_box 基础盒子，password_boxAfter输入内容后的盒子，password_boxHighLight 高亮盒子，password_boxLock 锁住状态下盒子
 * mEnableHideCode 是否隐藏输入内容
 * mEnableHighLight 是否开启高亮
 * mEnableCursor 是否开启光标
 * mEnableHideNotInputBox 是否将没有输入内容的盒子隐藏
 * mEnableSoftKeyboardAutoClose 开关自动关闭软键盘
 * mEnableSoftKeyboardAutoShow 开关自动展现软键盘
 * mEnableLockCodeTextIfMaxCode 开关输入内容满足长度后是否锁定
 */

public class PasswordView extends LinearLayout {


    private boolean mEnableHideCode =false;//是否隐藏输入code
    private boolean mEnableHighLight=true;//是否开启高亮
    private boolean mEnableCursor =false;//是否开启光标
    private boolean mEnableHideNotInputBox=false;//是否将没有输入内容的盒子隐藏
    private boolean mEnableSoftKeyboardAutoShow=true;//是否自动打开软键盘
    private boolean mEnableSoftKeyboardAutoClose=true;//是否自动关闭软键盘（输入内容长度==最大长度）
    private boolean mEnableLockCodeTextIfMaxCode =false;//是否限制输满后锁定view

    private final int PAINT_FILLED =100, PAINT_STROKE =101;
    private final int TEXT_INPUT_TYPE_NUMBER=200, TEXT_INPUT_TYPE_PHONE =201, TEXT_INPUT_TYPE_TEXT =202,TEXT_INPUT_TYPE_DATETIME=203;
    private final String DEFAULT_HIDE_CONTENT="*";

    private int mStrokeWidth=1;
    private boolean mIsEnableLock=false;
    private boolean mIsLocked=false;
    private boolean mIsCodeFull =false;
    private int mIsFirstTime=0;
    private final String TAG= PasswordView.class.getName();

    private Context mContext;
    private int measureWidthMode =0;
    private int measureWidthSize =0;
    private int measureHeightMode =0;
    private int measureHeightSize =0;
    private OnResultListener mOnResultListener;
    private InputMethodManager inputMethodManager;
    //组件
    private EditText mEditText;
    private String mHideCodeString;//隐藏输入code-显示的内容
    private int mViewBackground=Color.TRANSPARENT;//背景Drawable
    //盒子
    private Paint mPaintBox;//笔刷
    private RectF mBoxRectF;//矩形（绘制位置）
    private Rect mBoxRect;//矩形（绘制位置）
    private int mBoxMaxLength=4;//数量
    private int mBoxSizeDp=50;//大小
    private int mBoxMargin=10;//盒子之间的间距
    private int mBoxBackgroundColor =Color.RED;//背景颜色（空心边框，实心背景）
    private Drawable mBoxBackgroundDrawable;//背景Drawable
    private int mBoxStrokeStyle = PAINT_STROKE;//盒子样式（空心，实心）
    private float mBoxRadius=5f;//圆弧半径
    //高亮盒子
    private Paint mBoxHighLightPaint;//笔刷
    private int mBoxHighLightIndex =0;//下坐标
    private Drawable mBoxHighBackgroundDrawable;//背景
    private int mBoxHighLightBackgroundColor =Color.BLUE;//颜色
    private int mBoxHighLightStrokeStyle = PAINT_STROKE;//高亮样式（空心，实心）
    private float mBoxHighLightRadius =5f;//圆弧半径
    //输入后的盒子
    private int mBoxAfterStrokeStyle = PAINT_STROKE;//高亮样式（空心，实心）
    private int mBoxAfterBackgroundColor =Color.RED;//背景s
    private Drawable mBoxAfterBackgroundDrawable;//背景
    private Paint mBoxAfterPaint;//笔刷
    private float mBoxAfterRadius =5f;//圆弧半径
    //文字
    private Paint mPaintText;//笔刷
    private Rect mTextRect;//矩形（绘制位置）
    private String[] mCodeArray;//输入Code内容
    private int mTextColor=Color.BLACK;//颜色
    private int mTextSize=10;//大小
    private int mTextInputType=TEXT_INPUT_TYPE_NUMBER;//类型
    private boolean mTextBold=true;//粗细
    //光标-笔刷
    private Paint mCursorPaint;//笔刷
    private Timer mCursorTimer;//定时器
    private TimerTask mCursorTimerTask;//定时器任务
    private int mCursorBackgroundColor =Color.BLACK;//颜色
    private Drawable mCursorBackgroundDrawable;//背景
    private int mCursorHeight =1;//上下边距
    private int mCursorWidth =1;//上下边距
    private int mCursorFrequency=500;//闪烁频率
    private boolean mCursorDisplayingByTimer =false;//显示光标-定时器-闪烁效果
    private boolean mCursorDisplayingByIndex =false;//显示光标-第一次下坐标
    //锁定盒子
    private int mBoxLockBackgroundColor =-1; //背景
    private int mBoxLockStrokeStyle = PAINT_STROKE;//高亮样式（空心，实心）
    private int mBoxLockTextColor = -1;//文字颜色
    private Drawable  mBoxLockBackgroundDrawable;

    public PasswordView(@NonNull Context context) {
        super(context);
        initial(context);
    }
    public PasswordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.PasswordCode);
        mViewBackground =typedArray.getResourceId(R.styleable.PasswordCode_password_viewBackground,Color.TRANSPARENT);//View背景Drawable
        //文字颜色
        mTextColor=typedArray.getColor(R.styleable.PasswordCode_password_textColor,mTextColor);
        mTextSize=typedArray.getInt(R.styleable.PasswordCode_password_textSizePx,mTextSize);
        mTextInputType=typedArray.getInt(R.styleable.PasswordCode_password_textInputType,mTextInputType);
        mTextBold=typedArray.getBoolean(R.styleable.PasswordCode_password_textBold,mTextBold);
        //控制
        mEnableSoftKeyboardAutoShow=typedArray.getBoolean(R.styleable.PasswordCode_password_enableSoftKeyboardAutoShow, mEnableSoftKeyboardAutoShow);//自动弹出键盘
        mEnableSoftKeyboardAutoClose =typedArray.getBoolean(R.styleable.PasswordCode_password_enableSoftKeyboardAutoClose, mEnableSoftKeyboardAutoClose);//自动隐藏键盘
        mEnableHideCode =typedArray.getBoolean(R.styleable.PasswordCode_password_enableHideCode, mEnableHideCode);//是否隐藏输入内容
        mHideCodeString=typedArray.getString(R.styleable.PasswordCode_password_hideBoxDisplayContent);//隐藏内容时-显示的文案
        mEnableHideNotInputBox =typedArray.getBoolean(R.styleable.PasswordCode_password_enableHideBoxWhenNotInput, mEnableHideNotInputBox);//是否将没有输入内容的盒子隐藏
        mEnableHighLight=typedArray.getBoolean(R.styleable.PasswordCode_password_enableHighLight,mEnableHighLight);//开启关闭
        mEnableCursor =typedArray.getBoolean(R.styleable.PasswordCode_password_enableCursor, mEnableCursor);//开启关闭
        mEnableLockCodeTextIfMaxCode =typedArray.getBoolean(R.styleable.PasswordCode_password_enableLockTextView, mEnableLockCodeTextIfMaxCode);//开启关闭
        //盒子
        mBoxMaxLength=typedArray.getInt(R.styleable.PasswordCode_password_boxLength,mBoxMaxLength);//获取盒子数量（长度）
        mBoxMargin=typedArray.getInt(R.styleable.PasswordCode_password_boxMargin,mBoxMargin);//获取盒子边距
        mBoxSizeDp =typedArray.getInt(R.styleable.PasswordCode_password_boxSizeDp, mBoxSizeDp);//获取盒子大小
        mBoxBackgroundColor =typedArray.getColor(R.styleable.PasswordCode_password_boxBackgroundColor, mBoxBackgroundColor);//获取盒子背景颜色
        mBoxBackgroundDrawable=typedArray.getDrawable(R.styleable.PasswordCode_password_boxBackgroundDrawable);//获取盒子背景Drawable
        mBoxStrokeStyle =typedArray.getInt(R.styleable.PasswordCode_password_boxStrokeStyle, mBoxStrokeStyle);//笔刷样式
        mBoxRadius=typedArray.getFloat(R.styleable.PasswordCode_password_boxRadius,mBoxRadius);//圆弧半径
        //高亮
        mBoxHighBackgroundDrawable =typedArray.getDrawable(R.styleable.PasswordCode_password_boxHighLightBackgroundDrawable);//背景
        mBoxHighLightBackgroundColor =typedArray.getInt(R.styleable.PasswordCode_password_boxHighLightBackgroundColor, mBoxHighLightBackgroundColor);//颜色-默认跟盒子一样
        mBoxHighLightStrokeStyle =typedArray.getInt(R.styleable.PasswordCode_password_boxHighLightStrokeStyle, mBoxStrokeStyle);//笔刷样式-默认跟盒子一样
        mBoxHighLightRadius =typedArray.getFloat(R.styleable.PasswordCode_password_boxHighLightRadius,mBoxRadius);//圆弧半径-默认跟盒子一样
        //输入之后的盒子样式
        mBoxAfterStrokeStyle=typedArray.getInt(R.styleable.PasswordCode_password_boxAfterStrokeStyle,mBoxStrokeStyle);//样式-默认跟普通盒子一样
        mBoxAfterBackgroundColor=typedArray.getColor(R.styleable.PasswordCode_password_boxAfterBackgroundColor,mBoxBackgroundColor);//背景颜色-默认跟普通盒子一样
        mBoxAfterBackgroundDrawable=typedArray.getDrawable(R.styleable.PasswordCode_password_boxAfterBackgroundDrawable);//背景
        mBoxAfterRadius=typedArray.getFloat(R.styleable.PasswordCode_password_boxAfterRadius,mBoxRadius);//圆弧半径-默认跟普通盒子一样
        //光标
        mCursorBackgroundColor =typedArray.getColor(R.styleable.PasswordCode_password_cursorBackgroundColor, mCursorBackgroundColor);//颜色
        mCursorHeight =typedArray.getInt(R.styleable.PasswordCode_password_cursorHeight,mCursorHeight);//高度边距
        mCursorWidth =typedArray.getInt(R.styleable.PasswordCode_password_cursorWidth,mCursorWidth);//高度边距

        mCursorFrequency=typedArray.getInt(R.styleable.PasswordCode_password_cursorFrequencyMillisecond,mCursorFrequency);//闪烁频率
        mCursorBackgroundDrawable=typedArray.getDrawable(R.styleable.PasswordCode_password_cursorBackgroundDrawable);//背景
        //锁定
        mBoxLockBackgroundColor=typedArray.getColor(R.styleable.PasswordCode_password_boxLockBackgroundColor, mBoxLockBackgroundColor);//颜色
        mBoxLockStrokeStyle=typedArray.getInt(R.styleable.PasswordCode_password_boxLockStrokeStyle,mBoxLockStrokeStyle);//空心实心
        mBoxLockTextColor=typedArray.getColor(R.styleable.PasswordCode_password_boxLockTextColor, mBoxLockTextColor);//颜色
        mBoxLockBackgroundDrawable=typedArray.getDrawable(R.styleable.PasswordCode_password_boxLockBackgroundDrawable);//背景
        typedArray.recycle();
        initial(context);
    }

    //测量-CodeText大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureWidthMode =MeasureSpec.getMode(widthMeasureSpec);
        measureWidthSize =MeasureSpec.getSize(widthMeasureSpec);
        measureHeightMode =MeasureSpec.getMode(heightMeasureSpec);
        measureHeightSize =MeasureSpec.getSize(heightMeasureSpec);
        if(measureWidthMode==MeasureSpec.AT_MOST && measureHeightMode==MeasureSpec.AT_MOST){
            //宽高均未声明绝对值
            //组件宽 = (盒子大小*数量)+（盒子边距*(数量-1))+画笔宽度
            //组件高 = (盒子大小)
            measureWidthSize = mBoxSizeDp * (mBoxMaxLength) + mBoxMargin * (mBoxMaxLength - 1) ;
            measureHeightSize = mBoxSizeDp;
        }else if(measureWidthMode==MeasureSpec.EXACTLY && measureHeightMode==MeasureSpec.EXACTLY){
            //宽高均声明了绝对值
            //只需计算盒子大小= (测量高-（盒子边距*（数量-1）+画笔宽度）/ 盒子数量)
            mBoxSizeDp =(int)((measureWidthSize -  mBoxMargin * (mBoxMaxLength - 1))/(mBoxMaxLength));
        }else if(measureWidthMode==MeasureSpec.EXACTLY && measureHeightMode==MeasureSpec.AT_MOST){
            //只声明了宽的绝对值，高未声明
            mBoxSizeDp =(int)((measureWidthSize -  mBoxMargin * (mBoxMaxLength - 1))/(mBoxMaxLength));
        }else if(measureHeightMode==MeasureSpec.EXACTLY && measureWidthMode==MeasureSpec.AT_MOST){
            //只声明了高的绝对值，宽未声明
            mBoxSizeDp =(int)((measureWidthSize -  mBoxMargin * (mBoxMaxLength - 1))/(mBoxMaxLength));
        }
        setMeasuredDimension(measureWidthSize, measureHeightSize);
    }

    //初始化-CodeText
    @SuppressLint("ResourceType")
    private void initial(Context context){
        this.mContext=context;
            try{
                Drawable drawable= getResources().getDrawable(mViewBackground);
                this.setBackground(drawable);
            }catch (Exception e){
                this.setBackgroundColor(mViewBackground);
            }
        this.mCodeArray =new String[mBoxMaxLength];
        this.mIsEnableLock=mEnableLockCodeTextIfMaxCode;
        if(null==this.mHideCodeString){
            this.mHideCodeString=DEFAULT_HIDE_CONTENT;
        }else if(this.mHideCodeString.length()>0) {
            this.mHideCodeString = mHideCodeString.substring(0, 1);
        }
        mCursorTimerTask = new TimerTask() {
            @Override
            public void run() {
                mCursorDisplayingByTimer = !mCursorDisplayingByTimer;
                postInvalidate();
            }
        };
        mCursorTimer = new Timer();
        initialEditText();
        initialPaint();
        initialBoxAndRectPosition();
        setOnLayoutListener(this.mEditText);
        setOnTouchListener(this);
    }

    //初始化EdiText
    private void initialEditText(){
        this.mEditText=new EditText(this.getContext());
        LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mEditText.setLayoutParams(layoutParams);
        this.mEditText.setBackgroundColor(Color.TRANSPARENT);
        this.mEditText.setTextColor(Color.TRANSPARENT);
        this.addView(mEditText);
        this.mEditText.setWidth(1);
        this.mEditText.setHeight(1);
        this.mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mBoxMaxLength)});
        switch (mTextInputType){
            case TEXT_INPUT_TYPE_NUMBER:
                this.mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case TEXT_INPUT_TYPE_PHONE:
                this.mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case TEXT_INPUT_TYPE_TEXT:
                this.mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case TEXT_INPUT_TYPE_DATETIME:
                this.mEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                break;
        }
        this.mEditText.setSingleLine();
        this.mEditText.setCursorVisible(false);
        inputMethodManager = (InputMethodManager) this.mEditText.getContext().getSystemService(this.mEditText.getContext().INPUT_METHOD_SERVICE);
        this.mEditText.addTextChangedListener(new mWatcher());
    }
    //初始化-盒子和位置
    private void initialBoxAndRectPosition(){
        this.mBoxSizeDp = DisplayUtils.dip2px(mContext, mBoxSizeDp);
        this.mBoxMargin= DisplayUtils.dip2px(mContext,mBoxMargin);
        this.mBoxRadius= DisplayUtils.dip2pxFloat(mContext,mBoxRadius);
        this.mBoxHighLightRadius = DisplayUtils.dip2pxFloat(mContext, mBoxHighLightRadius);
        this.mBoxAfterRadius = DisplayUtils.dip2pxFloat(mContext, mBoxAfterRadius);
        this.mBoxRectF=new RectF();
        this.mBoxRect=new Rect();
        this.mTextRect=new Rect();
    }
    //初始化-笔刷
    private void initialPaint(){
        this.mStrokeWidth = DisplayUtils.dip2px(mContext, mStrokeWidth);
//        this.mCursorHeight = DisplayUtils.dip2px(mContext, mCursorHeight);
//        this.mCursorWidth = DisplayUtils.dip2px(mContext, mCursorWidth);
        //文字
        this.mPaintText=new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaintText.setStyle(Paint.Style.FILL);
        this.mPaintText.setTextSize(DisplayUtils.dip2px(this.getContext(),mTextSize)*2);
        this.mPaintText.setColor(mTextColor);
        this.mPaintText.setFakeBoldText(mTextBold);
        //盒子
        this.mPaintBox=new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaintBox.setStyle(mBoxStrokeStyle == PAINT_STROKE ?Paint.Style.STROKE:Paint.Style.FILL);
        this.mPaintBox.setColor(mBoxBackgroundColor);
        this.mPaintBox.setStrokeWidth(mStrokeWidth);
        //高亮
        this.mBoxHighLightPaint =new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mBoxHighLightPaint.setStyle(mBoxHighLightStrokeStyle == PAINT_STROKE ?Paint.Style.STROKE:Paint.Style.FILL);
        this.mBoxHighLightPaint.setColor(mBoxHighLightBackgroundColor);
        this.mBoxHighLightPaint.setStrokeWidth(mStrokeWidth);
        //光标
        this.mCursorPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mCursorPaint.setColor(mCursorBackgroundColor);
        this.mCursorPaint.setStyle(Paint.Style.FILL);
        //输入后
        this.mBoxAfterPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mBoxAfterPaint.setColor(mBoxAfterBackgroundColor);
        this.mBoxAfterPaint.setStyle(mBoxAfterStrokeStyle == PAINT_STROKE ?Paint.Style.STROKE:Paint.Style.FILL);
        this.mBoxAfterPaint.setStrokeWidth(mStrokeWidth);
    }
    //监听点击事件-打开弹窗
    private void setOnTouchListener(View view){
        if(null != view) {
            view.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                  if(event.getAction()==MotionEvent.ACTION_UP && !mIsLocked) {
                          openSoftKeyboard(mEditText);
                  }
                    return true;
                }
            });
        }
    }

    class mWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence text, int start, int end, int count) {
        }
        @Override
        public void afterTextChanged(Editable text) {
            mBoxHighLightIndex=text.length();
            if(null!= mCodeArray ) {
                    for (int i=0;i<mBoxMaxLength;i++){
                        if(i<=text.length()-1) {
                            mCodeArray[i] = text.toString().substring(i, i + 1);
                        }else {
                            mCodeArray[i] = "";
                        }
                    }
                    mCursorDisplayingByIndex=true;
                if( text.length()==mBoxMaxLength){ //内容长度与盒子数量一致->返回回调结果
                    mIsCodeFull = true;
                    if(null!=mOnResultListener) {
                        mOnResultListener.finish(text.toString());
                    }
                    if(mEnableSoftKeyboardAutoClose || mIsEnableLock){
                        closeSoftKeyboard(mEditText);
                    }
                    mIsLocked = mEnableLockCodeTextIfMaxCode ? true : false;
                }else {
                    if(null!=mOnResultListener) {
                        mOnResultListener.input(text.toString());
                    }
                }
             postInvalidate();

            }
        }
    }

    //锁定CodeText
    public void setOnLock(){
        mEnableLockCodeTextIfMaxCode=true;
        mIsLocked=true;
    }
    //解除锁定CodeText
    public void setUnLock(){
//        mEnableLockCodeTextIfMaxCode=false;
        if(mIsCodeFull) {
            openSoftKeyboard(mEditText);
            mIsLocked=false;
        }
    }

    //监听View是否渲染完成
    private void setOnLayoutListener(final View view){
        if(null != view) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!mIsCodeFull && mIsFirstTime <= 3 && mEnableSoftKeyboardAutoShow) {
                        openSoftKeyboard(view);
                        mIsFirstTime++;
                    }
                }
            });
        }
    }


    //画布-绘制板
    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mBoxMaxLength; i++) {
            mBoxRectF.left =(float)( i * (mBoxSizeDp + mBoxMargin) ) ;
            mBoxRectF.top =(float)(0);
            mBoxRectF.right = (float)(mBoxRectF.left + mBoxSizeDp );
            mBoxRectF.bottom = (float)(measureHeightSize);

            mBoxRect.left=(int)(i * (mBoxSizeDp + mBoxMargin)) ;
            mBoxRect.top=(int) (mBoxRectF.top);
            mBoxRect.right=(int) mBoxRectF.right;
            mBoxRect.bottom=(int) (mBoxRectF.bottom);
            if(mEnableHighLight && i == mBoxHighLightIndex){
                if(null!= mBoxHighBackgroundDrawable) {  //如果有规定drawable，则使用drawable
                    mBoxHighBackgroundDrawable.setBounds(mBoxRect);
                    mBoxHighBackgroundDrawable.draw(canvas);
                }else {
                    canvas.drawRoundRect(mBoxRectF, mBoxHighLightRadius, mBoxHighLightRadius, mBoxHighLightPaint);
                }
                    onDrawCursor(canvas, mCursorPaint, mBoxRectF,mBoxRect);

            } else if (null != mCodeArray[i]) {

                if(i<mBoxHighLightIndex){
                    mBoxAfterPaint.setStyle(mIsLocked?mBoxLockStrokeStyle == PAINT_STROKE ?Paint.Style.STROKE:Paint.Style.FILL:mBoxAfterStrokeStyle == PAINT_STROKE ?Paint.Style.STROKE:Paint.Style.FILL);
                    mBoxAfterPaint.setColor((mIsLocked&&mBoxLockBackgroundColor!=-1)?mBoxLockBackgroundColor:mBoxAfterBackgroundColor);
                    if(null!=mBoxAfterBackgroundDrawable) {  //如果有规定drawable，则使用drawable
                        if(mIsLocked){
                            if(null!=mBoxLockBackgroundDrawable){
                                mBoxLockBackgroundDrawable.setBounds(mBoxRect);
                                mBoxLockBackgroundDrawable.draw(canvas);

                            }else {
                                canvas.drawRoundRect(mBoxRectF, mBoxAfterRadius, mBoxAfterRadius, mBoxAfterPaint);
                            }
                        }else {
                            mBoxAfterBackgroundDrawable.setBounds(mBoxRect);
                            mBoxAfterBackgroundDrawable.draw(canvas);
                        }
                    }else {
                        canvas.drawRoundRect(mBoxRectF, mBoxAfterRadius, mBoxAfterRadius, mBoxAfterPaint);
                    }
                }else {
                    if(!mEnableHideNotInputBox) {
                        mPaintBox.setStyle(mIsLocked ? mBoxLockStrokeStyle == PAINT_STROKE ? Paint.Style.STROKE : Paint.Style.FILL : mBoxAfterStrokeStyle == PAINT_STROKE ? Paint.Style.STROKE : Paint.Style.FILL);
                        mPaintBox.setColor((mIsLocked && mBoxLockBackgroundColor != -1) ? mBoxLockBackgroundColor : mBoxBackgroundColor);
                        if (null != mBoxBackgroundDrawable) {
                            mBoxBackgroundDrawable.setBounds(mBoxRect);
                            mBoxBackgroundDrawable.draw(canvas);
                        } else {
                            canvas.drawRoundRect(mBoxRectF, mBoxRadius, mBoxRadius, mPaintBox);
                        }
                    }
                }
                mPaintText.setColor((mIsLocked&&mBoxLockTextColor!=-1)?mBoxLockTextColor: mTextColor);
                mPaintText.getTextBounds(mEnableHideCode ?mHideCodeString: mCodeArray[i], 0, mCodeArray[i].length(), mTextRect);
                if(mCodeArray[i].length()>0)
                canvas.drawText(mEnableHideCode ?mHideCodeString: mCodeArray[i], (mBoxRectF.left + mBoxRectF.right) / 2 - (mTextRect.left + mTextRect.right) / 2, (mBoxRectF.top + mBoxRectF.bottom) / 2 - (mTextRect.top + mTextRect.bottom) / 2, mPaintText);
            }else if(!mEnableHideNotInputBox){
                if(null!=mBoxBackgroundDrawable) {  //如果有规定drawable，则使用drawable
                    mBoxBackgroundDrawable.setBounds(mBoxRect);
                    mBoxBackgroundDrawable.draw(canvas);
                }else{
                    mPaintBox.setColor(mBoxBackgroundColor);
                    canvas.drawRoundRect(mBoxRectF, mBoxRadius, mBoxRadius, mPaintBox);
                }
            }
        }

    }
    //绘制-光标
    private void onDrawCursor(Canvas canvas,Paint paint,RectF rectF,Rect rect){
        if(paint!=null && mEnableCursor){
            if(null!=mCursorBackgroundDrawable ){
                rect.left= (int)((rectF.left + rectF.right) / 2 - mCursorWidth);
                rect.top=(int)(mCursorHeight <= 1 ? (rectF.top + rectF.bottom) / 4:mCursorHeight );
                rect.right=(int)((rectF.left + rectF.right) / 2 + mCursorWidth);
                rect.bottom=(int) (rectF.bottom - (mCursorHeight <= 1 ? (rectF.top + rectF.bottom) / 4:mCursorHeight ));
                mCursorBackgroundDrawable.setBounds(rect);
                if((mCursorDisplayingByTimer || mCursorDisplayingByIndex) ){
                    mCursorBackgroundDrawable.draw(canvas);
                }
            }else {
                mCursorPaint.setColor((mCursorDisplayingByTimer || mCursorDisplayingByIndex) ? mCursorBackgroundColor : Color.TRANSPARENT);
                canvas.drawRect(
                        (float) ((rectF.left + rectF.right) / 2 - mCursorWidth),
                        (float) (mCursorHeight <= 1 ? (rectF.top + rectF.bottom) / 4:mCursorHeight ),
                        (float) ((rectF.left + rectF.right) / 2 + mCursorWidth),
                        (float) (rectF.bottom - (mCursorHeight <= 1 ? (rectF.top + rectF.bottom) / 4 :mCursorHeight))
                        , paint);
            }
        }

        mCursorDisplayingByIndex=false;
    }

    //开始计时器，开始光标闪烁
    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow: ");
        super.onAttachedToWindow();
        if(mEnableCursor) {
            mCursorTimer.scheduleAtFixedRate(mCursorTimerTask, 0, mCursorFrequency);
        }
    }
    //停止计时器，停止光标闪烁
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCursorTimer.cancel();
    }
    //打开软键盘
    public void openSoftKeyboard(View view){
        if(null != view ) {
            Log.d(TAG, "openSoftKeyboard: ");
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            inputMethodManager.showSoftInput(view, 0);
        }

    }
    //关闭软键盘
    public void closeSoftKeyboard(View view){
        if(null != view) {
            Log.d(TAG, "closeSoftKeyboard: ");
            if (mEnableSoftKeyboardAutoClose || mIsLocked || mIsEnableLock) {
                view.clearFocus();
//            this.setFocusable(false);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    //接口回调输入结果
    public interface OnResultListener {
        void finish(String result);
        void input(String result);
    }

    //监听接口回调
    public void setOnResultListener(OnResultListener onResultListener){
        this.mOnResultListener=onResultListener;
    }


}
