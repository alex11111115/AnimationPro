package com.example.anim;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

public class ListItemCreator {
    
    public static View createListItem(Context context, String title, String subtitle) {
        CardView card = new CardView(context);
        card.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        card.setCardElevation(4);
        card.setRadius(8);
        card.setUseCompatPadding(true);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(16, 16, 16, 16);

        TextView titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextSize(18);

        TextView subtitleView = new TextView(context);
        subtitleView.setText(subtitle);
        subtitleView.setTextSize(14);
        subtitleView.setAlpha(0.7f);

        content.addView(titleView);
        content.addView(subtitleView);
        card.addView(content);

        return card;
    }
}