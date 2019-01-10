package moezbenselem.firechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class StartActivity extends AppCompatActivity {

    EditText etInsc_nom, etInsc_email, etInsc_psw, etlogin, etPsw;
    int index;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    DatabaseReference mDatabase;
    DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        try {
            mAuth = FirebaseAuth.getInstance();
            progressDialog = new ProgressDialog(this);

            userReference = FirebaseDatabase.getInstance().getReference().child("Users");

            etInsc_nom = (EditText) findViewById(R.id.insc_nom);
            etInsc_email = (EditText) findViewById(R.id.insc_email);
            etInsc_psw = (EditText) findViewById(R.id.insc_psw);

            final RelativeLayout loginLayout = (RelativeLayout) findViewById(R.id.login_layout);
            final RelativeLayout inscLayout = (RelativeLayout) findViewById(R.id.insc_layout);
            etlogin = (EditText) loginLayout.findViewById(R.id.editText);
            etPsw = (EditText) loginLayout.findViewById(R.id.editText2);


            index = 0;
            final Button btLogin = (Button) findViewById(R.id.button2);
            final Button btInsc = (Button) findViewById(R.id.button3);
            final Button btValidLogin = (Button) findViewById(R.id.btValidLogin);
            final Button btValidInsc = (Button) findViewById(R.id.bt_insc_valid);

            btValidLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String login = etlogin.getText().toString(),
                            psw = etPsw.getText().toString();


                    System.out.println("login " + login);
                    System.out.println("password " + psw);

                    if (isValidEmail(etlogin.getText().toString()) == false) {
                        Toast.makeText(getApplicationContext(), "Email non valide !", Toast.LENGTH_SHORT).show();
                    } else if (etPsw.getText().toString().length() < 8) {
                        Toast.makeText(getApplicationContext(), "Mot de passe doit être formé d'au moins 8 caractères !", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.setTitle("Connexion en cours");
                        progressDialog.setMessage("Veuillez patienter connexion en cours !");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        loginUSer(login, psw);
                    }
                }
            });

            btValidInsc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String nom = etInsc_nom.getText().toString(),
                            email = etInsc_email.getText().toString(),
                            psw = etInsc_psw.getText().toString();

                    System.out.println("email " + email);
                    System.out.println("nom " + nom);
                    System.out.println("password " + psw);

                    if (isValidEmail(etInsc_email.getText().toString()) == false) {
                        Toast.makeText(getApplicationContext(), "Email non valide !", Toast.LENGTH_SHORT).show();
                    } else if (etInsc_psw.getText().toString().length() < 8) {
                        Toast.makeText(getApplicationContext(), "Mot de passe doit être formé d'au moins 8 caractères !", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.setTitle("Inscription en cours");
                        progressDialog.setMessage("Veuillez patienter inscription en cours !");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        registerUSer(email, nom, psw);
                    }
                }
            });

            btLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    loginLayout.setVisibility(View.VISIBLE);
                    inscLayout.setVisibility(View.GONE);

                    btLogin.setVisibility(View.GONE);
                    btInsc.setVisibility(View.VISIBLE);

                }
            });

            btInsc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    loginLayout.setVisibility(View.GONE);
                    inscLayout.setVisibility(View.VISIBLE);

                    btLogin.setVisibility(View.VISIBLE);
                    btInsc.setVisibility(View.GONE);

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void registerUSer(String email, final String nom, String password) {

        System.out.println("==== fom function ==== ");
        System.out.println("email " + email);
        System.out.println("nom " + nom);
        System.out.println("password " + password);


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(StartActivity.this, "Registration Failed !\n"+task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            progressDialog.hide();
                            System.out.println(task.getException().getMessage());
                        } else {

                            try {
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String UserId = currentUser.getUid();

                                String device_token = FirebaseInstanceId.getInstance().getToken();

                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UserId);

                                HashMap<String, String> userMap = new HashMap<String, String>();

                                userMap.put("name", nom);
                                userMap.put("status", "I am chatting , join me !");
                                userMap.put("image", "default");
                                userMap.put("thumb_image", "default");

                                mDatabase.setValue(userMap);
                                mDatabase.child("device_token").setValue(device_token);

                                progressDialog.dismiss();
                                Intent toMain = new Intent(StartActivity.this, MainActivity.class);
                                startActivity(toMain);
                                finish();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        // ...
                    }
                });

    }

    public void loginUSer(String login, String password) {

        mAuth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            System.out.println("signInWithEmail:failed" + task.getException());
                            progressDialog.hide();
                            Toast.makeText(StartActivity.this, "Connexion Echouée !",
                                    Toast.LENGTH_LONG).show();
                        } else {

                            String onlide_user_id = mAuth.getCurrentUser().getUid();
                            String device_token = FirebaseInstanceId.getInstance().getToken();

                            userReference.child(onlide_user_id).child("device_token").setValue(device_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressDialog.dismiss();
                                    Intent toMain = new Intent(StartActivity.this, MainActivity.class);
                                    startActivity(toMain);
                                    finish();

                                }
                            });



                        }

                        // ...
                    }
                });

    }

    public final static boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



}

