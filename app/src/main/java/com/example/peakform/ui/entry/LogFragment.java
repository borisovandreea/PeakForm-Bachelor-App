package com.example.peakform.ui.entry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.peakform.MainActivity;
import com.example.peakform.R;
import com.example.peakform.data.entity.Entry;
import com.example.peakform.data.repository.EntryRepository;
import com.example.peakform.logic.settings.SettingsManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LogFragment extends Fragment {

    private EditText etActivityName, etActualMins, etDate;
    private Spinner spinnerCategory;
    private SeekBar seekFocus, seekImpact;
    private Button btnSave;

    private EntryRepository entryRepository;
    private SettingsManager settingsManager;
    private final Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_log, container, false);

        if (getActivity() != null) {
            entryRepository = new EntryRepository(getActivity().getApplication());
            settingsManager = new SettingsManager(requireContext());
        }

        etActivityName = root.findViewById(R.id.etActivityName);
        spinnerCategory = root.findViewById(R.id.spinnerCategory);
        etActualMins = root.findViewById(R.id.etActualMins);
        etDate = root.findViewById(R.id.etDate);
        seekFocus = root.findViewById(R.id.seekFocus);
        seekImpact = root.findViewById(R.id.seekImpact);
        btnSave = root.findViewById(R.id.btnSaveActivity);

        setupDatePicker();

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveEntryToDatabase());
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupSpinner();
    }

    private void setupSpinner() {
        if (getContext() != null && spinnerCategory != null) {
            List<String> categories = settingsManager.getAllDomains();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
        }
    }

    private void setupDatePicker() {
        updateDateLabel();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateDateLabel();
        };

        etDate.setOnClickListener(v -> new DatePickerDialog(requireContext(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etDate.setText(sdf.format(calendar.getTime()));
    }

    private void saveEntryToDatabase() {
        String name = etActivityName.getText().toString().trim();

        if (spinnerCategory.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Please add a domain in settings first", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = spinnerCategory.getSelectedItem().toString();
        String actualStr = etActualMins.getText().toString().trim();

        if (name.isEmpty() || actualStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in the details", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Entry newEntry = new Entry();
            newEntry.activityName = name;
            newEntry.domainName = category;
            newEntry.actualMinutes = Integer.parseInt(actualStr);
            newEntry.goalMinutes = settingsManager.getDailyMinutesTarget(category);
            newEntry.focusScore = seekFocus.getProgress();
            newEntry.impactScore = seekImpact.getProgress();
            newEntry.timestamp = calendar.getTimeInMillis();


            entryRepository.insert(newEntry);

            Toast.makeText(getContext(), "Logged successfully!", Toast.LENGTH_SHORT).show();


            etActivityName.setText("");
            etActualMins.setText("");


            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToDashboard();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers!", Toast.LENGTH_SHORT).show();
        }
    }
}