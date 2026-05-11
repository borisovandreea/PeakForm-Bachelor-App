package com.example.peakform.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.peakform.R;
import com.example.peakform.data.entity.Entry;
import com.example.peakform.data.repository.EntryRepository;
import com.example.peakform.logic.scoring.ScoreCalculator;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private TextView tvScore;
    private TextView tvInsight;
    private HorizontalBarChart barChart;
    private EntryRepository entryRepository;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvScore = root.findViewById(R.id.tvOverallScore);
        tvInsight = root.findViewById(R.id.tvInsightText);
        barChart = root.findViewById(R.id.performanceChart);

        if (getActivity() != null) {
            entryRepository = new EntryRepository(getActivity().getApplication());
        }

        setupScoreObserver();

        return root;
    }

    private void setupScoreObserver() {
        entryRepository.getEntriesForToday().observe(getViewLifecycleOwner(), entries -> {
            if (entries != null && !entries.isEmpty()) {
                int dailyScore = ScoreCalculator.calculateDailyPerformance(entries, getContext());
                tvScore.setText(String.valueOf(dailyScore));

                updateInsightMessage(tvInsight, dailyScore);
                setupHorizontalChart(entries);
            } else {
                tvScore.setText("0");
                tvInsight.setText(getString(R.string.insight_no_data));
                if (barChart != null) barChart.clear();
            }
        });
    }

    private void setupHorizontalChart(List<Entry> entries) {
        List<BarEntry> chartEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int colorHigh = getResources().getColor(R.color.score_high, null);
        int colorMedium = getResources().getColor(R.color.score_medium, null);
        int colorLow = getResources().getColor(R.color.score_low, null);

        for (int i = 0; i < entries.size(); i++) {
            Entry activity = entries.get(i);

            double efficiency = (activity.goalMinutes > 0) ? (double) activity.actualMinutes / activity.goalMinutes : 0;
            efficiency = Math.min(efficiency, 1.1);
            double activityScore = (efficiency * 50) + (activity.focusScore * 3) + (activity.impactScore * 2);

            chartEntries.add(new BarEntry(i, (float) activityScore));

            if (activityScore >= 80) colors.add(colorHigh);
            else if (activityScore >= 50) colors.add(colorMedium);
            else colors.add(colorLow);
        }

        BarDataSet dataSet = new BarDataSet(chartEntries, getString(R.string.chart_label_daily_activities));
        dataSet.setColors(colors);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        barChart.setData(data);


        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setExtraLeftOffset(15f);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(getResources().getColor(R.color.chart_label_color, null));

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < entries.size()) ? entries.get(index).activityName : "";
            }
        });


        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setAxisMaximum(110f);
        barChart.getAxisLeft().setGridColor(getResources().getColor(R.color.chart_grid_color, null));
        barChart.getAxisRight().setEnabled(false);

        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void updateInsightMessage(TextView view, int score) {
        if (score >= 80) {
            view.setText(getString(R.string.insight_excellent));
        } else if (score >= 50) {
            view.setText(getString(R.string.insight_good));
        } else {
            view.setText(getString(R.string.insight_low));
        }
    }
}
