package com.myhost.spyros.environmentdetector.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myhost.spyros.environmentdetector.Models.ViewAllUsersModel;
import com.myhost.spyros.environmentdetector.R;

import java.util.List;


public class ViewAllUsersAdapter extends RecyclerView.Adapter<ViewAllUsersAdapter.ViewHolder> {

    //declare views and variables for constructor
    private List<ViewAllUsersModel> usersList;
    private Activity activity;
    private int users_layout;
    ViewAllUsersAdapter viewAllUsersAdapter;
    RecyclerView recyclerView;

    public ViewAllUsersAdapter(Activity activity, List<ViewAllUsersModel> usersList, int users_layout, RecyclerView recyclerView) {
        this.usersList = usersList;
        this.activity = activity;
        this.users_layout = users_layout;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewAllUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //inflate users_layout and pass it to view holder
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.users_layout,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAllUsersAdapter.ViewHolder viewHolder, int i) {
        viewHolder.userEmailTxt.setText(usersList.get(i).getUserEmail());
        viewHolder.userIdTxt.setText(usersList.get(i).getUserId());
        viewHolder.userRoleTxt.setText(usersList.get(i).getUserRole());
    }

    @Override
    public int getItemCount() {
        return (null != usersList ? usersList.size() : 0);
    }



    /**
     * View holder to display each RecylerView item
     * initializes views
     */
    protected class ViewHolder extends RecyclerView.ViewHolder{

        //declare views of ViewUsersLayout
        private TextView userEmailTxt, userIdTxt, userRoleTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmailTxt = (TextView) itemView.findViewById(R.id.emailTxtViewAllUsers);
            userIdTxt = (TextView) itemView.findViewById(R.id.idTxtViewAllUsers);
            userRoleTxt = (TextView) itemView.findViewById(R.id.roleTxtViewAllUsers);

        }
    }
}
