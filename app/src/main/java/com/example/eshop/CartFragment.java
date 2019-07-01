package com.example.eshop;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class CartFragment extends Fragment  {
    HomeFragment.OnCallbackReceived mCallback;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> numbers = new ArrayList<>();
    ArrayList<String> prices = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    AdapterView.OnItemClickListener listener;
    HashMap<String, Integer> cartMap;
    TextView empty;
    Button remove1l;
    Button removeall;


    private Callbacks mCallbacks;



    public interface Callbacks {
        //Callback for when button clicked.
        public void onButtonClicked();
    }

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //images = bundle.getStringArrayList("images");
            titles = bundle.getStringArrayList("titles");
            numbers = bundle.getStringArrayList("numbers");
            prices = bundle.getStringArrayList("prices");
            descriptions = bundle.getStringArrayList("descriptions");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        empty = (TextView) rootView.findViewById(R.id.empty);
        remove1l =  rootView.findViewById(R.id.remove);
        removeall =  rootView.findViewById(R.id.remove_all);

        //Retrieve the values
        SharedPreferences SP = getContext().getSharedPreferences("cart", MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();

        Gson gson = new Gson();
        String currentCart = SP.getString("cart",null);
        if(currentCart!=null){
            empty.setText("");
            Type type = new TypeToken<HashMap<String,Integer>>() {}.getType();
            cartMap = gson.fromJson(currentCart,  type);
            System.out.println("CART");
            for(Map.Entry<String ,Integer> entry: cartMap.entrySet()){
                System.out.println(entry.getKey()+" "+entry.getValue());
            }


            final List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

            for(int i = 0; i<titles.size();i++){
                HashMap<String, String> hm = new HashMap<>();
                if(cartMap.containsKey(Integer.toString(i))){


                    int tempqty = cartMap.get(Integer.toString(i));
                    if(tempqty>0){
                        hm.put("qty","Qty: "+(cartMap.get(Integer.toString(i))));
                        hm.put("number",numbers.get(i));
                        hm.put("title",titles.get(i));
                        //hm.put("dsc",descriptions.get(i));
                        String temp = prices.get(i);
                        String temp2 = temp.substring(0,temp.length()-1);
                        System.out.println("temp2 "+temp2);
                        int priceofone = Integer.valueOf(temp2);
                        int totalprice = tempqty * priceofone;
                        hm.put("price",Integer.toString(totalprice)+"€");
                     aList.add(hm);
                 }

                }
            }

            String[] from = { "title","qty","price" };

            int[] to = { R.id.title,R.id.quantity,R.id.price};

            SimpleAdapter adapter = new SimpleAdapter(getContext(), aList, R.layout.cartlist, from, to){
                @Override
                public View getView (final int position, View convertView, ViewGroup parent)
                {
                    View v = super.getView(position, convertView, parent);

                    remove1l=(Button)v.findViewById(R.id.remove);
                    removeall = v.findViewById(R.id.remove_all);
                    remove1l.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            System.out.println("clicked remove 1 at "+position);
                            System.out.println("alist position" +aList.get(position).get("number"));

                            int qtybefore = cartMap.get(aList.get(position).get("number"));
                            if(qtybefore>1){
                                qtybefore--;
                                aList.get(position).put("qty","Qty: "+qtybefore);
                                String pricetemp = (prices.get(Integer.valueOf(aList.get(position).get("number"))));
                                int priceof1 = Integer.valueOf(pricetemp.substring(0,pricetemp.length()-1));
                                aList.get(position).put("price",priceof1*qtybefore+"€");

                                notifyDataSetChanged();
                            }else{
                                qtybefore=0;
                            }
                            cartMap.put((aList.get(position).get("number")),qtybefore);
                            SharedPreferences SP = getContext().getSharedPreferences("cart", MODE_PRIVATE);
                            SharedPreferences.Editor editor = SP.edit();
                            Gson gson = new Gson();
                            String jsonText = gson.toJson(cartMap);
                            editor.putString("cart", jsonText);
                            editor.apply();
                            if(qtybefore==0)aList.remove(position);
                            notifyDataSetChanged();
                            mCallbacks.onButtonClicked();

                        }
                    });

                    removeall.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            System.out.println("clicked remove all at "+position);
                            int qtybefore=0;


                            //aList.remove(position);
                            cartMap.put(aList.get(position).get("number"),qtybefore);
                            SharedPreferences SP = getContext().getSharedPreferences("cart", MODE_PRIVATE);
                            SharedPreferences.Editor editor = SP.edit();
                            Gson gson = new Gson();
                            String jsonText = gson.toJson(cartMap);
                            editor.putString("cart", jsonText);
                            editor.apply();
                            aList.remove(position);
                            notifyDataSetChanged();
                            mCallbacks.onButtonClicked();
                        }
                    });
                    return v;
                }
            };
            ListView listview =(ListView)rootView.findViewById(R.id.cart);
            //String[] items = new String[] {"Item 1", "Item 2", "Item 3"};
            listview.setAdapter(adapter);


            /*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("clicked ",Integer.toString(i));
                    Log.d("price",prices.get(i).substring(0,(prices.get(i).length()-1)));

                    SharedPreferences SP = getContext().getSharedPreferences("cart", MODE_PRIVATE);
                    SharedPreferences.Editor editor = SP.edit();
                    String currentCart = SP.getString("cart",null);

                }
            });*/



        }else{
            empty.setText("Empty Cart");
        }






        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}