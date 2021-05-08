package ch.heigvd.res;

import java.util.Date;
import java.util.List;

/**
 * Reprensent a mail that can be send
 * Each part of the mail is an attribute
 * @authors Aloïs Christen & Delphine Scherler
 * @date 2021/05/08
 */
public class Mail {
    public String mail_from;
    public List<String> rcpt_to;
    public String data_from;
    public List<String> data_to;
    public String subject;
    public Date date;
    public String text;

}
