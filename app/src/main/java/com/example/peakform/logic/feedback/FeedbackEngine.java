package com.example.peakform.logic.feedback;

import com.example.peakform.data.entity.FeedbackEvent;

public class FeedbackEngine {

    public static FeedbackEvent generateReflectivePrompt(float currentScore, String domainName) {
        FeedbackEvent event = new FeedbackEvent();
        event.timestamp = System.currentTimeMillis();

        if (currentScore >= 80) {
            event.message = "You have a great consistency when it comes to " + domainName + ". Way to go!";
            event.bctType = "BCT 2.2";
        } else if (currentScore >= 40) {
            event.message = "You are on the right track with " + domainName + ". Do you think you can improve?";
            event.bctType = "BCT 1.2";
        } else {
            event.message = "Today went different for " + domainName + ". You choose the priorities, what's next?";
            event.bctType = "BCT 1.1";
        }

        return event;
    }
}
