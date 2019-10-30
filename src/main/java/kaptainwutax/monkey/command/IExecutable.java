package kaptainwutax.monkey.command;

public interface IExecutable {

    IExecutable callConstructor(String[] params);

    Object callMethod(String call, String[] params);

}
