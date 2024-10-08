package tcpServer.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import tcpServer.controller.UserController;
import tcpServer.run.ServerRun;

public class Client implements Runnable {
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    String loginUser;
    Client clientCompetitor;
    private UserController userController = new UserController();

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public Client getClientCompetitor() {
        return clientCompetitor;
    }

    public void setcCompetitor(Client clientCompetitor) {
        this.clientCompetitor = clientCompetitor;
    }

    // Handle send data to client
    public String sendData(String data) {
        try {
            this.dos.writeUTF(data);
            return "success";
        } catch (IOException e) {
            System.err.println("Send data failed!");
            return "failed;" + e.getMessage();
        }
    }


    @Override
    public void run() {

        String received;
        boolean isRunning = true;

        while (!ServerRun.isShutDown && isRunning) {
            try {
                // receive the request from client
                received = dis.readUTF();
                System.out.println(received);
                String type = received.split(";")[0];

                switch (type) {
                    case "LOGIN":
                        onReceiveLogin(received);
                        break;
                    case "REGISTER":
                        onReceiveRegister(received);
                        break;
                    case "GET_LIST_ONLINE":
                        onReceiveGetListOnline();
                        break;
                    case "GET_INFO_USER":
                        onReceiveGetInfoUser(received);
                        break;
                    case "LOGOUT":
                        onReceiveLogout();
                        break;
                    case "CLOSE":
                        onReceiveClose();
                        break;
                    case "EXIT":
                        isRunning = false;
                }

            } catch (IOException ex) {

                // leave room if needed
//                onReceiveLeaveRoom("");
                break;
            }
        }

        try {
            // closing resources
            this.socket.close();
            this.dis.close();
            this.dos.close();
            System.out.println("- Client disconnected: " + socket);

            // remove from clientManager
            ServerRun.clientManager.remove(this);

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void onReceiveRegister(String received) {
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        // reigster
        String result = userController.register(username, password);

        // send result
        sendData("REGISTER" + ";" + result);
    }

    private void onReceiveLogin(String received) {
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        // check login
        String result = userController.login(username, password);

        if (result.split(";")[0].equals("success")) {
            // set login user
            this.loginUser = username;
        }

        // send result
        sendData("LOGIN" + ";" + result);
        onReceiveGetListOnline();
    }

    private void onReceiveGetListOnline() {

    }

    private void onReceiveGetInfoUser(String received) {

    }

    private void onReceiveLogout() {

    }

    private void onReceiveClose() {

    }


}
