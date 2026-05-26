package com.example.peakform.logic.feedback;

import com.example.peakform.R;
import com.example.peakform.data.entity.FeedbackEvent;
import com.example.peakform.data.model.FeedbackInsight;
import java.util.ArrayList;
import java.util.List;

public class InsightProcessor {
    public static List<FeedbackInsight> processHistory(List<FeedbackEvent> events) {
        List<FeedbackInsight> processed = new ArrayList<>();
        if (events == null) return processed;

        for (FeedbackEvent event : events) {
            int color;
            // Map the bctType from your repository to UI colors
            if ("achievement".equalsIgnoreCase(event.bctType)) {
                color = R.color.insight_green_bg; // Success/High Score
            } else if (event.message.contains("balanced")) {
                color = R.color.insight_blue_bg; // Moderate/Steady
            } else {
                color = R.color.insight_purple_bg; // Needs Improvement/Rest
            }
            processed.add(new FeedbackInsight(event.timestamp, event.message, color));
        }
        return processed;
    }
}