package src;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VectorClockProcess extends Process {
    private int[] vectorClock;
    private int numProcesses;
    private Random random;
    
    public VectorClockProcess(int processId, int port, List<Integer> neighborPorts, int numProcesses) {
        super(processId, port, neighborPorts);
        this.numProcesses = numProcesses;
        this.vectorClock = new int[numProcesses];
        Arrays.fill(vectorClock, 0);
        this.random = new Random(processId * 1000 + System.currentTimeMillis());
    }
    
    @Override
    public void run() {
        try {
            System.out.println("\n=== INICIANDO PROCESSO " + processId + " (Vetorial) ===");
            System.out.println("Vetor inicial: " + Arrays.toString(vectorClock));
            
            initializeConnections();
            Thread.sleep(2000);
            
            for (int i = 1; i <= 3 && running; i++) {
                Thread.sleep(random.nextInt(1000) + 500);
                
                vectorClock[processId]++;
                logEvent("EVENTO INTERNO " + i, null);
                
                if (!neighborPorts.isEmpty() && !clientSockets.isEmpty()) {
                    Integer[] portsArray = clientSockets.keySet().toArray(new Integer[0]);
                    int neighborPort = portsArray[random.nextInt(portsArray.length)];
                    
                    Message message = new Message(processId, "Msg #" + i + " de P" + processId);
                    vectorClock[processId]++;
                    message.setVectorTimestamp(vectorClock.clone());
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
        String logMsg = String.format("[VETORIAL] P%d - %s - Vetor: %s",
                processId, type, Arrays.toString(vectorClock));
        if (message != null) {
            logMsg += " | Conteúdo: " + message.getContent() + 
                     " | Vetor enviado: " + Arrays.toString(message.getVectorTimestamp());
        }
        System.out.println(logMsg);
    }
    
    @Override
    protected void processIncomingMessage(Message message) {
        int[] receivedVector = message.getVectorTimestamp();
        
        for (int j = 0; j < numProcesses; j++) {
            vectorClock[j] = Math.max(vectorClock[j], receivedVector[j]);
        }
        vectorClock[processId]++;
        
        String logMsg = String.format("[VETORIAL] P%d - RECEBIDO de P%d | " +
                "Vetor recebido: %s, Meu vetor atual: %s | Conteúdo: %s",
                processId, message.getSenderId(),
                Arrays.toString(receivedVector),
                Arrays.toString(vectorClock),
                message.getContent());
        
        System.out.println(logMsg);
    }
    
    @Override
    protected void sendMessage(int destinationPort, Message message) throws IOException {
        super.sendMessage(destinationPort, message);
    }
}
