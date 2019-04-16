package com.myhost.spyros.environmentdetector;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

//This is DAO(Data Access Object and i create it after Note
//Here we will define the various SQL queries in formal functions
@Dao
public interface ObjectDao {

    //we will first add method for reading from the database
    //then we need to create the wrapper function in our ViewModel
    @Insert
    void insertInitialData(List<Object> objects);

//    @Query("SELECT * FROM Objects WHERE name=:objectName")
//    LiveData<List<Object>> getObject(String objectName);

    @Query("SELECT * FROM Objects WHERE name=:objectName")
    LiveData<Object> getObject(String objectName);

    @Query("SELECT * FROM Objects")
    LiveData<List<Object>> getObjects();

    @Query("SELECT COUNT(*) FROM Objects")
    int getObj();


}
