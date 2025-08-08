package shush.commands;

public interface Command {
    void execute(String[] args) throws Exception;
}