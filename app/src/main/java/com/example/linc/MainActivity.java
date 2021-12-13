package com.example.linc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Patterns;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton btn_google; // 구글 로그인 버튼
    private LoginButton btn_facebook; // 페이스북 로그인 버튼
    private FirebaseAuth auth; // 파이어베이스 인증 객체
    private GoogleApiClient googleApiClient; // 구글 api 클라이언트 객체
    private static final int REQ_SIGN_GOOGLE = 100; // 구글 로그인 결과 코드
    private CallbackManager callbackManager; // callbackmanager
    private LoginCallback mLoginCallback; // LoginCallback 함수 만들어 둔거 호출
    private AccessToken accessToken;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private Button buttonSignUp;
    private Button buttonLogIn;
    private EditText editTextEmail;
    private EditText editTextPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) { // 앱이 실행될때 수행되는 곳
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // 페이스북 추가
        AppEventsLogger.activateApp(this); // 페이스북 추가

        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create(); // 페이스북 콜백 등록
        mLoginCallback = new LoginCallback();

        btn_facebook = (LoginButton) findViewById(R.id.btn_facebook);
        btn_facebook.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_facebook.registerCallback(callbackManager,mLoginCallback);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        auth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화.

        btn_facebook = findViewById(R.id.btn_facebook);
        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Result.class);
                startActivity(intent);
            }
        });

        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() { // 구글 로그인 버튼을 클릭했을 때 수행하는 부분
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_SIGN_GOOGLE);
            }
        });

        editTextEmail = (EditText) findViewById(R.id.et_email);
        editTextPassword = (EditText) findViewById(R.id.et_password);

        buttonSignUp = (Button) findViewById(R.id.btn_signup);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });

        buttonLogIn = (Button) findViewById(R.id.btn_signin);
        buttonLogIn.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View view) {
               if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                   loginUser(editTextEmail.getText().toString(),editTextPassword.getText().toString());
               } else {
                   Toast.makeText(MainActivity.this,"계정과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
               }
           }
        });

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(MainActivity.this,Result.class);
                    startActivity(intent);
                    finish();
                } else {

                }
            }
        };

    }

    public void loginUser(String email, String password){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // 로그인 성공
                    Toast.makeText(MainActivity.this,"로그인 성공",Toast.LENGTH_SHORT).show();
                    auth.addAuthStateListener(firebaseAuthListener);
                } else {
                    // 로그인 실패
                    Toast.makeText(MainActivity.this,"로그인 실패",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            auth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 구글 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는 곳
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) { // 인증 결과가 성공적이면
                GoogleSignInAccount account = result.getSignInAccount(); // account라는 데이터는 구글 로그인 정보를 담고 있음(닉네임,프로필사진url,이메일 주소 등)
                resultLogin(account); // 로그인 결과값 출력 수행
            }
        }
    }

    private void resultLogin(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // 로그인이 성공했으면
                            Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Result.class);
                            intent.putExtra("nickname", account.getDisplayName());
                            intent.putExtra("photoUrl", String.valueOf(account.getPhotoUrl())); // Sting.valueOf() 특정 자료형을 string 형태로 변환
                            startActivity(intent);
                        } else { //로그인이 실패했으면
                            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}