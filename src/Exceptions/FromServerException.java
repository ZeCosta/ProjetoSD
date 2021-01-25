package src.Exceptions;

public class FromServerException extends Exception{
    public FromServerException(){
        super();
    }

    public FromServerException(String s){
        super(s);
    }
}
