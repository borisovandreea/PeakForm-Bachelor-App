package com.example.peakform.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.peakform.data.entity.Goal;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Goal goal);

    @Update
    void update(Goal goal);

    //@Query("SELECT * FROM goals")
    //LiveData<List<Goal>> getAllGoals();

    //@Query("SELECT * FROM goals WHERE domain = :domainName LIMIT 1")
    //LiveData<Goal> getGoalForDomain(String domainName);
}
