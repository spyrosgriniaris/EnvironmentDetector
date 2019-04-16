package com.myhost.spyros.environmentdetector;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

//This is an Entity Component class of Objects that are detected through camera
@Entity(tableName = "Objects")//if i don't use a table name, default is the name of the class
public class Object {
    @NonNull
    public String getName() {return name;}

    @NonNull
    public String getObjectInfo() {return this.mObjectInfo;}

    //here we will declare the columns that our object will have
    @PrimaryKey
    @NonNull
    private String name;

    @NonNull
    @ColumnInfo(name = "objectInfo")
    private String mObjectInfo;

    //constructor
    public Object(String name, String objectInfo){
        this.name = name;
        this.mObjectInfo = objectInfo;
    }
}
