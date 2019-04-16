package com.myhost.spyros.environmentdetector;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import static com.myhost.spyros.environmentdetector.DisplayInfoActivity.OBJECT_NAME_TO_BE_DISPLAYED;


//we extend AndroidViewModel and not ViewModel, in order to use getApplicationContext()
public class ObjectViewModel extends AndroidViewModel {

    private ObjectDao objectDao;
    private ProjectRoomDatabase projectRoomDatabase;
    private LiveData<Object> mObject;


    //constructor
    public ObjectViewModel(@NonNull Application application) {
        super(application);
        InitDataReader reader = new InitDataReader(application);
        List<Object> data = reader.readData();

        projectRoomDatabase = ProjectRoomDatabase.getDatabase(application,data);
        objectDao = projectRoomDatabase.objectDao();

        mObject = objectDao.getObject(DisplayInfoActivity.OBJECT_NAME_TO_BE_DISPLAYED);


    }

    //we will create a wrapper function that will return the object we want from out ViewModel
    public LiveData<Object> getObject(String objectName){return mObject;}

}
