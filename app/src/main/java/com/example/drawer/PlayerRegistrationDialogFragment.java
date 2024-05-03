package com.example.drawer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.drawer.databinding.SportDialogBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PlayerRegistrationDialogFragment extends DialogFragment {

    private DatabaseReference mDatabaseRef;
    private SportDialogBinding binding;
    private TextView totalTextView;
    private TextView registeredPeopleTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SportDialogBinding.inflate(inflater, container, false);

        // Initialize Firebase Database reference
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Sports");

        // Initialize the Register button
        binding.registerNew.setOnClickListener(v -> {
            // Update the total count for the selected sport
            updateTotalCount();

            // Close the dialog fragment
            dismiss();
        });
        registeredPeopleTextView = binding.registeredTextView;

        // Display the list of registered people
        displayListOfPeople();

        return binding.getRoot();
    }

    private void updateTotalCount() {
        // Get machineId from arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            String machineId = bundle.getString("machineId");
            String username = getCurrentUsername(); // Method to get the current user's username
            if (machineId != null && username != null) {
                // Retrieve the full name of the current user from the Users node
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
                usersRef.orderByChild("userName").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final boolean[] userExists = {false};
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String fullName = userSnapshot.child("fullName").getValue(String.class);
                                if (fullName != null) {
                                    // Check if the user's full name already exists in the list of people
                                    mDatabaseRef.child(machineId).child("people").orderByValue().equalTo(fullName).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!snapshot.exists()) {
                                                // User's full name not found in the list, proceed to update count and add the user
                                                mDatabaseRef.child(machineId).child("total").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            // Get the current total count
                                                            long currentTotal = snapshot.getValue(Long.class);

                                                            // Increment the total count by 1
                                                            long newTotal = currentTotal + 1;

                                                            // Set the new total count back to the database
                                                            mDatabaseRef.child(machineId).child("total").setValue(newTotal);

                                                            // Add the person to the list of people with their full name
                                                            mDatabaseRef.child(machineId).child("people").child(String.valueOf(currentTotal)).setValue(fullName);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        // Handle error
                                                    }
                                                });
                                            } else {
                                                // User's full name already exists in the list
                                                userExists[0] = true;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle error
                                        }
                                    });

                                    if (userExists[0]) {
                                        // User is already in the list, no need to proceed further
                                        break;
                                    }
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
        }
    }


//    private void updateTotalCount() {
//        // Get machineId from arguments
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            String machineId = bundle.getString("machineId");
//            String username = getCurrentUsername(); // Method to get the current user's username
//            if (machineId != null && username != null) {
//                // Retrieve the full name of the current user from the Users node
//                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
//                usersRef.orderByChild("userName").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                                String fullName = userSnapshot.child("fullName").getValue(String.class);
//                                if (fullName != null) {
//                                    // Update the total count for the selected sport
//                                    mDatabaseRef.child(machineId).child("total").addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            if (snapshot.exists()) {
//                                                // Get the current total count
//                                                long currentTotal = snapshot.getValue(Long.class);
//
//                                                // Increment the total count by 1
//                                                long newTotal = currentTotal + 1;
//
//                                                // Set the new total count back to the database
//                                                mDatabaseRef.child(machineId).child("total").setValue(newTotal);
//
//                                                // Add the person to the list of people with their full name
//                                                mDatabaseRef.child(machineId).child("people").child(String.valueOf(currentTotal)).setValue(fullName);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//                                            // Handle error
//                                        }
//                                    });
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        // Handle error
//                    }
//                });
//            }
//        }
//    }

    private void displayListOfPeople() {
        // Get machineId from arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            String machineId = bundle.getString("machineId");
            if (machineId != null) {
                mDatabaseRef.child(machineId).child("people").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Clear any existing text in the dialog
                        registeredPeopleTextView.setText("");

                        // Iterate through the list of people
                        for (DataSnapshot personSnapshot : dataSnapshot.getChildren()) {
                            String personName = personSnapshot.getValue(String.class);
                            // Append the person's name to the text view with a new line
                            registeredPeopleTextView.append(personName + "\n");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            }
        }
    }
    private String getCurrentUsername() {
        // Retrieve the current user's username from your authentication system
        // You need to implement this method based on your authentication mechanism
        // For example, if you are using Firebase Authentication:
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getEmail(); // Assuming email is used as the username
        }
        return null;
    }

}
