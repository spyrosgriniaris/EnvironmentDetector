package com.myhost.spyros.environmentdetector.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.myhost.spyros.environmentdetector.Data.ImageData;
import com.myhost.spyros.environmentdetector.R;

import java.util.List;

import github.hellocsl.cursorwheel.CursorWheelLayout;

public class WheelImageAdapter extends CursorWheelLayout.CycleWheelAdapter {

    private Context mContext;
    private List<ImageData> menuItems;
    private LayoutInflater inflater;
    private int gravity;


    public WheelImageAdapter(Context mContext, List<ImageData> menuItems) {
        this.mContext = mContext;
        this.menuItems = menuItems;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public View getView(View parent, int position) {
        ImageData data = getItem(position);
        View root = inflater.inflate(R.layout.wheel_image_layout,null,false);
        ImageView imageView = (ImageView)root.findViewById(R.id.wheel_menu_item_tv);
        imageView.setImageResource(data.imageResource);
        return root;
    }

    @Override
    public ImageData getItem(int position) {
        return menuItems.get(position);
    }
}
