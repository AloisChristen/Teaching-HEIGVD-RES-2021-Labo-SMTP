package ch.heigvd.res.config;

/**
 * Exception that can be thrown by ConfigReader
 * @authors Aloïs Christen & Delphine Scherler
 * @date 2021/05/08
 */
public class ConfigReadException extends RuntimeException{
    public ConfigReadException(String msg){
        super(msg);
    }
}
