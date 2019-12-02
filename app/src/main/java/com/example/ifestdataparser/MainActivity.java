package com.example.ifestdataparser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "demo";
    private static final int MY_PERMISSION = 0x005;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseFirestore = FirebaseFirestore.getInstance();

        CollectionReference collectionReference = firebaseFirestore.collection("users");

        final ArrayList<User> users = new ArrayList<>();

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot documentSnapshot: task.getResult()){
//                        Log.d(TAG, "User: "+ documentSnapshot.getData().toString());
                        ArrayList<Event> events = new ArrayList<Event>();
                        extractEvents(documentSnapshot, users, events);
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

    private void extractEvents(final DocumentSnapshot documentSnapshot, final ArrayList<User> users, final ArrayList<Event> events) {
        CollectionReference eventsReference = documentSnapshot.getReference().collection("events");
//        Log.d(TAG, "extractEvents for user: "+documentSnapshot.getId());
//        Log.d(TAG, "Events:_______________________");

        eventsReference.orderBy("time")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot eventDoc: task.getResult()){
                    Event event = new Event(eventDoc.getData());

                    events.add(event);
                }
                User user = new User(documentSnapshot.getId(), events);

                Log.d(TAG, "onComplete: "+user.toString());

                users.add(user);

                writeToFile(user);
            }
        });
    }

    private void writeToFile(User user) {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION);
        } else {

            String baseDir = android.os.Environment.getExternalStorageState();
            if (!Environment.MEDIA_MOUNTED.equals(baseDir)) {

                //If it isn't mounted - we can't write into it.
                return;
            }

//            Log.d(TAG, "writeToFile: " + baseDir + "/iFest/" + user.email + ".csv");

            File outputDir = new File(baseDir, "iFest");
            if (!outputDir.exists())outputDir.mkdir();

            File outputFile = new File(getExternalFilesDir(null), "iFest/"+user.email+".csv");


            try {
                outputFile.createNewFile();
                FileWriter fwriter = new FileWriter(outputFile, false);
                fwriter.append(user.email + "\r");
                fwriter.append("Time, Description" + "\r");
                for (Event event : user.events) {
                    fwriter.append(event.getTime() + "," + event.getDescription() + "\r");
                }
                fwriter.flush();
                fwriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    FileReader freader = new FileReader(outputFile);
                    String fileContent = "";

                    freader.read(CharBuffer.wrap(fileContent));

                    Log.d(TAG, "File ReAd:" + fileContent);
                    freader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
