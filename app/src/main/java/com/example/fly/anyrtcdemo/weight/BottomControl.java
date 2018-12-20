package com.example.fly.anyrtcdemo.weight;

import android.content.Context;
import android.util.AttributeSet;

import android.widget.RelativeLayout;

public class BottomControl extends RelativeLayout {

    public boolean mAvailable;
    Context context;

    public BottomControl(Context context) {
        super(context);
        init(context, null);
    }

    public BottomControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public BottomControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, null);
    }

    private void init(Context paramContext, AttributeSet paramAttributeSet) {
        this.context = paramContext;
        this.mAvailable = true;
    }
}
