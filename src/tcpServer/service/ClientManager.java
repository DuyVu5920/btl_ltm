package tcpServer.service;

import java.util.ArrayList;

public class ClientManager {
    private static ArrayList<Client> clients;

    public ClientManager() {
        clients = new ArrayList<>();
    }

    public boolean add(Client client) {
        if (!clients.contains(client)) {
            clients.add(client);
            return true;
        }
        return true;
    }

    public boolean remove(Client client) {
        if (clients.contains(client)) {
            clients.remove(client);
            return true;
        }
        return false;
    }

    public Client find(String username) {
        for (Client client : clients) {
            if (client.getLoginUser()!= null && client.getLoginUser().equals(username)) {
                return client;
            }
        }
        return null;
    }

    public void broadcast(String msg) {
        for (Client client : clients) {
            client.sendData(msg);
        }
    }

    public void sendToAClient (String username, String msg) {
        for (Client client : clients) {
            if (client.getLoginUser().equals(username)) {
                client.sendData(msg);
            }
        }
    }

    public int getSize() {
        return clients.size();
    }

    public String getListUserOnline () {
        StringBuilder result = new StringBuilder("success;" + (clients.size()) + ";");
        for (Client client : clients) {
            result.append(client.getLoginUser()).append(";");
        }
        return result.toString();
    }
}
