package com.example.peakform.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.example.peakform.data.entity.Entry;
import com.example.peakform.data.repository.EntryRepository;

public class EntryViewModel extends AndroidViewModel {
    private EntryRepository repository;

    public EntryViewModel(Application application) {
        super(application);
        repository = new EntryRepository(application);
    }

    public void addEntry(Entry entry) {
        repository.insert(entry);
    }
}
