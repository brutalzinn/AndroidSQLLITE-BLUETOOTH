package info.androidhive.sqlite.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.model.Note;
import info.androidhive.sqlite.view.SectionsPageAdapter;
import info.androidhive.sqlite.view.tabs.Tab1Fragment;
import info.androidhive.sqlite.view.tabs.Tab2Fragment;
import info.androidhive.sqlite.view.tabs.Tab3Fragment;


public class AndruinoActivity extends AppCompatActivity {
    /* SPP UUID service - Isso deve funcionar para a maioria dos dispositivos. */
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address; /* String para endereço MAC */
    final int handlerState = 0; /* Utilizado para identificar mensagem manipulador */
    Handler bluetoothIn;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    public static ConnectedThread mConnectedThread;
    public static String ConnectedMessage;
    private Vibrator vibrator;
    private Toolbar mToolbar, mToolbarBotton;
    private SeekBar seekBarR, seekBarG, seekBarB;
    private TextView textViewR, textViewG, textViewB;
    private boolean statusLampada = false;
    private int progressR = 0, progressG = 0, progressB = 0;
    private int vibBar = 10, vibClick = 50;
    //private NotesAdapter mAdapter;
    //private List<Note> notesList = new ArrayList<>();
    //private CoordinatorLayout coordinatorLayout;
    //private RecyclerView recyclerView;
    //private TextView noNotesView;
    public interface MyStringListener{
        public Integer computeSomething(String myString);
    }

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;
    public static Context appContext = null;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_andruino);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);




        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Relógio do papai");
        mToolbar.setSubtitle("Status Conectado!");
      //  mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);



        mToolbarBotton = (Toolbar) findViewById(R.id.inc_tb_bottom);

        mToolbarBotton.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.action_led:

                            vibrator.vibrate(vibClick);
                            mConnectedThread.write("readall" );
                            statusLampada = true;
                            Toast.makeText(AndruinoActivity.this, "Ligado!", Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.action_led_demo:

                        vibrator.vibrate(vibClick);
                        mConnectedThread.write("database DELETEALL" );
                        for (Note item:Tab1Fragment.db.getAllNotes())

                            {

                                Note n = item;

                                mConnectedThread.write("database INSERT " + n.getId() + " " + n.getTimer() + " " + n.getisEnabled() );



                        }

                        statusLampada = true;
                        Toast.makeText(AndruinoActivity.this, "Ligado!", Toast.LENGTH_SHORT).show();

                        break;

                }

                return true;
            }
        });
        mToolbarBotton.inflateMenu(R.menu.menu_bottom_andruino);

        mToolbarBotton.findViewById(R.id.iv_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AndruinoActivity.this, "Configuração precionado!", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(v.getContext(), SplashInfoActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        // Vibrador do device
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);









        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj; // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage); // manter anexando a corda at\u00e9 ~
                    int endOfLineIndex = recDataString.indexOf("~"); // determinar o fim-de-linha

                    if (endOfLineIndex > 0) { //Certifique-se que os dados antes de ~
                        // extract string
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        // get length of data received
                        int dataLength = dataInPrint.length();

                        // se ele começa com # sabemos que \u00e9 o que estamos procurando
                        if (recDataString.charAt(0) == '#') {
                        }

                        //clear all string data
                        recDataString.delete(0, recDataString.length());
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        // get Bluetooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBTState();
        this.appContext = this;

    }
    public static Context getAppContext() {
        return appContext;
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Alarmes");
        adapter.addFragment(new Tab2Fragment(), "Configurações");
        adapter.addFragment(new Tab3Fragment(), "---");
        viewPager.setAdapter(adapter);
    }
    /**
     * createBluetoothSocket
     *
     * @param device
     * @return
     * @throws IOException
     */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        // Cria conexão de saída segura com dispositivo BT usando UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }


    /**
     * onResume
     */
    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Erro na criação Socket!", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
 //       mConnectedThread.write("x");
    }


    /**
     * onPause
     */
    @Override
    public void onPause() {
        super.onPause();
        try {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }


    /**
     * checkBTState - Checks that the Android device Bluetooth is available and prompts to be turned on if off
     */
    private void checkBTState() {
        if (btAdapter == null) { // Verifica se o aparelho possui bluetooth.
            Toast.makeText(getBaseContext(), "Dispositivo não possui tecnologia bluetooth!", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    /**
     * ConnectedThread - create new class for connect thread
     */

    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        /**
         * ConnectedThread - creation of the connect thread
         *
         * @param socket
         */
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {

            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /**
         * run
         */
        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer); //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    Log.d("BluetoothBT",readMessage);
                    ConnectedMessage = readMessage;
                  //  Toast.makeText(AndruinoActivity.this, "Arduino respondeu:" + readMessage , Toast.LENGTH_SHORT).show();
                   // Toast.makeText(getBaseContext(), readMessage, Toast.LENGTH_LONG).show();
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }


        public void write(String input) {
            input = input + "\r\n";
            // converts entered String into bytes
            byte[] msgBuffer = input.getBytes();
            try {
                // write bytes over BT connection via outstream
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                // if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Erro na conexão!", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    } // FIM ConnectedThread

} // Fim class Andruino.
