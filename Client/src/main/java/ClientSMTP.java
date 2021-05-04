import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
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

    private Socket clientSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private STEP currentStep;
    private ConfigReader config;
    private static final String serverSMTPaddress = "localhost";
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

    private enum RESPONSE {
        _250, _220, _550, _354, _221
    }

    private void connect() throws IOException, ParseException {
            clientSocket = new Socket(serverSMTPaddress, config.getMockPort());
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
            LOG.log(Level.WARNING, "Mail has invalid from email address (" + mail.getFrom() + ")");
        } else {
            currentStep = STEP.END;
            LOG.log(Level.SEVERE, "Something went terribly wrong");
            throw new IOException("Server is drunk again");
        }
    }

    private void sendRCPT_TOInfo() throws IOException {
        for(Contact contact : mail.getTo()){
            out.print("RCPT TO: " + contact.getEmail() + endLine);
            out.flush();
            String line = in.readLine();
            if(line.startsWith("250 ")){

            } else if (line.startsWith("550 ")){
                LOG.log(Level.WARNING, "Contact " + contact.getFirstName() + " " + contact.getLastname() +
                        "has invalid email address (" + contact.getEmail() + ")");
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
            out.print("From: " +mail.getFrom() + endLine);
            out.print("To: " +mail.getTo().get(0).getEmail() + endLine);
            out.print("Subject:  =?utf-8?B?" + Base64.getEncoder().encodeToString(mail.getSubject().getBytes(StandardCharsets.UTF_8))  + "?= " + endLine);
            out.print("Date: " + mail.getDate() + endLine);
            out.print(endLine + mail.getText());
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
        config = new ConfigReader();
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        ClientSMTP clientSMTP = new ClientSMTP();
        Mail mail = new Mail();
        ConfigReader cr = new ConfigReader();
        Group group = cr.getGroups().get(0);
        mail.setFrom("alois.christen@abcd.ch");
        mail.setTo(Collections.singletonList(new Contact("delphine.scherler@gmail.com", "Scherler", "Delphine")));
        mail.setText("Premier mail de l'humanité");
        mail.setSubject("Les patâtes");
        mail.setDate(new Date());
        clientSMTP.sendMail(mail);

    }

}
