import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Delphine Scherler & Alois Christen
 */
public class ClientSMTP {

    static final Logger LOG = Logger.getLogger(ClientSMTP.class.getName());

    final static int BUFFER_SIZE = 1024;

    /**
     * This method does the whole processing
     */
    public void sendOperation() {
        Socket clientSocket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            clientSocket = new Socket("localhost", 2021);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());

            while (true){
                String line;
                if ((line = in.readLine()) != null) {
                    System.out.println(line);
                    if(!line.equalsIgnoreCase("QUITTING...")){
                        Scanner scan = new Scanner(System.in);
                        String operation = scan.nextLine();
                        out.println(operation);
                        out.flush();
                    }else{
                        clientSocket.close();
                        break;
                    }
                }
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientSMTP.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (out != null) {
                out.close();
            }
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientSMTP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        ClientSMTP clientSMTP = new ClientSMTP();
        clientSMTP.sendOperation();

    }

}
