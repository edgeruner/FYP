package com.example.drawer;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.drawer.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_start, null);
        // Initialize UI elements and set click listeners
        Button startButton = dialogView.findViewById(R.id.startButton);
        TextView remainingTimeTextView = dialogView.findViewById(R.id.remainingTimeTextView);
        String machineId = getArguments().getString("machineId");
        DatabaseReference laundryRef = FirebaseDatabase.getInstance().getReference("Laundry").child(machineId);
        laundryRef.child("Time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long timeSetInMillis = snapshot.getValue(Long.class);
                if (timeSetInMillis != null) {
                    // Get the current time in milliseconds
                    long currentTimeInMillis = System.currentTimeMillis();

                    long timeDifferenceInSeconds = timeSetInMillis - currentTimeInMillis/1000;
                    if (timeDifferenceInSeconds < 0){
                        laundryRef.child("Status").setValue("Available");

                        // Remove the "Time" node associated with the laundry machine
                        laundryRef.child("Time").removeValue();

                        laundryRef.child("By").removeValue();
                    }

                    remainingTimeTextView.setText("Remaining Time: " + timeDifferenceInSeconds   + " seconds");
                } else {
                    remainingTimeTextView.setText("Remaining Time: N/A");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the machineId from arguments
                String machineId = getArguments().getString("machineId");

                // Get a reference to the specific laundry machine node in the database
                DatabaseReference laundryRef = FirebaseDatabase.getInstance().getReference("Laundry").child(machineId);

                // Retrieve the status of the laundry machine
                laundryRef.child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.getValue(String.class);
                        if (status != null && status.equals("In Use")) {
                            // Laundry machine is already in use, display an alert
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("This laundry machine is already booked.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Close the dialog
                                            dialog.dismiss();
                                        }
                                    });
                            // Create and show the AlertDialog
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            // Laundry machine is available, proceed to update the database
                            updateDatabase(machineId);
                            dismiss(); // Close the dialog after updating the database
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
            }


        });
        builder.setView(dialogView);
        return builder.create();
    }
    private void updateDatabase(String machineId) {
        DatabaseReference laundryRef = FirebaseDatabase.getInstance().getReference("Laundry").child(machineId);
        // Get the current time in milliseconds
        long currentTimeMillis = System.currentTimeMillis();

        // Calculate the future time when the machine should become available again
        long futureTimeMillis = currentTimeMillis + 30000; // 10 seconds in milliseconds

        // Convert the future time to seconds
        long futureTimeSeconds = futureTimeMillis / 1000;

        // Update the status in the database to "In Use"
        laundryRef.child("Status").setValue("In Use");

        // Update the future time in the database under the "Time" child
        laundryRef.child("Time").setValue(futureTimeSeconds);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmailCur = currentUser.getEmail();

        laundryRef.child("By").setValue(userEmailCur);
        // Start a CountDownTimer to handle the transition from "In Use" to "Available"
        CountDownTimer timer = new CountDownTimer(futureTimeMillis - currentTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Nothing to do here; we are waiting for the timer to finish
            }

            @Override
            public void onFinish() {
                // Timer finished, update the status in the database to "Available"
                laundryRef.child("Status").setValue("Available");

                // Remove the "Time" node associated with the laundry machine
                laundryRef.child("Time").removeValue();

                laundryRef.child("By").removeValue();
            }
        };
        timer.start();
    }

}
