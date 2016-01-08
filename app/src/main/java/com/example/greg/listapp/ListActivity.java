package com.example.greg.listapp;

import android.app.ProgressDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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

public class ListActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
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

		//CarAdapter myCarAdapter = new CarAdapter(this, getDataForListView());
		//ListView myCarListView = (ListView) findViewById(R.id.listView);
		//myCarListView.setAdapter(myCarAdapter);

		final Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Perform action on click
				EditText inputEditText = (EditText) findViewById(R.id.editText);
				String inputKeyword = inputEditText.getText().toString();

				EditText priceEditText = (EditText) findViewById(R.id.editText2);
				String inputPrice = priceEditText.getText().toString();

				String url = "http://107.170.200.107/carapp/getcars.php?make=" + inputKeyword + "&pricemax=" + inputPrice;

				getJSON(url);

				Log.d("URL", "Our URL is " + url);

				InputMethodManager inputManager = (InputMethodManager)
						getSystemService(getBaseContext().INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});

		//getJSON("http://192.168.0.111/carapp/getcars.php?keyword=o");
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

	// Return a list of cars
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

	private void getJSON(String url)
	{
		class GetJSON extends AsyncTask<String, Void, String>
		{

			ProgressDialog loading;

			String finalJSONString = "overwrite";
			String finalJSONArray = "";

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				((TextView) findViewById(R.id.textView)).setText("Searching for data, please wait");
			}

			@Override
			protected String doInBackground(String... params)
			{

				String uri = params[0];

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

					//finalJSONString = sb.toString();
					//Log.e("JSON", sb.toString() + " <- THE JSON SHOULD BE HERE");

					String result = sb.toString();

					finalJSONArray = result;

					JSONArray jArray = new JSONArray(result);

					finalJSONString = result;

					//Log.e("JSON", "JSON ELEMENT RESULT: " + finalJSONString);

					globalImg = LoadImageFromWebOperations("http://192.168.0.111/carapp/img/servercar.png");

					return sb.toString().trim();

				} catch (Exception e)
				{
					Log.e("JSON", e.toString() + "");

					return null;
				}

			}

			@Override
			protected void onPostExecute(String s)
			{
				super.onPostExecute(s);
				//loading.dismiss();
				//((TextView) findViewById(R.id.textView)).setText(finalJSONString);

				CarAdapter myCarAdapter = new CarAdapter(getBaseContext(), getListFromJSON(finalJSONString));
				ListView myCarListView = (ListView) findViewById(R.id.listView);
				myCarListView.setAdapter(myCarAdapter);
			}
		}
		GetJSON gj = new GetJSON();
		gj.execute(url);
	}

	public Drawable globalImg;

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
