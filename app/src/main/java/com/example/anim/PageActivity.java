package com.example.anim;

import android.os.Bundle;
import android.view.View;
import com.kilobyte.ActivityTransitionManager;
import com.kilobyte.AnimationManager;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.activity.OnBackPressedCallback;

/**
 * Activity that handles the page view with swipeable card functionality
 * and custom transition animations
 */
public class PageActivity extends AppCompatActivity {
    
    // UI Components
    private CardView swipeableCard;
    private CoordinatorLayout coordinator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        
        // Enable hardware acceleration for better performance
        setupHardwareAcceleration();
        
        // Initialize views and setup animations
        initializeViews();
        setupAnimations();
        setupBackPressedCallback();
    }

    /**
     * Enable hardware acceleration for the activity window
     */
    private void setupHardwareAcceleration() {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
    }

    /**
     * Initialize view references from layout
     */
    private void initializeViews() {
        swipeableCard = findViewById(R.id.swipeableCard);
        coordinator = findViewById(R.id.coordinator);
    }

    /**
     * Setup all animations including swipe gestures and transitions
     */
    private void setupAnimations() {
        // Optimize animations for better performance
        AnimationManager.AnimationPerformanceOptimizer.applyAnimationOptimizations(coordinator);
        
        // Setup swipe gesture handling for the card
        AnimationManager.TouchGestureHandler.initializeSwipeableView((View) swipeableCard);
        
        // Setup entry transition animation
        ActivityTransitionManager.setupEnterAnimation(this);
    }

    /**
     * Handle back button press with custom transition animation
     */
    private void setupBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new ActivityTransitionManager.TransitionBuilder(PageActivity.this)
                    .withCompletionCallback(() -> finish())
                    .start();
            }
        };
        
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}