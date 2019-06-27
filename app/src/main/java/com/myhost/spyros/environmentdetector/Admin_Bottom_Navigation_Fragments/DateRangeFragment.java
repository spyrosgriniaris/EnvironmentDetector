package com.myhost.spyros.environmentdetector.Admin_Bottom_Navigation_Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.myhost.spyros.environmentdetector.AdminActivity;
import com.myhost.spyros.environmentdetector.R;
import com.myhost.spyros.environmentdetector.ViewObjectsInDateRangeActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class DateRangeFragment extends Fragment implements View.OnClickListener{

    TextView first_date_display, last_date_display;
    Button submit;
    FloatingActionButton date1, date2;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    String date1_picked, date2_picked;
    private DateRangeFragment.DateRangeFragmentListener dateRangeFragmentListener;


    //in order to communicate with the underlying activity we have to create an interface to do it
    public interface DateRangeFragmentListener{
        void getDates(long date1, long date2);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_date_range_admin, container, false);

        //init fragment views
        first_date_display = v.findViewById(R.id.fragDR_first_date_display_admin);
        last_date_display = v.findViewById(R.id.fragDR_last_date_display_admin);
        submit = v.findViewById(R.id.fragDR_date_range_submit_btn_admin);
        date1 = v.findViewById(R.id.fragDR_pick_first_date_btn);
        date2 = v.findViewById(R.id.fragDR_pick_last_date_btn);
        date2.setEnabled(false);

        //init click listeners for buttons
        submit.setOnClickListener(this);
        date1.setOnClickListener(this);
        date2.setOnClickListener(this);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //check if the activity that we want to send data implements SearchFragmentActivityListener
        if(context instanceof DateRangeFragment.DateRangeFragmentListener)
            dateRangeFragmentListener = (DateRangeFragment.DateRangeFragmentListener) context;
        else
            throw new RuntimeException(context.toString()
                    +" must implement SearchFragmentListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dateRangeFragmentListener = null;
    }

    @Override
    public void onClick(View view) {
        //action when first date button is clicked
        if(view.getId() == R.id.fragDR_pick_first_date_btn){
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                    first_date_display.setText((mMonth+1)+"/"+mDay+"/"+mYear);
                    date1_picked = (mMonth+1)+"/"+mDay+"/"+mYear;
                    date2.setEnabled(true);//enables second calendar button when first date has been clicked
                }
            },day,month,year);
            datePickerDialog.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setCancelable(true);
            datePickerDialog.show();
        }
        //action when last date button is clicked
        else if(view.getId() == R.id.fragDR_pick_last_date_btn){
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                    last_date_display.setText((mMonth+1)+"/"+mDay+"/"+mYear);
                    date2_picked = (mMonth+1)+"/"+mDay+"/"+mYear;

                }
            },day,month,year);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date first_date = null;
            try {
                first_date = sdf.parse(date1_picked);
            } catch (ParseException e) {
            }
            datePickerDialog.getDatePicker().setMinDate(first_date.getTime());
            //datePickerDialog.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setCancelable(true);
            datePickerDialog.show();
        }
        //action when submit button is clicked
        else if(view.getId() == R.id.fragDR_date_range_submit_btn_admin){
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date first_date = null;
            Date last_date = null;
            try {
                first_date = sdf.parse(date1_picked);
                last_date = sdf.parse(date2_picked);
            } catch (ParseException e) {
            }

            if(first_date != null && last_date != null){
                dateRangeFragmentListener.getDates(first_date.getTime(), last_date.getTime());
            }
            else
                Toast.makeText(getContext(),"Pick Dates",Toast.LENGTH_SHORT).show();
        }
    }
}
