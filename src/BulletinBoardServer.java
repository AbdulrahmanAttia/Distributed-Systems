import com.jcraft.jsch.JSch;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;


public class BulletinBoardServer implements Runnable {

    private String portNumber;
    private int numberOfClients;
    public  BulletinBoardServer(String portNumber, int numberOfClients){
        this.portNumber = portNumber;
        this.numberOfClients = numberOfClients;

    }
    @Override
    public void run(){
        List<Thread> threads = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(portNumber))) {
            for(int i = 0; i < numberOfClients; i++){
                BulletinBoardServerThread e = new BulletinBoardServerThread(serverSocket.accept());
                e.start();
               threads.add(e);
           }

        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

}
