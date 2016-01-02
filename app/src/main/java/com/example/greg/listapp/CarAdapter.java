package com.example.greg.listapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greg on 12/28/2015.
 */
public class CarAdapter extends BaseAdapter {

    private final Context context;
    List<Car> carList;

    // CONSTRUCTOR
    public CarAdapter(Context context, List<Car> newCarList) {
        this.context = context;
        carList = newCarList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return carList.size();
    }

    @Override
    public Car getItem(int arg0) {
        // TODO Auto-generated method stub
        return carList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem, parent,false);
        }

        TextView carName = (TextView)convertView.findViewById(R.id.carNameText);
        TextView carYear = (TextView)convertView.findViewById(R.id.carYearText);
        TextView carPrice = (TextView)convertView.findViewById(R.id.carPriceText);

        Car newCar = carList.get(position);

        carName.setText(newCar.name);
        carYear.setText(newCar.year);
        carPrice.setText(newCar.price);

        return convertView;
    }


}
