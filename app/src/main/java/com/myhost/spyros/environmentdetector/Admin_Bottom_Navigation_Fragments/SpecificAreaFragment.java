package com.myhost.spyros.environmentdetector.Admin_Bottom_Navigation_Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;


import com.myhost.spyros.environmentdetector.AdminMapsActivity;
import com.myhost.spyros.environmentdetector.R;

import static com.myhost.spyros.environmentdetector.AdminActivity.usersCurrentLocation;

public class SpecificAreaFragment extends Fragment implements View.OnClickListener{

    public static final int REQUEST_MAPS = 1;

    Button submit_map_selection_point;
    ImageView openMap;
    NumberPicker np;
    int number_of_values = 20; //number of options in number picker
    String[] displayedValues = new String[number_of_values];
    private SpecificAreaFragment.SpecificAreaFragmentListener specificAreaFragmentListener;

    //declare variables for latitude and longitude admin selected from map activity and from number picker
    double selected_latitude_from_map;
    double selected_longitude_from_map;



    //in order to communicate with the underlying activity we have to create an interface to do it
    public interface SpecificAreaFragmentListener{
        void getInfoForMap(double latitude, double longitude, int radius);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_specific_area_admin, container, false);
                    //init views of select point from map layout
            submit_map_selection_point = v.findViewById(R.id.fragM_admin_select_from_map_submit_button);
            submit_map_selection_point.setOnClickListener(this);

            openMap = v.findViewById(R.id.fragM_select_from_map_icon);
            openMap.setOnClickListener(this);

            //init number picker and its values
            for(int i = 0; i < number_of_values; i++){
                displayedValues[i] = String.valueOf(500*(i+1)); //increase value of number picker by 500
            }

            np = v.findViewById(R.id.fragM_number_picker_admin);
            np.setMinValue(0);
            np.setMaxValue(displayedValues.length-1);
            np.setDisplayedValues(displayedValues);
            np.setValue(1000);
            np.setWrapSelectorWheel(true);
            /* how to get the current value
            *chosenValue = displayedValues[numPicker.getValue()];
             *
            * */

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //check if the activity that we want to send data implements SearchFragmentActivityListener
        if(context instanceof SpecificAreaFragment.SpecificAreaFragmentListener)
            specificAreaFragmentListener = (SpecificAreaFragment.SpecificAreaFragmentListener) context;
        else
            throw new RuntimeException(context.toString()
                    +" must implement SpecificAreaFragmentListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        specificAreaFragmentListener = null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fragM_select_from_map_icon){
            Intent openMapIntent = new Intent(getContext(), AdminMapsActivity.class);
            if(usersCurrentLocation == null){
                openMapIntent.putExtra("admin_latitude", 0);
                openMapIntent.putExtra("admin_longitude", 0);
            }
            else{
                openMapIntent.putExtra("admin_latitude", usersCurrentLocation.getLatitude());
                openMapIntent.putExtra("admin_longitude", usersCurrentLocation.getLongitude());
            }
            startActivityForResult(openMapIntent,REQUEST_MAPS);
        }
        else if(view.getId() == R.id.fragM_admin_select_from_map_submit_button){
            specificAreaFragmentListener.getInfoForMap(selected_latitude_from_map, selected_longitude_from_map,
                    Integer.valueOf(displayedValues[np.getValue()]));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_MAPS){
            if(resultCode == Activity.RESULT_OK){
                selected_latitude_from_map = data.getDoubleExtra("selected_latitude",0);
                selected_longitude_from_map = data.getDoubleExtra("selected_longitude",0);
            }
        }
    }
}
