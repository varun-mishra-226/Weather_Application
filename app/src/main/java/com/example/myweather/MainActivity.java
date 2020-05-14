package com.example.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView tvWeather;
    Button btnGetWeather;
    EditText etCity;

    public class GetWeather extends AsyncTask<String, Void, String>
    {
        String html="";
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1)
                {
                    char cur = (char) data;
                    html += cur;
                    data = reader.read();
                }
                return html;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find the weather...", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weather = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weather);
                for (int i=0; i<arr.length(); i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    tvWeather.setText(main + ": " + description);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find the weather...", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void getWeather (View view) throws UnsupportedEncodingException {
        if (etCity.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter all fields...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String cityName = etCity.getText().toString().trim();
            String encodedCityName = URLEncoder.encode(cityName, "UTF-8");
            GetWeather city = new GetWeather();
            try {
                String html = city.execute("https://openweathermap.org/data/2.5/weather?q="
                        + encodedCityName + "&appid=439d4b804bc8187953eb36d2a8c26a02").get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(etCity.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWeather = (TextView) findViewById(R.id.tvWeather);
        etCity = (EditText) findViewById(R.id.etCity);
        btnGetWeather = (Button) findViewById(R.id.btnGetWeather);
    }
}
