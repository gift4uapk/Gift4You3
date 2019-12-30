package com.example.gift4you.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gift4you.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Fragment_RecomendacionObsequio extends Fragment {

    private View vista;
    private Spinner spinnerEvento,spinnerPreguntaUno,spinnerPreguntaDos,spinnerPreguntaTres,spinnerPreguntaCuatro,spinnerParentesco;
    private String respuestaUno,respuestaDos,respuestaTres,respuestaCuatro,sumaRespuestas,respuestaSeleccionadaUno
            ,respuestaSeleccionadaDos,respuestaSeleccionadaTres,respuestaSeleccionadaCuatro;

    public static String parentescoSeleccionado_fragment_RecomendacionObsequio,nombre_fragment_RecomendacionObsequio,evento_seleccionado_FRO;
    private Button btn_buscar,btn_buscar_segun_evento;
    private TextView tv_Dirigido_a,tv_Pregunta_1,tv_Pregunta_2,tv_Pregunta_3,tv_Pregunta_4;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ConstraintLayout constraintLayout2;

    private EditText et_nombre;

    private TextInputLayout textInputLayout;



    public static Fragment_RecomendacionObsequio newInstance() {
        return new Fragment_RecomendacionObsequio();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment__recomendacion_obsequio,container,false);

        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        btn_buscar=vista.findViewById(R.id.btn_buscar_fragment_recomendacionObsequio);
        btn_buscar_segun_evento=vista.findViewById(R.id.btn_buscar_segunEvento_fragment_recomendacionObsequio);

        spinnerEvento=vista.findViewById(R.id.spinner_tipo_evento);
        spinnerPreguntaUno=vista.findViewById(R.id.spinner_pregunta_1_fragment_recomendacionObsequio);
        spinnerPreguntaDos=vista.findViewById(R.id.spinner_pregunta_2_fragment_recomendacionObsequio);
        spinnerPreguntaTres=vista.findViewById(R.id.spinner_pregunta_3_fragment_recomendacionObsequio);
        spinnerPreguntaCuatro=vista.findViewById(R.id.spinner_pregunta_4_fragment_recomendacionObsequio);
        spinnerParentesco=vista.findViewById(R.id.spinner_parentesco_fragment_recomendacionObsequio);
        et_nombre=vista.findViewById(R.id.et_nombre_fragment_recomendacion_obsequio);
        tv_Dirigido_a=vista.findViewById(R.id.tv_dirigido_a_fragment_recomendacionObsequio);
        tv_Pregunta_1=vista.findViewById(R.id.tv_pregunta_1_fragment_recomendacionObsequio);
        tv_Pregunta_2=vista.findViewById(R.id.tv_pregunta_2_fragment_recomendacionObsequio);
        tv_Pregunta_3=vista.findViewById(R.id.tv_pregunta_3_fragment_recomendacionObsequio);
        tv_Pregunta_4=vista.findViewById(R.id.tv_pregunta_4_fragment_recomendacionObsequio);
        constraintLayout2=vista.findViewById(R.id.fragment_RecomendacionObsequio2);
        textInputLayout=vista.findViewById(R.id.textInputLayout_fragment_recomendacion_obsequio);

        SpinnerEvento();

        nombre_fragment_RecomendacionObsequio=et_nombre.getText().toString().trim();
        btn_buscar.setOnClickListener(view ->GuardarEncuesta());

        btn_buscar_segun_evento.setOnClickListener(view ->RegaloEvento());

        return vista;
    }

    void SpinnerEvento(){

        String [] EventosDisponibles={"Seleccionar Evento","Dia de la Madre","Dia del Padre",
                "Dia del Amor y la Amistad","Baby Shower",
                "Matrimonio","Ocasional(cumpleaños, navidad)"};

        ArrayAdapter<String> eventosAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, EventosDisponibles);

        spinnerEvento.setAdapter(eventosAdapter);

        spinnerEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                final String evento_seleccionado=adapterView.getItemAtPosition(posicion).toString();

                if(evento_seleccionado.equals("Seleccionar Evento")|| evento_seleccionado.equals("Dia de la Madre")
                        || evento_seleccionado.equals("Dia del Padre")|| evento_seleccionado.equals("Dia del Amor y la Amistad")
                        || evento_seleccionado.equals("Baby Shower")|| evento_seleccionado.equals("Matrimonio")){

                    spinnerPreguntaUno.setVisibility(View.INVISIBLE);
                    spinnerPreguntaDos.setVisibility(View.INVISIBLE);
                    spinnerPreguntaTres.setVisibility(View.INVISIBLE);
                    spinnerPreguntaCuatro.setVisibility(View.INVISIBLE);
                    textInputLayout.setVisibility(View.INVISIBLE);
                    et_nombre.setVisibility(View.INVISIBLE);
                    spinnerParentesco.setVisibility(View.INVISIBLE);
                    tv_Dirigido_a.setVisibility(View.INVISIBLE);
                    tv_Pregunta_1.setVisibility(View.INVISIBLE);
                    tv_Pregunta_2.setVisibility(View.INVISIBLE);
                    tv_Pregunta_3.setVisibility(View.INVISIBLE);
                    tv_Pregunta_4.setVisibility(View.INVISIBLE);
                    btn_buscar.setVisibility(View.GONE);
                    btn_buscar_segun_evento.setVisibility(View.VISIBLE);

                    evento_seleccionado_FRO=evento_seleccionado;

                }

                if(evento_seleccionado.equals("Ocasional(cumpleaños, navidad)")){

                    evento_seleccionado_FRO=evento_seleccionado;
                    btn_buscar.setVisibility(View.VISIBLE);
                    btn_buscar_segun_evento.setVisibility(View.GONE);
                    ParentescoSeleccionado();
                    et_nombre.setVisibility(View.VISIBLE);
                    textInputLayout.setVisibility(View.VISIBLE);
                    PreguntaUno();
                    PreguntaDos();
                    PreguntaTres();
                    PreguntaCuatro();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    void ParentescoSeleccionado(){

        tv_Dirigido_a.setVisibility(View.VISIBLE);
        spinnerParentesco.setVisibility(View.VISIBLE);


        String [] ParentescoSeleccionado={"Madre","Padre","Hijo(a)","Hermano(a)","Novio(a)","Amigo(a)","Otro"};

        ArrayAdapter<String> DestinarioAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,ParentescoSeleccionado);

        spinnerParentesco.setAdapter(DestinarioAdapter);

        spinnerParentesco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                parentescoSeleccionado_fragment_RecomendacionObsequio=adapterView.getItemAtPosition(posicion).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    void PreguntaUno(){
        tv_Pregunta_1.setVisibility(View.VISIBLE);
        spinnerPreguntaUno.setVisibility(View.VISIBLE);
        String [] UnoRespuestaSeleccionada={"Nunca tiene tiempo libre",
                "Visitar a su familia y seres queridos",
                "Practica algún deporte",
                "Ver televisión"};

        ArrayAdapter<String> respuestaUnoAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,UnoRespuestaSeleccionada);

        spinnerPreguntaUno.setAdapter(respuestaUnoAdapter);

        spinnerPreguntaUno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                respuestaSeleccionadaUno=adapterView.getItemAtPosition(posicion).toString();

                if(respuestaSeleccionadaUno.equals("Nunca tiene tiempo libre")){

                    respuestaUno="10";

                }
                if(respuestaSeleccionadaUno.equals("Visitar a su familia y seres queridos")){

                    respuestaUno="20";

                }
                if(respuestaSeleccionadaUno.equals("Practicar algún deporte")){

                    respuestaUno="15";

                }
                if(respuestaSeleccionadaUno.equals("Ver televisión")){

                    respuestaUno="25";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    void PreguntaDos(){

        tv_Pregunta_2.setVisibility(View.VISIBLE);
        spinnerPreguntaDos.setVisibility(View.VISIBLE);
        String [] DosRespuestaSeleccionada={"Formal, camisas, vestidos",
                "Casual, jeans, sudadera",
                "Ropa cómoda y amplia",
                "Poleras con de grupos de música, series"};
        ArrayAdapter<String> respuestaDosAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,DosRespuestaSeleccionada);

        spinnerPreguntaDos.setAdapter(respuestaDosAdapter);

        spinnerPreguntaDos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                respuestaSeleccionadaDos=adapterView.getItemAtPosition(posicion).toString();

                if(respuestaSeleccionadaDos.equals("Formal, camisas, vestidos")){

                    respuestaDos="10";

                }
                if(respuestaSeleccionadaDos.equals("Casual, jeans, sudaderas")){

                    respuestaDos="20";

                }
                if(respuestaSeleccionadaDos.equals("Ropa cómoda y amplia")){

                    respuestaDos="15";

                }
                if(respuestaSeleccionadaDos.equals("Poleras con de grupos de música, series")){

                    respuestaDos="25";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    void PreguntaTres(){

        tv_Pregunta_3.setVisibility(View.VISIBLE);
        spinnerPreguntaTres.setVisibility(View.VISIBLE);
        String [] TresRespuestaSeleccionada={"Muchísimo","Lo necesario","Casi nada"};
        ArrayAdapter<String> respuestaTresAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,TresRespuestaSeleccionada);

        spinnerPreguntaTres.setAdapter(respuestaTresAdapter);

        spinnerPreguntaTres.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                respuestaSeleccionadaTres=adapterView.getItemAtPosition(posicion).toString();

                if(respuestaSeleccionadaTres.equals("Muchísimo")){

                    respuestaTres="10";

                }
                if(respuestaSeleccionadaTres.equals("Lo necesario")){

                    respuestaTres="20";

                }
                if(respuestaSeleccionadaTres.equals("Casi nada")){

                    respuestaTres="15";

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    void PreguntaCuatro(){

        tv_Pregunta_4.setVisibility(View.VISIBLE);
        spinnerPreguntaCuatro.setVisibility(View.VISIBLE);
        String [] CuatroRespuestaSeleccionada={"Jugar fútbol","Leer y escribir","Viajar","Reparar cosas"};
        ArrayAdapter<String> respuestaCuatroAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,CuatroRespuestaSeleccionada);

        spinnerPreguntaCuatro.setAdapter(respuestaCuatroAdapter);

        spinnerPreguntaCuatro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                respuestaSeleccionadaCuatro=adapterView.getItemAtPosition(posicion).toString();

                if(respuestaSeleccionadaCuatro.equals("Jugar fútbol")){

                    respuestaCuatro="10";

                }
                if(respuestaSeleccionadaCuatro.equals("Leer y escribir")){

                    respuestaCuatro="20";

                }
                if(respuestaSeleccionadaCuatro.equals("Viajar")){

                    respuestaCuatro="15";

                }
                if(respuestaSeleccionadaCuatro.equals("Reparar cosas")){

                    respuestaCuatro="25";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    void GuardarEncuesta(){

        SumaDeRespuestas();

        String uid=mAuth.getCurrentUser().getUid();

        nombre_fragment_RecomendacionObsequio=et_nombre.getText().toString().trim();

        Map<String, Object> encuesta= new HashMap<>();
        encuesta.put("Parentesco",parentescoSeleccionado_fragment_RecomendacionObsequio);
        encuesta.put("Nombre",nombre_fragment_RecomendacionObsequio);
        encuesta.put("Pregunta_Uno",respuestaSeleccionadaUno);
        encuesta.put("Pregunta_Dos",respuestaSeleccionadaDos);
        encuesta.put("Pregunta_Tres",respuestaSeleccionadaTres);
        encuesta.put("Pregunta_Cuatro",respuestaSeleccionadaCuatro);
        encuesta.put("Suma_De_Respuestas",sumaRespuestas);

        DocumentReference ref = db.collection("USUARIOS_REGISTRADOS")
                .document(uid)
                .collection("SOLICITUDES_REALIZADAS")
                .document(parentescoSeleccionado_fragment_RecomendacionObsequio);

        Map<String, Object> nombre = new HashMap<>();

        nombre.put(nombre_fragment_RecomendacionObsequio, encuesta);

        ref.set(nombre, SetOptions.merge())
                .addOnCompleteListener(task -> {

                    Toast.makeText(getContext(),"Solicitud Guardada",Toast.LENGTH_LONG).show();
                    constraintLayout2.setVisibility(View.GONE);
                    textInputLayout.setVisibility(View.GONE);
                    Fragment_ObsequioRecomendado nextFrag= new Fragment_ObsequioRecomendado();

                    this.getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_RecomendacionObsequio1, nextFrag, null)
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

    void RegaloEvento(){

        Fragment_ObsequioRecomendado nextFrag= new Fragment_ObsequioRecomendado();
        this.getFragmentManager().beginTransaction()
                .replace(R.id.fragment_RecomendacionObsequio1, nextFrag, null)
                .addToBackStack(null)
                .commit();


    }


}
