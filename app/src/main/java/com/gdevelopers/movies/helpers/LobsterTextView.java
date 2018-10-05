package com.gdevelopers.movies.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class LobsterTextView extends androidx.appcompat.widget.AppCompatTextView {
    public LobsterTextView(Context context) {

        super(context);

        if (!isInEditMode())
            init(context);
    }

    public LobsterTextView(Context context, AttributeSet attrs) {

        super(context, attrs);

        if (!isInEditMode())
            init(context);
    }

    public LobsterTextView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        if (!isInEditMode())
            init(context);

    }

    private void init(Context context) {
        Typeface t = Typeface.createFromAsset(context.getAssets(), "Lobster-Regular.ttf");
        this.setTypeface(t);
    }
}
