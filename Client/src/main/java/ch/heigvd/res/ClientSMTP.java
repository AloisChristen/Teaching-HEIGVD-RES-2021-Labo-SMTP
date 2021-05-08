package ch.heigvd.res;

import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Delphine Scherler & Alois Christen
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

    private enum STEP {
        CONNECTION, EHLO, MAIL_FROM, RCPT_TO, DATA, QUIT, END;
        private static STEP[] vals = values();
        public STEP next()
        {
            return vals[(this.ordinal()+1) % vals.length];
        }
    }

    public ClientSMTP(String serverSMTPaddress, int port) {
        this.serverSMTPaddress = serverSMTPaddress;
        this.port = port;
    }

    private void connect() throws IOException, ParseException {
            clientSocket = new Socket(serverSMTPaddress, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            String line = in.readLine();
            if(line.startsWith("220 ")){

            } else {
                currentStep = STEP.END;
                LOG.log(Level.SEVERE, "Something went terribly wrong");
                throw new IOException("Server is drunk again");
            }
    }

    private void sendEHLO() throws IOException {
        out.print("EHLO clientSMTP" + endLine);
        out.flush();
        String line;
        Boolean moreOptions = true;
        while(moreOptions && (line = in.readLine()) != null ){
            if(line.startsWith("250 ")){
                moreOptions = false;
            } else if (line.startsWith("250-")){

            } else {
                currentStep = STEP.END;
                LOG.log(Level.SEVERE, "Something went terribly wrong");
                throw new IOException("Server is drunk again");
            }
        }
    }

    private void sendMailFromInfo() throws IOException {
        out.print("MAIL FROM: " + mail.getFrom() + endLine);
        out.flush();
        String line = in.readLine();
        if(line.startsWith("250 ")){

        } else if (line.startsWith("550 ")){
            LOG.log(Level.WARNING, "ch.heigvd.res.Mail has invalid from email address (" + mail.getFrom() + ")");
        } else {
            currentStep = STEP.END;
            LOG.log(Level.SEVERE, "Something went terribly wrong");
            throw new IOException("Server is drunk again");
        }
    }

    private void sendRCPT_TOInfo() throws IOException {
        for(String contact : mail.getTo()){
            out.print("RCPT TO: " + contact + endLine);
            out.flush();
            String line = in.readLine();
            if(line.startsWith("250 ")){

            } else if (line.startsWith("550 ")){
                LOG.log(Level.WARNING, "Contact " + contact + " is invalid");
            } else {
                currentStep = STEP.END;
                LOG.log(Level.SEVERE, "Something went terribly wrong");
                throw new IOException("Server is drunk again");
            }
        }


    }

    private void sendData() throws IOException {
        out.print("DATA" + endLine);
        out.flush();
        String line = in.readLine();
        if(!line.startsWith("354 ")){
            currentStep = STEP.END;
            LOG.log(Level.SEVERE, "Something went terribly wrong");
            throw new IOException("Server is drunk again");
        } else {
            out.print("Content-Type: text/plain; charset=utf-8" + endLine);
            out.print("From: " + mail.getFrom() + endLine);
            out.print("To: " + mail.getTo().get(0) + endLine); // Envoyer à tous les destinataire
            out.print("Subject:  =?utf-8?B?" + Base64.getEncoder().encodeToString(mail.getSubject().getBytes(StandardCharsets.UTF_8))  + "?= " + endLine);
            out.print("Date: " + mail.getDate() + endLine);
            out.print(endLine + mail.getText()); // Double endline ?
            out.print(endLine + "." + endLine);
            out.flush();
            line = in.readLine();
            if(line.startsWith("250 ")){

            } else {
                currentStep = STEP.END;
                LOG.log(Level.SEVERE, "Something went terribly wrong");
                throw new IOException("Server is drunk again");
            }
        }
    }

    private void quit() throws IOException {
        out.print("QUIT" + endLine);
        out.flush();
        String line = in.readLine();
        if(line.startsWith("221 ")){

        } else {
            currentStep = STEP.END;
            LOG.log(Level.SEVERE, "Something went terribly wrong");
            throw new IOException("Server is drunk again");
        }
    }

    /**
     * This method does the whole processing
     */
    public void sendMail(Mail mail) {
        this.mail = mail;
        currentStep = STEP.CONNECTION;
        try {
        for(; currentStep != STEP.END; currentStep = currentStep.next()){
            switch(currentStep) {
                case CONNECTION:
                    connect();
                    LOG.log(Level.INFO, "Connecting to SMTP server");
                    break;
                case EHLO:
                    sendEHLO();
                    LOG.log(Level.INFO, "HandShake with EHLO");
                    break;
                case MAIL_FROM:
                    sendMailFromInfo();
                    LOG.log(Level.INFO, "Sending mail from infos");
                    break;
                case RCPT_TO:
                    sendRCPT_TOInfo();
                    LOG.log(Level.INFO, "Sending recepters infos");
                    break;
                case DATA:
                    sendData();
                    LOG.log(Level.INFO, "Sending mail body");
                    break;
                case QUIT:
                    quit();
                    LOG.log(Level.INFO, "Close connection");
                    break;
                default:
                    break;
            }
        }
        } catch (IOException | ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }finally {
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


}
