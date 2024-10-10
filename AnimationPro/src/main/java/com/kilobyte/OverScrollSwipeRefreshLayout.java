package com.kilobyte;

import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class OverScrollSwipeRefreshLayout {

    // Constants for behavior
    public static final float RESISTANCE_FACTOR = 0.2f;
    public static final long BOUNCE_BACK_DURATION = 300; // in milliseconds
    public static final float MAX_OVERSCROLL_DISTANCE = 300f; // in pixels

    // Variable to track the initial Y position of touch events
    private static float startY = 0f;
    private static boolean isOverscrolling = false;

    // Method to enable over-scroll effect on a SwipeRefreshLayout
    public static void enableOverScroll(SwipeRefreshLayout swipeRefreshLayout) {

        swipeRefreshLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Store the initial Y position when touch begins
                        startY = event.getY();
                        isOverscrolling = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float offsetY = (event.getY() - startY) * RESISTANCE_FACTOR;

                        // Check if user is pulling down and over-scrolling
                        if (offsetY > 0 && !swipeRefreshLayout.isRefreshing()) {
                            isOverscrolling = true;
                            float overScrollDistance = Math.min(offsetY, MAX_OVERSCROLL_DISTANCE);
                            swipeRefreshLayout.setTranslationY(overScrollDistance);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (isOverscrolling) {
                            // Animate the swipeRefreshLayout back to its original position
                            ObjectAnimator animator = ObjectAnimator.ofFloat(
                                    swipeRefreshLayout, "translationY", swipeRefreshLayout.getTranslationY(), 0);
                            animator.setDuration(BOUNCE_BACK_DURATION);
                            animator.start();
                            isOverscrolling = false;
                        }
                        break;
                }
                return false;
            }
        });
    }
}