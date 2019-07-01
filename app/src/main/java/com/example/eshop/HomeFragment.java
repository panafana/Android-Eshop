package com.example.eshop;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {
    OnCallbackReceived mCallback;
    ArrayList<Integer> images = new ArrayList<>();
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> prices = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    AdapterView.OnItemClickListener listener;

    public interface OnCallbackReceived {
        public void Update();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //images = bundle.getStringArrayList("images");
            titles = bundle.getStringArrayList("titles");
            prices = bundle.getStringArrayList("prices");
            descriptions = bundle.getStringArrayList("descriptions");
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //new DownloadImageTask((ImageView) findViewById(R.id.image)) .execute("http://scoopak.com/wp-content/uploads/2013/06/free-hd-natural-wallpapers-download-for-pc.jpg");
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        images.add(R.drawable.tomb);
        images.add(R.drawable.farcry);
        images.add(R.drawable.zelda);
        images.add(R.drawable.doom);
        images.add(R.drawable.fallout);
        images.add(R.drawable.fortnite);

        for(int i = 0; i<titles.size();i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("img",Integer.toString(images.get(i)));
            hm.put("title",titles.get(i));
            //hm.put("dsc",descriptions.get(i));
            hm.put("price",prices.get(i));
            aList.add(hm);
        }

        String[] from = { "img","title","price" };

        int[] to = { R.id.thumb,R.id.title,R.id.price};

        SimpleAdapter adapter = new SimpleAdapter(getContext(), aList, R.layout.mylist, from, to);
        ListView listview =(ListView)rootView.findViewById(R.id.products);
        //String[] items = new String[] {"Item 1", "Item 2", "Item 3"};
        listview.setAdapter(adapter);
        // Inflate the layout for this fragment
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("clicked ",Integer.toString(i));
                Log.d("price",prices.get(i).substring(0,(prices.get(i).length()-1)));
                SharedPreferences SP = getContext().getSharedPreferences("cart", MODE_PRIVATE);
                SharedPreferences.Editor editor = SP.edit();
                String currentCart = SP.getString("cart",null);

                Gson gson = new Gson();
                if(currentCart!=null){

                    Type type = new TypeToken<HashMap<String,Integer>>() {}.getType();
                    HashMap<String, Integer> cartMap = gson.fromJson(currentCart,  type);


                    if(cartMap.containsKey(Integer.toString(i))){
                        int currentQty = cartMap.get(Integer.toString(i));
                        System.out.println("current QTY "+currentQty);
                        System.out.println("current qty"+currentQty);
                        cartMap.put(Integer.toString(i),currentQty +1);
                        String jsonText = gson.toJson(cartMap);
                        editor.putString("cart", jsonText);
                        editor.apply();

                    }else{
                        cartMap.put(Integer.toString(i),1);
                        String jsonText = gson.toJson(cartMap);
                        editor.putString("cart", jsonText);
                        editor.apply();
                    }
                }else{
                    HashMap<String, Integer> cartMap = new HashMap<>();
                    cartMap.put(Integer.toString(i),1);
                    String jsonText = gson.toJson(cartMap);
                    editor.putString("cart", jsonText);
                    editor.apply();
                }
                mCallback.Update();
            }
        });

        return rootView;


    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnCallbackReceived) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}