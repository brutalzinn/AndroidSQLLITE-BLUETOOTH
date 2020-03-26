package info.androidhive.sqlite.view.tabs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.bluetooth.AndruinoActivity;
import info.androidhive.sqlite.database.DatabaseHelper;
import info.androidhive.sqlite.database.model.Note;
import info.androidhive.sqlite.utils.MyDividerItemDecoration;
import info.androidhive.sqlite.utils.RecyclerTouchListener;
import info.androidhive.sqlite.view.NotesAdapter;



/**
 * Created by User on 2/28/2017.
 */

public class Tab1Fragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "Tab1Fragment";

    private Button btnTEST;
    private NotesAdapter mAdapter;

    private List<Note> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;
    private Context appContext = null;
    public static DatabaseHelper db;
    public static String EXTRA_DEVICE_ADDRESS = "device_addres";
    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    String dias_semana;
    String send_resp;
    public static final String seg_v = "2", ter_v = "3", qua_v = "4", qui_v = "5", sex_v = "6", sab_v = "7", dom_v = "1";
    Dialog mDialog;
    TextView inputTextTimer;
    ToggleButton seg, ter, qua, qui, sex, sab, dom;
    public static String resposta_clock;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);
        //Se a RecyclerView estiver dentro do layout do fragment, pegue ele direto da View v inflada
        appContext = AndruinoActivity.appContext;
        recyclerView = view.findViewById(R.id.recycler_view);

        db = new DatabaseHelper(appContext);
        notesList.addAll(db.getAllNotes());


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });


//    mAdapter = new NotesAdapter(appContext, notesList);
//    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(appContext);
//    recyclerView.setLayoutManager(mLayoutManager);
//    recyclerView.setHasFixedSize(true);
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(appContext, LinearLayoutManager.VERTICAL, 16));


        mAdapter = new NotesAdapter(appContext, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(appContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(appContext, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        //   toggleEmptyNotes();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(appContext,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        try {
            recyclerView = view.findViewById(R.id.recycler_view);
            Log.d(TAG, "pegou o recycle do layout");

        } catch (Exception e) {
            Log.d(TAG, "Erro ao pegar o recycle do layout " + e);
        }

        return view;
    }


    /**
     * Inserting new note in db
     * and refreshing the list
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

//    mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void createNote(String note, int isenabled, String response, String timer, int type) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(note, isenabled, response, timer, type);

        // get the newly inserted note from db
        Note n = db.getNote(id);
        AndruinoActivity.mConnectedThread.write("database INSERT " + n.getId() + " " + timer + " " + isenabled);
        Log.d("BluetoothBT", "database INSERT " + n + " " + timer + " " + isenabled);
        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            //   toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String note, int isenabled, String response, String timer, int type, int position) {
        Note n = notesList.get(position);
        // updating note text
        n.setNote(note);
        n.setisEnabled(isenabled);
        n.setResponse(response);
        n.setTimer(timer);
        n.setType(type);
        // updating note in db

        db.updateNote(n);

        // refreshing the list
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);
        AndruinoActivity.mConnectedThread.write("database UPDATE " + n.getId() + " " + timer + " " + isenabled);
        //   Log.d("BluetoothBT","database UPDATE " + n.getId() + " "  +timer + " " + isenabled);
        // toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        AndruinoActivity.mConnectedThread.write("database DELETE " + position);

        if (AndruinoActivity.ConnectedMessage == "#DB:1") {
            Log.d("BluetoothBT", "DELETADO DO Arduino. " + position);
        }

        db.deleteNote(notesList.get(position));

        // removing the note from the list
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        //   toggleEmptyNotes();


    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Editar", "Deletar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(appContext, R.style.MyAlertDialogStyle);


        builder.setTitle("Opções");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, notesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }


    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void menu_semana_alarm() {


        dias_semana = "";

        if (seg.isChecked()) {
            dias_semana = dias_semana + seg_v + ":";

        }
        if (ter.isChecked()) {
            dias_semana = dias_semana + ter_v + ":";

        }
        if (qua.isChecked()) {
            dias_semana = dias_semana + qua_v + ":";

        }
        if (qui.isChecked()) {
            dias_semana = dias_semana + qui_v + ":";

        }
        if (sex.isChecked()) {
            dias_semana = dias_semana + sex_v + ":";

        }
        if (sab.isChecked()) {
            dias_semana = dias_semana + sab_v + ":";

        }
        if (dom.isChecked()) {
            dias_semana = dias_semana + dom_v + ":";

        }
        if (dias_semana != "") {

            dias_semana = dias_semana.substring(0, dias_semana.length() - 1);
        } else {
            dias_semana = "0:0:0:0:0:0:0";

        }


    }

    private void read_weeks(String input) {

        String[] weeks = input.split(":");

        for (String day : weeks) {

            switch (day) {

                case seg_v:
                    seg.setChecked(true);
                    menu_semana_alarm();
                    break;

                case ter_v:
                    ter.setChecked(true);
                    menu_semana_alarm();
                    break;

                case qua_v:
                    qua.setChecked(true);
                    menu_semana_alarm();
                    break;

                case qui_v:
                    qui.setChecked(true);
                    menu_semana_alarm();
                    break;

                case sex_v:
                    sex.setChecked(true);
                    menu_semana_alarm();
                    break;

                case sab_v:
                    sab.setChecked(true);
                    menu_semana_alarm();
                    break;

                case dom_v:
                    dom.setChecked(true);
                    menu_semana_alarm();
                    break;


                default:

                    return;
            }


        }

    }

    public static String read_weeks_to_user(String input) {

        String[] weeks = input.split(":");
String days = "";
        for (String day : weeks) {

            switch (day) {

                case seg_v:
days = days  + "seg";


break;
                case ter_v:
                    days = days  + ",ter";

                    break;

                case qua_v:
                    days = days  + ",qua";
                    break;

                case qui_v:
                    days = days  + ",quin";

                    break;
                case sex_v:
                    days = days  + ",sex";

                    break;

                case sab_v:
                    days = days  + ",sab";

                    break;


                case dom_v:
                    days = days  + " e dom";


                    break;


                default:

                    days += days;
            }


        }

        return days;
    }


private void setalldaysoff(){

    seg.setChecked(false);
    ter.setChecked(false);
    qua.setChecked(false);

    qui.setChecked(false);
    sex.setChecked(false);
    sab.setChecked(false);
    dom.setChecked(false);

}
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(appContext);
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

      //  android.view.View view = LayoutInflater.from(appContext)
     //           .inflate(R.layout.note_dialog, null, false);



        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(AndruinoActivity.appContext);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        final CheckBox inputIsEnabled = view.findViewById(R.id.isenabled);
        //final Button inputResponse = view.findViewById(R.id.syncronize);


        final CheckBox checkbox_recorring = view.findViewById(R.id.check_alarm_recorring);

        final HorizontalScrollView menu_alarm_recorring = view.findViewById(R.id.menu_recorrente);
        final Button button_time_picker = view.findViewById(R.id.timer_picker);
        try {
          inputTextTimer = view.findViewById(R.id.horadata);
        //   final Button inputTimer = view.findViewById(R.id.timer_picker);



        seg  = view.findViewById(R.id.seg);


         ter  = view.findViewById(R.id.ter);
         qua  = view.findViewById(R.id.qua);

         qui  = view.findViewById(R.id.qui);

          sex  = view.findViewById(R.id.sex);

          sab  = view.findViewById(R.id.sab);
          dom  = view.findViewById(R.id.dom);


        seg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu_semana_alarm();
                inputTextTimer.setText (hourFinal + ":" +  minuteFinal +  ", dia "  + dayFinal  + "/" + monthFinal + "/" + yearFinal + " + " + read_weeks_to_user(dias_semana));
                resposta_clock = dayFinal  + " " + monthFinal + " " + yearFinal + " " +hourFinal +" " + minuteFinal + " " + dias_semana;
            }
        });
        ter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu_semana_alarm();
                inputTextTimer.setText (hourFinal + ":" +  minuteFinal +  ", dia "  + dayFinal  + "/" + monthFinal + "/" + yearFinal + " + " + read_weeks_to_user(dias_semana));
                resposta_clock = dayFinal  + " " + monthFinal + " " + yearFinal + " " +hourFinal +" " + minuteFinal + " " + dias_semana;
            }
        });
        qua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu_semana_alarm();
                inputTextTimer.setText (hourFinal + ":" +  minuteFinal +  ", dia "  + dayFinal  + "/" + monthFinal + "/" + yearFinal + " + " + read_weeks_to_user(dias_semana));
                resposta_clock = dayFinal  + " " + monthFinal + " " + yearFinal + " " +hourFinal +" " + minuteFinal + " " + dias_semana;
            }
        });
        qui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu_semana_alarm();
                inputTextTimer.setText (hourFinal + ":" +  minuteFinal +  ", dia "  + dayFinal  + "/" + monthFinal + "/" + yearFinal + " + " + read_weeks_to_user(dias_semana));
                resposta_clock = dayFinal  + " " + monthFinal + " " + yearFinal + " " +hourFinal +" " + minuteFinal + " " + dias_semana;
            }
        });
        sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu_semana_alarm();
                inputTextTimer.setText (hourFinal + ":" +  minuteFinal +  ", dia "  + dayFinal  + "/" + monthFinal + "/" + yearFinal + " + " + read_weeks_to_user(dias_semana));
                resposta_clock = dayFinal  + " " + monthFinal + " " + yearFinal + " " +hourFinal +" " + minuteFinal + " " + dias_semana;
            }
        });
        sab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu_semana_alarm();
                inputTextTimer.setText (hourFinal + ":" +  minuteFinal +  ", dia "  + dayFinal  + "/" + monthFinal + "/" + yearFinal + " + " + read_weeks_to_user(dias_semana));
                resposta_clock = dayFinal  + " " + monthFinal + " " + yearFinal + " " +hourFinal +" " + minuteFinal + " " + dias_semana;
            }
        });
        dom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menu_semana_alarm();
                inputTextTimer.setText (hourFinal + ":" +  minuteFinal +  ", dia "  + dayFinal  + "/" + monthFinal + "/" + yearFinal + " + " + read_weeks_to_user(dias_semana));
                resposta_clock = dayFinal  + " " + monthFinal + " " + yearFinal + " " +hourFinal +" " + minuteFinal + " " + dias_semana;
            }
        });


        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null) {
            boolean type = (note.getType()  == 1);
            boolean isenabled = (note.getisEnabled()  == 1);

            //  mAdapter.notifyDataSetChanged();

           String[] data_to_format = note.getTimer().split(" ");


            inputNote.setText(note.getNote());
            Log.d("DEBBUGERTAG ", "note.getTimer" + note.getTimer());
            inputTextTimer.setText(data_to_format[3] +":" + data_to_format[4] + ", dia " + data_to_format[0] + "/" + data_to_format[1] + "/" + data_to_format[2]);
            resposta_clock = note.getTimer();
            checkbox_recorring.setChecked(type);

     //       mAdapter.notifyDataSetChanged();

            inputIsEnabled.setChecked(isenabled);

            if (checkbox_recorring.isChecked()) {


                menu_alarm_recorring.setVisibility(View.VISIBLE);


            } else {
                menu_alarm_recorring.setVisibility(View.GONE);

            }


           String[] timer_weekeds = note.getTimer().split(" ");
            read_weeks(timer_weekeds[5]);

        }





    checkbox_recorring.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkbox_recorring.isChecked()) {


                menu_alarm_recorring.setVisibility(View.VISIBLE);

                menu_semana_alarm();
                dayFinal = 0;
                monthFinal = 0;
                yearFinal = 0;
                resposta_clock = dayFinal + " " + monthFinal + " " + yearFinal + " " + hourFinal + " " + minuteFinal + " " + dias_semana;

                if (hourFinal != 0) {


                    inputTextTimer.setText(hourFinal + ":" + minuteFinal + ", dia " + dayFinal + "/" + monthFinal + "/" + yearFinal + " + " + read_weeks_to_user(dias_semana));


                } else {

                    if (shouldUpdate && note != null) {


                        String[] data_to_format = note.getTimer().split(" ");
                        inputTextTimer.setText(data_to_format[3] + ":" + data_to_format[4] + ", dia " + data_to_format[0] + "/" + data_to_format[1] + "/" + data_to_format[2]);
                    }


                }

            } else {
                menu_alarm_recorring.setVisibility(View.GONE);
                setalldaysoff();

                menu_semana_alarm();
                resposta_clock = dayFinal + " " + monthFinal + " " + yearFinal + " " + hourFinal + " " + minuteFinal + " " + dias_semana;
                if (hourFinal != 0) {


                    inputTextTimer.setText(hourFinal + ":" + minuteFinal + ", dia " + dayFinal + "/" + monthFinal + "/" + yearFinal + " + " + read_weeks_to_user(dias_semana));


                } else  if (shouldUpdate && note != null) {
                    String[] data_to_format = note.getTimer().split(" ");
                    inputTextTimer.setText(data_to_format[3] + ":" + data_to_format[4] + ", dia " + data_to_format[0] + "/" + data_to_format[1] + "/" + data_to_format[2]);

                }
            }

        }
    });

}
        catch (Exception e ){


            Log.d("DEBUG ", "PROBLEMA DE NULL. IGNORE");
        }
finally {


        button_time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);


                if(checkbox_recorring.isChecked()){

                    TimePickerDialog timePickerDialog = new TimePickerDialog(appContext,Tab1Fragment.this, hour,minute, DateFormat.is24HourFormat(appContext));
                    timePickerDialog.show();



                }else{


                    DatePickerDialog datePickerDialog = new DatePickerDialog(appContext,Tab1Fragment.this, year, month, day);
                    datePickerDialog.show();

                }

             //   TimePickerDialog datePickerDialog = new TimePickerDialog(appContext,Tab1Fragment.this, hourFinal,minuteFinal ,true);
           //     datePickerDialog.show();




            }
        });

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "atualizar" : "salvar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(appContext, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }






                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    int myInt = inputIsEnabled.isChecked() ? 1 : 0;
// tipo de alarme
                    int alarm_type = checkbox_recorring.isChecked() ? 1 : 0;


                    updateNote(inputNote.getText().toString(),myInt,"",resposta_clock,alarm_type, position);
                } else {
                    // create new note
                    int myInt = inputIsEnabled.isChecked() ? 1 : 0;
                    int alarm_type = checkbox_recorring.isChecked() ? 1 : 0;
                    createNote(inputNote.getText().toString(),myInt,"",resposta_clock,alarm_type);

                }
            }
        });
    }


    }

@Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);

        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(appContext,Tab1Fragment.this, hour,minute, DateFormat.is24HourFormat(appContext));
    timePickerDialog.show();

    }
    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(appContext);
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);











        hourFinal = i;
        minuteFinal = i1;

     //   Log.d("DEBUGANDOXML","year: " + yearFinal + "\n" + "month: " + monthFinal + "\n" +"day: " + dayFinal +"\n" +"hour: " + hourFinal +"\n" + "minute: " + minuteFinal);
       // textedit.setText("year: " + yearFinal + "\n" + "month: " + monthFinal + "\n" +"day: " + dayFinal +"\n" +"hour: " + hourFinal +"\n" + "minute: " + minuteFinal);
     menu_semana_alarm();
     resposta_clock = dayFinal  + " " + monthFinal + " " + yearFinal + " " +hourFinal +" " + minuteFinal + " " + dias_semana;
String resposta_clock_user = hourFinal + ":" +  new DecimalFormat("00").format(minuteFinal) +  ", dia "  + dayFinal  + "/" + monthFinal + "/" + yearFinal + " + " + read_weeks_to_user(dias_semana);
        inputTextTimer.setText (resposta_clock_user);
//        inputTextTimer.setText("TGESTE");
    }
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0

        if (db.getNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }


}
