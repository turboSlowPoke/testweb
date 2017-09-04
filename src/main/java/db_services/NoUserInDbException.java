package db_services;

public class NoUserInDbException extends Throwable {
    public NoUserInDbException(long userId) {
    }
}
