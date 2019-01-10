package moezbenselem.firechat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    DatabaseReference reqRef;
    String current_user_id;
    FirebaseAuth mAuth;
    View mainView;
    RecyclerView recyclerReq;
    DatabaseReference userRef;
    public static Context context;


    public RequestsFragment() {// Required empty public constructor
    }

    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        this.recyclerConv.setAdapter(new FirebaseRecyclerAdapter<Conv, ChatFragment.ConvViewHolder>(Conv.class, R.layout.user_layout, ChatFragment.ConvViewHolder.class, this.convRef.orderByChild("timestamp")) {
            public ChatFragment.ConvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return (ChatFragment.ConvViewHolder) super.onCreateViewHolder(parent, viewType);
            }

            protected void populateViewHolder(final ChatFragment.ConvViewHolder convViewHolder, final Conv conv, int i) {
                final String list_user_id = getRef(i).getKey();
                RequestsFragment.this.reqRef.child(list_user_id).limitToLast(1).addChildEventListener(new ChildEventListener() {
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        System.out.println("s ===="+s);
                        String data = dataSnapshot.child("message").getValue().toString();
                        String type = dataSnapshot.child("type").getValue().toString();
                        String from = dataSnapshot.child("from").getValue().toString();
                        if (type.equals("text")) {
                            convViewHolder.setMessage(data, type, from, conv.isSeen());
                        } else if (type.equals("image")) {
                            convViewHolder.setMessage(data, type, from, conv.isSeen());
                        }
                    }

                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                RequestsFragment.this.userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        if (dataSnapshot.hasChild("online")) {
                            convViewHolder.setUserOnline(dataSnapshot.child("online").getValue().toString());
                        }
                        convViewHolder.setName(userName);
                        convViewHolder.setUserImage(userThumb, RequestsFragment.this.getContext());
                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Intent chatIntent = new Intent(RequestsFragment.this.getContext(), ChatActivity.class);
                                System.out.println("uid ="+ list_user_id);
                                System.out.println("name =="+ userName);
                                chatIntent.putExtra("uid", list_user_id);
                                chatIntent.putExtra("name", userName);
                                RequestsFragment.this.startActivity(chatIntent);
                            }
                        });
                    }

                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        context = getContext();
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }




    /*public static class ReqViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ReqViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public void setTime(String temp) {
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            long time = Long.parseLong(temp);

            String lastTime = getTimeAgo.getTimeAgo(time,context);
            TextView tvOnline = (TextView) this.mView.findViewById(R.id.item_status);
            tvOnline.setText(lastTime);

        }

        public void setName(String name) {
            ((TextView) this.mView.findViewById(R.id.item_display_name)).setText(name);
        }

        public void setUserImage(String thumb_image, Context ctx) {
            Picasso.with(ctx).load(thumb_image).placeholder((int) R.drawable.male_avatar).into((CircleImageView) this.mView.findViewById(R.id.item_image));
        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = (ImageView) this.mView.findViewById(R.id.online_icon);
            if (online_status.equals("true")) {
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }*/


}
