package com.example.drawer.ui.slideshow;

import com.example.drawer.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

//import com.example.drawer.StartDialogFragment;
import com.example.drawer.databinding.FragmentSlideshowBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class SlideshowFragment extends Fragment{
    private FragmentSlideshowBinding binding;
    private Context context;
    private TextView time1;
    private TextView time2;
    private TextView time3;
    private TextView time4;
    private TextView time5;
    private TextView time6;

    private DatabaseReference laundryRef1;
    private DatabaseReference laundryRef2;
    private DatabaseReference laundryRef3;
    private DatabaseReference laundryRef4;
    private DatabaseReference laundryRef5;
    private DatabaseReference laundryRef6;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final CardView laundryA1LCardView = binding.laundryA1L;
        final CardView laundryA1RCardView = binding.laundryA1R;
        final CardView laundryA2LCardView = binding.laundryA2L;
        final CardView laundryA2RCardView = binding.laundryA2R;
        final CardView laundryB1LCardView = binding.laundryB1L;
        final CardView laundryB1RCardView = binding.laundryB1R;

        final CardView check1 = binding.indicator1;
        final CardView check2 = binding.indicator2;
        final CardView check3 = binding.indicator3;
        final CardView check4 = binding.indicator4;
        final CardView check5 = binding.indicator5;
        final CardView check6 = binding.indicator6;

        time1 = binding.A1LTimer;
        time2 = binding.A1RTimer;
        time3 = binding.A2LTimer;
        time4 = binding.A2RTimer;
        time5 = binding.B1LTimer;
        time6 = binding.B1RTimer;


        DatabaseReference laundryRef = FirebaseDatabase.getInstance().getReference("Laundry");
        laundryRef1 = FirebaseDatabase.getInstance().getReference("Laundry").child("A1L");
        laundryRef2 = FirebaseDatabase.getInstance().getReference("Laundry").child("A1R");
        laundryRef3 = FirebaseDatabase.getInstance().getReference("Laundry").child("A2L");
        laundryRef4 = FirebaseDatabase.getInstance().getReference("Laundry").child("A2R");
        laundryRef5 = FirebaseDatabase.getInstance().getReference("Laundry").child("B1L");
        laundryRef6 = FirebaseDatabase.getInstance().getReference("Laundry").child("B1R");


        setupMachineOnClickListener(time1, laundryA1LCardView, laundryRef1);
        setupMachineStatusListener(time1, laundryRef1);

        setupMachineOnClickListener(time2, laundryA1RCardView, laundryRef2);
        setupMachineStatusListener(time2, laundryRef2);

        setupMachineOnClickListener(time3, laundryA2LCardView, laundryRef3);
        setupMachineStatusListener(time3, laundryRef3);

        setupMachineOnClickListener(time4, laundryA2RCardView, laundryRef4);
        setupMachineStatusListener(time4, laundryRef4);

        setupMachineOnClickListener(time5, laundryB1LCardView, laundryRef5);
        setupMachineStatusListener(time5, laundryRef5);

        setupMachineOnClickListener(time6, laundryB1RCardView, laundryRef6);
        setupMachineStatusListener(time6, laundryRef6);






        laundryRef.child("A1L").child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                updateLaundryStatus(check1, status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
        laundryRef.child("A1R").child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                updateLaundryStatus(check2, status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        laundryRef.child("A2L").child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                updateLaundryStatus(check3, status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
        laundryRef.child("A2R").child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                updateLaundryStatus(check4, status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        laundryRef.child("B1L").child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                updateLaundryStatus(check5, status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
        laundryRef.child("B1R").child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                updateLaundryStatus(check6, status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

        return root;
    }


    private void updateLaundryStatus(CardView cardView, String status) {
        if (context != null) {
            if ("Available".equals(status)) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupMachineOnClickListener(TextView textView, CardView cardView, DatabaseReference laundryRef) {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                laundryRef.child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.getValue(String.class);
                        if (status != null) {
                            if (status.equals("Available")) {
                                laundryRef.child("Status").setValue("In Use");
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmailCur = currentUser.getEmail();
                                laundryRef.child("User").setValue(userEmailCur);
                                updateDatabase(textView,laundryRef);

                            } else if (status.equals("In Use")) {
                                Toast.makeText(getContext(), "This machine is currently in use!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }
        });
    }

    private void setupMachineStatusListener(TextView textView,DatabaseReference laundryRef) {
        laundryRef.child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if (status != null && status.equals("In Use")) {
                    long currentTimeMillis = System.currentTimeMillis();

                    laundryRef.child("Time").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Long futureTimeSeconds = dataSnapshot.getValue(Long.class);
                            if (futureTimeSeconds != null) {
                                long futureTimeMillis = futureTimeSeconds * 1000; // Convert seconds to milliseconds
                                startCountdownTimer(laundryRef, textView, currentTimeMillis, futureTimeMillis);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void updateDatabase(TextView textView, DatabaseReference laundryRef) {
        long currentTimeMillis = System.currentTimeMillis();
        long futureTimeMillis = currentTimeMillis + 90000;
        long futureTimeSeconds = futureTimeMillis / 1000;
        laundryRef.child("Time").setValue(futureTimeSeconds);
        startCountdownTimer(laundryRef, textView, currentTimeMillis, futureTimeMillis);
    }

    private void startCountdownTimer(DatabaseReference laundryRef, TextView textView, long currentTimeMillis, long futureTimeMillis) {
        CountDownTimer timer = new CountDownTimer(futureTimeMillis - currentTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long remainingTimeMillis = millisUntilFinished;
                int seconds = (int) (remainingTimeMillis / 1000);
                String timeLeftFormatted = String.format("%02d:%02d", seconds / 60, seconds % 60);
                textView.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                laundryRef.child("Status").setValue("Available");
                laundryRef.child("Time").removeValue();
                laundryRef.child("User").setValue("None");
            }
        };
        timer.start();
    }




}