package com.scravlon.fooray;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class MainAPIScreen extends AppCompatActivity {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    Button but_cook;
    String food_str;
    TextView tvhungry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        but_cook = findViewById(R.id.but_cook);
        but_cook.setEnabled(false);
        tvhungry = findViewById(R.id.tv_hungry);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                final Vibrator vibe = (Vibrator) MainAPIScreen.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(150);
                Toast.makeText(MainAPIScreen.this, "Cooking!!!", Toast.LENGTH_SHORT).show();

                String url ="https://api.wegmans.io/meals/recipes?api-version=2018-10-18";
                url = url + "&Subscription-Key="+constantClass.subKey;
                apiCall(url);
            }
        });


    }

    public void populateJSON(String response){
//        Toast.makeText(MainAPIScreen.this, food_str, Toast.LENGTH_SHORT).show();

    }

    /**
     * Random number
     * @return
     */
    public int random(int total){
        Random random = new Random();
        return random.nextInt(total);
    }

    public void apiCall(String urlReq){
        RequestQueue queue = Volley.newRequestQueue(this);


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlReq,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("respond",response);
                        but_cook.setEnabled(true);

//                        Toast.makeText(MainAPIScreen.this, response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("recipes");
                            int randomNumber = random(jsonArray.length());
//                            Toast.makeText(MainAPIScreen.this, String.valueOf(randomNumber), Toast.LENGTH_SHORT).show();
                            JSONObject randomObject = jsonArray.getJSONObject(randomNumber);
                            int ranID = randomObject.getInt("id");
                            tvhungry.setText(randomObject.getString("name"));
                            Toast.makeText(MainAPIScreen.this, randomObject.getString("name"), Toast.LENGTH_SHORT).show();
                            apiCallItem(ranID);
                        } catch (Exception e){
                            Toast.makeText(MainAPIScreen.this, "Error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    public void apiCallItem(int id){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.wegmans.io/meals/recipes/22187/?api-version=2018-10-18";
        url = url + "&Subscription-Key="+constantClass.subKey+"&id="+id;
//        apiCall(url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("respond",response);
                        food_str = response;
                        but_cook.setEnabled(true);
                        but_cook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainAPIScreen.this, foodActivity.class);
                                try {
                                    JSONObject j =new JSONObject(response);
                                    JSONObject serveOBJ = j.getJSONObject("servings");
                                    JSONObject prepareObj = j.getJSONObject("preparationTime");
                                    JSONObject cookOBJ = j.getJSONObject("cookingTime");
                                    JSONObject instructOBJ = j.getJSONObject("instructions");
                                    JSONArray ingreArray = j.getJSONArray("ingredients");
                                    JSONArray linkArr = j.getJSONArray("_links");
                                    String name = j.getString("name");

                                    int id = j.getInt("id");
                                    double servingTime = serveOBJ.getDouble("min");
                                    double prepareTime = prepareObj.getDouble("min");
                                    double cookingTime = cookOBJ.getDouble("min");
                                    String instruction = instructOBJ.getString("directions");
                                    String imgLink = "";
                                    for(int i = 0; i < linkArr.length(); i++){
                                        JSONObject relObj = linkArr.getJSONObject(i);
                                        if(relObj.getString("rel").equals("image")){
                                            imgLink =relObj.getString("href");
                                            break;
                                        }
                                    }
                                    ArrayList<String[]> ingredirent = new ArrayList<>();
                                    for(int i = 0; i< ingreArray.length();i++){
                                        JSONObject ins = ingreArray.getJSONObject(i);
                                        String[] strArr = new String[3];
                                        strArr[0] = ins.getString("name");
                                        strArr[1] = ins.getString("type");
                                        JSONObject quantiOBJ = ins.getJSONObject("quantity");
                                        strArr[2] = quantiOBJ.getString("text");
                                        ingredirent.add(strArr);
                                    }
                                    itemObject item = new itemObject(id,name,prepareTime,cookingTime,servingTime,instruction,imgLink,ingredirent);
                                    intent.putExtra("food" , item);
                                } catch(Exception e){
                                    e.printStackTrace();
                                }
                                //intent.putExtra("foodjson",response);
                                startActivity(intent);
                            }
                        });
//                        Toast.makeText(MainAPIScreen.this, response, Toast.LENGTH_SHORT).show();
                        try {
                            populateJSON(response);
                        } catch (Exception e){
                            Toast.makeText(MainAPIScreen.this, "Error", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }
}
