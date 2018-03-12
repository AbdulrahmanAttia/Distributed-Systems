import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import static java.lang.Thread.sleep;

public class WriterClient{



    public static void main(String args[]) throws IOException {

        String hostName;
        int portNumber;
        int id;
        int numberOfAccesses;
        String request = "WRITE";

        hostName = args[0];
        portNumber = Integer.parseInt(args[1]);
        numberOfAccesses = Integer.parseInt(args[2]);
        id = Integer.parseInt(args[3]);


        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./Writer" + id));
        bufferedWriter.write("Client type:\n");
        bufferedWriter.write("Client name: " +  id + "\n");
        bufferedWriter.write("rSeq   sSeq\n");

        try (
                Socket kkSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(kkSocket.getInputStream()));
        ) {


            String fromServer;
            int currentAccess = 0;
            Random random = new Random();
            while (currentAccess < numberOfAccesses) {
                int wait = random.nextInt(10000);
                if (currentAccess < numberOfAccesses - 1) {
                    sleep(wait);
                }
                out.println(request + "," + id);
                fromServer = in.readLine();
                System.out.println("from Server: " + fromServer);
                String []output = fromServer.split(",");
                bufferedWriter.append(output[0] + "      " + output[1] + "\n");
                currentAccess++;
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        bufferedWriter.flush();
        bufferedWriter.close();

        System.out.println(Thread.currentThread().getName() + " Terminated" );

    }
}