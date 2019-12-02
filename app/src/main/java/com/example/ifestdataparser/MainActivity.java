package com.example.ifestdataparser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "demo";
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseFirestore = FirebaseFirestore.getInstance();

        CollectionReference collectionReference = firebaseFirestore.collection("users");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot documentSnapshot: task.getResult()){
                        Log.d(TAG, "User: "+ documentSnapshot.getData().toString());
                        extractEvents(documentSnapshot);
//                        CollectionReference eventsReference = documentSnapshot.getReference().collection("events");
//                        Log.d(TAG, "extractEvents for user: "+documentSnapshot.getId());
//                        Log.d(TAG, "Events:_______________________");
//
//                        eventsReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                for (DocumentSnapshot eventDoc: task.getResult()){
//                                    Log.d(TAG, ""+eventDoc.toString());
//                                }
//                            }
//                        });
                    }
                }

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: "+ e.getMessage());
            }
        });

    }

    private void extractEvents(DocumentSnapshot documentSnapshot) {
        CollectionReference eventsReference = documentSnapshot.getReference().collection("events");
        Log.d(TAG, "extractEvents for user: "+documentSnapshot.getId());
        Log.d(TAG, "Events:_______________________");

        eventsReference.orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot eventDoc: task.getResult()){
                    Log.d(TAG, ""+eventDoc.toString());
                }
            }
        });
    }


}
