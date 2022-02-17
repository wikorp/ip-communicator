package com.example.ip_communicator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    String IP;

    public void setIp(String ip) {
        IP = ip;
    }

    public void sendMessage(final String msg) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Socket s = new Socket(IP, 9002);

                    OutputStream out = s.getOutputStream();
                    PrintWriter output = new PrintWriter(out);

                    output.println(msg);
                    output.flush();

                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}