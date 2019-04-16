package com.myhost.spyros.environmentdetector;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class InitDataReader {
    List<Object> objects = new ArrayList<>();
    Context mContext ;

    public InitDataReader(Context context) {
        //this.objects = objects;
        mContext = context;
    }

    public List<Object> readData() {
        InputStream is = mContext.getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(",");

                Object objectRead = new Object(tokens[0],tokens[1]);
                objects.add(objectRead);
            }
        } catch (IOException e1) {
//            Log.e("MainActivity", "Error" + line, e1);
//            e1.printStackTrace();
        }

        return objects;
    }
}
