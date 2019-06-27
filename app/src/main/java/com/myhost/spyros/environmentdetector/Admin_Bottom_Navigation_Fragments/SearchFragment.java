package com.myhost.spyros.environmentdetector.Admin_Bottom_Navigation_Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.myhost.spyros.environmentdetector.R;

public class SearchFragment extends Fragment implements View.OnClickListener{

    //views of fragment
    private EditText objectToSearchEdTxt;
    private Button objectToSearchBtn;
    private SearchFragmentListener searchFragmentListener;



    //in order to communicate with the underlying activity we have to create an interface to do it
    public interface SearchFragmentListener{
        void getObjectToSearch(String objectToSearch);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_search_admin, container, false);

        //init fragment views
        objectToSearchEdTxt = v.findViewById(R.id.fragS_search_object_edTxt);
        objectToSearchBtn = v.findViewById(R.id.fragS_search_object_submit_btn);
        objectToSearchBtn.setOnClickListener(this);

        return v;
    }

    //this method is called when our fragment is attached in our activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //check if the activity that we want to send data implements SearchFragmentActivityListener
        if(context instanceof SearchFragmentListener)
            searchFragmentListener = (SearchFragmentListener) context;
        else
            throw new RuntimeException(context.toString()
            +" must implement SearchFragmentListener");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        searchFragmentListener = null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fragS_search_object_submit_btn){
            String object = objectToSearchEdTxt.getText().toString().trim();
            searchFragmentListener.getObjectToSearch(object);
        }
    }


}
