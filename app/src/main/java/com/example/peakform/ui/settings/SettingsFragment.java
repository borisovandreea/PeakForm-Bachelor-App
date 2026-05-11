package com.example.peakform.ui.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.peakform.R;
import com.example.peakform.data.repository.EntryRepository;
import com.example.peakform.data.entity.Entry;
import com.example.peakform.logic.settings.SettingsManager;
import com.example.peakform.ui.user.LoginActivity;
import com.example.peakform.viewmodel.SettingsViewModel;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    private SettingsManager settingsManager;
    private EntryRepository entryRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private LinearLayout domainsContainer;
    private TextView tvTotalWeight;
    private Button btnSave, btnExport, btnClearActivity, btnDeleteProfile, btnAddDomain, btnReset;

    private final List<View> rowViews = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        settingsManager = new SettingsManager(requireContext());
        entryRepository = new EntryRepository(requireActivity().getApplication());

        domainsContainer = view.findViewById(R.id.domainsContainer);
        tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
        btnSave = view.findViewById(R.id.btnSave);
        btnExport = view.findViewById(R.id.btnExportCSV);
        btnClearActivity = view.findViewById(R.id.btnClearActivity);
        btnDeleteProfile = view.findViewById(R.id.btnDeleteProfile);
        btnAddDomain = view.findViewById(R.id.btnAddDomain);
        btnReset = view.findViewById(R.id.btnReset);

        renderDomainRows();

        btnAddDomain.setOnClickListener(v -> showAddDomainDialog());
        btnSave.setOnClickListener(v -> saveAllSettings());

        btnReset.setOnClickListener(v -> showConfirmDialog(
                "Reset to Defaults?",
                "This will remove custom domains and reset all targets. Your history will not be deleted.",
                () -> {
                    settingsManager.resetToDefaults();
                    renderDomainRows();
                    Toast.makeText(getContext(), "Restored default domains", Toast.LENGTH_SHORT).show();
                }
        ));

        btnExport.setOnClickListener(v -> exportDataToCSV());

        btnClearActivity.setOnClickListener(v -> showConfirmDialog(
                "Clear History?",
                "Delete all logged activities? Goals and weights will stay.",
                () -> settingsViewModel.deleteUserActivity()
        ));

        btnDeleteProfile.setOnClickListener(v -> showConfirmDialog(
                "Delete Everything?",
                "This reset is permanent.",
                () -> {
                    settingsViewModel.deleteFullProfile(requireContext());
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    if (getActivity() != null) getActivity().finish();
                }
        ));
    }

    private void exportDataToCSV() {
        Toast.makeText(getContext(), "Preparing report...", Toast.LENGTH_SHORT).show();

        executorService.execute(() -> {
            try {
                List<Entry> entries = entryRepository.getAllEntriesSync();

                StringBuilder csvData = new StringBuilder();
                // Added Activity to the header
                csvData.append("Date,Activity,Domain,Actual_Minutes,Goal_Minutes,Weekly_Target_Minutes\n");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

                for (Entry entry : entries) {
                    int target = settingsManager.getWeeklyMinutesTarget(entry.domainName);
                    String dateStr = dateFormat.format(new Date(entry.timestamp));

                    // Clean names: remove commas so they don't break CSV columns
                    String cleanActivity = entry.activityName != null ? entry.activityName.replace(",", " ") : "Unnamed";
                    String cleanDomain = entry.domainName != null ? entry.domainName.replace(",", " ") : "General";

                    csvData.append(dateStr).append(",")
                            .append(cleanActivity).append(",")
                            .append(cleanDomain).append(",")
                            .append(entry.actualMinutes).append(",")
                            .append(entry.goalMinutes).append(",")
                            .append(target).append("\n");
                }

                File file = new File(requireContext().getCacheDir(), "PeakForm_Progress.csv");
                FileWriter writer = new FileWriter(file);
                writer.write(csvData.toString());
                writer.close();

                requireActivity().runOnUiThread(() -> {
                    Uri contentUri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.example.peakform.fileprovider",
                            file);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/csv");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "PeakForm Activity Report");
                    intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(Intent.createChooser(intent, "Save or Send Report"));
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Export error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void renderDomainRows() {
        domainsContainer.removeAllViews();
        rowViews.clear();

        List<String> domains = settingsManager.getAllDomains();
        String[] units = {"Daily", "Weekly", "Monthly"};

        for (String domain : domains) {
            View row = getLayoutInflater().inflate(R.layout.item_setting_domain, domainsContainer, false);

            TextView tvName = row.findViewById(R.id.tvDomainName);
            SeekBar sbWeight = row.findViewById(R.id.sbWeight);
            TextView tvWeightVal = row.findViewById(R.id.tvWeightVal);
            EditText etTarget = row.findViewById(R.id.etTarget);
            Spinner spnUnit = row.findViewById(R.id.spnUnit);

            tvName.setText(domain);
            int weight = (int) (settingsManager.getWeightByDomain(domain) * 100);
            sbWeight.setProgress(weight);
            tvWeightVal.setText(weight + "%");
            etTarget.setText(String.valueOf(settingsManager.getRawTarget(domain)));

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, units);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnUnit.setAdapter(adapter);

            String currentUnit = settingsManager.getUnit(domain);
            for (int i = 0; i < units.length; i++) {
                if (units[i].equalsIgnoreCase(currentUnit)) spnUnit.setSelection(i);
            }

            sbWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tvWeightVal.setText(progress + "%");
                    updateTotalWeightDisplay();
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            domainsContainer.addView(row);
            rowViews.add(row);
        }
        updateTotalWeightDisplay();
    }

    private void saveAllSettings() {
        int totalWeight = 0;
        for (View row : rowViews) {
            totalWeight += ((SeekBar) row.findViewById(R.id.sbWeight)).getProgress();
        }

        if (totalWeight != 100) {
            Toast.makeText(getContext(), "Total weight must equal 100%!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (View row : rowViews) {
            String name = ((TextView) row.findViewById(R.id.tvDomainName)).getText().toString();
            int weight = ((SeekBar) row.findViewById(R.id.sbWeight)).getProgress();
            String targetStr = ((EditText) row.findViewById(R.id.etTarget)).getText().toString();
            int target = targetStr.isEmpty() ? 0 : Integer.parseInt(targetStr);
            String unit = ((Spinner) row.findViewById(R.id.spnUnit)).getSelectedItem().toString();

            settingsManager.saveDomainSettings(name, weight, target, unit);
        }

        Toast.makeText(getContext(), "Settings saved!", Toast.LENGTH_SHORT).show();
    }

    private void showAddDomainDialog() {
        EditText input = new EditText(getContext());
        input.setHint("e.g. Reading, Guitar");

        FrameLayout container = new FrameLayout(requireContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        int margin = (int) (20 * getResources().getDisplayMetrics().density);
        params.setMargins(margin, 0, margin, 0);
        input.setLayoutParams(params);
        container.addView(input);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Custom Domain")
                .setView(container)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        settingsManager.addNewDomain(name);
                        renderDomainRows();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTotalWeightDisplay() {
        int total = 0;
        for (View v : rowViews) {
            total += ((SeekBar) v.findViewById(R.id.sbWeight)).getProgress();
        }
        tvTotalWeight.setText("Total Weight: " + total + "%");
        tvTotalWeight.setTextColor(total == 100 ? Color.parseColor("#2ECC71") : Color.RED);
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, id) -> onConfirm.run())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}