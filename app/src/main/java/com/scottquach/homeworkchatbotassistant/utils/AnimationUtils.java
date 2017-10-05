package com.scottquach.homeworkchatbotassistant.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;

import static java.lang.System.in;

/**
 * Created by Scott Quach on 10/5/2017.
 */

public class AnimationUtils {

//    https://developer.android.com/training/animation/crossfade.html
//    public static void crossFade(View view) {
//        // Set the content view to 0% opacity but visible, so that it is visible
//        // (but fully transparent) during the animation.
//        view.setAlpha(0f);
//        view.setVisibility(View.VISIBLE);
//
//        // Animate the content view to 100% opacity, and clear any animation
//        // listener set on the view.
//        view.animate()
//                .alpha(1f)
//                .setDuration(mShortAnimationDuration)
//                .setListener(null);
//
//        // Animate the loading view to 0% opacity. After the animation ends,
//        // set its visibility to GONE as an optimization step (it won't
//        // participate in layout passes, etc.)
//        view.animate()
//                .alpha(0f)
//                .setDuration(mShortAnimationDuration)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mLoadingView.setVisibility(View.GONE);
//                    }
//                });
//
//    }

    public static void textFade(final TextView view, final String newText, final int animationDuration) {

        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(animationDuration);

        view.startAnimation(fadeOut);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setText(newText);

                final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(animationDuration);
                view.startAnimation(fadeIn);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void fadeOut(final View view, final int animationDuration) {
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(animationDuration);
        view.startAnimation(fadeOut);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void fadeIn(final View view, final int animationDuration) {
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(animationDuration);
        view.startAnimation(fadeIn);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
