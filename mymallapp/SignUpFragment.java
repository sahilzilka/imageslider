 package com.example.mymallapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpFragment extends Fragment {


    public SignUpFragment() {
        // Required empty public constructor
    }

    private TextView alreadyhaveanaccount;
    private FrameLayout parentframeLayout;

    private EditText email;
    private EditText Fullname;
    private EditText password;
    private EditText confirmpassword;
    private ImageButton closebtn;
    private Button signupbtn;
    private ProgressBar progressbar;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        alreadyhaveanaccount = view.findViewById(R.id.sign_in_forgot_pass);
        parentframeLayout = getActivity().findViewById(R.id.reg_frame_layout);

        // edittext views
        email = view.findViewById(R.id.sign_up_email);
        Fullname = view.findViewById(R.id.sign_up_name);
        password = view.findViewById(R.id.sign_up_password);
        confirmpassword = view.findViewById(R.id.sign_up_confirm_password);

        // buttons
        closebtn = view.findViewById(R.id.sign_up_close_btn);
        signupbtn  = view.findViewById(R.id.sign_up_btn);

        //progress bar
        progressbar = view.findViewById(R.id.sign_up_progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alreadyhaveanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkinputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Fullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkinputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkinputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkinputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkemailandpassword();
            }
        });
    }

    private void checkemailandpassword() {
        if(email.getText().toString().matches(emailPattern)) {
            if(password.length()>=8){
                //this is send the data to firebase
                progressbar.setVisibility(View.VISIBLE);
                signupbtn.setEnabled(false);
                signupbtn.setTextColor(Color.rgb(151,153,155));
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent mainIntent = new Intent(getActivity(),MainActivity.class);
                                    startActivity(mainIntent);
                                    getActivity().finish();
                                }else{
                                    progressbar.setVisibility(View.INVISIBLE);
                                    signupbtn.setEnabled(true);
                                    signupbtn.setTextColor(Color.rgb(255,255,255));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(),error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else{

            }

        }else{

        }
    }

    private void checkinputs() {
        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty((Fullname.getText()))){
                if(!TextUtils.isEmpty(password.getText()) && password.length() >=10){
                    if(!TextUtils.isEmpty(confirmpassword.getText())){
                        signupbtn.setEnabled(true);
                        signupbtn.setTextColor(Color.WHITE);
                    }else{
                        signupbtn.setEnabled(false);
                        signupbtn.setTextColor(Color.rgb(50,255,255));
                    }
                }else{
                    signupbtn.setEnabled(false);
                    signupbtn.setTextColor(Color.rgb(50,255,255));
                }
            }else{
                signupbtn.setEnabled(false);
                signupbtn.setTextColor(Color.rgb(50,255,255));
            }
        }else {
            signupbtn.setEnabled(false);
            signupbtn.setTextColor(Color.rgb(50,255,255));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slide_from_left);
        fragmentTransaction.replace(parentframeLayout.getId(),fragment);
        fragmentTransaction.commit();

    }
}