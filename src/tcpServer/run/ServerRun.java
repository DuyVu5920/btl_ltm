package tcpServer.run;

import tcpServer.service.Client;
import tcpServer.service.ClientManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerRun {
    private static ServerSocket serverSocket;
    public static ClientManager clientManager;

    private static final int SERVER_PORT = 2000;
    public static boolean isShutDown = false;

    public ServerRun() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server started at port " + SERVER_PORT);

            clientManager = new ClientManager();

            // create threadpool
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    10, // corePoolSize
                    100, // maximumPoolSize
                    10, // thread timeout
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(8) // queueCapacity
            );

            while (!isShutDown) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("new connection from " + socket);
                    Client client = new Client(socket);
                    clientManager.add(client);
                    executor.execute(client);
                } catch (IOException e) {
                    isShutDown = true;
                }
            }

            System.out.println("Server stopped");
            executor.shutdownNow();

        } catch (IOException e) {
            System.err.println("Could not listen on port: " + SERVER_PORT);
        }
    }

    public static void main(String[] args) {
        new ServerRun();
    }
}