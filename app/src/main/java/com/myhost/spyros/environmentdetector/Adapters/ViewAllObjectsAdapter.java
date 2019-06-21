package com.myhost.spyros.environmentdetector.Adapters;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myhost.spyros.environmentdetector.Models.ViewAllObjectsModel;
import com.myhost.spyros.environmentdetector.Models.ViewAllUsersModel;
import com.myhost.spyros.environmentdetector.R;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewAllObjectsAdapter extends RecyclerView.Adapter<ViewAllObjectsAdapter.ViewHolder>{

    //declare views and variables for constructor
    private List<ViewAllObjectsModel> objectsList;
    private Activity activity;
    ViewAllObjectsAdapter viewAllObjectsAdapter;
    RecyclerView recyclerView;

    //variables to get info about location from latitude and longitude of object
    Geocoder geocoder;
    List<Address> addresses;

    public ViewAllObjectsAdapter(List<ViewAllObjectsModel> objectsList, Activity activity, RecyclerView recyclerView) {
        this.objectsList = objectsList;
        this.activity = activity;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewAllObjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate users_layout and pass it to view holder
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.objects_layout,viewGroup,false);
        ViewAllObjectsAdapter.ViewHolder viewHolder = new ViewAllObjectsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAllObjectsAdapter.ViewHolder viewHolder, int i) {
        viewHolder.objectName.setText(objectsList.get(i).getDetectedObjectName());
        geocoder = new Geocoder(activity, Locale.getDefault());
        if(objectsList.get(i).getDetectedObjectLatitude() != 0 && objectsList.get(i).getDetectedObjectLongitude() != 0){
            try {
                addresses = geocoder.getFromLocation(objectsList.get(i).getDetectedObjectLatitude(),objectsList.get(i).getDetectedObjectLongitude(),1);
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                viewHolder.objectCity.setText(city);
                viewHolder.objectCountry.setText(country);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            viewHolder.objectCity.setText("---");
            viewHolder.objectCountry.setText("---");
        }
        viewHolder.objectDate.setText(String.valueOf(DateFormat.getDateTimeInstance().format(new Date(objectsList.get(i).getTimestamp()))));

    }

    @Override
    public int getItemCount() {
        return (null != objectsList ? objectsList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //declare views of objects layout of recycler view
        TextView objectName, objectCity, objectCountry, objectDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            objectName = (TextView) itemView.findViewById(R.id.nameTxtViewAllObjects);
            objectCity = (TextView) itemView.findViewById(R.id.cityTxtViewAllObjects);
            objectCountry = (TextView) itemView.findViewById(R.id.countryTxtViewAllObjects);
            objectDate = (TextView) itemView.findViewById(R.id.dateTxtViewAllObjects);
        }
    }
}
