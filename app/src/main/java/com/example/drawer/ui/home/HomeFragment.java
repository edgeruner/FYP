package com.example.drawer.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.drawer.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseReference laundryRef;
    private DatabaseReference sportsRef;
    private DatabaseReference usersRef;
    private String currentUserFullName;
    private String currentUserEmail;
    private String savedSportHomeText;
    private TextView sportHome;
    private HashMap<String, String> machineDescriptions;
    private HashMap<String, String> sportDescriptions;


    private ImageView imageView;
    private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Glide.get(requireContext()).getRegistry().append(StorageReference.class, InputStream.class,
                new FirebaseImageLoader.Factory());

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initSportDescriptions();
        initMachineDescriptions();

        imageView = binding.imageView;

        laundryRef = FirebaseDatabase.getInstance().getReference().child("Laundry");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        sportsRef = FirebaseDatabase.getInstance().getReference().child("Sports");


        getCurrentUserFullName();
        currentUserEmail = getCurrentUserEmail();


        checkUserBookings();
        checkLaundryMachines();


        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
        StorageReference storageRef2 = FirebaseStorage.getInstance().getReference().child("draft").child("wait.png");

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                TimeZone timeZone = TimeZone.getDefault();
                int offsetMillis = timeZone.getOffset(currentTimeMillis);
                long localTimeMillis = currentTimeMillis + offsetMillis;
                long currentHour = (localTimeMillis / (60 * 60 * 1000)) % 24;
                Log.d("CurrentHour", "Current hour: " + currentHour);

                if ((currentHour >= 7 && currentHour < 9) || (currentHour >= 12 && currentHour < 14) || (currentHour >= 18 && currentHour < 20)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            storageRef.listAll().addOnSuccessListener(listResult -> {
                                int maxNumber = 0;
                                for (StorageReference item : listResult.getItems()) {
                                    String itemName = item.getName();
                                    String[] parts = itemName.split("\\.");

                                    if (parts.length > 0) {
                                        try {
                                            int number = Integer.parseInt(parts[0]);
                                            maxNumber = Math.max(maxNumber, number);
                                        } catch (NumberFormatException e) {
                                            // Handle if the filename is not a number
                                        }
                                    }
                                }

                                StorageReference maxImageRef = storageRef.child(maxNumber + ".jpg");
                                Glide.with(HomeFragment.this)
                                        .load(maxImageRef)
                                        .into(imageView);

                            }).addOnFailureListener(exception -> {
                                Log.e("FirebaseStorage", "Error retrieving image filenames: " + exception.getMessage());
                            });
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(HomeFragment.this)
                                    .load(storageRef2)
                                    .into(imageView);
                        }
                    });
                }
            }
        }, 0,  1000*600); // Check every 10 min





        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String getCurrentUserEmail() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getEmail();
        }
        return null;
    }
    private void getCurrentUserFullName() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                usersRef.orderByChild("userName").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                currentUserFullName = userSnapshot.child("fullName").getValue(String.class);
                                // Once we have the current user's full name, we can proceed to check for booked machines
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }
        }
    }

    private void checkLaundryMachines() {
        laundryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot machineSnapshot : dataSnapshot.getChildren()) {
                    String machineId = machineSnapshot.getKey();
                    String machineStatus = machineSnapshot.child("Status").getValue(String.class);
                    String machineUser = machineSnapshot.child("User").getValue(String.class);

                    if ("In Use".equals(machineStatus) && currentUserEmail.equals(machineUser)) {
                        CardView cardView = createCardView(machineId, false);
                        LinearLayout linearLayout = binding.linearLayoutLaundry;
                        linearLayout.addView(cardView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    private void checkUserBookings() {
        sportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot sportSnapshot : dataSnapshot.getChildren()) {
                    String sportId = sportSnapshot.getKey();
                    DataSnapshot peopleSnapshot = sportSnapshot.child("people");
                    for (DataSnapshot personSnapshot : peopleSnapshot.getChildren()) {
                        String personName = personSnapshot.getValue(String.class);
                        if (personName != null && personName.equals(currentUserFullName)) {
                            // Create CardView for the booked sport
                            CardView cardView = createCardView(sportId,true);
                            // Add CardView to LinearLayout
                            LinearLayout linearLayout = binding.linearLayoutSports;
                            linearLayout.addView(cardView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private CardView createCardView(String itemId, boolean isSport) {

        CardView cardView = new CardView(requireContext());

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(10, 10, 10, 10);
        cardView.setLayoutParams(layoutParams);

        cardView.setRadius(10);
        cardView.setElevation(8);

        TextView textView = new TextView(requireContext());
        String description;
        if (isSport) {
            description = sportDescriptions.get(itemId);
        } else {
            description = machineDescriptions.get(itemId);
        }
        if (description != null) {
            textView.setText(description);
        } else {
            textView.setText("Unknown");
        }
        textView.setPadding(16, 16, 16, 16);
        cardView.addView(textView);

        return cardView;
    }

    private void initMachineDescriptions() {

        machineDescriptions = new HashMap<>();
        machineDescriptions.put("A1L", "A1 Block First Floor Left");
        machineDescriptions.put("A1R", "A1 Block First Floor Right");
        machineDescriptions.put("A2L", "A2 Block First Floor Left");
        machineDescriptions.put("A2R", "A2 Block First Floor Right");
        machineDescriptions.put("B1L", "B1 Block First Floor Left");
        machineDescriptions.put("B1R", "B1 Block First Floor Right");

    }
    private void initSportDescriptions() {

        sportDescriptions = new HashMap<>();
        sportDescriptions.put("1C", "Monday Cricket 21:00 - 23:00");
        sportDescriptions.put("2B","Tuesday Basketball 21:00 - 23:00");
        sportDescriptions.put("2V","Tuesday Volleyball 19:00 - 21:00");
        sportDescriptions.put("3F","Wednesday Football 21:00 - 23:00");
        sportDescriptions.put("4B","Thursday Basketball 19:00 - 21:00");
        sportDescriptions.put("4V","Thursday Volleyball 21:00 - 23:00");
        sportDescriptions.put("5F","Friday Football 21:00 - 23:00");
        sportDescriptions.put("6C","Saturday Cricket 16:00 - 18:00");
        sportDescriptions.put("6V","Saturday Volleyball 18:00 - 21:00");
        sportDescriptions.put("6B","Saturday Basketball 21:00 - 23:00");
        sportDescriptions.put("7F","Sunday Football 18:00 - 23:00");

    }



}




