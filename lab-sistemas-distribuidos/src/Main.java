package src;

import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("==========================================");
        System.out.println("  LABORATÓRIO - TEMPO E ESTADO GLOBAL");
        System.out.println("  Sistemas Distribuídos");
        System.out.println("==========================================");
        
        while (true) {
            System.out.println("\nMENU PRINCIPAL:");
            System.out.println("1 - Parte 1: Relógios Físicos");
            System.out.println("2 - Parte 2: Relógios de Lamport");
            System.out.println("3 - Parte 3: Relógios Vetoriais");
            System.out.println("0 - Sair");
            System.out.print("\nEscolha uma opção: ");
            
            int choice;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Opção inválida!");
                scanner.next();
                continue;
            }
            
            if (choice == 0) {
                System.out.println("Encerrando programa...");
                break;
            }
            
            switch (choice) {
                case 1:
                    runPhysicalClocks();
                    break;
                case 2:
                    runLamportClocks();
                    break;
                case 3:
                    runVectorClocks();
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
        
        scanner.close();
    }
    
    private static void runPhysicalClocks() throws InterruptedException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PARTE 1: RELÓGIOS FÍSICOS");
        System.out.println("=".repeat(60));
        
        List<Integer> ports = Arrays.asList(5001, 5002, 5003);
        List<Thread> threads = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            int processId = i;
            int port = ports.get(i);
            List<Integer> neighbors = new ArrayList<>();
            
            for (int p : ports) {
                if (p != port) {
                    neighbors.add(p);
                }
            }
            
            Thread t = new Thread(new PhysicalClockProcess(processId, port, neighbors));
            threads.add(t);
        }
        
        for (Thread t : threads) {
            t.start();
            Thread.sleep(1000);
        }
        
        for (Thread t : threads) {
            t.join();
        }
    }
    
    private static void runLamportClocks() throws InterruptedException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PARTE 2: RELÓGIOS DE LAMPORT");
        System.out.println("=".repeat(60));
        
        List<Integer> ports = Arrays.asList(6001, 6002, 6003);
        List<Thread> threads = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            int processId = i;
            int port = ports.get(i);
            List<Integer> neighbors = new ArrayList<>();
            
            for (int p : ports) {
                if (p != port) {
                    neighbors.add(p);
                }
            }
            
            Thread t = new Thread(new LamportProcess(processId, port, neighbors));
            threads.add(t);
        }
        
        for (Thread t : threads) {
            t.start();
            Thread.sleep(1000);
        }
        
        for (Thread t : threads) {
            t.join();
        }
    }
    
    private static void runVectorClocks() throws InterruptedException {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PARTE 3: RELÓGIOS VETORIAIS");
        System.out.println("=".repeat(60));
        
        List<Integer> ports = Arrays.asList(7001, 7002, 7003);
        List<Thread> threads = new ArrayList<>();
        int numProcesses = 3;
        
        for (int i = 0; i < numProcesses; i++) {
            int processId = i;
            int port = ports.get(i);
            List<Integer> neighbors = new ArrayList<>();
            
            for (int p : ports) {
                if (p != port) {
                    neighbors.add(p);
                }
            }
            
            Thread t = new Thread(new VectorClockProcess(processId, port, neighbors, numProcesses));
            threads.add(t);
        }
        
        for (Thread t : threads) {
            t.start();
            Thread.sleep(1000);
        }
        
        for (Thread t : threads) {
            t.join();
        }
    }
}
