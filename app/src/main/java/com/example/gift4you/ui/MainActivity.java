package com.example.gift4you.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.gift4you.MenuPrincipal;
import com.example.gift4you.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button boton_registro_email,boton_registro_google,boton_ingresar,boton_registro_facebook;
    private FirebaseAuth mAuth;

    static final int GOOGLE_SIGN = 123;
    private GoogleSignInClient mGoogleSignClient;
    private FirebaseFirestore db;

    private String uid_registro,nombre_registro,apellido_registro,email_registro,pass_registro,email_ingreso,pass_ingreso;

    private ProgressBar progressBar_registro_email_pass,progressBar_activityMain;

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private AlertDialog alertDialog;

    private EditText et_nombre,et_apellido,et_email,et_pass,et_pass_ingreso,et_email_ingreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        callbackManager = CallbackManager.Factory.create();

        et_pass_ingreso=findViewById(R.id.edit_pass_activity_main);
        et_email_ingreso=findViewById(R.id.edit_email_activity_main);
        boton_ingresar=findViewById(R.id.btn_ingresar_activity_main);
        boton_registro_email=findViewById(R.id.btn_registro_con_email_activity_main);
        boton_registro_google=findViewById(R.id.btn_registro_con_google_activity_main);
        boton_registro_facebook=findViewById(R.id.btn_registro_con_facebookView_activity_main);
        progressBar_activityMain=findViewById(R.id.progress_bar_activity_main);


        boton_registro_facebook.setOnClickListener(v -> loginButton.performClick());
        //FACEBOOK
        loginButton = findViewById(R.id.btn_registro_con_facebook_activity_main);
        loginButton.setPermissions ("public_profile", "email");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getApplicationContext(),"Error de conexion Facebook", Toast.LENGTH_SHORT).show();
            }
        });


        //GOOGLE
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions
                .Builder()
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignClient= GoogleSignIn.getClient(this,googleSignInOptions);
        boton_registro_google.setOnClickListener(v->SignInGoogle());

        //CORREO
        boton_registro_email.setOnClickListener(v -> abrir_registro_email());
        boton_ingresar.setOnClickListener(v -> ingresoConEmail());


        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent intent=new Intent(this, MenuPrincipal.class);
            startActivity(intent);
            finish();
        }

    }


    //LISTO INGRESO
    void ingresoConEmail(){
        progressBar_activityMain.setVisibility(View.VISIBLE);
        mAuth= FirebaseAuth.getInstance();
        email_ingreso=et_email_ingreso.getText().toString().trim();
        pass_ingreso=et_pass_ingreso.getText().toString().trim();

        if(TextUtils.isEmpty(email_ingreso) || TextUtils.isEmpty(pass_ingreso)||pass_ingreso.length()<6) {

            if(TextUtils.isEmpty(email_ingreso)) {
                et_email_ingreso.setError("Campo vacio");
            }
            if(TextUtils.isEmpty(pass_ingreso)) {
                et_pass_ingreso.setError("Campo vacio");
            }

            if((!TextUtils.isEmpty(pass_ingreso)) && pass_ingreso.length()<6){
                et_pass_ingreso.setError("Debe tener 6 o mas caracteres");
            }
            progressBar_activityMain.setVisibility(View.GONE);
            return;
        }


        mAuth.signInWithEmailAndPassword(email_ingreso,pass_ingreso).addOnCompleteListener(this, task -> {

            if(task.isSuccessful()){

                String uid=mAuth.getCurrentUser().getUid();

                Map<String, Object> user= new HashMap<>();
                user.put("FormaInicio","inicioConEmail");

                db.collection("USUARIOS_REGISTRADOS")
                        .document(uid)
                        .update(user)
                        .addOnCompleteListener(task1 -> {

                            Intent intent = new Intent (MainActivity.this, MenuPrincipal.class);
                            startActivity(intent);
                            finish();

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this,"Error de Conexion",Toast.LENGTH_LONG).show();
                        });

            }
            if(!task.isSuccessful()){

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                builder.setMessage("E-mail o Contraseña Invalida")
                        .setPositiveButton("Aceptar", null).create().show();
                progressBar_activityMain.setVisibility(View.GONE);
            }

        });
    }


    //LISTO REGISTRO CON EMAIL
    void abrir_registro_email(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        final View addView = LayoutInflater.from(this).inflate(R.layout.registro_email_pass,null);

        et_nombre=addView.findViewById(R.id.edit_nombre_registro_email_pass);
        et_apellido=addView.findViewById(R.id.edit_apellido_registro_email_pass);
        et_email=addView.findViewById(R.id.edit_email_registro_email_pass);
        et_pass=addView.findViewById(R.id.edit_pass_registro_email_pass);
        progressBar_registro_email_pass=addView.findViewById(R.id.progress_bar_registro_email_pass);

        Button btn_RegistrarUsuario = addView.findViewById(R.id.btn_ingresar_registro_email_pass);

        btn_RegistrarUsuario.setOnClickListener(v -> RegistrarUsuariofaseUno());

        builder.setView(addView);
        alertDialog=builder.create();
        alertDialog.show();

    }
    void RegistrarUsuariofaseUno(){

        progressBar_registro_email_pass.setVisibility(View.VISIBLE);
        mAuth= FirebaseAuth.getInstance();
        nombre_registro= et_nombre.getText().toString().trim();
        apellido_registro=et_apellido.getText().toString().trim();
        pass_registro=et_pass.getText().toString().trim();
        email_registro=et_email.getText().toString().trim();

        if(TextUtils.isEmpty(nombre_registro) || TextUtils.isEmpty(apellido_registro)|| TextUtils.isEmpty(pass_registro)|| TextUtils.isEmpty(email_registro)
            || nombre_registro.length()<3) {

            if(TextUtils.isEmpty(nombre_registro)) {
                et_nombre.setError("Campo vacio");
            }
            if(TextUtils.isEmpty(apellido_registro)) {
                et_apellido.setError("Campo vacio");
            }
            if(TextUtils.isEmpty(email_registro)) {
                et_email.setError("Campo vacio");
            }
            if(TextUtils.isEmpty(pass_registro)) {
                et_pass.setError("Campo vacio");
            }
            if((!TextUtils.isEmpty(nombre_registro)) && nombre_registro.length()<3){
                et_nombre.setError("Debe tener 3 o mas caracteres");
            }
            if((!TextUtils.isEmpty(apellido_registro)) && apellido_registro.length()<3){
                et_apellido.setError("Debe tener 3 o mas caracteres");
            }
            if((!TextUtils.isEmpty(pass_registro)) && pass_registro.length()<6){
                et_pass.setError("Debe tener 6 o mas caracteres");
            }
            progressBar_registro_email_pass.setVisibility(View.GONE);
            return;
        }



        mAuth.signInWithEmailAndPassword(email_registro,pass_registro).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //Si se pudo logear es xq ya existe
                et_email.setError("Ingrese un email valido");
                progressBar_registro_email_pass.setVisibility(View.GONE);
                mAuth.signOut();
            }

            if (!task.isSuccessful()) {
                RegistrarUsuariofaseDos();
            }

        });
    }
    void RegistrarUsuariofaseDos(){

        mAuth.createUserWithEmailAndPassword(email_registro,pass_registro).addOnCompleteListener(task -> {

            if((!TextUtils.isEmpty(pass_registro)) && pass_registro.length()<6){
                et_pass.setError("Debe tener 6 o mas caracteres");
                progressBar_registro_email_pass.setVisibility(View.GONE);
                return;
            }
            if(!task.isSuccessful()) {
                et_email.setError("Ingrese un email valido");
                progressBar_registro_email_pass.setVisibility(View.GONE);
                return;
            }

            if (task.isSuccessful()){

                uid_registro=mAuth.getCurrentUser().getUid();

                Map<String, Object> user= new HashMap<>();
                user.put("Nombre",nombre_registro);
                user.put("Apellido",apellido_registro);
                user.put("Email",email_registro);
                user.put("Uid",uid_registro);
                user.put("FormaInicio","");

                db.collection("USUARIOS_REGISTRADOS")
                        .document(uid_registro)
                        .set(user)
                        .addOnCompleteListener(task1 -> {
                            mAuth.getCurrentUser().sendEmailVerification();
                            mAuth.signOut();
                            alertDialog.dismiss();
                        });

            }
        });
    }


    //LISTO REGISTRO CON GOOGLE
    void SignInGoogle(){
        progressBar_activityMain.setVisibility(View.VISIBLE);
        Intent signIntent = mGoogleSignClient.getSignInIntent();
        startActivityForResult(signIntent,GOOGLE_SIGN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GOOGLE_SIGN){
            Task<GoogleSignInAccount>task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);

                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if(account!=null) {
                    mAuth=FirebaseAuth.getInstance();

                    firebaseAuthWithGoogle(account,result);
                }
            }catch (ApiException e){
                e.printStackTrace();
                progressBar_activityMain.setVisibility(View.GONE);
            }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account,GoogleSignInResult result) {
        Log.d("TAG", "firebaseAuthWithGoogle: "+account.getId());

        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this,task ->{

                    if(task.isSuccessful()){

                        Log.d("TAG","signin success");
                        FirebaseUser user=mAuth.getCurrentUser();
                        CrearUserEnDaseDatos(user,result);

                    }else{

                        Log.w("TAG", "signin failure",task.getException());
                        Toast.makeText(this, "SignIn Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void CrearUserEnDaseDatos(FirebaseUser user,GoogleSignInResult result){
        if(user !=null){

            GoogleSignInAccount acct = result.getSignInAccount();

            String nombre=acct.getGivenName();
            String uid=user.getUid();
            String email=user.getEmail();
            String apellido=acct.getFamilyName();

            Map<String, Object> Regisuser= new HashMap<>();
            Regisuser.put("Nombre",nombre);
            Regisuser.put("Apellido",apellido);
            Regisuser.put("Email",email);
            Regisuser.put("Uid",uid);
            Regisuser.put("FormaInicio","inicioConGmail");

            db.collection("USUARIOS_REGISTRADOS")
                    .document(uid)
                    .set(Regisuser)
                    .addOnCompleteListener(task1 -> {
                        Intent intent = new Intent (MainActivity.this, MenuPrincipal.class);
                        startActivity(intent);
                        finish();

                    })
                    .addOnFailureListener(task -> {
                        Toast.makeText(getApplicationContext(),"Error de Conexion", Toast.LENGTH_SHORT).show();
                    });

        }else{

            Toast.makeText(getApplicationContext(),"Hubo un error Desconocido", Toast.LENGTH_SHORT).show();
        }

    }


    // LISTO FACEBOOK
    private void handleFacebookAccessToken(AccessToken accessToken) {
        progressBar_activityMain.setVisibility(View.VISIBLE);
        AuthCredential credential= FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if(!task.isSuccessful()){
                Toast.makeText(getApplicationContext(),"El email ya esta registrado con otra cuenta",Toast.LENGTH_LONG).show();
                progressBar_activityMain.setVisibility(View.GONE);
                LoginManager.getInstance().logOut();

            }
            if(task.isSuccessful()){

                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String email=user.getEmail();
                String uid=user.getUid();
                String nombre= Profile.getCurrentProfile().getFirstName();
                String apellido=Profile.getCurrentProfile().getLastName();

                Map<String, Object> Regisuser= new HashMap<>();
                Regisuser.put("Nombre",nombre);
                Regisuser.put("Apellido",apellido);
                Regisuser.put("Email",email);
                Regisuser.put("Uid",uid);
                Regisuser.put("FormaInicio","inicioConFacebook");

                db.collection("USUARIOS_REGISTRADOS")
                        .document(uid)
                        .set(Regisuser)
                        .addOnCompleteListener(task1 -> {
                            goMainScreen();
                        })
                        .addOnFailureListener(task2 ->{
                            Toast.makeText(this,"Error de Conexion",Toast.LENGTH_LONG).show();
                        });

            }

        });

    }
    private void goMainScreen() {
        Intent intent=new Intent(this, MenuPrincipal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
