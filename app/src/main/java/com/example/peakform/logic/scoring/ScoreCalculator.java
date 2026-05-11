package com.example.peakform.logic.scoring;

import android.content.Context;
import com.example.peakform.data.entity.Entry;
import com.example.peakform.logic.settings.SettingsManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreCalculator {

    public static int calculateDailyPerformance(List<Entry> entries, Context context) {
        if (entries == null || entries.isEmpty()) return 0;

        SettingsManager settings = new SettingsManager(context);

        Map<String, Integer> domainActualMinutes = new HashMap<>();
        Map<String, Double> domainQualitySum = new HashMap<>();
        Map<String, Integer> domainEntryCount = new HashMap<>();

        for (Entry entry : entries) {
            String domain = entry.domainName;

            domainActualMinutes.put(domain, domainActualMinutes.getOrDefault(domain, 0) + entry.actualMinutes);

            double quality = (entry.focusScore * 3.0) + (entry.impactScore * 2.0);
            domainQualitySum.put(domain, domainQualitySum.getOrDefault(domain, 0.0) + quality);
            domainEntryCount.put(domain, domainEntryCount.getOrDefault(domain, 0) + 1);
        }

        double finalWeightedScore = 0;


        List<String> activeDomains = settings.getAllDomains();


        for (String domain : activeDomains) {
            int actualMins = domainActualMinutes.getOrDefault(domain, 0);

            int targetMins = settings.getDailyMinutesTarget(domain);

            float weight = settings.getWeightByDomain(domain);


            if (actualMins > 0 && targetMins > 0) {
                double efficiency = Math.min((double) actualMins / targetMins, 1.0);

                double avgQuality = domainQualitySum.get(domain) / domainEntryCount.get(domain);

                double domainScore = (efficiency * 50) + avgQuality;

                finalWeightedScore += (domainScore * weight);
            }
        }

        return (int) Math.round(Math.min(finalWeightedScore, 100));
    }
}