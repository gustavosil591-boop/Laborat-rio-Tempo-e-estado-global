package src;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class LamportProcess extends Process {
    private int lamportClock = 0;
    private Random random;
    
    public LamportProcess(int processId, int port, List<Integer> neighborPorts) {
        super(processId, port, neighborPorts);
        this.random = new Random(processId * 1000 + System.currentTimeMillis());
    }
    
    @Override
    public void run() {
        try {
            System.out.println("\n=== INICIANDO PROCESSO " + processId + " (Lamport) ===");
            initializeConnections();
            Thread.sleep(2000);
            
            for (int i = 1; i <= 3 && running; i++) {
                Thread.sleep(random.nextInt(1000) + 500);
                
                lamportClock++;
                logEvent("EVENTO INTERNO " + i, null);
                
                if (!neighborPorts.isEmpty() && !clientSockets.isEmpty()) {
                    Integer[] portsArray = clientSockets.keySet().toArray(new Integer[0]);
                    int neighborPort = portsArray[random.nextInt(portsArray.length)];
                    
                    Message message = new Message(processId, "Msg #" + i + " de P" + processId);
                    lamportClock++;
                    message.setLamportTimestamp(lamportClock);
                    sendMessage(neighborPort, message);
                }
            }
            
            Thread.sleep(2000);
            stop();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void logEvent(String type, Message message) {
        String logMsg = String.format("[LAMPORT] P%d - %s - Relógio: %d",
                processId, type, lamportClock);
        if (message != null) {
            logMsg += " | Conteúdo: " + message.getContent() + 
                     " | Lamport: " + message.getLamportTimestamp();
        }
        System.out.println(logMsg);
    }
    
    @Override
    protected void processIncomingMessage(Message message) {
        int receivedClock = message.getLamportTimestamp();
        lamportClock = Math.max(lamportClock, receivedClock) + 1;
        
        String logMsg = String.format("[LAMPORT] P%d - RECEBIDO de P%d | " +
                "Relógio recebido: %d, Meu relógio atual: %d | Conteúdo: %s",
                processId, message.getSenderId(),
                receivedClock, lamportClock,
                message.getContent());
        System.out.println(logMsg);
    }
    
    @Override
    protected void sendMessage(int destinationPort, Message message) throws IOException {
        super.sendMessage(destinationPort, message);
    }
}
