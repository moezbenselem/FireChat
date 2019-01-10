package moezbenselem.firechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        getSupportActionBar().setTitle("All Users");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null)
        {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            userRef.child("online").setValue(true);
        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_users);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }




    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User,usersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<User, usersViewHolder>(User.class,
                        R.layout.user_layout,
                        usersViewHolder.class,
                        mDatabaseReference) {
                    @Override
                    protected void populateViewHolder(usersViewHolder viewHolder, final User model, int position) {

                        viewHolder.tvName.setText(model.getName());
                        viewHolder.tvStatus.setText(model.getStatus());
                        CircleImageView circleImageView = viewHolder.imageView;
                        Picasso.with(AllUsersActivity.this).load(model.getThumb_image()).placeholder(R.drawable.male_avatar).into(circleImageView);

                        final String uid = getRef(position).getKey();

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent toProfile = new Intent(AllUsersActivity.this,ProfileActivity.class);
                                toProfile.putExtra("uid",uid);
                                toProfile.putExtra("name",model.getName());
                                toProfile.putExtra("status",model.getStatus());
                                toProfile.putExtra("image",model.getImage());
                                startActivity(toProfile);
                            }
                        });

                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class usersViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView tvName,tvStatus;
        CircleImageView imageView;

        public usersViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            tvName = (TextView)itemView.findViewById(R.id.item_display_name);
            tvStatus = (TextView)itemView.findViewById(R.id.item_status);
            imageView = (CircleImageView) itemView.findViewById(R.id.item_image);

        }

    }

}
