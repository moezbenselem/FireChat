package moezbenselem.firechat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    TextView tvDisplay, tvStatus, tvFriends;
    Button btRequest,btDecline;
    ImageView imageView;
    String friends_state ="not_friend";
    DatabaseReference friendsDatabaseRef;
    DatabaseReference userDatabaseRef;
    DatabaseReference requestDatabaseRef;
    DatabaseReference notifDatabaseRef;
    String status , image;
    FirebaseUser currentUser;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null)
        {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            userRef.child("online").setValue(true);
        }

        try {

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading User Data");
            progressDialog.setMessage("Please wait while loading the data !");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            final String name = getIntent().getExtras().getString("name");
            getSupportActionBar().setTitle("Profile : " + name);
            final String uid = getIntent().getExtras().getString("uid");
            System.out.println("uid from profile ==" +uid);

            tvDisplay = (TextView) findViewById(R.id.profile_tvName);
            tvStatus = (TextView) findViewById(R.id.profile_tvStatus);
            tvFriends = (TextView) findViewById(R.id.profile_tvFriends);
            btRequest = (Button) findViewById(R.id.profile_btRequest);
            btDecline = (Button) findViewById(R.id.profile_btDecline);
            btDecline.setVisibility(View.INVISIBLE);
            imageView = (ImageView) findViewById(R.id.profile_imageView);

            //status = getIntent().getExtras().getString("status");
            //image = getIntent().getExtras().getString("image");

            userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            friendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
            requestDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_request");
            notifDatabaseRef = FirebaseDatabase.getInstance().getReference().child("notifications");

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            requestDatabaseRef.child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if(dataSnapshot.hasChild(uid)){
                        progressDialog.show();
                        String req_type = dataSnapshot.child(uid).child("request_type").getValue().toString();

                        if(req_type.equals("recieved")){

                            System.out.println("you have recieved a friend request !");
                            friends_state = "req_recieved";
                            btRequest.setText("ACCEPT REQUEST");
                            btDecline.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }else if(req_type.equals("sent")){
                            System.out.println("you have sent a friend request !");
                            friends_state = "req_sent";
                            btRequest.setText("Cancel REQUEST");
                            progressDialog.dismiss();
                        }

                    }
                    else{

                        friendsDatabaseRef.child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                progressDialog.show();
                                if (dataSnapshot.hasChild(uid)){

                                    btRequest.setEnabled(true);
                                    friends_state ="friends";
                                    System.out.println("this is a friend!");
                                    btDecline.setVisibility(View.INVISIBLE);
                                    btRequest.setText("Unfriend "+name);
                                    progressDialog.dismiss();
                                }
                                else
                                    progressDialog.dismiss();

                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                progressDialog.show();
                                btRequest.setEnabled(true);
                                friends_state = "not_friend";
                                System.out.println("this is not a friend!");
                                btDecline.setVisibility(View.INVISIBLE);
                                btRequest.setText("Send Request");
                                progressDialog.dismiss();
                            }


                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    progressDialog.show();
                    btRequest.setEnabled(true);
                    friends_state = "not_friend";
                    System.out.println("this is not a friend!");
                    btDecline.setVisibility(View.INVISIBLE);
                    btRequest.setText("Send Request");
                    progressDialog.dismiss();

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            userDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    //progressDialog.show();
                    image = dataSnapshot.child("image").getValue().toString();
                    status = dataSnapshot.child("status").getValue().toString();

                    tvDisplay.setText(name);
                    tvStatus.setText(status);
                    //Picasso.with(this).load(image).placeholder(R.drawable.male_avatar).into(imageView);
                    Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.male_avatar).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.male_avatar).into(imageView);
                        }
                    });
                    System.out.println("status == "+status);
                    System.out.println("image == "+image);
                    requestDatabaseRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild(uid)){
                                progressDialog.show();
                                String req_type = dataSnapshot.child(uid).child("request_type").getValue().toString();

                                if(req_type.equals("recieved")){

                                    System.out.println("you have recieved a friend request !");
                                    friends_state = "req_recieved";
                                    btRequest.setText("ACCEPT REQUEST");
                                    btDecline.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                }else if(req_type.equals("sent")){
                                    System.out.println("you have sent a friend request !");
                                    friends_state = "req_sent";
                                    btRequest.setText("Cancel REQUEST");
                                    progressDialog.dismiss();
                                }



                            }else{

                                friendsDatabaseRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        progressDialog.show();
                                        if (dataSnapshot.hasChild(uid)){

                                            btRequest.setEnabled(true);
                                            friends_state ="friends";
                                            System.out.println("this is a friend!");
                                            btDecline.setVisibility(View.INVISIBLE);
                                            btRequest.setText("Unfriend "+name);
                                            progressDialog.dismiss();
                                        }
                                        else
                                            progressDialog.dismiss();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {



                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });



            btDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(friends_state.equalsIgnoreCase("req_recieved")){

                        final String currentDate = getDateTime();


                                        requestDatabaseRef.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                requestDatabaseRef.child(uid).child(currentUser.getUid()).removeValue();
                                                Snackbar.make(btRequest, "Friend Request Declined !", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                                btRequest.setEnabled(true);
                                                btRequest.setText("Send Request");
                                                friends_state ="not_friend";
                                                btDecline.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                    }

            });

            btRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    btRequest.setEnabled(false);
                    if(friends_state.equalsIgnoreCase("not_friend")){

                        requestDatabaseRef.child(currentUser.getUid()).child(uid).child("request_type").setValue("sent")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        requestDatabaseRef.child(uid).child(currentUser.getUid()).child("request_type").setValue("recieved")
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        Snackbar.make(btRequest, "Friend Request Sent !", Snackbar.LENGTH_LONG)
                                                                .setAction("Action", null).show();
                                                        btRequest.setEnabled(true);
                                                        friends_state ="req_sent";
                                                        btRequest.setText("Cancel Request");

                                                        HashMap<String,String> notifData = new HashMap<String, String>();
                                                        notifData.put("from",currentUser.getUid());
                                                        notifData.put("type","request");
                                                        notifDatabaseRef.child(uid).push().setValue(notifData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });

                                                    }
                                                });

                                    }
                                });
                    }

                    if(friends_state.equalsIgnoreCase("req_sent")){


                        requestDatabaseRef.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                requestDatabaseRef.child(uid).child(currentUser.getUid()).removeValue();
                                Snackbar.make(btRequest, "Friend Request Cancelled !", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                btRequest.setEnabled(true);
                                friends_state ="not_friend";
                                btRequest.setText("SEND REQUEST");
                            }
                        });


                    }

                    if(friends_state.equalsIgnoreCase("friends")){


                        friendsDatabaseRef.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                friendsDatabaseRef.child(uid).child(currentUser.getUid()).removeValue();
                                Snackbar.make(btRequest, name+" UNFRIENDED !", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                btRequest.setEnabled(true);
                                friends_state ="not_friend";
                                btRequest.setText("SEND REQUEST");
                            }
                        });


                    }

                    if(friends_state.equalsIgnoreCase("req_recieved")){

                        final String currentDate = getDateTime();
                        friendsDatabaseRef.child(currentUser.getUid()).child(uid).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                friendsDatabaseRef.child(uid).child(currentUser.getUid()).child(("date")).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        requestDatabaseRef.child(currentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                requestDatabaseRef.child(uid).child(currentUser.getUid()).removeValue();
                                                Snackbar.make(btRequest, "Friend Request Accepted !", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                                btRequest.setEnabled(true);
                                                friends_state ="friends";
                                                btRequest.setText("Unfriend "+name);

                                            }
                                        });

                                    }
                                });

                            }

                        });
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }
}
