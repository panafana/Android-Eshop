package com.example.eshop;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class Main2Activity extends AppCompatActivity implements HomeFragment.OnCallbackReceived , CartFragment.Callbacks {
    private TextView mTextMessage;
    Fragment currentFragment = null;
    FragmentTransaction ft;
    String resp;
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<String> numbers = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<String> prices = new ArrayList<>();
    int totalProducts =0;
    int totalPrice = 0;
    BottomNavigationView navView;
    Button checkout;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String title = getString(R.string.title_activity_main2);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    currentFragment = new HomeFragment();
                    title = getString(R.string.title_activity_main2);
                    if (currentFragment != null) {
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("ids", ids);
                        bundle.putStringArrayList("images", images);
                        bundle.putStringArrayList("titles", titles);
                        bundle.putStringArrayList("prices", prices);
                        bundle.putStringArrayList("descriptions", descriptions);
                        ft = getSupportFragmentManager().beginTransaction();
                        currentFragment.setArguments(bundle);
                        ft.replace(R.id.container_body, currentFragment);
                        ft.commit();
                        getSupportActionBar().setTitle(title);
                        checkout.setVisibility(View.INVISIBLE);
                        //navView.setSelectedItemId(R.id.navigation_home);
                    }


                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText("");
                    currentFragment = new CartFragment();
                    title = getString(R.string.title_activity_shopping_cart);
                    if (currentFragment != null) {
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("ids", ids);
                        bundle.putStringArrayList("numbers", numbers);
                        bundle.putStringArrayList("titles", titles);
                        bundle.putStringArrayList("prices", prices);
                        bundle.putStringArrayList("descriptions", descriptions);
                        ft = getSupportFragmentManager().beginTransaction();
                        currentFragment.setArguments(bundle);
                        ft.replace(R.id.container_body, currentFragment);
                        ft.commit();
                        checkout.setVisibility(View.VISIBLE);
                        getSupportActionBar().setTitle(title);
                    }
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    currentFragment = new UserFragment();
                    title = getString(R.string.title_activity_user_profile);
                    if (currentFragment != null) {
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.container_body, currentFragment);
                        ft.commit();
                        getSupportActionBar().setTitle(title);
                        checkout.setVisibility(View.INVISIBLE);
                        //navView.setSelectedItemId(R.id.navigation_notifications);
                    }
                    return true;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        checkout = findViewById(R.id.checkout);
        checkout.setVisibility(View.INVISIBLE);

        GetProducts gp = new GetProducts();
        gp.execute();

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkout = new Intent(Main2Activity.this,Checkout.class);
                checkout.putExtra("ids",ids);
                checkout.putExtra("titles",titles);
                checkout.putExtra("prices",prices);
                startActivity(checkout);
            }
        });
    }


    @Override
    public void Update() {
        System.out.println("Clicked");
        mTextMessage.setText(R.string.title_dashboard);
        currentFragment = new CartFragment();
        String title = getString(R.string.title_activity_shopping_cart);
        if (currentFragment != null) {
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container_body, currentFragment);
            ft.commit();
            getSupportActionBar().setTitle(title);
            checkout.setVisibility(View.VISIBLE);
            navView.setSelectedItemId(R.id.navigation_dashboard);
        }

    }

    @Override
    public void onButtonClicked() {
        getSupportFragmentManager()
                .beginTransaction()
                .detach(new CartFragment())
                .attach(new CartFragment())
                .commit();
    }

    private class GetProducts extends AsyncTask< String, Void,String> {


        @Override
        protected String doInBackground(String... voids) {

            try {
                URL url = new URL("http://83.212.84.230:3000/android/products");
                //String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mid), "UTF-8") +"&"+URLEncoder.encode("choice", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mchoice), "UTF-8")+"&"+URLEncoder.encode("male", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mmale), "UTF-8")+"&"+URLEncoder.encode("female", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mfemale), "UTF-8")+"&"+URLEncoder.encode("other", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mother), "UTF-8");


                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                //conn.setDoInput(true);
                //conn.setDoOutput(true);
                /*OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.flush();
                writer.close();
                os.close();*/
                conn.connect();

                InputStream IS = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(IS));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Pass data to onPostExecute method
                String r = (result.toString());
                IS.close();


                Log.d("DATA", r);
                resp = r;
                conn.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed";
            }
            if (resp.contains("Success")) {
                return "Success";
            } else {
                return "Invalid";
            }


        }


        @Override
        protected void onPostExecute(final String success) {

            try {

                JSONArray itemArray=new JSONArray(resp);
                for (int i=0; i<itemArray.length(); i++) {
                    JSONObject obj = itemArray.getJSONObject(i);
                    String image = obj.getString("imagePath");
                    String id = obj.getString("_id");

                    String title = obj.getString("title");
                    String price = obj.getString("price");
                    String description = obj.getString("description");
                    ids.add(id);
                    images.add(image);
                    numbers.add(Integer.toString(i));
                    titles.add(title);
                    prices.add(price+"€");
                    descriptions.add(description);
                    //System.out.println("id "+id+" title "+title+" image "+image+" description "+description+" price "+price );
                }
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("images", images);
                bundle.putStringArrayList("titles", titles);
                bundle.putStringArrayList("prices", prices);
                bundle.putStringArrayList("descriptions", descriptions);

                ft = getSupportFragmentManager().beginTransaction();
                currentFragment = new HomeFragment();
                currentFragment.setArguments(bundle);
                ft.replace(R.id.container_body, currentFragment);
                ft.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }



    }





    ArrayList<String> jsonStringToArray(String jsonString) throws JSONException {

        ArrayList<String> stringArray = new ArrayList<String>();

        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            stringArray.add(jsonArray.getString(i));
        }

        return stringArray;
    }
}
