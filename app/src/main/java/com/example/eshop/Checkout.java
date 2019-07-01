package com.example.eshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Checkout extends AppCompatActivity {

    Button buy;
    EditText name;
    EditText address;
    TextView totalprice;
    HashMap<String, Integer> cartMap;
    ArrayList<String> ids ;
    ArrayList<String> titles;
    ArrayList<String> prices;
    String jsonInString;
    String resp;
    String mail;
    SharedPreferences SP;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

         buy = findViewById(R.id.buy);
         name = findViewById(R.id.name);
         address = findViewById(R.id.address);
         totalprice = findViewById(R.id.total_price);
         ids = getIntent().getStringArrayListExtra("ids");
         titles = getIntent().getStringArrayListExtra("titles");
         prices = getIntent().getStringArrayListExtra("prices");

         totalprice.setText("0€");

        SP = this.getSharedPreferences("cart", MODE_PRIVATE);
        SharedPreferences SP2 = this.getSharedPreferences("user", MODE_PRIVATE);
        mail = SP2.getString("user",null);

        final Gson gson = new Gson();
        String currentCart = SP.getString("cart",null);
        if(currentCart!=null){


            Type type = new TypeToken<HashMap<String,Integer>>() {}.getType();
            cartMap = gson.fromJson(currentCart,  type);
            System.out.println("CART");
            System.out.println(cartMap);
            final Cart cart = new Cart();
            for(Map.Entry<String ,Integer> entry: cartMap.entrySet()){
                //System.out.println(entry.getKey()+" "+entry.getValue());
                if(entry.getValue()>0){
                    System.out.println(entry.getKey()+" "+entry.getValue());
                    String temptitle = titles.get(Integer.valueOf(entry.getKey()));
                    int tempprice = Integer.valueOf(prices.get(Integer.valueOf(entry.getKey())).substring(0,prices.get(Integer.valueOf(entry.getKey())).length()-1));
                    String tempid = ids.get(Integer.valueOf(entry.getKey()));
                    Item temp = new Item(tempid,temptitle,tempprice);

                    int tempqty = entry.getValue();
                    temp.itemqty(tempqty);
                    System.out.println("temp "+temp+" entry "+tempqty);
                    cart.add(temp,tempqty);
                }
            }
            totalprice.setText("Total Price: "+cart.getTotalPrice()+"€");

            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!(name.getText().toString().equals(""))&&!(address.getText().toString().equals(""))) {

                        cart.addUser(mail,name.getText().toString(),address.getText().toString());
                        jsonInString = gson.toJson(cart);
                        System.out.println("JSON "+jsonInString);
                        sendCart send = new sendCart();
                        send.execute();
                        SharedPreferences.Editor SPE = SP.edit();
                        SPE.remove("cart");
                        SPE.apply();

                        Intent main = new Intent(Checkout.this,Main2Activity.class);
                        startActivity(main);

                    }
                }
            });

        }

    }

    public class sendCart extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL("http://83.212.84.230:3000/android/checkout");
                //String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mid), "UTF-8") +"&"+URLEncoder.encode("choice", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mchoice), "UTF-8")+"&"+URLEncoder.encode("male", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mmale), "UTF-8")+"&"+URLEncoder.encode("female", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mfemale), "UTF-8")+"&"+URLEncoder.encode("other", "UTF-8")+ "=" + URLEncoder.encode(String.valueOf(mother), "UTF-8");
               /* Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email", musername)
                        .appendQueryParameter("password", mPassword);
                String query = builder.build().getEncodedQuery();*/

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonInString);

                writer.flush();
                writer.close();
                os.close();
                conn.connect();


                InputStream IS = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(IS));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Pass data to onPostExecute method
                String r =(result.toString());
                IS.close();


                Log.d("Response", r);
                resp = r;
                conn.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed";
            }
            if(resp.contains("Success")){
                return "Success";
            }else{
                return "Invalid";
            }

        }

        @Override
        protected void onPostExecute(final String success) {
            if (success.equals("Success")) {
                System.out.println("Success");
                Toast.makeText(getApplicationContext(), "Cart registered successfully", Toast.LENGTH_LONG).show();
            }


        }



    }


    public class Cart{
        private ArrayList<Item> items = new ArrayList<>();
        private int totalPrice = 0;
        private int totalQty = 0;
        private String email;
        private String name;
        private String address;

        public Cart(){

        }

        public void addUser(String user,String name,String address){
            this.email = user;
            this.name = name;
            this.address = address;
        }

        public void add(Item item,int qty){
            this.items.add(item);
            totalQty+=qty;
            totalPrice+=qty*item.price;
        }

        public int getTotalPrice() {
            return totalPrice;
        }
    }

    public class Item {
        private int price;
        private String title;
        private String id;
        private int qty = 0;

        public Item(String id,String title,int price){
            this.price = price;
            this.id = id;
            this.title = title;

        }
        public void itemqty(int Qty){
            this.qty= Qty;
        }
    }


}
