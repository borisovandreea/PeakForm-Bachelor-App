package com.example.peakform.data.dao;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.peakform.data.entity.Domain;

import java.util.List;

@Dao
public interface DomainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Domain domain);

    @Update
    void update(Domain domain);

    @Delete
    void delete(Domain domain);

    //@Query("SELECT * FROM domains")
    //LiveData<List<Domain>> getAllDomains();
}