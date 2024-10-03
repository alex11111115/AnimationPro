package com.kilobyte;

import android.animation.*;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.animation.PathInterpolator;
import android.view.animation.Interpolator;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import android.os.Build;
import android.view.ViewGroup;
import android.graphics.drawable.BitmapDrawable;

public class ActivityTransitionManager {
    private static final long DEFAULT_DURATION = 300;
    private static final float SCALE_DOWN_VALUE = 0.99f;
    private static final float ALPHA_VALUE = 0.85f;
    
    private static final Interpolator IOS_INTERPOLATOR = new PathInterpolator(0.42f, 0f, 0.58f, 1f);
    
    public static class TransitionBuilder {
        private Activity activity;
        private Intent intent;
        private View sourceView;
        private long duration = DEFAULT_DURATION;
        private boolean withStatusBar = true;
        private Integer backgroundColor;
        
        public TransitionBuilder(@NonNull Activity activity) {
            this.activity = activity;
        }
        
        public TransitionBuilder setIntent(Intent intent) {
            this.intent = intent;
            return this;
        }
        
        public TransitionBuilder setSourceView(View sourceView) {
            this.sourceView = sourceView;
            return this;
        }
        
        public TransitionBuilder setDuration(long duration) {
            this.duration = duration;
            return this;
        }
        
        public TransitionBuilder setWithStatusBar(boolean withStatusBar) {
            this.withStatusBar = withStatusBar;
            return this;
        }
        
        public TransitionBuilder setBackgroundColor(int color) {
            this.backgroundColor = color;
            return this;
        }
        
        public void start() {
            if (intent != null) {
                startActivityWithTransition();
            } else {
                finishWithTransition();
            }
        }
        
        private void startActivityWithTransition() {
            prepareWindow();
            final View rootView = getRootView();
            final ViewGroup container = (ViewGroup) rootView.getParent();
            
            final View snapshotView = createSnapshot(rootView);
            if (container != null && snapshotView != null) {
                container.addView(snapshotView);
            }
            
            AnimatorSet animatorSet = new AnimatorSet();
            
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(rootView, View.SCALE_X, 1f, SCALE_DOWN_VALUE);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(rootView, View.SCALE_Y, 1f, SCALE_DOWN_VALUE);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(rootView, View.ALPHA, 1f, ALPHA_VALUE);
            ObjectAnimator translateZ = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_Z, 0f, -50f);
            ObjectAnimator translateX = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_X, 0f, rootView.getWidth() * 0.3f);
            
            animatorSet.playTogether(scaleX, scaleY, alpha, translateZ);
            animatorSet.setInterpolator(IOS_INTERPOLATOR);
            animatorSet.setDuration(duration);
            
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                        activity, 0, 0);
                    activity.startActivity(intent, options.toBundle());
                    resetView(rootView);
                    if (container != null && snapshotView != null) {
                        container.removeView(snapshotView);
                    }
                }
            });
            
            animatorSet.start();
        }
        
        private void finishWithTransition() {
            prepareWindow();
            final View rootView = getRootView();
            
            AnimatorSet animatorSet = new AnimatorSet();
            
            ObjectAnimator translateX = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_X, 0f, rootView.getWidth() * 0.3f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(rootView, View.ALPHA, 1f, 0f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(rootView, View.SCALE_X, 1f, 0.99f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(rootView, View.SCALE_Y, 1f, 0.99f);
            
            animatorSet.playTogether(translateX, alpha, scaleX, scaleY);
            animatorSet.setInterpolator(IOS_INTERPOLATOR);
            animatorSet.setDuration(duration);
            
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                        activity, 0, 0);
                    activity.finish();
                }
            });
            
            animatorSet.start();
        }
        
        private void prepareWindow() {
            Window window = activity.getWindow();
            if (backgroundColor != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.getDecorView().setBackgroundColor(backgroundColor);
            }
            
            if (withStatusBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
        
        private View getRootView() {
            return activity.getWindow().getDecorView().findViewById(android.R.id.content);
        }
        
        private View createSnapshot(View view) {
            Bitmap bitmap = createBitmapFromView(view);
            if (bitmap != null) {
                View snapshot = new View(activity);
                snapshot.setBackground(new BitmapDrawable(activity.getResources(), bitmap));
                snapshot.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ));
                return snapshot;
            }
            return null;
        }
        
        private Bitmap createBitmapFromView(View view) {
            Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }
    }
    
    public static void setupEnterAnimation(@NonNull Activity activity) {
        final View rootView = getRootView(activity);
        
        rootView.setAlpha(0f);
        rootView.setScaleX(1.05f);
        rootView.setScaleY(1.05f);
        rootView.setTranslationZ(100f);
        
        AnimatorSet animatorSet = new AnimatorSet();
        
        ObjectAnimator alpha = ObjectAnimator.ofFloat(rootView, View.ALPHA, 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(rootView, View.SCALE_X, 1.02f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(rootView, View.SCALE_Y, 1.02f, 1f);
        ObjectAnimator translateZ = ObjectAnimator.ofFloat(rootView, View.TRANSLATION_Z, 100f, 0f);
        
        animatorSet.playTogether(alpha, scaleX, scaleY, translateZ);
        animatorSet.setInterpolator(IOS_INTERPOLATOR);
        animatorSet.setStartDelay(60);
        animatorSet.setDuration(DEFAULT_DURATION);
        animatorSet.start();
    }
    
    private static View getRootView(Activity activity) {
        return activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }
    
    private static void resetView(View view) {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f);
        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f);
        PropertyValuesHolder translationZ = PropertyValuesHolder.ofFloat(View.TRANSLATION_Z, 0f);
        
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(
            view, alpha, scaleX, scaleY, translationX, translationZ);
        animator.setDuration(0).start();
    }
}