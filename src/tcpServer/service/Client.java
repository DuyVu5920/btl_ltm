package tcpServer.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import tcpServer.controller.UserController;
import tcpServer.helper.Question;
import tcpServer.run.ServerRun;

public class Client implements Runnable {
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    String loginUser;
    Client clientCompetitor;
    Room joinedRoom;

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

    public void setClientCompetitor(Client clientCompetitor) {
        this.clientCompetitor = clientCompetitor;
    }

    public Room getJoinedRoom() {
        return joinedRoom;
    }

    public void setJoinedRoom(Room joinedRoom) {
        this.joinedRoom = joinedRoom;
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
                    // chat
                    case "INVITE_TO_CHAT":
                        onReceiveInviteToChat(received);
                        break;
                    case "ACCEPT_MESSAGE":
                        onReceiveAcceptMessage(received);
                        break;
                    case "REJECT_MESSAGE":
                        onReceiveRejectMessage(received);
                        break;
                    case "LEAVE_CHAT":
                        onReceiveLeaveChat(received);
                        break;
                    case "CHAT_MESSAGE":
                        onReceiveChatMessage(received);
                        break;

                    case "OPEN_LEADERBOARD":
                        onReceiveOpenLeaderboard();
                        break;
                    //gameplay
                    case "INVITE_TO_PLAY":
                        onReceiveInviteToPlay(received);
                        break;
                    case "ACCEPT_PLAY":
                        onReceiveAcceptPlay(received);
                        break;
                    case "REJECT_PLAY":
                        onReceiveRejectPlay(received);
                        break;
                    case "LEAVE_GAME":
                        onReceiveLeaveGame(received);
                        break;
                    case "CHECK_STATUS_USER":
                        onReceiveCheckStatusUser(received);
                        break;
                    case "START_GAME":
                        onReceiveStartGame(received);
                        break;
                    case "SUBMIT_RESULT":
                        onReceiveSubmitResult(received);
                        break;
                    case "ASK_PLAY_AGAIN":
                        onReceiveAskPlayAgain(received);
                        break;
                    case "EXIT":
                        isRunning = false;
                }

            } catch (IOException | SQLException ex) {

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
        String result = UserController.register(username, password);

        // send result
        sendData("REGISTER" + ";" + result);
    }

    private void onReceiveLogin(String received) {
        String[] splitted = received.split(";");
        String username = splitted[1];
        String password = splitted[2];

        // check login
        String result = UserController.login(username, password);

        if (result.split(";")[0].equals("success")) {
            // set login user
            this.loginUser = username;
        }

        // send result
        sendData("LOGIN" + ";" + result);
        onReceiveGetListOnline();
    }

    private void onReceiveGetListOnline() {
        String result = ServerRun.clientManager.getListUserOnline();

        // send result
        String msg = "GET_LIST_ONLINE" + ";" + result;
        ServerRun.clientManager.broadcast(msg);
    }

    private void onReceiveGetInfoUser(String received) {
        String[] splitted = received.split(";");
        String username = splitted[1];
        // get info user
        String result = UserController.getInfoUser(username);

        String status = "";
        Client client = ServerRun.clientManager.find(username);
        if (client == null) {
            status = "Offline";
        } else {
            if (client.getJoinedRoom() == null) {
                status = "Online";
            } else {
                status = "In Game";
            }
        }
        // send result
        sendData("GET_INFO_USER" + ";" + result + ";" + status);
    }

    private void onReceiveLogout() {
        this.loginUser = null;
        // send result
        sendData("LOGOUT" + ";" + "success");
        onReceiveGetListOnline();
    }

    private void onReceiveClose() {
        this.loginUser = null;
        ServerRun.clientManager.remove(this);
        onReceiveGetListOnline();
    }

    //chat
    private void onReceiveInviteToChat(String received) {
        String[] splitted = received.split(";");
        String hostUser = splitted[1];
        String invitedUser = splitted[2];

        // send result
        String msg = "INVITE_TO_CHAT;" + "success;" + hostUser + ";" + invitedUser;
        ServerRun.clientManager.sendToAClient(invitedUser, msg);
    }

    private void onReceiveAcceptMessage(String received) {
        String[] splitted = received.split(";");
        String hostUser = splitted[1];
        String invitedUser = splitted[2];

        // send result
        String msg = "ACCEPT_MESSAGE;" + "success;" + hostUser + ";" + invitedUser;
        ServerRun.clientManager.sendToAClient(hostUser, msg);
    }

    private void onReceiveRejectMessage(String received) {
        String[] splitted = received.split(";");
        String hostUser = splitted[1];
        String invitedUser = splitted[2];

        // send result
        String msg = "REJECT_MESSAGE;" + "success;" + hostUser + ";" + invitedUser;
        ServerRun.clientManager.sendToAClient(hostUser, msg);
    }

    private void onReceiveLeaveChat(String received) {
        String[] splitted = received.split(";");
        String hostUser = splitted[1];
        String invitedUser = splitted[2];

        // send result
        String msg = "LEAVE_CHAT;" + "success;" + hostUser + ";" + invitedUser;
        ServerRun.clientManager.sendToAClient(invitedUser, msg);
    }

    private void onReceiveChatMessage(String received) {
        String[] splitted = received.split(";");
        String hostUser = splitted[1];
        String invitedUser = splitted[2];
        String message = splitted[3];

        // send result
        String msg = "CHAT_MESSAGE;" + "success;" + hostUser + ";" + invitedUser + ";" + message;
        ServerRun.clientManager.sendToAClient(invitedUser, msg);
    }

    private void onReceiveOpenLeaderboard() {
        String result = UserController.getAllUsers();
        String msg = "OPEN_LEADERBOARD;" + result;
        sendData(msg);
    }

    //gameplay
    private void onReceiveInviteToPlay(String received) {
        String[] splitted = received.split(";");
        String hostUser = splitted[1];
        String invitedUser = splitted[2];

        // create new room
        joinedRoom = ServerRun.roomManager.createRoom();
        // add client
        Client c = ServerRun.clientManager.find(loginUser);
        joinedRoom.addClient(this);
        clientCompetitor = ServerRun.clientManager.find(invitedUser);

        // send result
        String msg = "INVITE_TO_PLAY;" + "success;" + hostUser + ";" + invitedUser + ";" + joinedRoom.getId();
        ServerRun.clientManager.sendToAClient(invitedUser, msg);
    }

    private void onReceiveAcceptPlay(String received) {
        String[] splitted = received.split(";");
        String hostUser = splitted[1];
        String invitedUser = splitted[2];
        String roomId = splitted[3];

        Room room = ServerRun.roomManager.find(roomId);
        joinedRoom = room;
        joinedRoom.addClient(this);

        clientCompetitor = ServerRun.clientManager.find(hostUser);

        // send result
        String msg = "ACCEPT_PLAY;" + "success;" + hostUser + ";" + invitedUser + ";" + joinedRoom.getId();
        ServerRun.clientManager.sendToAClient(hostUser, msg);

    }

    private void onReceiveRejectPlay(String received) {
        String[] splitted = received.split(";");
        String hostUser = splitted[1];
        String invitedUser = splitted[2];
        String roomId = splitted[3];

        // hostUser out room
        ServerRun.clientManager.find(hostUser).setJoinedRoom(null);
        // Delete competitor of hostUser
        ServerRun.clientManager.find(hostUser).setClientCompetitor(null);

        // delete room
        Room room = ServerRun.roomManager.find(roomId);
        ServerRun.roomManager.remove(room);

        // send result
        String msg = "REJECT_PLAY;" + "success;" + hostUser + ";" + invitedUser + ";" + room.getId();
        ServerRun.clientManager.sendToAClient(hostUser, msg);
    }

    private void onReceiveLeaveGame(String received) throws SQLException {
        String[] splitted = received.split(";");
        String user1 = splitted[1];
        String user2 = splitted[2];
        String roomId = splitted[3];

        joinedRoom.userLeaveGame(user1);

        this.clientCompetitor = null;
        this.joinedRoom = null;

        // delete room
        Room room = ServerRun.roomManager.find(roomId);
        ServerRun.roomManager.remove(room);

        // hostUser out room
        Client c = ServerRun.clientManager.find(user2);
        c.setJoinedRoom(null);
        // Delete competitor of hostUser
        c.setClientCompetitor(null);

        // send result
        String msg = "LEAVE_GAME;" + "success;" + user1 + ";" + user2;
        ServerRun.clientManager.sendToAClient(user2, msg);
    }

    private void onReceiveCheckStatusUser(String received) {
        String[] splitted = received.split(";");
        String username = splitted[1];

        String status;
        Client client = ServerRun.clientManager.find(username);
        if (client == null) {
            status = "OFFLINE";
        } else {
            if (client.getJoinedRoom() == null) {
                status = "ONLINE";
            } else {
                status = "INGAME";
            }
        }
        // send result
        sendData("CHECK_STATUS_USER" + ";" + username + ";" + status);
    }

    private void onReceiveStartGame(String received) {
        String[] splitted = received.split(";");
        String user1 = splitted[1];
        String user2 = splitted[2];
        String roomId = splitted[3];

        String question1 = Question.genQuestion();
        String question2 = Question.genQuestion();
        String question3 = Question.genQuestion();
        String question4 = Question.genQuestion();

        String data = "START_GAME;success;" + roomId + ";" + question1 + question2 + question3 + question4;
        // Send question here
        joinedRoom.resetRoom();
        joinedRoom.broadcast(data);
        joinedRoom.startGame();
    }

    private void onReceiveSubmitResult(String received) throws SQLException {
        String[] splitted = received.split(";");
        String user1 = splitted[1];
        String user2 = splitted[2];
        String roomId = splitted[3];

        if (user1.equals(joinedRoom.getClient1().getLoginUser())) {
            joinedRoom.setResultClient1(received);
        } else if (user1.equals(joinedRoom.getClient2().getLoginUser())) {
            joinedRoom.setResultClient2(received);
        }

        while (!joinedRoom.getTime().equals("00:00") && joinedRoom.getTime() != null) {
            System.out.println(joinedRoom.getTime());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }

        String data = "RESULT_GAME;success;" + joinedRoom.handleResultClient()
                + ";" + joinedRoom.getClient1().getLoginUser() + ";" + joinedRoom.getClient2().getLoginUser() + ";" + joinedRoom.getId();
        System.out.println(data);
        joinedRoom.broadcast(data);
    }

    private void onReceiveAskPlayAgain(String received) throws SQLException {
        String[] splitted = received.split(";");
        String reply = splitted[1];
        String user1 = splitted[2];

        System.out.println("client1: " + joinedRoom.getClient1().getLoginUser());
        System.out.println("client2: " + joinedRoom.getClient2().getLoginUser());

        if (user1.equals(joinedRoom.getClient1().getLoginUser())) {
            joinedRoom.setPlayAgainC1(reply);
        } else if (user1.equals(joinedRoom.getClient2().getLoginUser())) {
            joinedRoom.setPlayAgainC2(reply);
        }

        while (!joinedRoom.getWaitingTime().equals("00:00")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }

        String result = this.joinedRoom.handlePlayAgain();
        if (result.equals("YES")) {
            joinedRoom.broadcast("ASK_PLAY_AGAIN;YES;" + joinedRoom.getClient1().loginUser + ";" + joinedRoom.getClient2().loginUser);
        } else if (result.equals("NO")) {
            joinedRoom.broadcast("ASK_PLAY_AGAIN;NO;");

            Room room = ServerRun.roomManager.find(joinedRoom.getId());
            // delete room
            ServerRun.roomManager.remove(room);
            this.joinedRoom = null;
            this.clientCompetitor = null;
        } else if (result == null) {
            System.out.println("receive message failed");
        }
    }
}
