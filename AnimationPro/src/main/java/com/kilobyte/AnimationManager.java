package com.kilobyte;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.List;

public class AnimationManager {

    private static final int ANIMATION_STAGGER_DELAY = 50;
    private static final int ANIMATION_BASE_DURATION = 500;

    public static class SmoothAnimationCreator {
        
        public static void startContainerContentAnimation(ViewGroup containerView) {
            containerView.postDelayed(() -> animateChildViewsWithDelay(containerView), 100);
        }

        private static void animateChildViewsWithDelay(ViewGroup containerView) {
            int numberOfChildren = containerView.getChildCount();
            for (int i = 0; i < numberOfChildren; i++) {
                View childView = containerView.getChildAt(i);
                childView.setTranslationY(100f);
                childView.setAlpha(0f);

                childView.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(ANIMATION_BASE_DURATION)
                    .setStartDelay(i * ANIMATION_STAGGER_DELAY)
                    .setInterpolator(new CustomSpringInterpolator())
                    .start();
            }
        }

        public static void animateListViewItems(LinearLayout containerView, List<View> viewItems) {
            containerView.removeAllViews();
            for (int i = 0; i < viewItems.size(); i++) {
                View itemView = viewItems.get(i);
                containerView.addView(itemView);
                itemView.setTranslationY(100f);
                itemView.setAlpha(0f);

                itemView.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(ANIMATION_BASE_DURATION)
                    .setStartDelay((i + 3) * ANIMATION_STAGGER_DELAY)
                    .setInterpolator(new CustomSpringInterpolator())
                    .start();
            }
        }

        public static void startFadeInAnimation(View targetView) {
            targetView.setAlpha(0f);
            targetView.animate()
                .alpha(1f)
                .setDuration(800)
                .setInterpolator(new LinearInterpolator())
                .start();
        }

        public static void startFadeOutAnimation(View targetView) {
            targetView.animate()
                .alpha(0f)
                .setDuration(800)
                .setInterpolator(new LinearInterpolator())
                .start();
        }

        public static void startGradualScaleAnimation(View targetView) {
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(targetView, "scaleX", 1f, 1.2f, 1f);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(targetView, "scaleY", 1f, 1.2f, 1f);

            AnimatorSet scaleAnimationSet = new AnimatorSet();
            scaleAnimationSet.playTogether(scaleXAnimator, scaleYAnimator);
            scaleAnimationSet.setDuration(1500);
            scaleAnimationSet.setInterpolator(new OvershootInterpolator());
            scaleAnimationSet.start();
        }

        public static void startGentleRotationAnimation(View targetView) {
            ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(targetView, "rotation", 0f, 3f, -3f, 0f);
            rotationAnimator.setDuration(2000);
            rotationAnimator.setInterpolator(new LinearInterpolator());
            rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            rotationAnimator.start();
        }
    }

    public static class AnimationEffectsCreator {
        
        public static void startSpringAnimation(View targetView) {
            SpringForce springForce = new SpringForce(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM);

            SpringAnimation springAnimation = new SpringAnimation(targetView, DynamicAnimation.TRANSLATION_X)
                .setSpring(springForce);

            springAnimation.start();
        }

        public static void startBounceAnimation(View targetView) {
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(targetView, "scaleX", 1f, 1.07f, 1f);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(targetView, "scaleY", 1f, 1.07f, 1f);

            AnimatorSet bounceAnimationSet = new AnimatorSet();
            bounceAnimationSet.playTogether(scaleXAnimator, scaleYAnimator);
            bounceAnimationSet.setDuration(600);
            bounceAnimationSet.setInterpolator(new BounceInterpolator());
            bounceAnimationSet.start();
        }

        public static void applyParallaxScrollEffect(View foregroundView, View backgroundView, float scrollOffset) {
            backgroundView.setTranslationX(scrollOffset * 0.5f);
            foregroundView.setTranslationX(scrollOffset * 0.8f);
        }

        public static void startTapResponseAnimation(View targetView) {
            PropertyValuesHolder scaleDownXHolder = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.95f);
            PropertyValuesHolder scaleDownYHolder = PropertyValuesHolder.ofFloat("scaleY", 1f, 0.95f);
            PropertyValuesHolder scaleUpXHolder = PropertyValuesHolder.ofFloat("scaleX", 0.97f, 1.05f, 1f);
            PropertyValuesHolder scaleUpYHolder = PropertyValuesHolder.ofFloat("scaleY", 0.97f, 1.05f, 1f);

            ObjectAnimator scaleDownAnimator = ObjectAnimator.ofPropertyValuesHolder(targetView, scaleDownXHolder, scaleDownYHolder);
            ObjectAnimator scaleUpAnimator = ObjectAnimator.ofPropertyValuesHolder(targetView, scaleUpXHolder, scaleUpYHolder);

            scaleDownAnimator.setDuration(100);
            scaleUpAnimator.setDuration(200);
            scaleDownAnimator.setInterpolator(new AccelerateInterpolator());
            scaleUpAnimator.setInterpolator(new OvershootInterpolator(2.0f));

            AnimatorSet tapAnimationSet = new AnimatorSet();
            tapAnimationSet.playSequentially(scaleDownAnimator, scaleUpAnimator);
            targetView.setAlpha(0.8f);
            
            tapAnimationSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    targetView.setAlpha(0.8f);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    targetView.setAlpha(1f);
                }
            });

            tapAnimationSet.start();
        }
    }

    public static class AnimationPerformanceOptimizer {
        
        public static void applyAnimationOptimizations(ViewGroup containerView) {
            containerView.setHasTransientState(true);
            enableHardwareAcceleration(containerView);

            containerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        containerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        SmoothAnimationCreator.startContainerContentAnimation(containerView);
                        return true;
                    }
                });
        }

        public static void enableHardwareAcceleration(View targetView) {
            targetView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        public static void disableHardwareAcceleration(View targetView) {
            targetView.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    public static class TouchGestureHandler extends GestureDetector.SimpleOnGestureListener {
        private final View animatedView;
        private static float touchOffsetX;
        private static float touchOffsetY;

        public TouchGestureHandler(View animatedView) {
            this.animatedView = animatedView;
        }

        @Override
        public boolean onFling(MotionEvent startEvent, MotionEvent endEvent, float velocityX, float velocityY) {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                return velocityX > 0 ? startSwipeRightAnimation(animatedView) : startSwipeLeftAnimation(animatedView);
            } else {
                return velocityY > 0 ? startSwipeDownAnimation(animatedView) : startSwipeUpAnimation(animatedView);
            }
        }

        private boolean startSwipeRightAnimation(View targetView) {
            targetView.animate()
                .translationX(300f)
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> resetViewPosition(targetView))
                .start();
            return true;
        }

        private boolean startSwipeLeftAnimation(View targetView) {
            targetView.animate()
                .translationX(-300f)
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> resetViewPosition(targetView))
                .start();
            return true;
        }

        private boolean startSwipeUpAnimation(View targetView) {
            targetView.animate()
                .translationY(-300f)
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> resetViewPosition(targetView))
                .start();
            return true;
        }

        private boolean startSwipeDownAnimation(View targetView) {
            targetView.animate()
                .translationY(300f)
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> resetViewPosition(targetView))
                .start();
            return true;
        }

        private void resetViewPosition(View targetView) {
            targetView.animate()
                .translationX(0f)
                .translationY(0f)
                .alpha(1f)
                .setDuration(200)
                .start();
        }

        public static void initializeSwipeableView(View swipeableView) {
            if (swipeableView == null) return;

            GestureDetector gestureDetector = new GestureDetector(
                swipeableView.getContext(), 
                new TouchGestureHandler(swipeableView)
            );

            swipeableView.setOnTouchListener((view, event) -> {
                try {
                    if (gestureDetector.onTouchEvent(event)) {
                        return true;
                    }

                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            touchOffsetX = view.getX() - event.getRawX();
                            touchOffsetY = view.getY() - event.getRawY();
                            AnimationManager.AnimationEffectsCreator.startBounceAnimation(view);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            view.animate()
                                .x(event.getRawX() + touchOffsetX)
                                .y(event.getRawY() + touchOffsetY)
                                .setDuration(0)
                                .start();
                            break;

                        case MotionEvent.ACTION_UP:
                            AnimationManager.AnimationEffectsCreator.startSpringAnimation(view);
                            break;

                        default:
                            return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            });
        }
    }

    private static class CustomSpringInterpolator implements TimeInterpolator {
        private float springTension = 0.7f;

        @Override
        public float getInterpolation(float progress) {
            float x = progress * 2;
            return (float) (1 - (1 - progress) * (1 - progress) * Math.exp(-springTension * x));
        }
    }

    public static class ExitAnimationController {
        public static void startExitAnimation(LinearLayout contentContainer, Runnable completionAction) {
            int childCount = contentContainer.getChildCount();

            for (int i = childCount - 1; i >= 0; i--) {
                View childView = contentContainer.getChildAt(i);
                childView.animate()
                    .translationY(100f)
                    .alpha(0f)
                    .setDuration(70)
                    .setStartDelay(i * 20)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
            }

            new Handler(Looper.getMainLooper()).postDelayed(completionAction, childCount * 30 + 200);
        }
    }
}