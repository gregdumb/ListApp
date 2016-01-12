package com.example.greg.listapp;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MyListActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// This was mostly made for me automatically
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("Cars in your area");

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Snackbar.make(view, "Haha this does nothing loser", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		// This handles what happens when you press the button
		final Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Get the "make" search from the text box
				EditText inputEditText = (EditText) findViewById(R.id.editText);
				String inputKeyword = inputEditText.getText().toString();

				// Get the max price from the text box
				EditText priceEditText = (EditText) findViewById(R.id.editText2);
				String inputPrice = priceEditText.getText().toString();

				// make the URL to get our JSON
				String url = "http://107.170.200.107/carapp/getcars.php?make=" + inputKeyword + "&pricemax=" + inputPrice;

				// THE MAGIC: goes to the server, gets the list of cars
				// in JSON format, and updates the listView accordingly
				getJSON(url);

				// Log, for debugging purposes
				Log.d("URL", "Our URL is " + url);

				// This makes the keyboard drop after we press the button
				InputMethodManager inputManager = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// Returns a generated list of cars
	// This does NOT use the database and was only created
	// for debugging purposes
	public static List<Car> getDataForListView()
	{
		List<Car> carList = new ArrayList<Car>();
		for (int i = 0; i < 20; i++)
		{
			Car car = new Car();
			car.name = "Ford Taurus";
			car.year = Integer.toString(1995 + i);
			car.price = "$" + i * 100;
			carList.add(car);
		}
		return carList;
	}

	// The heavy lifter.  Goes to the online server, gets
	// the list of cars as a JSON string, parses the string,
	// creates an adapter for the listview, and assigns the
	// data from the JSON to the listview through the adapter.
	private void getJSON(String url)
	{
		// This is a separate class since android does not allow
		// accessing the internet in the main UI thread to avoid
		// hanging.  AsyncTask is the base class that allows
		// things to happen outside the UI thread.
		class GetJSON extends AsyncTask<String, Void, String>
		{

			ProgressDialog loading;

			String finalJSONString = "overwrite";

			// This happens before we do the separate slow thread
			// we are still in the UI thread here, which is why we
			// can set the text in the textView
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				((TextView) findViewById(R.id.textView)).setText("Searching for data, please wait");
			}

			// This happens outside the UI thread.  Here is where we
			// go to the server and get the JSON string.
			@Override
			protected String doInBackground(String... params)
			{
				String uri = params[0];

				// This try/catch statement is the part that
				// accesses the web
				BufferedReader bufferedReader = null;
				try
				{
					URL url = new URL(uri);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					StringBuilder sb = new StringBuilder();

					bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

					String json;
					while ((json = bufferedReader.readLine()) != null)
					{
						sb.append(json + "\n");
					}

					// "result" is our JSON string
					String result = sb.toString();

					// We have finalJSONString so we can get the result
					// outside of this background thread
					finalJSONString = result;

					globalImg = LoadImageFromWebOperations("http://192.168.0.111/carapp/img/servercar.png");

					return sb.toString().trim();

				} catch (Exception e)
				{
					// Log the error if there was one
					Log.e("JSON", e.toString() + "");
					return null;
				}

			}

			// This is called after the backround thread completes
			@Override
			protected void onPostExecute(String s)
			{
				super.onPostExecute(s);

				// Here we spawn a new car adapter and pass the parsed JSON in
				// The adapter functions take care of the rest (see CarAdapter.java)
				final CarAdapter myCarAdapter = new CarAdapter(getBaseContext(), getListFromJSON(finalJSONString));
				ListView myCarListView = (ListView) findViewById(R.id.listView);
				myCarListView.setAdapter(myCarAdapter);

				// Called when a list item is clicked
				myCarListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						Car currentCar = myCarAdapter.getItem(position);
						//Toast.makeText(MyListActivity.this, currentCar.description, Toast.LENGTH_LONG).show();

						String carTitle = currentCar.year + " " + currentCar.make + " " + currentCar.model;
						String carPrice = "$" + currentCar.price;

						Intent newIntent = new Intent(getBaseContext(), CarActivity.class);
						newIntent.putExtra("TITLE", carTitle);
						newIntent.putExtra("PRICE", carPrice);
						newIntent.putExtra("DESCRIPTION", currentCar.description);
						newIntent.putExtra("MILEAGE", currentCar.mileage);
						startActivity(newIntent);
					}
				});
			}
		}
		GetJSON gj = new GetJSON();
		gj.execute(url);
	}

	public Drawable globalImg;

	// This parses the JSON and returns the cars as
	// a list of "Car" classes.
	public List<Car> getListFromJSON(String arrayString)
	{
		List<Car> carList = new ArrayList<Car>();

		try
		{
			JSONArray jArray = new JSONArray(arrayString);

			for (int i = 0; i < jArray.length(); i++)
			{
				JSONObject carJSON = jArray.getJSONObject(i);
				Car newCar = new Car();

				newCar.make = carJSON.getString("make");
				newCar.model = carJSON.getString("model");
				newCar.year = carJSON.getString("year");
				newCar.price = carJSON.getString("price");
				newCar.description = carJSON.getString("description");
				newCar.mileage = carJSON.getString("mileage");

				//newCar.image = LoadImageFromWebOperations("http://192.168.0.111/carapp/img/servercar.png");
				newCar.image = globalImg;

				//Log.e("JSON", "NEW CAR MAKE: " + newCar.make);

				carList.add(newCar);
			}
		} catch (JSONException e)
		{
			Log.e("JSON", "JSON ERROR: " + e);
		}

		return carList;
	}

	public static Drawable LoadImageFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (Exception e) {
			Log.e("IMAGE", "ERROR: " + e);
			return null;
		}
	}

}
