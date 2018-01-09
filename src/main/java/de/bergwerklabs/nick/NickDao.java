package de.bergwerklabs.nick;

import de.bergwerklabs.framework.commons.database.tablebuilder.Database;
import de.bergwerklabs.framework.commons.database.tablebuilder.DatabaseType;
import de.bergwerklabs.framework.commons.database.tablebuilder.statement.Row;
import de.bergwerklabs.framework.commons.database.tablebuilder.statement.Statement;
import de.bergwerklabs.framework.commons.database.tablebuilder.statement.StatementResult;
import de.bergwerklabs.framework.commons.spigot.entity.npc.PlayerSkin;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by Yannic Rieger on 09.01.2018.
 * <p>
 *
 * @author Yannic Rieger
 */
public class NickDao {

    private Database database;

    public NickDao(Config config) {
        this.database = new Database(DatabaseType.MySQL, config.getHost(), config.getDatabase(), config.getUser(), config.getPassword());
    }

    /**
     *
     * @return
     */
    public Optional<PlayerSkin> retrieveRandomSkin() {
        return this.execute(statementResult -> {
            if (statementResult.isEmpty()) return Optional.empty();
            Row row = statementResult.getRows()[0];
            return Optional.of(new PlayerSkin(row.getString("skin_value"), "signature"));
        }, "SELECT signature, skin_value FROM player_skin ORDER BY RAND() LIMIT 1");
    }

    /**
     *
     */
    public Optional<String> retrieveRandomName() {
        return this.execute(statementResult -> {
            if (statementResult.isEmpty()) return Optional.empty();
            Row row = statementResult.getRows()[0];
            return Optional.of(row.getString("name"));
        },"SELECT name FROM nickname ORDER BY RAND() LIMIT 1");
    }

    private <T> T execute(Function<StatementResult, T> function, String query, Object... params) {
        Statement statement = this.database.prepareStatement(query);
        StatementResult result = statement.execute(params);
        statement.close();
        return function.apply(result);
    }
}
