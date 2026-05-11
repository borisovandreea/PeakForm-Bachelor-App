package com.example.peakform.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.peakform.data.entity.WeightProfile;
import java.util.List;

@Dao
public interface WeightProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WeightProfile profile);

    @Update
    void update(WeightProfile profile);

    @Query("SELECT * FROM weighting_profiles")
    LiveData<List<WeightProfile>> getAllProfiles();

    @Query("SELECT * FROM weighting_profiles WHERE isActive = 1 LIMIT 1")
    LiveData<WeightProfile> getActiveProfile();

}
