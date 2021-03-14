package com.gmoney.fuelcheck;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private EditText mEditText1;
    private TextView startingfuelline;
    private TextView endingfuelline;
    private Button mButtonAdd;
    private TextView instructions;
    private TextView burned;
    private TextView burnrate;
    private TextView fuelcheckhistory;
    private TextView burnouttime;
    private TextView reservetimes;
    private int stepnumber = 1; //1 for getting starting fuel, 2 for ending fuel
    private int startfuelnum = 0;
    private int endfuelnum = 0;

    private long elapsedMillis = 0;
    private int seconds = 0;
    private double timesinhour = 0;
    private double burnperhour = 0;

    private Chronometer clock;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText1 = findViewById(R.id.edittext);
        startingfuelline = findViewById(R.id.textview_starting);
        endingfuelline = findViewById(R.id.textview_end);
        burned = findViewById(R.id.textview_burned);
        burnrate = findViewById(R.id.textview_burnrate);
        mEditText1.setInputType(InputType.TYPE_CLASS_NUMBER); //forces user to input only numbers

        mButtonAdd = findViewById(R.id.button_start);
        instructions = findViewById(R.id.Instructions);

        displaytest = findViewById(R.id.Instructions3);
        clock = findViewById(R.id.clock);
        fuelcheckhistory = findViewById(R.id.lastcheck);

        burnouttime = findViewById(R.id.textview_burnouttime);// getting burnout time
        reservetimes = findViewById(R.id.textview_reservetimes);


        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText1.getText().toString().length() == 0) {
                    mEditText1.setText("0");
                }
                if (stepnumber == 1) {
                    startfuelnum = Integer.parseInt(mEditText1.getText().toString());
                    startingfuelline.setText("Starting fuel: " + String.valueOf(startfuelnum));
                    instructions.setText("Enter ending fuel and press button");
                    mEditText1.setText("");
                    clock.setBase(SystemClock.elapsedRealtime());
                    clock.start();
                    startTime = SystemClock.elapsedRealtime(); // start time for fuel remaining clock
                    stepnumber++; //increment for next button press
                    fuelcheckhistory.setText("Last fuel check: " + String.valueOf(burnperhour));

                    //Reset other lines
                    endingfuelline.setText("End fuel check fuel: ");
                    burned.setText("Burned: ");
                    burnrate.setText("Burnrate: ");
                    burnouttime.setText("Burnout: ");
                    reservetimes.setText("Reserve (VFR,IFR): ");

                }
                else if (stepnumber == 2) {
                    endfuelnum = Integer.parseInt(mEditText1.getText().toString());
                    endingfuelline.setText("End fuel check fuel: " + String.valueOf(endfuelnum));
                    instructions.setText("Enter ending fuel and press button");
                    elapsedMillis = SystemClock.elapsedRealtime() - clock.getBase();
                    burnrate.setText(String.valueOf(elapsedMillis));
                    mEditText1.setText("");

                    int burnedtotal = startfuelnum - endfuelnum;
                    burned.setText("Burned: " +  String.valueOf(burnedtotal));
                    seconds = (int)elapsedMillis / 1000;
                    timesinhour = 3600 / seconds;
                    burnperhour = timesinhour * burnedtotal;
                    burnrate.setText("Burnrate: " + String.valueOf(burnperhour));
                    clock.stop();
                    clock.setBase(SystemClock.elapsedRealtime());
                    double timeremaininghrs = endfuelnum / burnperhour;

                    double timeremainingmins = timeremaininghrs * 60;
                    Calendar cl = Calendar.getInstance();
                    cl.add(Calendar.MINUTE, (int)timeremainingmins);
                    SimpleDateFormat dateFormat  = new SimpleDateFormat("HH:mm");

                    String fixedcl = dateFormat.format(cl.getTime());
                    burnouttime.setText("Burnout: " + fixedcl);

                    Calendar vfrCal = Calendar.getInstance(); //reset calendar to nows time for new calculation
                    int vfrreserve = (int) timeremainingmins - 20; // VFR flight time reserve
                    vfrCal.add(Calendar.MINUTE, vfrreserve);
                    String fixedvfr = dateFormat.format(vfrCal.getTime());

                    Calendar ifrCal = Calendar.getInstance(); //reset calendar to nows time for new calculation
                    int ifrreserve = (int) timeremainingmins - 30; // IFR flight time reserve
                    ifrCal.add(Calendar.MINUTE, ifrreserve);
                    String fixedifr = dateFormat.format(ifrCal.getTime());

                    String vfrandifr2 = vfrCal.get(Calendar.HOUR_OF_DAY) +":"+ vfrCal.get(Calendar.MINUTE)+", "+
                            ifrCal.get(Calendar.HOUR_OF_DAY) +":"+ ifrCal.get(Calendar.MINUTE);
                    String vfrandifr = fixedvfr + ", " + fixedifr;

                    reservetimes.setText("Reserve (VFR,IFR): " + vfrandifr);
                    stepnumber--;
                    endfuelnum = 0;
                    burnedtotal = 0;
                    instructions.setText("Enter new starting fuel and press button to start a new fuel check.");

                }


            }
        });
    }
}