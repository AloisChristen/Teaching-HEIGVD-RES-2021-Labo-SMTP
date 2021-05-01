import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Mailer {

    private Mail email;
    private Socket smtpServer;
    private int step;

    public Mailer(Mail email, Socket smtpServer) {
        this.email = email;
        this.smtpServer = smtpServer;
    }

    public boolean sendMail(){
        BufferedReader in = null;
        PrintWriter out = null;
        step = 0;
        return false;
    }
}
