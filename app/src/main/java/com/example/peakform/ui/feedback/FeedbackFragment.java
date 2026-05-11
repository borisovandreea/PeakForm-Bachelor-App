package com.example.peakform.ui.feedback;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.peakform.R;
import com.example.peakform.logic.settings.SettingsManager;
import com.example.peakform.viewmodel.FeedbackViewModel;
import com.example.peakform.data.repository.EntryRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedbackFragment extends Fragment {

    private FeedbackViewModel feedbackViewModel;
    private SettingsManager settingsManager;
    private EntryRepository entryRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private TextView tvMainFeedback, tvInsightTitle, tvInsightTimestamp;
    private ImageView ivIcon;
    private LinearLayout goalsContainer;
    private RecyclerView rvPreviousInsights;
    private Button btnAdjustGoals;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feedback, container, false);

        tvMainFeedback = root.findViewById(R.id.tvMainFeedback);
        tvInsightTitle = root.findViewById(R.id.tvInsightTitle);
        tvInsightTimestamp = root.findViewById(R.id.tvInsightTimestamp);
        ivIcon = root.findViewById(R.id.ivIcon);
        goalsContainer = root.findViewById(R.id.goalsContainer);
        rvPreviousInsights = root.findViewById(R.id.rvPreviousInsights);
        btnAdjustGoals = root.findViewById(R.id.btnAdjustGoals);

        settingsManager = new SettingsManager(requireContext());
        entryRepository = new EntryRepository(requireActivity().getApplication());
        feedbackViewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupPreviousInsights();
        setupWeeklyGoals();
        observeInsights();

        btnAdjustGoals.setOnClickListener(v -> {
            BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_navigation);

            if (navBar != null) {
                navBar.setSelectedItemId(R.id.nav_settings);
            } else {
                Toast.makeText(getContext(), "Navigation bar not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeInsights() {
        feedbackViewModel.getLatestInsight().observe(getViewLifecycleOwner(), insight -> {
            if (insight != null) {
                tvMainFeedback.setText(insight.message);
                tvInsightTimestamp.setText("Just now");
                setInsightHeader(insight.bctType);
            } else {
                tvInsightTitle.setText("Morning Motivation");
                tvInsightTimestamp.setText("Today");
                tvMainFeedback.setText("A fresh day begins! Your goals are set in minutes—log your first activity to unlock today's insight.");
                ivIcon.setImageResource(android.R.drawable.ic_menu_compass);
            }
        });
    }

    private void setupWeeklyGoals() {
        List<String> domains = settingsManager.getAllDomains();
        goalsContainer.removeAllViews();

        for (String domain : domains) {
            executorService.execute(() -> {
                int target = settingsManager.getWeeklyMinutesTarget(domain);
                int actual = entryRepository.getWeeklyTotalForDomain(domain);

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        addGoalToUI(domain, actual, target);
                    });
                }
            });
        }
    }

    private void addGoalToUI(String name, int actual, int target) {
        View goalView = getLayoutInflater().inflate(R.layout.item_goal_card, goalsContainer, false);

        TextView tvName = goalView.findViewById(R.id.tvGoalName);
        ProgressBar pb = goalView.findViewById(R.id.goalProgressBar);
        TextView tvStatus = goalView.findViewById(R.id.tvGoalStatus);
        ImageView ivStatus = goalView.findViewById(R.id.ivGoalStatus);

        tvName.setText(name + ": " + actual + " / " + target + " min");

        int progress = (target > 0) ? (actual * 100 / target) : 0;
        pb.setProgress(Math.min(progress, 100));

        if (progress >= 100) {
            tvStatus.setText("Completed");
            tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            ivStatus.setColorFilter(Color.parseColor("#4CAF50"));
            ivStatus.setImageResource(R.drawable.ic_check_circle);
        } else {
            tvStatus.setText("In Progress");
            tvStatus.setTextColor(Color.parseColor("#888888"));
        }

        goalsContainer.addView(goalView);
    }

    private void setupPreviousInsights() {
        rvPreviousInsights.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setInsightHeader(String type) {
        if (type == null) return;
        switch (type.toLowerCase()) {
            case "suggestion":
                tvInsightTitle.setText("💡 Suggestion");
                break;
            case "achievement":
                tvInsightTitle.setText("🏆 Achievement");
                break;
            default:
                tvInsightTitle.setText("✨ Latest Insight");
                break;
        }
    }
}