package tcpClient.controller;

import tcpClient.run.ClientRun;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketHandler {
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    String loginUser;

    Thread listener;

    public SocketHandler() {}

    public String connect(String addr, int port) {
        try {
            // getting ip
            InetAddress ip = InetAddress.getByName(addr);

            // establish the connection with server port
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 4000);
            System.out.println("Connected to " + ip + ":" + port + ", localport:" + socket.getLocalPort());

            // obtaining input and output streams
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // close old listener
            if (listener != null && listener.isAlive()) {
                listener.interrupt();
            }

            // listen to server
            listener = new Thread(this::listen);
            listener.start();

            // connect success
            return "success";

        } catch (IOException e) {
            // connect failed
            return "failed;" + e.getMessage();
        }
    }

    private void listen() {
        boolean running = true;

        while (running) {
            try {
                // receive the data from server
                String received = dis.readUTF();

                System.out.println("RECEIVED: " + received);

                String type = received.split(";")[0];

                switch (type) {
                    case "LOGIN":
                        onReceiveLogin(received);
                        break;
                    case "REGISTER":
                        onReceiveRegister(received);
                        break;
                    case "GET_LIST_ONLINE":
                        onReceiveGetListOnline(received);
                        break;
                    case "LOGOUT":
                        onReceiveLogout(received);
                        break;
                    case "GET_INFO_USER":
                        onReceiveGetInfoUser(received);
                        break;

                    case "EXIT":
                        running = false;
                }

            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                running = false;
            }
        }

        try {
            // closing resources
            socket.close();
            dis.close();
            dos.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        // alert if connect interup
        JOptionPane.showMessageDialog(null, "Mất kết nối tới server", "Lỗi", JOptionPane.ERROR_MESSAGE);
        ClientRun.closeAllScene();
        ClientRun.openScene(ClientRun.SceneName.CONNECTSERVER);
    }

    //Handle send data to server
    public void sendData(String data) {
        try {
            dos.writeUTF(data);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /***
     * Handle from client
     */
    public void login(String username, String password) {
        // prepare data
        String data = "LOGIN" + ";" + username + ";" + password;
        // send data
        sendData(data);
    }

    public void register(String username, String password) {
        // prepare data
        String data = "REGISTER" + ";" + username + ";" + password;
        // send data
        sendData(data);
    }

    public void logout () {
        this.loginUser = null;
        sendData("LOGOUT");
    }

    public void close () {
        sendData("CLOSE");
    }

    /***
     * Handle receive data from server
     */
    private void onReceiveLogin(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {
            // hiển thị lỗi
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(ClientRun.loginView, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            // lưu user login
            this.loginUser = splitted[2];
            System.out.println(loginUser);
            JOptionPane.showMessageDialog(ClientRun.loginView,"Hello" + loginUser + "!");
        }
    }

    private void onReceiveRegister(String received) {
        // get status from data
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {
            // hiển thị lỗi
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(ClientRun.registerView, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            JOptionPane.showMessageDialog(ClientRun.registerView, "Register account successfully! Please login!");
            // chuyển scene
            ClientRun.closeScene(ClientRun.SceneName.REGISTER);
            ClientRun.openScene(ClientRun.SceneName.LOGIN);
        }
    }

    private void onReceiveLogout(String received) {
    }

    private void onReceiveGetListOnline(String received) {
    }

    private void onReceiveGetInfoUser(String received) {
    }
}
