package com.example.eshop;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;


public class UserFragment extends Fragment {

    Button logout;
    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        logout = rootView.findViewById(R.id.logout);
        SharedPreferences SP = getContext().getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences SP2 = getContext().getSharedPreferences("cart", MODE_PRIVATE);

        final SharedPreferences.Editor editor = SP.edit();
        final SharedPreferences.Editor editor2 = SP2.edit();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.remove("user");
                editor.apply();
                editor2.remove("cart");
                editor2.apply();
                Intent login = new Intent(getContext(), LoginActivity.class);
                startActivity(login);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}