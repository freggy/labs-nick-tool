package de.bergwerklabs.nick;

/**
 * Created by Yannic Rieger on 09.01.2018.
 * <p>
 *
 * @author Yannic Rieger
 */
public class Config {

    private String user, host, password, database, game;


    public Config(String user, String host, String password, String database, String game) {
        this.user = user;
        this.host = host;
        this.password = password;
        this.database = database;
        this.game = game;
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

    public String getGame() {
        return game;
    }
}
