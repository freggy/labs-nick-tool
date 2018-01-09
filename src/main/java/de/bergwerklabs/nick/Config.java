package de.bergwerklabs.nick;

/**
 * Created by Yannic Rieger on 09.01.2018.
 * <p>
 *
 * @author Yannic Rieger
 */
public class Config {

    private String user, host, password, database;


    public Config(String user, String host, String password, String databse) {
        this.user = user;
        this.host = host;
        this.password = password;
        this.database = databse;
    }

    public String getUser() {
        return user;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }
}
