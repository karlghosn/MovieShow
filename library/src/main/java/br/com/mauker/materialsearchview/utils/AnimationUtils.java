package br.com.mauker.materialsearchview.utils;

import android.animation.Animator;
import android.annotation.TargetApi;
import androidx.core.view.ViewCompat;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by Mauker on 18/04/2016. (dd/MM/yyyy).
 * <p>
 * Utility class used to easily animate Views. Most used for revealing or hiding Views.
 */
public class AnimationUtils {

    private static final int ANIMATION_DURATION_SHORTEST = 150;
    private static final int ANIMATION_DURATION_SHORT = 250;

    @TargetApi(21)
    public static void circleRevealView(View view) {
        // get the center for the clipping circle
        int cx = view.getWidth();
        int cy = view.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);


        anim.setDuration(ANIMATION_DURATION_SHORT);

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }


    /**
     * Reveal the provided View with a fade-in animation.
     *
     * @param view The View that's being animated.
     */
    public static void fadeInView(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);

        // Setting the listener to null, so it won't keep getting called.
        ViewCompat.animate(view).alpha(1f).setDuration(AnimationUtils.ANIMATION_DURATION_SHORTEST).setListener(null);
    }
}
