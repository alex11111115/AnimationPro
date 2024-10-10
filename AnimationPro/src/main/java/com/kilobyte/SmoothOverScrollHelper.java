package com.kilobyte;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.widget.NestedScrollView;

public class SmoothOverScrollHelper {
    public static final float RESISTANCE_FACTOR = 0.3f;
    public static final long BOUNCE_BACK_DURATION = 300;
    public static final float MAX_OVERSCROLL_DISTANCE = 500f;

    private View targetView;
    private float lastTouchX, lastTouchY;
    private float overscrollDistanceX, overscrollDistanceY;
    private boolean isOverscrollingHorizontal, isOverscrollingVertical;
    private ValueAnimator bounceBackAnimatorX, bounceBackAnimatorY;
    private boolean isHorizontalScrollEnabled;
    private boolean isVerticalScrollEnabled;

    public SmoothOverScrollHelper(View view) {
        this.targetView = view;
        determineScrollOrientation();
        setupOverscroll();
    }

    private void determineScrollOrientation() {
        if (targetView instanceof HorizontalScrollView) {
            isHorizontalScrollEnabled = true;
            isVerticalScrollEnabled = false;
        } else if (targetView instanceof ScrollView || targetView instanceof NestedScrollView) {
            isHorizontalScrollEnabled = false;
            isVerticalScrollEnabled = true;
        } else if (targetView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) targetView;
            isHorizontalScrollEnabled = recyclerView.canScrollHorizontally(1) || 
                                      recyclerView.canScrollHorizontally(-1);
            isVerticalScrollEnabled = recyclerView.canScrollVertically(1) || 
                                    recyclerView.canScrollVertically(-1);
        } else if (targetView instanceof GridView) {
            GridView gridView = (GridView) targetView;
            isHorizontalScrollEnabled = gridView.getNumColumns() > 1;
            isVerticalScrollEnabled = true;
        } else if (targetView instanceof ViewGroup) {
            isHorizontalScrollEnabled = true;
            isVerticalScrollEnabled = true;
        } else {
            isHorizontalScrollEnabled = false;
            isVerticalScrollEnabled = true;
        }
    }

    private void setupOverscroll() {
        targetView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        
        targetView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handleTouchDown(event);
                    break;
                    
                case MotionEvent.ACTION_MOVE:
                    return handleTouchMove(event);
                    
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handleTouchUp();
                    break;
            }
            return false;
        });
    }

    private void handleTouchDown(MotionEvent event) {
        cancelBounceBackAnimations();
        lastTouchX = event.getRawX();
        lastTouchY = event.getRawY();
        isOverscrollingHorizontal = false;
        isOverscrollingVertical = false;
    }

    private boolean handleTouchMove(MotionEvent event) {
        float currentX = event.getRawX();
        float currentY = event.getRawY();
        float deltaX = currentX - lastTouchX;
        float deltaY = currentY - lastTouchY;
        lastTouchX = currentX;
        lastTouchY = currentY;

        boolean handled = false;

        if (isHorizontalScrollEnabled && Math.abs(deltaX) > Math.abs(deltaY)) {
            if (canScrollHorizontally()) {
                if (isOverscrollingHorizontal) {
                    applyHorizontalOverscroll(deltaX);
                    handled = true;
                } else if ((deltaX > 0 && isAtLeft()) || (deltaX < 0 && isAtRight())) {
                    isOverscrollingHorizontal = true;
                    applyHorizontalOverscroll(deltaX);
                    handled = true;
                }
            }
        }

        if (isVerticalScrollEnabled && !handled && Math.abs(deltaY) > Math.abs(deltaX)) {
            if (canScrollVertically()) {
                if (isOverscrollingVertical) {
                    applyVerticalOverscroll(deltaY);
                    handled = true;
                } else if ((deltaY > 0 && isAtTop()) || (deltaY < 0 && isAtBottom())) {
                    isOverscrollingVertical = true;
                    applyVerticalOverscroll(deltaY);
                    handled = true;
                }
            }
        }

        return handled;
    }

    private void handleTouchUp() {
        if (isOverscrollingHorizontal) {
            startHorizontalBounceBackAnimation();
        }
        if (isOverscrollingVertical) {
            startVerticalBounceBackAnimation();
        }
    }

    private void applyHorizontalOverscroll(float deltaX) {
        float resistedDelta = deltaX * RESISTANCE_FACTOR;
        overscrollDistanceX += resistedDelta;
        overscrollDistanceX = Math.max(-MAX_OVERSCROLL_DISTANCE, 
            Math.min(MAX_OVERSCROLL_DISTANCE, overscrollDistanceX));
        applyTranslation(overscrollDistanceX, overscrollDistanceY);
    }

    private void applyVerticalOverscroll(float deltaY) {
        float resistedDelta = deltaY * RESISTANCE_FACTOR;
        overscrollDistanceY += resistedDelta;
        overscrollDistanceY = Math.max(-MAX_OVERSCROLL_DISTANCE, 
            Math.min(MAX_OVERSCROLL_DISTANCE, overscrollDistanceY));
        applyTranslation(overscrollDistanceX, overscrollDistanceY);
    }

    private void startHorizontalBounceBackAnimation() {
        if (bounceBackAnimatorX != null && bounceBackAnimatorX.isRunning()) {
            bounceBackAnimatorX.cancel();
        }

        bounceBackAnimatorX = ValueAnimator.ofFloat(overscrollDistanceX, 0);
        bounceBackAnimatorX.setDuration(BOUNCE_BACK_DURATION);
        bounceBackAnimatorX.setInterpolator(new DecelerateInterpolator(1.5f));
        
        bounceBackAnimatorX.addUpdateListener(animation -> {
            overscrollDistanceX = (float) animation.getAnimatedValue();
            applyTranslation(overscrollDistanceX, overscrollDistanceY);
        });
        
        bounceBackAnimatorX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isOverscrollingHorizontal = false;
                overscrollDistanceX = 0;
            }
        });
        
        bounceBackAnimatorX.start();
    }

    private void startVerticalBounceBackAnimation() {
        if (bounceBackAnimatorY != null && bounceBackAnimatorY.isRunning()) {
            bounceBackAnimatorY.cancel();
        }

        bounceBackAnimatorY = ValueAnimator.ofFloat(overscrollDistanceY, 0);
        bounceBackAnimatorY.setDuration(BOUNCE_BACK_DURATION);
        bounceBackAnimatorY.setInterpolator(new DecelerateInterpolator(1.5f));
        
        bounceBackAnimatorY.addUpdateListener(animation -> {
            overscrollDistanceY = (float) animation.getAnimatedValue();
            applyTranslation(overscrollDistanceX, overscrollDistanceY);
        });
        
        bounceBackAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isOverscrollingVertical = false;
                overscrollDistanceY = 0;
            }
        });
        
        bounceBackAnimatorY.start();
    }

    private void cancelBounceBackAnimations() {
        if (bounceBackAnimatorX != null && bounceBackAnimatorX.isRunning()) {
            bounceBackAnimatorX.cancel();
        }
        if (bounceBackAnimatorY != null && bounceBackAnimatorY.isRunning()) {
            bounceBackAnimatorY.cancel();
        }
    }

    private void applyTranslation(float translationX, float translationY) {
        targetView.setTranslationX(translationX);
        targetView.setTranslationY(translationY);
    }

    private boolean canScrollHorizontally() {
        if (targetView instanceof HorizontalScrollView) {
            return ((HorizontalScrollView) targetView).getChildCount() > 0;
        } else if (targetView instanceof RecyclerView) {
            return ((RecyclerView) targetView).canScrollHorizontally(1) || 
                   ((RecyclerView) targetView).canScrollHorizontally(-1);
        }
        return false;
    }

    private boolean canScrollVertically() {
        if (targetView instanceof ScrollView || targetView instanceof NestedScrollView) {
            return targetView instanceof ViewGroup && ((ViewGroup) targetView).getChildCount() > 0;
        } else if (targetView instanceof RecyclerView) {
            return ((RecyclerView) targetView).canScrollVertically(1) || 
                   ((RecyclerView) targetView).canScrollVertically(-1);
        } else if (targetView instanceof GridView) {
            return ((GridView) targetView).getChildCount() > 0;
        }
        return false;
    }

    private boolean isAtLeft() {
        if (targetView instanceof HorizontalScrollView) {
            return ((HorizontalScrollView) targetView).getScrollX() == 0;
        } else if (targetView instanceof RecyclerView) {
            return !((RecyclerView) targetView).canScrollHorizontally(-1);
        }
        return false;
    }

    private boolean isAtRight() {
        if (targetView instanceof HorizontalScrollView) {
            HorizontalScrollView hsv = (HorizontalScrollView) targetView;
            View child = hsv.getChildAt(0);
            return child != null && hsv.getScrollX() >= 
                   (child.getWidth() - hsv.getWidth());
        } else if (targetView instanceof RecyclerView) {
            return !((RecyclerView) targetView).canScrollHorizontally(1);
        }
        return false;
    }

    private boolean isAtTop() {
        if (targetView instanceof ScrollView) {
            return ((ScrollView) targetView).getScrollY() == 0;
        } else if (targetView instanceof NestedScrollView) {
            return ((NestedScrollView) targetView).getScrollY() == 0;
        } else if (targetView instanceof RecyclerView) {
            return !((RecyclerView) targetView).canScrollVertically(-1);
        } else if (targetView instanceof GridView) {
            GridView gridView = (GridView) targetView;
            if (gridView.getChildCount() > 0) {
                View firstChild = gridView.getChildAt(0);
                return gridView.getFirstVisiblePosition() == 0 && firstChild.getTop() >= 0;
            }
            return true;
        }
        return false;
    }

    private boolean isAtBottom() {
        if (targetView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) targetView;
            View child = scrollView.getChildAt(0);
            if (child != null) {
                return scrollView.getScrollY() >= (child.getHeight() - scrollView.getHeight());
            }
            return true;
        } else if (targetView instanceof NestedScrollView) {
            NestedScrollView scrollView = (NestedScrollView) targetView;
            View child = scrollView.getChildAt(0);
            if (child != null) {
                return scrollView.getScrollY() >= (child.getHeight() - scrollView.getHeight());
            }
            return true;
        } else if (targetView instanceof RecyclerView) {
            return !((RecyclerView) targetView).canScrollVertically(1);
        } else if (targetView instanceof GridView) {
            GridView gridView = (GridView) targetView;
            if (gridView.getAdapter() != null && gridView.getChildCount() > 0) {
                int lastPosition = gridView.getLastVisiblePosition();
                View lastChild = gridView.getChildAt(gridView.getChildCount() - 1);
                return lastPosition == gridView.getAdapter().getCount() - 1 && 
                       lastChild.getBottom() <= gridView.getHeight();
            }
            return true;
        }
        return false;
    }
}