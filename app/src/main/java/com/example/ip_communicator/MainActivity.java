package com.example.ip_communicator;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

//LAN working fine
//WAN need to port forwarding server
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditTextSendMessage;
    private EditText mEditTextSetIP;
    private TextView textViewDataFromClient;

    private com.example.ip_communicator.Client client = new com.example.ip_communicator.Client();
    private boolean end = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSend = (Button) findViewById(R.id.btn_send);
        mEditTextSendMessage = (EditText) findViewById(R.id.edt_send_message);
        Button buttonOk = (Button) findViewById(R.id.btn_ok);
        mEditTextSetIP = (EditText) findViewById(R.id.edt_set_ip);
        textViewDataFromClient = (TextView) findViewById(R.id.tv_data_from_client);

        buttonSend.setOnClickListener(this);
        buttonOk.setOnClickListener(this);

        startServerSocket();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_send:
                client.sendMessage(mEditTextSendMessage.getText().toString());
                updateUI(mEditTextSendMessage.getText().toString(), "SENT TO " + client.IP + "\n");
                mEditTextSendMessage.setText("");
                break;

            case R.id.btn_ok:
                client.setIp(mEditTextSetIP.getText().toString());
                break;
        }
    }


    private void startServerSocket() {
        Thread thread = new Thread(new Runnable() {
            private String stringData = null;

            @Override
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(9002);
                    while (!end) {
                        //Server is waiting for client here, if needed
                        Socket s = ss.accept();
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        stringData = input.readLine();

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        updateUI(stringData, "RECIVED FROM " + trimIP(s.getRemoteSocketAddress().toString()) + "\n");
                        s.close();
                    }
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void updateUI(final String stringData, String from) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String s = textViewDataFromClient.getText().toString();
                if (stringData.trim().length() != 0)
                    textViewDataFromClient.setText(s + "\n" +"\n" + from + stringData);
            }
        });
    }

    private String trimIP(String ip) {

        for (int i = 1; i < ip.length(); i++){
            if (ip.charAt(i) == ':') {
                ip = ip.substring(1, i);
                break;
            }
        }
        return ip;
    }

}