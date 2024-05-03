package com.example.drawer.ui.room;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.drawer.R;

import com.example.drawer.databinding.FragmentRoomBinding;
import com.example.drawer.ui.room.RoomViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class RoomFragment extends Fragment {

    Switch ipSwitch1;
    Switch ipSwitch2;

    Switch ipSwitch3;

    private FragmentRoomBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRoomBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        ipSwitch1 = binding.ipSwitch1;
        ipSwitch2 = binding.ipSwitch2;
        ipSwitch3 = binding.ipSwitch3;

        DatabaseReference status =  FirebaseDatabase.getInstance().getReference("STATUS");
        status.setValue("OFF");

        // lamp
        ipSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                String ipaddress = "192.168.185.133"; //for hotspot

                if (isChecked) {
                    sendRequest( ipaddress, "on");
                } else {
                    sendRequest( ipaddress,"off");
                }
            }
        });

        // relay 2
        ipSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                String ipaddress = "10.129.1.71"; for wifi
                String ipaddress = "192.168.185.178"; //for hotspot
                if (isChecked) {
                    sendRequest( ipaddress,"on2");
                } else {
                    sendRequest( ipaddress,"off2");
                }
            }
        });

        //led
        ipSwitch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                String ipaddress = "10.129.1.70"; for wifi
                String ipaddress = "192.168.185.125"; //for hotspot

                if (isChecked) {
//                    sendRequest( ipaddress, "on1");
                    status.setValue("ON");
                } else {
//                    sendRequest( ipaddress,"off1");
                    status.setValue("OFF");
                }
            }
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void sendRequest(String ipAddress, String command) {
        String url = "http://" + ipAddress + "/" + command;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Status", response.trim());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Status", error.toString());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }
}
