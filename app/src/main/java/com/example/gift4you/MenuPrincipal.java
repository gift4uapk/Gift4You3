package com.example.gift4you;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import com.example.gift4you.ui.MainActivity;
import com.example.gift4you.ui.fragment.Fragment_Calendario;
import com.example.gift4you.ui.fragment.Fragment_Historial;
import com.example.gift4you.ui.fragment.Fragment_Inicio;
import com.example.gift4you.ui.fragment.Fragment_RecomendacionObsequio;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MenuPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private GoogleSignInClient mGoogleSignClient;
    private ProgressBar progressBar_cerrar_sesion;
    private TextView tv_aceptar,tv_cancelar;
    private AlertDialog alertDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String Respuesta_InicioSesion,nombre_registrado,apellido_registrado,email_registrado,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Regalos");
        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor,
                    new Fragment_Inicio()).commit();
            navigationView.setCheckedItem(R.id.fragment_Inicio1);
        }


        View headerView = navigationView.getHeaderView(0);
        cargarDatos(headerView);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_inicio:
                toolbar.setTitle("Regalos");
                getSupportFragmentManager().beginTransaction().replace(R.id.contenedor,
                        new Fragment_Inicio()).commit();
                break;
            case R.id.nav_historial:
                toolbar.setTitle("Historial");
                getSupportFragmentManager().beginTransaction().replace(R.id.contenedor,
                        new Fragment_Historial()).commit();
                break;
            case R.id.nav_calendario:
                toolbar.setTitle("Calendario");
                getSupportFragmentManager().beginTransaction().replace(R.id.contenedor,
                        new Fragment_Calendario()).commit();
                break;
            case R.id.nav_recomendacion_obsequio:
                toolbar.setTitle("Recomendacion Obsequio");
                getSupportFragmentManager().beginTransaction().replace(R.id.contenedor,
                        new Fragment_RecomendacionObsequio()).commit();
                break;
            case R.id.cerrar_sesion_menu_principal:
                cerrarSesion();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void cerrarSesion(){
        cargarRespuestaInicioSesion();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        final View addView = LayoutInflater.from(this).inflate(R.layout.cerrar_sesion,null);

        tv_aceptar=addView.findViewById(R.id.tv_aceptar_cerrar_sesion);
        tv_cancelar=addView.findViewById(R.id.tv_cancelar_cerrar_sesion);
        progressBar_cerrar_sesion=addView.findViewById(R.id.progress_bar_cerrar_sesion);

        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions
                .Builder()
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        builder.setView(addView);
        alertDialog=builder.create();
        alertDialog.show();

        tv_aceptar.setOnClickListener(v -> {
            progressBar_cerrar_sesion.setVisibility(View.VISIBLE);

            if(Respuesta_InicioSesion.equals("inicioConGmail")){
                mGoogleSignClient= GoogleSignIn.getClient(MenuPrincipal.this,googleSignInOptions);
                FirebaseAuth.getInstance().signOut();
                mGoogleSignClient.signOut();
                mAuth.signOut();
                Intent intent=new Intent(MenuPrincipal.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            if(Respuesta_InicioSesion.equals("inicioConFacebook")){
                LoginManager.getInstance().logOut();
                mAuth.signOut();
                Intent intent=new Intent(MenuPrincipal.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            if(Respuesta_InicioSesion.equals("inicioConEmail")){

                mAuth.signOut();
                Intent intent=new Intent(MenuPrincipal.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_cancelar.setOnClickListener(v -> alertDialog.dismiss());

    }

    void cargarRespuestaInicioSesion(){

        DocumentReference docRespuestasRef=db.collection("USUARIOS_REGISTRADOS")
                .document(uid);

        docRespuestasRef.get().addOnSuccessListener(documentSnapshot -> {

            Respuesta_InicioSesion=documentSnapshot.get("FormaInicio").toString();

        });

    }

    void cargarDatos(View headerView){

        TextView nombre_apellido = headerView.findViewById(R.id.nombre_apellido_nav_header_menu_principal);
        TextView email = headerView.findViewById(R.id.email_nav_header_menu_principal);

        DocumentReference docRespuestasRef=db.collection("USUARIOS_REGISTRADOS")
                .document(uid);

        docRespuestasRef.get().addOnSuccessListener(documentSnapshot -> {

            nombre_registrado=documentSnapshot.get("Nombre").toString();
            apellido_registrado=documentSnapshot.get("Apellido").toString();
            email_registrado=documentSnapshot.get("Email").toString();

            nombre_apellido.setText(nombre_registrado+" "+apellido_registrado);
            email.setText(email_registrado);
        });
    }

}
