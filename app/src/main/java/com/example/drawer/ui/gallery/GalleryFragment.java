package com.example.drawer.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.drawer.PlayerRegistrationDialogFragment;
import com.example.drawer.R;
import com.example.drawer.databinding.FragmentGalleryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private Map<String, TextView> sportTextViewMap;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        initializeCardViews();
        initializeTextViews();
        setupSportTotalListeners();

        return root;
    }
    private void initializeTextViews() {
        sportTextViewMap = new HashMap<>();
        sportTextViewMap.put("1C", binding.show1C);
        sportTextViewMap.put("2V", binding.show2V);
        sportTextViewMap.put("2B", binding.show2B);
        sportTextViewMap.put("3F", binding.show3F);
        sportTextViewMap.put("4B", binding.show4B);
        sportTextViewMap.put("4V", binding.show4V);
        sportTextViewMap.put("5F", binding.show5F);
        sportTextViewMap.put("6C", binding.show6C);
        sportTextViewMap.put("6V", binding.show6V);
        sportTextViewMap.put("6B", binding.show6B);
        sportTextViewMap.put("7F", binding.show7F);
    }
    private void setupSportTotalListeners() {
        DatabaseReference sportsRef = FirebaseDatabase.getInstance().getReference("Sports");

        for (Map.Entry<String, TextView> entry : sportTextViewMap.entrySet()) {
            String sportKey = entry.getKey();
            TextView textView = entry.getValue();

            sportsRef.child(sportKey).child("total").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long total = dataSnapshot.getValue(Long.class);
                    if (total != null) {
                        textView.setText(String.valueOf(total));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    private void initializeCardViews() {
        // Initialize CardViews for each day and sport
        CardView monday1CardView = binding.monday1;
        CardView tuesday1CardView = binding.tuesday1;
        CardView tuesday2CardView = binding.tuesday2;
        CardView wednesday1CardView = binding.wednesday1;
        CardView thursday1CardView = binding.thursday1;
        CardView thursday2CardView = binding.thursday2;
        CardView friday1CardView = binding.friday1;
        CardView saturday1CardView = binding.saturday1;
        CardView saturday2CardView = binding.saturday2;
        CardView saturday3CardView = binding.saturday3;
        CardView sunday1CardView = binding.sunday1;

        // Set onClickListener for each CardView
        monday1CardView.setOnClickListener(view -> showRegistrationDialog("1C"));
        tuesday1CardView.setOnClickListener(view -> showRegistrationDialog("2V"));
        tuesday2CardView.setOnClickListener(view -> showRegistrationDialog("2B"));
        wednesday1CardView.setOnClickListener(view -> showRegistrationDialog("3F"));
        thursday1CardView.setOnClickListener(view -> showRegistrationDialog("4B"));
        thursday2CardView.setOnClickListener(view -> showRegistrationDialog("4V"));
        friday1CardView.setOnClickListener(view -> showRegistrationDialog("5F"));
        saturday1CardView.setOnClickListener(view -> showRegistrationDialog("6C"));
        saturday2CardView.setOnClickListener(view -> showRegistrationDialog("6V"));
        saturday3CardView.setOnClickListener(view -> showRegistrationDialog("6B"));
        sunday1CardView.setOnClickListener(view -> showRegistrationDialog("7F"));
    }

    private void showRegistrationDialog(String machineId) {
        // Create an instance of the dialog fragment
        PlayerRegistrationDialogFragment dialogFragment = new PlayerRegistrationDialogFragment();

        // Pass machineId to the dialog fragment
        Bundle args = new Bundle();
        args.putString("machineId", machineId);
        dialogFragment.setArguments(args);

        // Show the dialog fragment
        dialogFragment.show(getChildFragmentManager(), "PlayerRegistrationDialogFragment");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}