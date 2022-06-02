package com.lyft.lyft.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lyft.lyft.Model.Booking;
import com.lyft.lyft.R;

import java.util.ArrayList;

public class BookingAdapter extends ArrayAdapter<Booking> {

    private Context mContext;
    private int mResource;

    public BookingAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Booking> objects) {
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

        vehicle.setText(getItem(position).getBus_reg_no());
        date.setText(getItem(position).getAvailable_date_time());
        sheet.setText("Travel Date : " + getItem(position).getBooked_date_time());
        root.setText("Pickup Location : " + getItem(position).getPicked_location());
        start.setText("Start : " + getItem(position).getStart_location());
        end.setText("End : " + getItem(position).getEnd_location());

        return convertView;
    }

}