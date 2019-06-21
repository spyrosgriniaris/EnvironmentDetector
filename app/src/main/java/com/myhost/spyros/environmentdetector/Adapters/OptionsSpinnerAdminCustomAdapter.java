package com.myhost.spyros.environmentdetector.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhost.spyros.environmentdetector.Data.OptionsSpinnerAdminData;
import com.myhost.spyros.environmentdetector.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OptionsSpinnerAdminCustomAdapter extends ArrayAdapter<OptionsSpinnerAdminData> {

    private Context mContext;
    private List<OptionsSpinnerAdminData> mSpinnerData;

    public OptionsSpinnerAdminCustomAdapter(@NonNull Context context, int resource, List<OptionsSpinnerAdminData> spinnerData) {
        super(context, resource, spinnerData);
        this.mContext = context;
        this.mSpinnerData = spinnerData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return myCustomSpinnerView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return myCustomSpinnerView(position, convertView, parent);
    }

    public View myCustomSpinnerView(int position, @Nullable View myView, @android.support.annotation.NonNull ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.options_spinner_admin,parent,false);
        TextView optionsSpinnerAdminText = (TextView) customView.findViewById(R.id.optionsAdminSpinnerText);
        ImageView optionsSpinnerAdminImg = (ImageView) customView.findViewById(R.id.optionsSpinnerAdminImg);

        optionsSpinnerAdminText.setText(mSpinnerData.get(position).getIconName());
        optionsSpinnerAdminImg.setImageResource(mSpinnerData.get(position).getIcon());
        return customView;
    }
}
