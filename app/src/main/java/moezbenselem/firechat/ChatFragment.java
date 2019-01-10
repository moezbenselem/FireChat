package moezbenselem.firechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
    DatabaseReference convRef;
    String current_user_id;
    FirebaseAuth mAuth;
    View mainView;
    DatabaseReference messageRef;
    RecyclerView recyclerConv;
    DatabaseReference userRef;

    public static class ConvViewHolder extends ViewHolder {
        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public void setMessage(String message, String type, String from, boolean isSeen) {
            TextView userStatusView = (TextView) this.mView.findViewById(R.id.item_status);
            System.out.println("from ==== " + from);
            String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (!type.equals("image")) {
                userStatusView.setText(message);
                userStatusView.setMaxLines(2);

                System.out.println("isSeen === " + isSeen);
                if (user.equals(from)) {
                    userStatusView.setTypeface(userStatusView.getTypeface(), 2);
                } else if (isSeen) {
                    userStatusView.setTypeface(userStatusView.getTypeface(), 0);
                } else {
                    userStatusView.setTypeface(userStatusView.getTypeface(), 1);
                }
            } else if (user.equals(from)) {
                userStatusView.setText("you sent a picture");
                userStatusView.setTypeface(userStatusView.getTypeface(), 2);
            } else {
                userStatusView.setText("sent you a picture");
                if (isSeen) {
                    userStatusView.setTypeface(userStatusView.getTypeface(), 0);
                } else {
                    userStatusView.setTypeface(userStatusView.getTypeface(), 1);
                }
            }
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        this.recyclerConv.setAdapter(new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(Conv.class, R.layout.user_layout, ConvViewHolder.class, this.convRef.orderByChild("timestamp")) {
            public ConvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return (ConvViewHolder) super.onCreateViewHolder(parent, viewType);
            }

            protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {
                final String list_user_id = getRef(i).getKey();
                ChatFragment.this.messageRef.child(list_user_id).limitToLast(1).addChildEventListener(new ChildEventListener() {
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
                ChatFragment.this.userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        if (dataSnapshot.hasChild("online")) {
                            convViewHolder.setUserOnline(dataSnapshot.child("online").getValue().toString());
                        }
                        convViewHolder.setName(userName);
                        convViewHolder.setUserImage(userThumb, ChatFragment.this.getContext());
                        convViewHolder.mView.setOnClickListener(new OnClickListener() {
                            public void onClick(View view) {
                                Intent chatIntent = new Intent(ChatFragment.this.getContext(), ChatActivity.class);
                                System.out.println("uid ="+ list_user_id);
                                System.out.println("name =="+ userName);
                                chatIntent.putExtra("uid", list_user_id);
                                chatIntent.putExtra("name", userName);
                                ChatFragment.this.startActivity(chatIntent);
                            }
                        });
                    }

                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mainView = inflater.inflate(R.layout.fragment_chat, container, false);
        this.recyclerConv = (RecyclerView) this.mainView.findViewById(R.id.recyclerConv);
        this.mAuth = FirebaseAuth.getInstance();
        this.current_user_id = this.mAuth.getCurrentUser().getUid();
        this.convRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(this.current_user_id);
        this.convRef.keepSynced(true);
        this.userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        this.messageRef = FirebaseDatabase.getInstance().getReference().child("messages").child(this.current_user_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        this.recyclerConv.setLayoutManager(linearLayoutManager);
        return this.mainView;
    }

    public void onStart() {
        super.onStart();

    }
}
