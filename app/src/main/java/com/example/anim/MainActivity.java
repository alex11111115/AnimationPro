package com.example.anim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import com.kilobyte.ActivityTransitionManager;
import com.kilobyte.AnimationManager.AnimationEffectsCreator;
import com.kilobyte.AnimationManager.SmoothAnimationCreator;
import com.kilobyte.AnimationManager;
import com.kilobyte.SmoothOverScrollHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity class that handles the primary user interface and animations
 */
public class MainActivity extends AppCompatActivity {

    // UI Components
    private CardView swipeableCard;
    private LinearLayout contentContainer;
    private LinearLayout listContainer;
    private List<View> listItems;
    private Button butoonTap;
    private ImageView imageViewSample;
    private ProgressBar loadingProgressBar;
    private NestedScrollView scro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable hardware acceleration for better performance
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );

        // Initialize and setup the UI
        initializeViews();
        prepareListItems();
        setupAnimations();
        setupButton();
        setupBackPressedCallback();
        SmoothOverScrollHelper smoothOverScrollHelper = new SmoothOverScrollHelper(scro);
    }

    /**
     * Initialize all view references from layout
     */
    private void initializeViews() {
        contentContainer = findViewById(R.id.contentContainer);
        listContainer = findViewById(R.id.listContainer);
        swipeableCard = findViewById(R.id.swipeableCard);
        butoonTap = findViewById(R.id.butoonTap);
        imageViewSample = findViewById(R.id.imageViewSample);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        scro = findViewById(R.id.scro);
    }

    /**
     * Setup button click listener and transition animation
     */
    private void setupButton() {
        butoonTap.setOnClickListener(v -> {
            // Create tap animation effect
            AnimationEffectsCreator.startTapResponseAnimation(v);
            SmoothAnimationCreator.startGradualScaleAnimation(v);

            // Start activity transition
            Intent intent = new Intent(MainActivity.this, PageActivity.class);
            new ActivityTransitionManager.TransitionBuilder(this)
            .setIntent(intent)
            .setDuration(200)
            .setSourceView(v)
            .start();
        });
    }

    /**
     * Setup and optimize animations for the page
     */
    private void setupAnimations() {
        // Apply performance optimizations
        AnimationManager.AnimationPerformanceOptimizer.applyAnimationOptimizations(contentContainer);
        
        // Start initial page animations
        startPageAnimations();
    }

    /**
     * Create and populate list items dynamically
     */
    private void prepareListItems() {
        listItems = new ArrayList<>();

        // Create 5 sample list items
        for (int i = 0; i < 5; i++) {
            View item = ListItemCreator.createListItem(
                this,
                "Item " + (i + 1),
                "This is item description " + (i + 1)
            );
            listItems.add(item);
            listContainer.addView(item);
        }
    }

    /**
     * Start all page animations
     */
    private void startPageAnimations() {
        // Animate the main container
        AnimationManager.SmoothAnimationCreator.startContainerContentAnimation(contentContainer);

        // Animate list items with stagger effect
        AnimationManager.SmoothAnimationCreator.animateListViewItems(listContainer, listItems);
    }

    /**
     * Show loading indicator
     */
    private void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hide loading indicator
     */
    private void hideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    /**
     * Setup back button handling with exit animation
     */
    private void setupBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Animate content before finishing activity
                AnimationManager.ExitAnimationController.startExitAnimation(contentContainer, () -> finish());
            }
        };
        
        // Add callback to back press dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}