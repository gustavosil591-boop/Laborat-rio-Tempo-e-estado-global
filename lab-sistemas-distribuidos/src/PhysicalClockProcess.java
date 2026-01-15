package src;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PhysicalClockProcess extends Process {
    private Random random;
    
    public PhysicalClockProcess(int processId, int port, List<Integer> neighborPorts) {
        super(processId, port, neighborPorts);
        this.random = new Random(processId * 1000 + System.currentTimeMillis());
    }
    
    @Override
    public void run() {
        try {
            System.out.println("\n=== INICIANDO PROCESSO " + processId + " (Relógio Físico) ===");
            initializeConnections();
            Thread.sleep(2000);
            
            for (int i = 1; i <= 3 && running; i++) {
                Thread.sleep(random.nextInt(1000) + 500);
                
                logEvent("EVENTO INTERNO " + i, null);
                
                if (!neighborPorts.isEmpty() && !clientSockets.isEmpty()) {
                    Integer[] portsArray = clientSockets.keySet().toArray(new Integer[0]);
                    int neighborPort = portsArray[random.nextInt(portsArray.length)];
                    
                    Message message = new Message(processId, "Msg #" + i + " de P" + processId);
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
        long timestamp = System.currentTimeMillis();
        String logMsg = String.format("[FÍSICO] P%d - %s - Timestamp: %d",
                processId, type, timestamp);
        if (message != null) {
            logMsg += " | Conteúdo: " + message.getContent();
        }
        System.out.println(logMsg);
    }
    
    @Override
    protected void processIncomingMessage(Message message) {
        long receiveTime = System.currentTimeMillis();
        long sentTime = message.getPhysicalTimestamp();
        
        String logMsg = String.format("[FÍSICO] P%d - RECEBIDO de P%d | " +
                "Enviado: %d, Recebido: %d, Delay: %dms | Conteúdo: %s",
                processId, message.getSenderId(),
                sentTime, receiveTime, receiveTime - sentTime,
                message.getContent());
        System.out.println(logMsg);
    }
}
