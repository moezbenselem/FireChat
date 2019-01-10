package moezbenselem.firechat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    FirebaseUser currentUser;

    Button btStaus,btImage;

    CircleImageView imageView;
    TextView tvDisplay,tvStatus;

    private StorageReference mStorageRef;

    public static int REQUEST_RESULT = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Profile Settings");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null)
        {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            userRef.child("online").setValue(true);
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();
        tvDisplay = (TextView) findViewById(R.id.display_name);
        tvStatus = (TextView) findViewById(R.id.description);
        imageView = (CircleImageView) findViewById(R.id.circleImageView);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String current_uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mDatabase.keepSynced(true);

        btStaus = (Button)findViewById(R.id.btStatus);
        btStaus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog cd = new CustomDialog(SettingsActivity.this,current_uid);
                cd.show();

            }
        });

        btImage = (Button) findViewById(R.id.btImage);
        btImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),REQUEST_RESULT);
                */

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);

            }
        });


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String  name = dataSnapshot.child("name").getValue().toString(),
                        status = dataSnapshot.child("status").getValue().toString(),
                        image = dataSnapshot.child("image").getValue().toString(),
                        thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                tvStatus.setText(status);
                tvDisplay.setText(name);
                if(image.equalsIgnoreCase("default")==false)
                    //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.male_avatar).into(imageView);

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.male_avatar).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.male_avatar).into(imageView);
                        }
                    });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                try {
                    File thumb_file = new File(resultUri.getPath());
                    Bitmap thumb_image = new Compressor(this).setMaxHeight(200).setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_file);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte =  baos.toByteArray();




                final StorageReference thumbfilePath = mStorageRef.child("profile_images").child(currentUser.getUid()+".jpg");

                final StorageReference filePath = mStorageRef.child("profile_images").child("thumbs").child(currentUser.getUid()+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful())
                        {
                            final String download_link = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumbfilePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumbDownloadLink = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){

                                        Map updateHashMap = new HashMap();
                                        updateHashMap.put("image",download_link);
                                        updateHashMap.put("thumb_image",thumbDownloadLink);
                                        mDatabase.updateChildren(updateHashMap);

                                    }

                                }
                            });




                        }


                    }
                });
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }




}
