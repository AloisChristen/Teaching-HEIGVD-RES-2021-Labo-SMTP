package ch.heigvd.res;

import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe implémentant le Protocol SMTP
 * @authors Aloïs Christen & Delphine Scherler
 * @date 2021/05/08
 */
public class ClientSMTP {

    static final Logger LOG = Logger.getLogger(ClientSMTP.class.getName());

    private Socket clientSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private STEP currentStep;
    private String serverSMTPaddress;
    private int port;
    private static final String endLine = "\r\n";
    private Mail mail;

    /**
     * Class interne permettant de savoir à quelle
     * étape du protocole le client est
     */
    private enum STEP {
        CONNECTION, EHLO, MAIL_FROM, RCPT_TO, DATA, QUIT, END;
        private static STEP[] vals = values();

        /**
         * @return la prochaine étape
         */
        public STEP next()
        {
            return vals[(this.ordinal()+1) % vals.length];
        }
    }

    public ClientSMTP(String serverSMTPaddress, int port) {
        this.serverSMTPaddress = serverSMTPaddress;
        this.port = port;
    }

    /**
     * Se connect au serveur cible
     * @throws IOException
     */
    private void connect() throws IOException {
            clientSocket = new Socket(serverSMTPaddress, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            String line = in.readLine();
            if(line.startsWith("220 ")){
                // OK
            } else {
                currentStep = STEP.END;
                throw new IOException("Connection refused");
            }
    }

    /**
     * Envoie le nom du client au serveur
     * @throws IOException
     */
    private void sendEHLO() throws IOException {
        out.print("EHLO clientSMTP" + endLine);
        out.flush();
        String line;
        Boolean moreOptions = true;
        while(moreOptions && (line = in.readLine()) != null ){
            if(line.startsWith("250 ")){
                moreOptions = false;
            } else if (line.startsWith("250-")){
                // More options to listen to
            } else {
                currentStep = STEP.END;
                throw new IOException("Problem with server options");
            }
        }
    }

    /**
     * Envoie la provenance du mail
     * @throws IOException
     */
    private void sendMailFromInfo() throws IOException {
        out.print("MAIL FROM: " + mail.mail_from + endLine);
        out.flush();
        String line = in.readLine();
        if(line.startsWith("250 ")){
            // OK
        } else if (line.startsWith("550 ")){
            LOG.log(Level.WARNING, "Invalid mail from address (" + mail.mail_from + ")");
        } else {
            currentStep = STEP.END;
            throw new IOException("Unsupported server response in step MAIL FROM");
        }
    }

    /**
     * Envoie les destinaitaires du mail
     * @throws IOException
     */
    private void sendRCPT_TOInfo() throws IOException {
        for(String contact : mail.rcpt_to){
            out.print("RCPT TO: " + contact + endLine);
            out.flush();
            String line = in.readLine();
            if(line.startsWith("250 ")){
                // OK
            } else if (line.startsWith("550 ")){
                LOG.log(Level.WARNING, "Contact " + contact + " is invalid");
            } else {
                currentStep = STEP.END;
                LOG.log(Level.SEVERE, "Something went terribly wrong");
                throw new IOException("Unsupported server response in step RCPT TO");
            }
        }


    }

    /**
     * Envoie les données affichées du mail
     * (From, to, date, subject, text)
     * @throws IOException
     */
    private void sendData() throws IOException {
        out.print("DATA" + endLine);
        out.flush();
        String line = in.readLine();
        if(!line.startsWith("354 ")){
            currentStep = STEP.END;
            throw new IOException("Server refused data");
        } else {
            out.print("Content-Type: text/plain; charset=utf-8" + endLine);
            out.print("From: " + mail.data_from + endLine);
            out.print("To: " + String.join(", ", mail.data_to) + endLine); // Envoyer à tous les destinataire
            out.print("Subject:  =?utf-8?B?" + Base64.getEncoder().encodeToString(mail.subject.getBytes(StandardCharsets.UTF_8))  + "?= " + endLine);
            out.print("Date: " + mail.date + endLine);
            out.print(endLine + mail.text); // Double endline ?
            out.print(endLine + "." + endLine);
            out.flush();
            line = in.readLine();
            if(line.startsWith("250 ")){
                // OK
            } else {
                currentStep = STEP.END;
                throw new IOException("Unsupported server response in step DATA");
            }
        }
    }

    /**
     * Termine la connexion
     * @throws IOException
     */
    private void quit() throws IOException {
        out.print("QUIT" + endLine);
        out.flush();
        String line = in.readLine();
        if(line.startsWith("221 ")){
            // OK
        } else {
            currentStep = STEP.END;
            throw new IOException("Unsupported server response in step QUIT");
        }
    }

    /**
     * Envoie un mail au serveur.
     * Applique le protocole en entier
     * @param mail le mail a envoyer
     */
    public void sendMail(Mail mail) {
        LOG.log(Level.INFO, "Sending mail to " + String.join(", ", mail.rcpt_to));
        this.mail = mail;
        currentStep = STEP.CONNECTION;
        try {
        for(; currentStep != STEP.END; currentStep = currentStep.next()){
            switch(currentStep) {
                case CONNECTION:
                    connect();
//                    LOG.log(Level.INFO, "Connecting to SMTP server");
                    break;
                case EHLO:
                    sendEHLO();
//                    LOG.log(Level.INFO, "HandShake with EHLO");
                    break;
                case MAIL_FROM:
                    sendMailFromInfo();
//                    LOG.log(Level.INFO, "Sending mail from infos");
                    break;
                case RCPT_TO:
                    sendRCPT_TOInfo();
//                    LOG.log(Level.INFO, "Sending recepters infos");
                    break;
                case DATA:
                    sendData();
//                    LOG.log(Level.INFO, "Sending mail body");
                    break;
                case QUIT:
                    quit();
//                    LOG.log(Level.INFO, "Close connection");
                    break;
                case END:
                default:
                    break;
            }
        }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }finally {
            // Close socket and reader/printer
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientSMTP.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
            if (out != null) {
                out.close();
            }
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientSMTP.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
    }


}
