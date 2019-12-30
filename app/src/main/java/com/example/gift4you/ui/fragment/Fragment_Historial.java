package com.example.gift4you.ui.fragment;


import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gift4you.R;
import com.example.gift4you.model.Solicitud;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment_Historial extends Fragment {

    View vista;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    CollectionReference collectionRef;

    Spinner spinnerPreguntaUnoSeleccionada,spinnerPreguntaDosSeleccionada,spinnerCargarNombre
            ,spinnerPreguntaTresSeleccionada,spinnerPreguntaCuatroSeleccionada,spinnerCargarParentesco;

    String respuestaUno,respuestaDos,respuestaTres,respuestaCuatro,sumaRespuestas,respuestaSeleccionadaUno
            ,respuestaSeleccionadaDos,respuestaSeleccionadaTres,respuestaSeleccionadaCuatro;

    public static String parentescoSeleccionado_fragment_historial,nombreSeleccionado_fragment_historial;

    Button Btn_Actualizar_Respuestas,Btn_Nueva_Recomendacion;

    private TextView tv_dirigido_a,tv_Parentesco,Pregunta_1_guardada,Pregunta_2_guardada,Pregunta_3_guardada,Pregunta_4_guardada
            ,No_hay_recomendaciones,wea_aaa;

    private ProgressBar progressBar;

    private ConstraintLayout constraintLayout2,constraintLayout_fragment_historial;

    public Fragment_Historial() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment__historial,container,false);

        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        progressBar=vista.findViewById(R.id.progress_bar_recomendaciones_realizadas);

        spinnerPreguntaUnoSeleccionada=vista.findViewById(R.id.spinner_pregunta_1_guardada);
        spinnerPreguntaDosSeleccionada=vista.findViewById(R.id.spinner_pregunta_2_guardada);
        spinnerPreguntaTresSeleccionada=vista.findViewById(R.id.spinner_pregunta_3_guardada);
        spinnerPreguntaCuatroSeleccionada=vista.findViewById(R.id.spinner_pregunta_4_guardada);
        spinnerCargarParentesco=vista.findViewById(R.id.spinner_parentesco_fragment_historial);
        spinnerCargarNombre=vista.findViewById(R.id.spinner_nombre_fragment_historial);

        tv_dirigido_a=vista.findViewById(R.id.tv_dirigido_a_fragment_historial);
        Pregunta_1_guardada=vista.findViewById(R.id.txt_pregunta_1_guardada);
        Pregunta_2_guardada=vista.findViewById(R.id.txt_pregunta_2_guardada);
        Pregunta_3_guardada=vista.findViewById(R.id.txt_pregunta_3_guardada);
        Pregunta_4_guardada=vista.findViewById(R.id.txt_pregunta_4_guardada);
        tv_Parentesco=vista.findViewById(R.id.tv_parentesco_fragment_historial);

        No_hay_recomendaciones=vista.findViewById(R.id.txt_no_hay_recomendaciones);

        constraintLayout2=vista.findViewById(R.id.fragment_Historial2);
        constraintLayout_fragment_historial=vista.findViewById(R.id.constraintLayout_respuestas_fragment_historial);

        cargarParentescoyRespuestas();
        progressBar.setVisibility(View.GONE);

        Btn_Actualizar_Respuestas=vista.findViewById(R.id.btn_actualizar_respuestas);
        Btn_Nueva_Recomendacion=vista.findViewById(R.id.btn_nueva_recomendacion);

        Btn_Actualizar_Respuestas.setOnClickListener(view ->ActualizarEncuesta());

        Btn_Nueva_Recomendacion.setOnClickListener(view ->NuevaRecomendacion());

        return vista;

    }



    void cargarParentescoyRespuestas(){

        String uid=mAuth.getCurrentUser().getUid();

        collectionRef=db.collection("USUARIOS_REGISTRADOS")
                .document(uid)
                .collection("SOLICITUDES_REALIZADAS");


        collectionRef.addSnapshotListener(getActivity(),(queryDocumentSnapshots, e) -> {

            if (e != null) {
                return;
            }

            List<String> Parentesco = new ArrayList<>();

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                Solicitud solicitud = documentSnapshot.toObject(Solicitud.class);

                solicitud.setParentesco(documentSnapshot.getId());

                String parentesco = solicitud.getParentesco();

                Parentesco.add(parentesco);

                progressBar.setVisibility(View.GONE);
                No_hay_recomendaciones.setVisibility(View.GONE);
                tv_Parentesco.setVisibility(View.VISIBLE);
                spinnerCargarParentesco.setVisibility(View.VISIBLE);
                Btn_Actualizar_Respuestas.setVisibility(View.VISIBLE);
                Btn_Nueva_Recomendacion.setVisibility(View.VISIBLE);

            }

            if(getContext()==null){
                return;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, Parentesco);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCargarParentesco.setAdapter(adapter);

            spinnerCargarParentesco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                    progressBar.setVisibility(View.VISIBLE);
                    tv_dirigido_a.setVisibility(View.INVISIBLE);
                    spinnerCargarNombre.setVisibility(View.INVISIBLE);

                    parentescoSeleccionado_fragment_historial=adapterView.getItemAtPosition(posicion).toString();
                    cargarNombre();

                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        });

    }

    void cargarNombre(){
        String uid=mAuth.getCurrentUser().getUid();

        DocumentReference docRef = db.collection("USUARIOS_REGISTRADOS")
                .document(uid)
                .collection("SOLICITUDES_REALIZADAS")
                .document(parentescoSeleccionado_fragment_historial);

        docRef.get().addOnSuccessListener(documentSnapshot -> {

            Map<String, Object> forms = documentSnapshot.getData();

            List<String> Nombres = new ArrayList<>();
            for (Map.Entry<String, Object> form: forms.entrySet()) {
                String key = (String) form.getKey();
                //Map<Object, Object> values = (Map<Object, Object>)form.getValue();
                //String name = (String) values.get("Nombre");
                Nombres.add(key);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_spinner_item, Nombres);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCargarNombre.setAdapter(adapter);

            spinnerCargarNombre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                    progressBar.setVisibility(View.VISIBLE);
                    constraintLayout_fragment_historial.setVisibility(View.INVISIBLE);
                    tv_dirigido_a.setVisibility(View.VISIBLE);
                    spinnerCargarNombre.setVisibility(View.VISIBLE);

                    nombreSeleccionado_fragment_historial=adapterView.getItemAtPosition(posicion).toString();

                    cargarRespuestaUno();
                    cargarRespuestaDos();
                    cargarRespuestaTres();
                    cargarRespuestaCuatro();

                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        });



    }

    void cargarRespuestaUno(){

        String uid=mAuth.getCurrentUser().getUid();

        DocumentReference docRef = db.collection("USUARIOS_REGISTRADOS")
                .document(uid)
                .collection("SOLICITUDES_REALIZADAS")
                .document(parentescoSeleccionado_fragment_historial);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                List<String> Pregunta_Uno_Seleccionada = new ArrayList<>();

                Map<String, Object> myMap = (Map<String, Object>) documentSnapshot.get(nombreSeleccionado_fragment_historial);

                String Pregunta_Uno = myMap.get("Pregunta_Uno").toString();

                Pregunta_Uno_Seleccionada.add(Pregunta_Uno);


                if(Pregunta_Uno.equals("Nunca tiene tiempo libre")){
                    Pregunta_Uno_Seleccionada.add("Visitar a su familia y seres queridos");
                    Pregunta_Uno_Seleccionada.add("Practicar algún deporte");
                    Pregunta_Uno_Seleccionada.add("Ver televisión");

                }
                if(Pregunta_Uno.equals("Visitar a su familia y seres queridos")){
                    Pregunta_Uno_Seleccionada.add("Nunca tiene tiempo libre");
                    Pregunta_Uno_Seleccionada.add("Practicar algún deporte");
                    Pregunta_Uno_Seleccionada.add("Ver televisión");
                }
                if(Pregunta_Uno.equals("Practicar algún deporte")){
                    Pregunta_Uno_Seleccionada.add("Visitar a su familia y seres queridos");
                    Pregunta_Uno_Seleccionada.add("Nunca tiene tiempo libre");
                    Pregunta_Uno_Seleccionada.add("Ver televisión");

                }
                if(Pregunta_Uno.equals("Ver televisión")){
                    Pregunta_Uno_Seleccionada.add("Visitar a su familia y seres queridos");
                    Pregunta_Uno_Seleccionada.add("Nunca tiene tiempo libre");
                    Pregunta_Uno_Seleccionada.add("Practicar algún deporte");

                }


                ArrayAdapter<String> adapterUno = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, Pregunta_Uno_Seleccionada);
                adapterUno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPreguntaUnoSeleccionada.setAdapter(adapterUno);

                progressBar.setVisibility(View.GONE);
                constraintLayout_fragment_historial.setVisibility(View.VISIBLE);
            };

        });

        spinnerPreguntaUnoSeleccionada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                respuestaSeleccionadaUno=adapterView.getItemAtPosition(posicion).toString();

                if(respuestaSeleccionadaUno.equals("Ver televisión")){

                    respuestaUno="10";

                }
                if(respuestaSeleccionadaUno.equals("Visitar a su familia y seres queridos")){

                    respuestaUno="20";

                }
                if(respuestaSeleccionadaUno.equals("Nunca tiene tiempo libre")){

                    respuestaUno="15";

                }
                if(respuestaSeleccionadaUno.equals("Practicar algún deporte")){

                    respuestaUno="25";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    void cargarRespuestaDos(){

        String uid=mAuth.getCurrentUser().getUid();

        DocumentReference docRef = db.collection("USUARIOS_REGISTRADOS")
                .document(uid)
                .collection("SOLICITUDES_REALIZADAS")
                .document(parentescoSeleccionado_fragment_historial);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                List<String> Pregunta_Dos_Seleccionada = new ArrayList<>();

                Map<String, Object> myMap = (Map<String, Object>) documentSnapshot.get(nombreSeleccionado_fragment_historial);

                String Pregunta_Dos = myMap.get("Pregunta_Dos").toString();

                Pregunta_Dos_Seleccionada.add(Pregunta_Dos);


                if(Pregunta_Dos.equals("Formal, camisas, vestidos")){
                    Pregunta_Dos_Seleccionada.add("Casual, jeans, sudaderas");
                    Pregunta_Dos_Seleccionada.add("Ropa cómoda y amplia");
                    Pregunta_Dos_Seleccionada.add("Poleras con de grupos de música, series");
                }
                if(Pregunta_Dos.equals("Casual, jeans, sudaderas")){
                    Pregunta_Dos_Seleccionada.add("Formal, camisas, vestidos");
                    Pregunta_Dos_Seleccionada.add("Ropa cómoda y amplia");
                    Pregunta_Dos_Seleccionada.add("Poleras con de grupos de música, series");
                }
                if(Pregunta_Dos.equals("Ropa cómoda y amplia")){
                    Pregunta_Dos_Seleccionada.add("Poleras con de grupos de música, series");
                    Pregunta_Dos_Seleccionada.add("Formal, camisas, vestidos");
                    Pregunta_Dos_Seleccionada.add("Casual, jeans, sudaderas");
                }
                if(Pregunta_Dos.equals("Poleras con de grupos de música, series")){
                    Pregunta_Dos_Seleccionada.add("Ropa cómoda y amplia");
                    Pregunta_Dos_Seleccionada.add("Formal, camisas, vestidos");
                    Pregunta_Dos_Seleccionada.add("Casual, jeans, sudaderas");
                }

                ArrayAdapter<String> adapterDos = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, Pregunta_Dos_Seleccionada);
                adapterDos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPreguntaDosSeleccionada.setAdapter(adapterDos);

                Pregunta_2_guardada.setVisibility(View.VISIBLE);
                spinnerPreguntaDosSeleccionada.setVisibility(View.VISIBLE);
            };

        });

        spinnerPreguntaDosSeleccionada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                respuestaSeleccionadaDos=adapterView.getItemAtPosition(posicion).toString();

                if(respuestaSeleccionadaDos.equals("Poleras con de grupos de música, series")){

                    respuestaDos="10";

                }
                if(respuestaSeleccionadaDos.equals("Ropa cómoda y amplia")){

                    respuestaDos="20";

                }
                if(respuestaSeleccionadaDos.equals("Formal, camisas, vestidos")){

                    respuestaDos="15";

                }
                if(respuestaSeleccionadaDos.equals("Casual, jeans, sudaderas")){

                    respuestaDos="25";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    void cargarRespuestaTres(){

        String uid=mAuth.getCurrentUser().getUid();

        DocumentReference docRef = db.collection("USUARIOS_REGISTRADOS")
                .document(uid)
                .collection("SOLICITUDES_REALIZADAS")
                .document(parentescoSeleccionado_fragment_historial);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                List<String> Pregunta_Tres_Seleccionada = new ArrayList<>();

                Map<String, Object> myMap = (Map<String, Object>) documentSnapshot.get(nombreSeleccionado_fragment_historial);

                String Pregunta_Tres = myMap.get("Pregunta_Tres").toString();

                Pregunta_Tres_Seleccionada.add(Pregunta_Tres);


                if(Pregunta_Tres.equals("Muchísimo")){
                    Pregunta_Tres_Seleccionada.add("Lo necesario");
                    Pregunta_Tres_Seleccionada.add("Casi nada");
                }
                if(Pregunta_Tres.equals("Lo necesario")){
                    Pregunta_Tres_Seleccionada.add("Muchísimo");
                    Pregunta_Tres_Seleccionada.add("Casi nada");
                }
                if(Pregunta_Tres.equals("Casi nada")){
                    Pregunta_Tres_Seleccionada.add("Muchísimo");
                    Pregunta_Tres_Seleccionada.add("Lo necesario");
                }

                ArrayAdapter<String> adapterTres = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, Pregunta_Tres_Seleccionada);
                adapterTres.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPreguntaTresSeleccionada.setAdapter(adapterTres);

                Pregunta_3_guardada.setVisibility(View.VISIBLE);
                spinnerPreguntaTresSeleccionada.setVisibility(View.VISIBLE);
            };

        });

        spinnerPreguntaTresSeleccionada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                respuestaSeleccionadaTres=adapterView.getItemAtPosition(posicion).toString();

                if(respuestaSeleccionadaTres.equals("Casi nada")){

                    respuestaTres="10";
                }
                if(respuestaSeleccionadaTres.equals("Muchísimo")){

                    respuestaTres="20";
                }
                if(respuestaSeleccionadaTres.equals("Lo necesario")){

                    respuestaTres="15";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void cargarRespuestaCuatro(){

        String uid=mAuth.getCurrentUser().getUid();

        DocumentReference docRef = db.collection("USUARIOS_REGISTRADOS")
                .document(uid)
                .collection("SOLICITUDES_REALIZADAS")
                .document(parentescoSeleccionado_fragment_historial);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                List<String> Pregunta_Cuatro_Seleccionada = new ArrayList<>();

                Map<String, Object> myMap = (Map<String, Object>) documentSnapshot.get(nombreSeleccionado_fragment_historial);

                String Pregunta_Cuatro = myMap.get("Pregunta_Cuatro").toString();

                Pregunta_Cuatro_Seleccionada.add(Pregunta_Cuatro);

                if(Pregunta_Cuatro.equals("Jugar fútbol")){
                    Pregunta_Cuatro_Seleccionada.add("Leer y escribir");
                    Pregunta_Cuatro_Seleccionada.add("Viajar");
                    Pregunta_Cuatro_Seleccionada.add("Reparar cosas");
                }
                if(Pregunta_Cuatro.equals("Leer y escribir")){
                    Pregunta_Cuatro_Seleccionada.add("Jugar fútbol");
                    Pregunta_Cuatro_Seleccionada.add("Viajar");
                    Pregunta_Cuatro_Seleccionada.add("Reparar cosas");
                }
                if(Pregunta_Cuatro.equals("Viajar")){
                    Pregunta_Cuatro_Seleccionada.add("Jugar fútbol");
                    Pregunta_Cuatro_Seleccionada.add("Leer y escribir");
                    Pregunta_Cuatro_Seleccionada.add("Reparar cosas");
                }
                if(Pregunta_Cuatro.equals("Reparar cosas")){
                    Pregunta_Cuatro_Seleccionada.add("Jugar fútbol");
                    Pregunta_Cuatro_Seleccionada.add("Leer y escribir");
                    Pregunta_Cuatro_Seleccionada.add("Viajar");
                }

                ArrayAdapter<String> adapterCuatro = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, Pregunta_Cuatro_Seleccionada);
                adapterCuatro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPreguntaCuatroSeleccionada.setAdapter(adapterCuatro);

                Pregunta_4_guardada.setVisibility(View.VISIBLE);
                spinnerPreguntaCuatroSeleccionada.setVisibility(View.VISIBLE);
            };

        });

        spinnerPreguntaCuatroSeleccionada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                respuestaSeleccionadaCuatro=adapterView.getItemAtPosition(posicion).toString();

                if(respuestaSeleccionadaCuatro.equals("Reparar cosas")){

                    respuestaCuatro="10";
                }
                if(respuestaSeleccionadaCuatro.equals("Jugar fútbol")){

                    respuestaCuatro="20";
                }
                if(respuestaSeleccionadaCuatro.equals("Leer y escribir")){

                    respuestaCuatro="15";
                }
                if(respuestaSeleccionadaCuatro.equals("Viajar")){

                    respuestaCuatro="25";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void ActualizarEncuesta(){

        SumaDeRespuestas();

        String uid=mAuth.getCurrentUser().getUid();

        Map<String, Object> encuesta= new HashMap<>();
        encuesta.put("Parentesco",parentescoSeleccionado_fragment_historial);
        encuesta.put("Nombre",nombreSeleccionado_fragment_historial);
        encuesta.put("Pregunta_Uno",respuestaSeleccionadaUno);
        encuesta.put("Pregunta_Dos",respuestaSeleccionadaDos);
        encuesta.put("Pregunta_Tres",respuestaSeleccionadaTres);
        encuesta.put("Pregunta_Cuatro",respuestaSeleccionadaCuatro);
        encuesta.put("Suma_De_Respuestas",sumaRespuestas);

        DocumentReference ref = db.collection("USUARIOS_REGISTRADOS")
                .document(uid)
                .collection("SOLICITUDES_REALIZADAS")
                .document(parentescoSeleccionado_fragment_historial);

        Map<String, Object> nombre = new HashMap<>();

        nombre.put(nombreSeleccionado_fragment_historial, encuesta);

        ref.update(nombre)
                .addOnCompleteListener(task -> {

                    Toast.makeText(getContext(),"Respuestas Guardadas",Toast.LENGTH_LONG).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),"Fallo en el guardado",Toast.LENGTH_LONG).show();
                });

    }

    void NuevaRecomendacion(){

        SumaDeRespuestas();

        String uid=mAuth.getCurrentUser().getUid();

        Map<String, Object> encuesta= new HashMap<>();
        encuesta.put("Parentesco",parentescoSeleccionado_fragment_historial);
        encuesta.put("Nombre",nombreSeleccionado_fragment_historial);
        encuesta.put("Pregunta_Uno",respuestaSeleccionadaUno);
        encuesta.put("Pregunta_Dos",respuestaSeleccionadaDos);
        encuesta.put("Pregunta_Tres",respuestaSeleccionadaTres);
        encuesta.put("Pregunta_Cuatro",respuestaSeleccionadaCuatro);
        encuesta.put("Suma_De_Respuestas",sumaRespuestas);

        DocumentReference ref = db.collection("USUARIOS_REGISTRADOS")
                .document(uid)
                .collection("SOLICITUDES_REALIZADAS")
                .document(parentescoSeleccionado_fragment_historial);

        Map<String, Object> nombre = new HashMap<>();

        nombre.put(nombreSeleccionado_fragment_historial, encuesta);

        ref.set(nombre, SetOptions.merge())
                .addOnCompleteListener(task -> {

                    Toast.makeText(getContext(),"Solicitud Guardada",Toast.LENGTH_LONG).show();
                    constraintLayout2.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    No_hay_recomendaciones.setVisibility(View.GONE);
                    constraintLayout2.setVisibility(View.GONE);
                    Fragment_ObsequioRecomendado nextFrag= new Fragment_ObsequioRecomendado();

                    this.getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_Historial1, nextFrag, null)
                            .addToBackStack(null)
                            .commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),"Fallo en el guardado",Toast.LENGTH_LONG).show();
        });


    }

    void SumaDeRespuestas(){

        int respuesta_uno=Integer.parseInt(respuestaUno);
        int respuesta_dos=Integer.parseInt(respuestaDos);
        int respuesta_tres=Integer.parseInt(respuestaTres);
        int respuesta_cuatro=Integer.parseInt(respuestaCuatro);

        int suma_respuestas=respuesta_uno+respuesta_dos+respuesta_tres+respuesta_cuatro;

        sumaRespuestas=String.valueOf(suma_respuestas);


    }


}