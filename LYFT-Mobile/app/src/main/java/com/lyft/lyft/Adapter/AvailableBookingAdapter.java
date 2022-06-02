package com.lyft.lyft.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lyft.lyft.Model.AvailableBooking;
import com.lyft.lyft.R;

import java.util.ArrayList;

public class AvailableBookingAdapter extends ArrayAdapter<AvailableBooking> {

    private Context mContext;
    private int mResource;

    public AvailableBookingAdapter(@NonNull Context context, int resource, @NonNull ArrayList<AvailableBooking> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView vehicle = (TextView) convertView.findViewById(R.id.vehicle);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView sheet = (TextView) convertView.findViewById(R.id.sheet);
        TextView root = (TextView) convertView.findViewById(R.id.root);
        TextView start = (TextView) convertView.findViewById(R.id.start);
        TextView end = (TextView) convertView.findViewById(R.id.end);

        vehicle.setText(getItem(position).getBusRegNo());
        date.setText(getItem(position).getDateTime());
        sheet.setText("Available Sheets : " + getItem(position).getSheets());
        root.setText("Root : " + getItem(position).getRootNo());
        start.setText("Start : " + getItem(position).getStartLocation());
        end.setText("End : " + getItem(position).getEndLocation());

        return convertView;
    }

}