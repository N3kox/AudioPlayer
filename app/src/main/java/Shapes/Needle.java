package Shapes;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class Needle extends AppCompatImageView {

    public static final int STATE_DOWN = 1;//播放
    public static final int STATE_UP = 0;//未播放
    public int state;

    private ObjectAnimator objectAnimator;

    public Needle(Context context) {
        super(context);
        init();
    }

    public Needle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Needle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressWarnings("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void init(){
        this.setPivotX(70);
        this.setPivotY(50);
        state = STATE_UP;

    }

    public void play(){
        if(state == STATE_UP){
            objectAnimator = ObjectAnimator.ofFloat(this,"rotation",0f,30f);
            objectAnimator.setDuration(500);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.start();
            state = STATE_DOWN;
        }else{
            objectAnimator = ObjectAnimator.ofFloat(this,"rotation",30f,0f);
            objectAnimator.setDuration(500);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.start();
            state = STATE_UP;
        }
    }


    public void stop(){
        if(state == STATE_UP){

        }else{
            objectAnimator = ObjectAnimator.ofFloat(this,"rotation",45f,0f);
            objectAnimator.setDuration(500);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.start();
            state = STATE_UP;
        }
    }
}
