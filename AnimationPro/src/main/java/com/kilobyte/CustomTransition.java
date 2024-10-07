package com.kilobyte;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator; // إضافة المروِّج البديل

public class CustomTransition {
    private View sourceView;
    private View targetView;
    private ViewGroup rootView;
    private long duration = 300; // مدة الانتقال بالميللي ثانية

    public CustomTransition(View sourceView, View targetView, ViewGroup rootView) {
        this.sourceView = sourceView;
        this.targetView = targetView;
        this.rootView = rootView;
    }

    public void startTransition() {
        // حفظ الموقع الأصلي للعناصر
        final float startX = sourceView.getX();
        final float startY = sourceView.getY();
        final float startWidth = sourceView.getWidth();
        final float startHeight = sourceView.getHeight();

        // حساب الموقع النهائي
        int[] targetLocation = new int[2];
        targetView.getLocationInWindow(targetLocation);
        final float endX = targetLocation[0];
        final float endY = targetLocation[1];
        final float endWidth = targetView.getWidth();
        final float endHeight = targetView.getHeight();

        // إنشاء نسخة مؤقتة من العنصر للانتقال
        final View transitionView = createTransitionView(sourceView);
        rootView.addView(transitionView);

        // إنشاء الأنيميشن
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator()); // استخدام المروِّج الجديد

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                
                // حساب القيم الحالية باستخدام التداخل
                float currentX = lerp(startX, endX, fraction);
                float currentY = lerp(startY, endY, fraction);
                float currentWidth = lerp(startWidth, endWidth, fraction);
                float currentHeight = lerp(startHeight, endHeight, fraction);

                // تحديث موقع وحجم العنصر المؤقت
                transitionView.setX(currentX);
                transitionView.setY(currentY);
                transitionView.getLayoutParams().width = (int) currentWidth;
                transitionView.getLayoutParams().height = (int) currentHeight;
                transitionView.requestLayout();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // إزالة العنصر المؤقت بعد انتهاء الانتقال
                rootView.removeView(transitionView);
                targetView.setVisibility(View.VISIBLE);
            }
        });

        // إخفاء العنصر الهدف أثناء الانتقال
        targetView.setVisibility(View.INVISIBLE);
        animator.start();
    }

    // دالة مساعدة لإنشاء نسخة من العنصر للانتقال
    private View createTransitionView(View source) {
        // إنشاء نسخة من العنصر المصدر
        View transitionView = new View(source.getContext());
        transitionView.setLayoutParams(new ViewGroup.LayoutParams(
                source.getWidth(),
                source.getHeight()
        ));
        transitionView.setX(source.getX());
        transitionView.setY(source.getY());
        transitionView.setBackground(source.getBackground());
        return transitionView;
    }

    // دالة مساعدة للتداخل الخطي
    private float lerp(float start, float end, float fraction) {
        return start + (end - start) * fraction;
    }

    // تعيين مدة الانتقال
    public void setDuration(long duration) {
        this.duration = duration;
    }
}