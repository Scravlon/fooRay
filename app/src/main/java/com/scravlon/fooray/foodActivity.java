package com.scravlon.fooray;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class foodActivity extends AppCompatActivity {

    String foodStr;
    itemObject item;
    TextView tvName;
    ImageView imgView;
    TextView tvServing;
    TextView tvPrepare;
    TextView tvCooking;
    TextView tv_instruction;
    LinearLayout linearLayout;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        tvName = findViewById(R.id.tv_name);
        tvServing = findViewById(R.id.tv_serving);
        tvPrepare = findViewById(R.id.tv_prepare);
        tvCooking = findViewById(R.id.tv_cooking);
        tv_instruction = findViewById(R.id.tv_instruction);
        scrollView = findViewById(R.id.scview);
        imgView = findViewById(R.id.imgView);
        linearLayout = findViewById(R.id.ll_ingredients);
        try{
//            foodStr = getIntent().getStringExtra("foodjson");

//            jsonObject = new JSONObject(foodStr);
            item = (itemObject) getIntent().getSerializableExtra("food");
            tvName.setText(item.name);
            tvServing.setText(String.valueOf(item.servingAmount));
            tvPrepare.setText(String.valueOf(item.prepareTime)+ " min");
            tvCooking.setText(String.valueOf(item.cookingTime)+ " min");
            tv_instruction.setText(Html.fromHtml(item.instruction));
            new DownloadImageTask(imgView) .execute(item.imgLink);
            ArrayList<String[]> ingre = item.ingredient;
            for(String[] s:ingre){

                TextView tv = new TextView(this);
                if(s[2].equals("null")){
                    tv.setText("-" + s[0] + "\nType: " + s[1]);

                } else {
                    tv.setText("-" + s[0] + "\nType: " + s[1] + "\nAmount: " + s[2]);
                }
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                tv.setTypeface(null, Typeface.BOLD);
                linearLayout.addView(tv);

                View v = new View(this);
                v.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                ));
                v.setBackgroundColor(Color.parseColor("#FBFBFB"));
                linearLayout.addView(v);
            }
            scrollView.forceLayout();

        } catch (Exception e){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
