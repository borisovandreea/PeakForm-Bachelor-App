package com.example.peakform.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.peakform.data.entity.Entry;

import java.util.List;

@Dao
public interface EntryDao {
    @Insert
    long insert(Entry entry);

    @Update
    void update(Entry entry);

    @Delete
    void delete(Entry entry);


    @Query("SELECT * FROM entries ORDER BY timestamp DESC")
    LiveData<List<Entry>> getAllEntries();


    @Query("SELECT * FROM entries WHERE timestamp >= :start AND timestamp <= :end ORDER BY timestamp DESC")
    LiveData<List<Entry>> getEntriesForToday(long start, long end);

    @Query("SELECT * FROM entries ORDER BY timestamp ASC")
    List<Entry> getAllEntriesSync();

    @Query("SELECT * FROM entries WHERE timestamp >= :start AND timestamp <= :end")
    List<Entry> getEntriesForTodaySync(long start, long end);

    @Query("SELECT * FROM entries WHERE timestamp >= :start AND timestamp <= :end")
    List<Entry> getEntriesByDateSync(long start, long end);


    @Query("SELECT SUM(actualMinutes) FROM entries WHERE domainName = :domain AND timestamp >= :start AND timestamp <= :end")
    int getSumMinutesForDomain(String domain, long start, long end);

    @Query("DELETE FROM entries")
    void deleteAllEntries();
}