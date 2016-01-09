package com.example.greg.listapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greg on 12/28/2015.
 * Pretty much all of these functions were overridden from
 * the base class, they all are called on their own.  The
 * most important one is getView.
 */
public class CarAdapter extends BaseAdapter
{

	private final Context context;
	List<Car> carList;

	// CONSTRUCTOR
	public CarAdapter(Context context, List<Car> newCarList)
	{
		this.context = context;
		carList = newCarList;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return carList.size();
	}

	@Override
	public Car getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return carList.get(arg0);
	}

	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return arg0;
	}

	// Here is where each element of the listView is modified to
	// use the values we got from the server
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// I don't know what this does but we need it
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listitem, parent, false);
		}

		// Get references to the textviews in the listview
		TextView carName = (TextView) convertView.findViewById(R.id.carNameText);
		TextView carYear = (TextView) convertView.findViewById(R.id.carYearText);
		TextView carPrice = (TextView) convertView.findViewById(R.id.carPriceText);

		ImageView carImage = (ImageView) convertView.findViewById(R.id.carImage);

		// Get the Car that corresponds to the element
		// of the listview we are doing right now
		Car newCar = carList.get(position);

		// Update the listview elements with the proper values
		carName.setText(newCar.make + " " + newCar.model);
		carYear.setText(newCar.year);
		carPrice.setText("$" + newCar.price);

		// WARNING! THIS MAY CAUSE CRASH IF IMAGE DOES NOT EXIST
		// ^ Actually from what I can tell it doesn't (?)
		carImage.setImageDrawable(newCar.image);
		//carImage.setScaleX(5);
		//carImage.setScaleY(5);

		return convertView;
	}


}
