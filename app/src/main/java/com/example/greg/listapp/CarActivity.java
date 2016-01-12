package com.example.greg.listapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class CarActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		/*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});*/
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Intent newIntent = getIntent();

		String title = newIntent.getStringExtra("TITLE");
		String mileage = "Odometer: " + newIntent.getStringExtra("MILEAGE");
		String description = newIntent.getStringExtra("DESCRIPTION");
		String price = newIntent.getStringExtra("PRICE");

		((TextView) findViewById(R.id.titleTextView)).setText(title);
		((TextView) findViewById(R.id.mileageTextView)).setText(mileage);
		((TextView) findViewById(R.id.descriptionTextView)).setText(description);
		((TextView) findViewById(R.id.priceTextView)).setText(price);
	}

}
