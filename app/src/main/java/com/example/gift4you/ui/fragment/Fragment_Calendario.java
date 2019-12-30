package com.example.gift4you.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gift4you.MenuPrincipal;
import com.example.gift4you.R;
import com.example.gift4you.model.Evento;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


import com.allyants.notifyme.NotifyMe;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Fragment_Calendario extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static String parentescoEvento;
    public static String tipoDeEvento;
    private AlertDialog alertDialog;
    private String uidRecordatorio, fechaClickeada, añoClickeado, diaClickeado, mesClickeado, hora_recordatorio, min_recordatorio,
            uidEvento, añoEvento, mesEvento, diaEvento, EventoSeleccionado_editar, ParentescoSelecionado_editar, formatDay, formatYear,
            uidEventoClickeado, dia_recordatorio, mes_recordatorio, año_recordatorio;
    private View vista;
    private Spinner spinnerEvento, spinnerParentesco, spinnerEvento_edicion, spinnerParentesco_edicion;
    private Button btn_eliminar_recordatorio,btn_agregar_recordatorio,btn_agregar_evento, btn_agregar_recordatorio_edicion,
            btn_eliminar_recordatorio_edicion, btn_actualizar_evento;
    private TextView tv_fecha_evento,tv_fecha_evento_edicion, tv_fecha_fragment_calendario, tv_recordatorio, tv_sin_evento, tv_evento_registrado,
            tv_recordatorio_enunciado, tv_tipoEvento, tv_parentesco, tv_referenteA, tv_recordatorio_vista,tv_enunciado_recordatorio_edicion,
            tv_recordatorio_edicion, tv_uid_evento;
    private EditText et_referente_a, et_referente_a_edicion;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference collectionRef;
    private CompactCalendarView CalendarView;
    private ProgressBar progressBar;
    private SimpleDateFormat FormatMonthyYear = new SimpleDateFormat("MMMM - yyyy", Locale.forLanguageTag("es-ES"));
    private SimpleDateFormat FormatYear = new SimpleDateFormat("yyyy", Locale.forLanguageTag("es-ES"));
    private SimpleDateFormat FormatDay = new SimpleDateFormat("dd", Locale.forLanguageTag("es-ES"));
    private int month;
    private ConstraintLayout constraintLayout_mostrar_Evento;
    private Date date = new Date();
    private Calendar now = Calendar.getInstance();
    private TimePickerDialog tpd;
    private DatePickerDialog dpd;
    private ImageButton imageButtonEliminar_Evento, imageButtonEditar_Evento;
    private String uidUsuario;
    private ProgressBar progressBar_evento,progressBar_edicion;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment__calendario,container,false);

        db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        uidUsuario=mAuth.getCurrentUser().getUid();

        btn_agregar_evento=vista.findViewById(R.id.btn_agregar_evento_fragment_calendario);

        btn_agregar_evento.setOnClickListener(view ->abrir_agregar_evento());

        CalendarView = vista.findViewById(R.id.calendar_view_fragment_calendario);
        CalendarView.setUseThreeLetterAbbreviation(true);
        tv_fecha_fragment_calendario=vista.findViewById(R.id.tv_fecha_fragment_calendario);
        tv_sin_evento=vista.findViewById(R.id.tv_sin_evento_fragment_calendario);
        tv_evento_registrado=vista.findViewById(R.id.tv_evento_registrado_fragment_calendario);
        tv_tipoEvento=vista.findViewById(R.id.tv_tipoEvento_fragment_calendario);
        tv_parentesco=vista.findViewById(R.id.tv_parentesco_fragment_calendario);
        tv_referenteA=vista.findViewById(R.id.tv_referenteA_fragment_calendario);
        tv_recordatorio_vista=vista.findViewById(R.id.tv_recordatorio_fragment_calendario);
        tv_uid_evento=vista.findViewById(R.id.tv_uid_fragment_calendario);
        constraintLayout_mostrar_Evento=vista.findViewById(R.id.constraintLayout_mostrar_evento_regis_fragment_calendario);
        imageButtonEditar_Evento=vista.findViewById(R.id.imageButton_editar_fragment_calendario);
        imageButtonEliminar_Evento=vista.findViewById(R.id.imageButton_eliminar_fragment_calendario);
        progressBar=vista.findViewById(R.id.progress_bar_fragment_calendario);

        imageButtonEditar_Evento.setOnClickListener(view ->abrirEditarEvento());



        //gett current date and set it to DatePickerDialog
        dpd=DatePickerDialog.newInstance(
                Fragment_Calendario.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        //initialize timepickerdialog with current time
        tpd=TimePickerDialog.newInstance(
                Fragment_Calendario.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND),
                false
        );

        tv_fecha_fragment_calendario.setText(FormatMonthyYear.format(date));
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        month = cal.get(Calendar.MONTH);
        formatDay=FormatDay.format(date);
        formatYear=FormatYear.format(date);
        fechaClickeada=formatDay+"/"+(month+1)+"/"+formatYear;
        diaClickeado=formatDay;
        mesClickeado= String.valueOf((month+1));
        añoClickeado=formatYear;

        // define un oyente para recibir devoluciones de llamada cuando ocurren ciertos eventos.
        CalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                progressBar.setVisibility(View.VISIBLE);
                formatDay=FormatDay.format(dateClicked);
                formatYear=FormatYear.format(dateClicked);

                Calendar cal = Calendar.getInstance();
                cal.setTime(dateClicked);
                month = cal.get(Calendar.MONTH);

                fechaClickeada=formatDay+"/"+(month+1)+"/"+formatYear;
                diaClickeado=formatDay;
                mesClickeado= String.valueOf((month+1));
                añoClickeado=formatYear;

                uidEventoClickeado=dateClicked.toString().trim();

                cargarEventoRegistradoVistaPrincipal();

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

                tv_fecha_fragment_calendario.setText(FormatMonthyYear.format(firstDayOfNewMonth));
            }
        });

        cargarEventoRegistradosEnCalendario();

        imageButtonEliminar_Evento.setOnClickListener(v -> {
            NotifyMe.cancel(getApplicationContext(),uidRecordatorio);
            eliminarEvento();

        });

        return vista;
    }


    void eliminarEvento(){
        DocumentReference docRespuestasRef=db.collection("USUARIOS_REGISTRADOS")
                .document(uidUsuario)
                .collection("EVENTOS_REGISTRADOS")
                .document(uidEventoClickeado);
        docRespuestasRef.delete();
        cargarEventoRegistradoVistaPrincipal();
        Toast.makeText(getContext(),"Evento Eliminado",Toast.LENGTH_LONG).show();
    }

    void cargarEventoRegistradosEnCalendario(){

        collectionRef=db.collection("USUARIOS_REGISTRADOS")
                .document(uidUsuario)
                .collection("EVENTOS_REGISTRADOS");

        collectionRef.addSnapshotListener(getActivity(), (queryDocumentSnapshots, e) -> {

            if (e != null) {
                return;
            }

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                Evento evento = documentSnapshot.toObject(Evento.class);

                evento.setUid(documentSnapshot.getId());

                uidEvento = evento.getUid();
                añoEvento=evento.getAño();
                mesEvento=evento.getMes();
                diaEvento=evento.getDia();

                int año=Integer.parseInt(añoEvento);
                int mes=Integer.parseInt(mesEvento);
                int dia=Integer.parseInt(diaEvento);

                Event event = new Event(Color.GREEN, getTimeInMillis(dia,(mes-1),año), uidEvento);
                CalendarView.addEvent(event);

            }

        });


    }
    void cargarEventoRegistradoVistaPrincipal(){

        DocumentReference docRespuestasRef=db.collection("USUARIOS_REGISTRADOS")
                .document(uidUsuario)
                .collection("EVENTOS_REGISTRADOS")
                .document(uidEventoClickeado);

        docRespuestasRef.get().addOnSuccessListener(documentSnapshot -> {

            progressBar.setVisibility(View.GONE);
            if (documentSnapshot.exists()) {
                btn_agregar_evento.setVisibility(View.GONE);
                constraintLayout_mostrar_Evento.setVisibility(View.VISIBLE);
                imageButtonEditar_Evento.setVisibility(View.VISIBLE);
                imageButtonEliminar_Evento.setVisibility(View.VISIBLE);
                tv_evento_registrado.setVisibility(View.VISIBLE);
                tv_sin_evento.setVisibility(View.GONE);
                tv_tipoEvento.setText(documentSnapshot.get("Tipo_Evento").toString());
                tv_parentesco.setText(documentSnapshot.get("Parentesco").toString());
                tv_referenteA.setText(documentSnapshot.get("Referente_A").toString());
                tv_recordatorio_vista.setText(documentSnapshot.get("Recordatorio").toString());
                tv_uid_evento.setText(documentSnapshot.get("Uid").toString());
            }else{
                btn_agregar_evento.setVisibility(View.VISIBLE);
                constraintLayout_mostrar_Evento.setVisibility(View.GONE);
                imageButtonEditar_Evento.setVisibility(View.GONE);
                imageButtonEliminar_Evento.setVisibility(View.GONE);
                tv_evento_registrado.setVisibility(View.GONE);
                tv_sin_evento.setVisibility(View.VISIBLE);
            }

        });


    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        int convertirMesActual=monthOfYear+1;

        dia_recordatorio= String.valueOf(dayOfMonth);
        mes_recordatorio= String.valueOf(convertirMesActual);
        año_recordatorio= String.valueOf(year);

        now.set(Calendar.YEAR,year);
        now.set(Calendar.MONTH,monthOfYear);
        now.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        tpd.show(getActivity().getFragmentManager(),"Timepickerdialog");

    }
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        hora_recordatorio= String.valueOf(hourOfDay);
        min_recordatorio= String.valueOf(minute);



        now.set(Calendar.HOUR_OF_DAY,hourOfDay);
        now.set(Calendar.MINUTE,minute);
        now.set(Calendar.SECOND,second);

        if(tv_recordatorio_edicion!=null){
            tv_recordatorio_edicion.setText(dia_recordatorio+"/"+mes_recordatorio+"/"+año_recordatorio+" a las "+hora_recordatorio+":"+min_recordatorio);

            uidRecordatorio=tv_recordatorio_edicion.getText().toString();
            tv_enunciado_recordatorio_edicion.setVisibility(View.VISIBLE);
            btn_eliminar_recordatorio_edicion.setVisibility(View.VISIBLE);

        }
        if(tv_recordatorio!=null){
            tv_recordatorio.setText(dia_recordatorio+"/"+mes_recordatorio+"/"+año_recordatorio+" a las "+hora_recordatorio+":"+min_recordatorio);

            uidRecordatorio=tv_recordatorio.getText().toString();
            btn_eliminar_recordatorio.setVisibility(View.VISIBLE);
            tv_recordatorio_enunciado.setVisibility(View.VISIBLE);
        }


}
    public static long getTimeInMillis(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }



    void abrir_agregar_evento(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);

        final View addView = LayoutInflater.from(getActivity()).inflate(R.layout.agregar_evento,null);

        tv_fecha_evento=addView.findViewById(R.id.tv_fecha_agregar_evento);
        et_referente_a=addView.findViewById(R.id.et_referente_a_agregar_evento);

        spinnerEvento=addView.findViewById(R.id.spinner_evento_agregar_evento);
        spinnerParentesco=addView.findViewById(R.id.spinner_parentesco_agregar_evento);

        tv_recordatorio_enunciado=addView.findViewById(R.id.tv_recordatorio_enunciado_agregar_evento);
        tv_recordatorio=addView.findViewById(R.id.tv_recordatorio_agregar_evento);

        btn_agregar_recordatorio=addView.findViewById(R.id.btn_agregar_recordatorio_agregar_evento);
        btn_eliminar_recordatorio=addView.findViewById(R.id.btn_eliminar_recordatorio_agregar_evento);

        progressBar_evento=addView.findViewById(R.id.progress_bar_agregar_evento);

        btn_agregar_recordatorio.setOnClickListener(v -> {
            dpd.show(getActivity().getFragmentManager(),"Timepickerdialog");
        });

        btn_eliminar_recordatorio.setOnClickListener(v -> {
            NotifyMe.cancel(getApplicationContext(),uidRecordatorio);
            tv_recordatorio.setText("Sin Recordatorio");
            tv_recordatorio_enunciado.setVisibility(View.GONE);
            btn_eliminar_recordatorio.setVisibility(View.GONE);
        });

        tv_fecha_evento.setText(fechaClickeada);

        SpinnerEvento();
        SpinnerParentesco();

        Button btn_RegistrarEvento = addView.findViewById(R.id.btn_ingresar_agregar_evento);

        btn_RegistrarEvento.setOnClickListener(v -> registrarEvento());

        builder.setView(addView);
        alertDialog=builder.create();
        alertDialog.show();
    }
    void SpinnerEvento(){

        String [] EventosDisponibles={"Seleccionar Evento","Aniversario","Baby Shower","Cumpleaños","Matrimonio"};

        ArrayAdapter<String> eventosAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, EventosDisponibles);

        spinnerEvento.setAdapter(eventosAdapter);

        spinnerEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                tipoDeEvento=adapterView.getItemAtPosition(posicion).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    void SpinnerParentesco(){

        String [] DestinarioSeleccionado={"Seleccionar","Madre","Padre","Hijo(a)","Hermano(a)","Novia(o)","Amigo(a)","Otro"};

        ArrayAdapter<String> DestinarioAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,DestinarioSeleccionado);

        spinnerParentesco.setAdapter(DestinarioAdapter);

        spinnerParentesco.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                parentescoEvento=adapterView.getItemAtPosition(posicion).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    void registrarEvento(){

        progressBar_evento.setVisibility(View.VISIBLE);
        if(tv_recordatorio.getText().toString().equals("Sin Recordatorio")){
            Map<String, Object> respuestas= new HashMap<>();
            respuestas.put("Año",añoClickeado);
            respuestas.put("Mes",mesClickeado);
            respuestas.put("Dia",diaClickeado);
            respuestas.put("Recordatorio",tv_recordatorio.getText().toString().trim());
            respuestas.put("Parentesco",parentescoEvento);
            respuestas.put("Referente_A",et_referente_a.getText().toString().trim());
            respuestas.put("Tipo_Evento",tipoDeEvento);
            respuestas.put("Uid",uidEventoClickeado);

            db.collection("USUARIOS_REGISTRADOS")
                    .document(uidUsuario)
                    .collection("EVENTOS_REGISTRADOS")
                    .document(uidEventoClickeado)
                    .set(respuestas)
                    .addOnCompleteListener(task -> {
                        Toast.makeText(getContext(),"Evento Guardada",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(),"Error de Conexion",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    });
        }else{
            Map<String, Object> respuestas= new HashMap<>();
            respuestas.put("Año",añoClickeado);
            respuestas.put("Mes",mesClickeado);
            respuestas.put("Dia",diaClickeado);
            respuestas.put("Recordatorio",tv_recordatorio.getText().toString().trim());
            respuestas.put("Parentesco",parentescoEvento);
            respuestas.put("Referente_A",et_referente_a.getText().toString().trim());
            respuestas.put("Tipo_Evento",tipoDeEvento);
            respuestas.put("Uid",uidEventoClickeado);

            db.collection("USUARIOS_REGISTRADOS")
                    .document(uidUsuario)
                    .collection("EVENTOS_REGISTRADOS")
                    .document(uidEventoClickeado)
                    .set(respuestas)
                    .addOnCompleteListener(task -> {

                        guardarRecordatorio();
                        Toast.makeText(getContext(),"Evento Guardada",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(),"Error de Conexion",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                    });
        }

    }
    void guardarRecordatorio(){
        //initialize notification
        Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotifyMe notifyMe=new NotifyMe.Builder(getApplicationContext())
                .title("Recordatorio de Evento")
                .content(tipoDeEvento)
                .color(255,0,0,255)
                .led_color(255,255,255,255)
                .time(now)
                .key(uidRecordatorio)
                .addAction(new Intent(),"Aceptar")
                .addAction(intent,"Abrir App")
                .large_icon(R.mipmap.ic_launcher_round)
                .rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=2")
                .build();

    }



    void abrirEditarEvento(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);

        final View addView = LayoutInflater.from(getActivity()).inflate(R.layout.editar_evento,null);
        spinnerEvento_edicion=addView.findViewById(R.id.spinner_evento_editar_evento);
        spinnerParentesco_edicion=addView.findViewById(R.id.spinner_parentesco_editar_evento);
        et_referente_a_edicion=addView.findViewById(R.id.et_referente_a_editar_evento);
        tv_recordatorio_edicion=addView.findViewById(R.id.tv_recordatorio_editar_evento);
        tv_enunciado_recordatorio_edicion=addView.findViewById(R.id.tv_recordatorio_enunciado_editar_evento);
        btn_agregar_recordatorio_edicion=addView.findViewById(R.id.btn_agregar_recordatorio_editar_evento);
        btn_eliminar_recordatorio_edicion=addView.findViewById(R.id.btn_eliminar_recordatorio_editar_evento);
        btn_actualizar_evento=addView.findViewById(R.id.btn_ingresar_editar_evento);
        tv_fecha_evento_edicion=addView.findViewById(R.id.tv_fecha_editar_evento);
        progressBar_edicion=addView.findViewById(R.id.progress_bar_editar_evento);

        et_referente_a_edicion.setText(tv_referenteA.getText().toString());

        tv_fecha_evento_edicion.setText(fechaClickeada);

        tv_recordatorio_edicion.setText(tv_recordatorio_vista.getText().toString());

        if(!tv_recordatorio_edicion.getText().toString().equals("Sin Recordatorio")){
            tv_enunciado_recordatorio_edicion.setVisibility(View.VISIBLE);
            btn_eliminar_recordatorio_edicion.setVisibility(View.VISIBLE);
        }


        cargarRespuestaTipoEvento();
        cargarRespuestaParentesco();

        btn_agregar_recordatorio_edicion.setOnClickListener(v -> {
            dpd.show(getActivity().getFragmentManager(),"Timepickerdialog");

        });

        btn_eliminar_recordatorio_edicion.setOnClickListener(v -> {
            NotifyMe.cancel(getApplicationContext(),uidRecordatorio);
            tv_recordatorio_edicion.setText("Sin Recordatorio");
            tv_enunciado_recordatorio_edicion.setVisibility(View.GONE);
            btn_eliminar_recordatorio_edicion.setVisibility(View.GONE);
        });

        btn_actualizar_evento.setOnClickListener(v -> actualizarEvento());

        builder.setView(addView);
        alertDialog=builder.create();
        alertDialog.show();
    }
    void actualizarEvento(){

        progressBar_edicion.setVisibility(View.VISIBLE);
        if(tv_recordatorio_edicion.getText().toString().equals("Sin Recordatorio")){
            Map<String, Object> respuestas= new HashMap<>();
            respuestas.put("Año",añoClickeado);
            respuestas.put("Mes",mesClickeado);
            respuestas.put("Dia",diaClickeado);
            respuestas.put("Recordatorio",tv_recordatorio_edicion.getText().toString().trim());
            respuestas.put("Parentesco",ParentescoSelecionado_editar);
            respuestas.put("Referente_A",et_referente_a_edicion.getText().toString().trim());
            respuestas.put("Tipo_Evento",EventoSeleccionado_editar);
            respuestas.put("Uid",uidEventoClickeado);

            db.collection("USUARIOS_REGISTRADOS")
                    .document(uidUsuario)
                    .collection("EVENTOS_REGISTRADOS")
                    .document(uidEventoClickeado)
                    .update(respuestas)
                    .addOnCompleteListener(task -> {

                        Toast.makeText(getContext(),"Evento Actualizado",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                        cargarEventoRegistradoVistaPrincipal();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(),"Error de Conexion",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();

                    });
        }
        else{
            Map<String, Object> respuestas= new HashMap<>();
            respuestas.put("Año",añoClickeado);
            respuestas.put("Mes",mesClickeado);
            respuestas.put("Dia",diaClickeado);
            respuestas.put("Recordatorio",tv_recordatorio_edicion.getText().toString().trim());
            respuestas.put("Parentesco",ParentescoSelecionado_editar);
            respuestas.put("Referente_A",et_referente_a_edicion.getText().toString().trim());
            respuestas.put("Tipo_Evento",EventoSeleccionado_editar);
            respuestas.put("Uid",uidEventoClickeado);

            db.collection("USUARIOS_REGISTRADOS")
                    .document(uidUsuario)
                    .collection("EVENTOS_REGISTRADOS")
                    .document(uidEventoClickeado)
                    .update(respuestas)
                    .addOnCompleteListener(task -> {
                        actualizarRecordatorio();
                        Toast.makeText(getContext(),"Evento Actualizado",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();
                        cargarEventoRegistradoVistaPrincipal();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(),"Error de Conexion",Toast.LENGTH_LONG).show();
                        alertDialog.dismiss();

                    });
        }

    }
    void cargarRespuestaTipoEvento(){

        DocumentReference docRespuestasRef=db.collection("USUARIOS_REGISTRADOS")
                .document(uidUsuario)
                .collection("EVENTOS_REGISTRADOS")
                .document(uidEventoClickeado);

        docRespuestasRef.get().addOnSuccessListener(documentSnapshot -> {

            String Evento=documentSnapshot.get("Tipo_Evento").toString();

            List<String> Evento_Seleccionado = new ArrayList<>();

            Evento_Seleccionado.add(Evento);

            if(Evento.equals("Aniversario")){
                Evento_Seleccionado.add("Baby Shower");
                Evento_Seleccionado.add("Cumpleaños");
                Evento_Seleccionado.add("Matrimonio");

            }
            if(Evento.equals("Baby Shower")){
                Evento_Seleccionado.add("Cumpleaños");
                Evento_Seleccionado.add("Matrimonio");
                Evento_Seleccionado.add("Aniversario");
            }
            if(Evento.equals("Cumpleaños")){
                Evento_Seleccionado.add("Matrimonio");
                Evento_Seleccionado.add("Aniversario");
                Evento_Seleccionado.add("Baby Shower");

            }
            if(Evento.equals("Matrimonio")){
                Evento_Seleccionado.add("Aniversario");
                Evento_Seleccionado.add("Baby Shower");
                Evento_Seleccionado.add("Cumpleaños");

            }

            ArrayAdapter<String> adapterUno = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Evento_Seleccionado);
            adapterUno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerEvento_edicion.setAdapter(adapterUno);
        });

        spinnerEvento_edicion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                EventoSeleccionado_editar=adapterView.getItemAtPosition(posicion).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    void cargarRespuestaParentesco(){

        DocumentReference docRespuestasRef=db.collection("USUARIOS_REGISTRADOS")
                .document(uidUsuario)
                .collection("EVENTOS_REGISTRADOS")
                .document(uidEventoClickeado);

        docRespuestasRef.get().addOnSuccessListener(documentSnapshot -> {

            String Parentesco=documentSnapshot.get("Parentesco").toString();

            List<String> Parentesco_Seleccionado = new ArrayList<>();

            Parentesco_Seleccionado.add(Parentesco);

            if(Parentesco.equals("Madre")){
                Parentesco_Seleccionado.add("Padre");
                Parentesco_Seleccionado.add("Hijo(a)");
                Parentesco_Seleccionado.add("Hermano(a)");
                Parentesco_Seleccionado.add("Novia(o)");
                Parentesco_Seleccionado.add("Amigo(a)");
                Parentesco_Seleccionado.add("Otro");

            }
            if(Parentesco.equals("Padre")){
                Parentesco_Seleccionado.add("Madre");
                Parentesco_Seleccionado.add("Hijo(a)");
                Parentesco_Seleccionado.add("Hermano(a)");
                Parentesco_Seleccionado.add("Novia(o)");
                Parentesco_Seleccionado.add("Amigo(a)");
                Parentesco_Seleccionado.add("Otro");
            }
            if(Parentesco.equals("Hijo(a)")){
                Parentesco_Seleccionado.add("Madre");
                Parentesco_Seleccionado.add("Padre");
                Parentesco_Seleccionado.add("Hermano(a)");
                Parentesco_Seleccionado.add("Novia(o)");
                Parentesco_Seleccionado.add("Amigo(a)");
                Parentesco_Seleccionado.add("Otro");

            }
            if(Parentesco.equals("Hermano(a)")){
                Parentesco_Seleccionado.add("Madre");
                Parentesco_Seleccionado.add("Padre");
                Parentesco_Seleccionado.add("Hijo(a)");
                Parentesco_Seleccionado.add("Novia(o)");
                Parentesco_Seleccionado.add("Amigo(a)");
                Parentesco_Seleccionado.add("Otro");

            }
            if(Parentesco.equals("Novia(o)")){
                Parentesco_Seleccionado.add("Madre");
                Parentesco_Seleccionado.add("Padre");
                Parentesco_Seleccionado.add("Hijo(a)");
                Parentesco_Seleccionado.add("Hermano(a)");
                Parentesco_Seleccionado.add("Amigo(a)");
                Parentesco_Seleccionado.add("Otro");
            }
            if(Parentesco.equals("Amigo(a)")){
                Parentesco_Seleccionado.add("Madre");
                Parentesco_Seleccionado.add("Padre");
                Parentesco_Seleccionado.add("Hijo(a)");
                Parentesco_Seleccionado.add("Hermano(a)");
                Parentesco_Seleccionado.add("Novia(o)");
                Parentesco_Seleccionado.add("Otro");

            }
            if(Parentesco.equals("Otro")){
                Parentesco_Seleccionado.add("Madre");
                Parentesco_Seleccionado.add("Padre");
                Parentesco_Seleccionado.add("Hijo(a)");
                Parentesco_Seleccionado.add("Novia(o)");
                Parentesco_Seleccionado.add("Amigo(a)");
                Parentesco_Seleccionado.add("Amigo(a)");

            }

            ArrayAdapter<String> adapterUno = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Parentesco_Seleccionado);
            adapterUno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerParentesco_edicion.setAdapter(adapterUno);
        });

        spinnerParentesco_edicion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long l) {

                ParentescoSelecionado_editar=adapterView.getItemAtPosition(posicion).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    void actualizarRecordatorio(){
        //initialize notification
        Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotifyMe notifyMe=new NotifyMe.Builder(getApplicationContext())
                .title("Recordatorio de Evento")
                .content(EventoSeleccionado_editar)
                .color(255,0,0,255)
                .led_color(255,255,255,255)
                .time(now)
                .key(uidRecordatorio)
                .addAction(new Intent(),"Aceptar")
                .addAction(intent,"Abrir App")
                .large_icon(R.mipmap.ic_launcher_round)
                .rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=2")
                .build();

    }


}
