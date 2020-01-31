package server;

public class IncorrectResponseException extends Exception{
    IncorrectResponseException(){
        super("树莓派的响应不正确");
    }
}
