package info.androidhive.sqlite.view.tabs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.bluetooth.AndruinoActivity;

import static info.androidhive.sqlite.bluetooth.AndruinoActivity.appContext;

/**
 * Created by User on 2/28/2017.
 */

public class Tab2Fragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "Tab2Fragment";
    int day , month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private Button btnTEST;
    TextView inputTextTimer;
    private String new_timer;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container, false);
        final Button button_time_picker = view.findViewById(R.id.timer_picker);
        final Button salvar = view.findViewById(R.id.save);

        final SeekBar VolumeBar = view.findViewById(R.id.VolumeBar);
        final Switch LightsOn = view.findViewById(R.id.lights);
        inputTextTimer = view.findViewById(R.id.response_timer);
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                if(new_timer != ""){

                    AndruinoActivity.mConnectedThread.write("config " + new_timer);
                    //new_timer = "";

                }

                if(VolumeBar.getProgress() > 0){
                    TimeUnit.MINUTES.toSeconds(3);
                    AndruinoActivity.mConnectedThread.write("config " + VolumeBar.getProgress());
                }

                if(LightsOn.isChecked()){

                    AndruinoActivity.mConnectedThread.write("config 0 " + "1");



                }else{

                    AndruinoActivity.mConnectedThread.write("config 0 " + "0");


                }







            }

            //   TimePickerDialog datePickerDialog = new TimePickerDialog(appContext,Tab1Fragment.this, hourFinal,minuteFinal ,true);
            //     datePickerDialog.show();





        });



        button_time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);




                DatePickerDialog datePickerDialog = new DatePickerDialog(appContext,Tab2Fragment.this, year, month, day);
                datePickerDialog.show();

            }

            //   TimePickerDialog datePickerDialog = new TimePickerDialog(appContext,Tab1Fragment.this, hourFinal,minuteFinal ,true);
            //     datePickerDialog.show();





        });
        return view;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);

        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(appContext,Tab2Fragment.this, hour,minute, DateFormat.is24HourFormat(appContext));
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
        new_timer = hourFinal +" " + minuteFinal + " "+  0  + " " + dayFinal + " " + monthFinal+ " " +yearFinal;
        inputTextTimer.setText (hourFinal +" " + minuteFinal + " "+  0  + " " + dayFinal + " " + monthFinal+ " " +yearFinal );
//        inputTextTimer.setText("TGESTE");
    }
}
