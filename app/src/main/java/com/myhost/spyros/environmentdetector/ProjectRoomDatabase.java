package com.myhost.spyros.environmentdetector;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.fstyle.library.helper.AssetSQLiteOpenHelperFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

import static org.apache.commons.collections.functors.NullPredicate.INSTANCE;

@Database(entities = Object.class,version = 1,exportSchema = false)
public abstract class ProjectRoomDatabase extends RoomDatabase {
    //i need to create a database access object that i will use
    public abstract ObjectDao objectDao();

    //now i need to create an instance of the database
    //instance object must be only one and singleton
    private static volatile ProjectRoomDatabase projectRoomDatabaseInstance;


    static ProjectRoomDatabase getDatabase(final Context context, final List<Object> data){
        if(projectRoomDatabaseInstance == null){
            synchronized (ProjectRoomDatabase.class){
                if(projectRoomDatabaseInstance == null){
//                    projectRoomDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
//                            ProjectRoomDatabase.class,"environment_detector_db.db")
//                            .openHelperFactory(new AssetSQLiteOpenHelperFactory())
//                            .allowMainThreadQueries()
//                            .build();
                    projectRoomDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            ProjectRoomDatabase.class,"environment_detector_db")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull final SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            // insert data using DAO
                                            projectRoomDatabaseInstance.objectDao().insertInitialData(data);
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return projectRoomDatabaseInstance;
    }

}
