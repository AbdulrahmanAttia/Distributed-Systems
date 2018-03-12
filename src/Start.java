import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Start {

    private static final String SERVER = "RW.server";
    private static final String SERVER_PORT = "RW.server.port";
    private static final String READERS_NUMBER = "RW.numberOfReaders";
    private static final String READER = "RW.reader";
    private static final String WRITERS_NUMBER = "RW.numberOfWriters";
    private static final String WRITER = "RW.writer";
    private static final String ACCESS_NUMBER = "RW.numberOfAccesses";
    private static final String READER_PASSWORD = "PW.reader";
    private static final String WRITER_PASSWORD = "PW.writer";


    public static void main(String args[]) throws IOException, JSchException, SftpException, InterruptedException {
        Start start = new Start();
        Properties systemProperties = new Properties();
        InputStream input = null;
        String serverIP = null;
        String serverPort = null;
        int readersNum = 0;
        List<String> readers = new ArrayList<>();
        List<String> readersPasswords = new ArrayList<>();
        int writersNum = 0;
        List<String> writers = new ArrayList<>();
        List<String> writersPasswords = new ArrayList<>();
        String accessNum = null;

        try {
            input = Start.class.getClassLoader().getResourceAsStream("system.properties");
            systemProperties.load(input);

            serverIP = systemProperties.getProperty(SERVER);
            serverPort = systemProperties.getProperty(SERVER_PORT);
            readersNum = Integer.parseInt(systemProperties.getProperty(READERS_NUMBER));
            writersNum = Integer.parseInt(systemProperties.getProperty(WRITERS_NUMBER));
            accessNum = systemProperties.getProperty(ACCESS_NUMBER);
            for (int i = 0; i < readersNum; i++) {
                readers.add(systemProperties.getProperty(READER + i));
            }
            for (int i = 0; i < readersNum; i++) {
                readersPasswords.add(systemProperties.getProperty(READER_PASSWORD + i));
            }
            for (int i = 0; i < writersNum; i++) {
                writers.add(systemProperties.getProperty(WRITER + i));
            }
            for (int i = 0; i < writersNum; i++) {
                writersPasswords.add(systemProperties.getProperty(WRITER_PASSWORD + i));
            }

        } catch (Exception e) {

        }

        int numberOfClients = readersNum + writersNum;
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("serverLogR"));
        bufferedWriter.write("Readers:\n");
        bufferedWriter.write("sSeq  oVal  rID  rNum\n");
        bufferedWriter.flush();
        bufferedWriter.close();

        bufferedWriter = new BufferedWriter(new FileWriter("serverLogW"));
        bufferedWriter.write("Writers:\n");
        bufferedWriter.write("sSeq  oVal  wID\n");
        bufferedWriter.flush();
        bufferedWriter.close();

        System.out.println(serverPort);
        BulletinBoardServer bbServer = new BulletinBoardServer(serverPort, numberOfClients);

        Thread server = new Thread(bbServer);
        server.start();
        start.startThreads(readers, readersPasswords, writers, writersPasswords, serverIP, Integer.parseInt(serverPort), Integer.parseInt(accessNum));
        
    }

    public void startThreads(List<String> readers, List<String> readersPasswords, List<String> writers, List<String> writersPasswords, String serverIP, int serverPort, int numberOfAccesses) throws JSchException, SftpException, IOException, InterruptedException {
        String host = serverIP;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < readers.size(); i++) {
            String user = readers.get(i);
            String[] userCred = user.split("@");
            String userName = userCred[0];
            String userIP = userCred[1];
            String password = readersPasswords.get(i);
            Executor e = new Executor("ReaderClient", userName, userIP, password, serverIP, serverPort, numberOfAccesses, i);
            threads.add(e);
            e.start();



        }

        for (int i = 0; i < writers.size(); i++) {
            String user = writers.get(i);
            String[] userCred = user.split("@");
            String userName = userCred[0];
           // System.out.println(userName);
            String userIP = userCred[1];
          //  System.out.println(userIP);
            String password = writersPasswords.get(i);
            Executor e = new Executor("WriterClient", userName, userIP, password, serverIP, serverPort, numberOfAccesses, i);
            threads.add(e);
            e.start();
        }

        for(int i = 0; i < threads.size(); i++){
            threads.get(i).join();
        }
    }
}
