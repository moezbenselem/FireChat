package moezbenselem.firechat;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    View mainView;
    FirebaseAuth mAuth;
    DatabaseReference friendsRef,userRef;
    String current_user_id;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Friend, FriendsViewHolder> adapter;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            mAuth = FirebaseAuth.getInstance();
            current_user_id = mAuth.getCurrentUser().getUid();
            friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_id);
            userRef = FirebaseDatabase.getInstance().getReference().child("Users");


            recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_friends);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            adapter = new FirebaseRecyclerAdapter<Friend, FriendsViewHolder>(
                    Friend.class,
                    R.layout.user_layout,
                    FriendsViewHolder.class,
                    friendsRef
            ) {
                @Override
                protected void populateViewHolder(final FriendsViewHolder viewHolder, Friend model, int position) {

                    viewHolder.tvDate.setText(model.date);
                    final String list_user_id = getRef(position).getKey();

                    userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final String name = dataSnapshot.child("name").getValue().toString();
                            final String thumb = dataSnapshot.child("thumb_image").getValue().toString();
                            String online = dataSnapshot.child("online").getValue().toString();
                            viewHolder.tvName.setText(name);
                            if(dataSnapshot.hasChild("online"))
                            {
                                if(online.equalsIgnoreCase("true")){

                                    viewHolder.onlineIcon.setVisibility(View.VISIBLE);

                                }else
                                    viewHolder.onlineIcon.setVisibility(View.INVISIBLE);
                            }
                            CircleImageView circleImageView = viewHolder.imageView;
                            Picasso.with(getContext()).load(thumb).placeholder(R.drawable.male_avatar).into(circleImageView);

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    CharSequence options[] = new CharSequence[]{"Open Profile","Send Message"};
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Select option");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if(which == 0){
                                                Intent toProfile = new Intent(getContext(),ProfileActivity.class);
                                                toProfile.putExtra("uid",list_user_id);
                                                toProfile.putExtra("name",name);
                                                toProfile.putExtra("image",thumb);
                                                startActivity(toProfile);

                                            }
                                            else if(which ==1){

                                                Intent toChat = new Intent(getContext(),ChatActivity.class);
                                                toChat.putExtra("uid",list_user_id);
                                                toChat.putExtra("name",name);
                                                startActivity(toChat);

                                            }

                                        }
                                    });

                                    builder.show();

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            };
        }catch (Exception e){
            e.printStackTrace();
        }
        recyclerView.setAdapter(adapter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView =  inflater.inflate(R.layout.fragment_friends, container, false);



        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();



    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView tvName,tvDate;
        CircleImageView imageView;
        ImageView onlineIcon;

        public FriendsViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            tvName = (TextView)itemView.findViewById(R.id.item_display_name);
            tvDate = (TextView)itemView.findViewById(R.id.item_status);
            imageView = (CircleImageView) itemView.findViewById(R.id.item_image);
            onlineIcon = (ImageView) itemView.findViewById(R.id.online_icon);


        }

    }

}
