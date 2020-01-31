package server;

public class NoResponseException extends Exception {
    NoResponseException(){
        super("树莓派没响应！");
    }
}

