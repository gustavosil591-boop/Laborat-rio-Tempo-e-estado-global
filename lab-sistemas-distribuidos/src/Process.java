package src;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class Process implements Runnable {
    protected int processId;
    protected int port;
    protected List<Integer> neighborPorts;
    protected ServerSocket serverSocket;
    protected Map<Integer, Socket> clientSockets;
    protected Map<Integer, ObjectOutputStream> outputs;
    protected volatile boolean running = true;
    
    public Process(int processId, int port, List<Integer> neighborPorts) {
        this.processId = processId;
        this.port = port;
        this.neighborPorts = new ArrayList<>(neighborPorts);
        this.clientSockets = new ConcurrentHashMap<>();
        this.outputs = new ConcurrentHashMap<>();
    }
    
    protected void initializeConnections() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("[P" + processId + "] Ouvindo na porta " + port);
        
        for (int neighborPort : neighborPorts) {
            if (neighborPort > port) {
                try {
                    Thread.sleep(500);
                    Socket socket = new Socket("localhost", neighborPort);
                    clientSockets.put(neighborPort, socket);
                    outputs.put(neighborPort, new ObjectOutputStream(socket.getOutputStream()));
                    System.out.println("[P" + processId + "] Conectado à porta " + neighborPort);
                } catch (Exception e) {
                    System.err.println("[P" + processId + "] Falha: " + e.getMessage());
                }
            }
        }
        
        Thread acceptThread = new Thread(() -> {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    int clientPort = socket.getPort();
                    clientSockets.put(clientPort, socket);
                    outputs.put(clientPort, new ObjectOutputStream(socket.getOutputStream()));
                    startReceiverThread(socket);
                } catch (IOException e) {
                    if (running) e.printStackTrace();
                }
            }
        });
        acceptThread.start();
    }
    
    private void startReceiverThread(Socket socket) {
        new Thread(() -> {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                while (running) {
                    Message message = (Message) in.readObject();
                    processIncomingMessage(message);
                }
            } catch (Exception e) {
                if (running) e.printStackTrace();
            }
        }).start();
    }
    
    protected void sendMessage(int destinationPort, Message message) throws IOException {
        ObjectOutputStream out = outputs.get(destinationPort);
        if (out != null) {
            out.writeObject(message);
            out.flush();
            logEvent("ENVIADO", message);
        }
    }
    
    protected abstract void logEvent(String type, Message message);
    protected abstract void processIncomingMessage(Message message);
    
    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
