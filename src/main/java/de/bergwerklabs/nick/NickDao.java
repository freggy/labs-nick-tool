package de.bergwerklabs.nick;

import de.bergwerklabs.framework.commons.database.tablebuilder.Database;
import de.bergwerklabs.framework.commons.database.tablebuilder.DatabaseType;
import de.bergwerklabs.framework.commons.database.tablebuilder.statement.Row;
import de.bergwerklabs.framework.commons.database.tablebuilder.statement.Statement;
import de.bergwerklabs.framework.commons.database.tablebuilder.statement.StatementResult;
import de.bergwerklabs.framework.commons.spigot.entity.npc.PlayerSkin;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Yannic Rieger on 09.01.2018.
 * <p>
 *
 * @author Yannic Rieger
 */
public class NickDao {

    private Database database;

    NickDao(Config config) {
        this.database = new Database(DatabaseType.MySQL, config.getHost(), config.getDatabase(), config.getUser(), config.getPassword());
    }

    /**
     *
     * @return
     */
    public PlayerSkin[] retrieveRandomSkins() {
        return this.execute(statementResult -> {
            PlayerSkin[] skins = new PlayerSkin[100];
            Row[] rows = statementResult.getRows();
            for (int i = 0; i < skins.length; i++) {
                skins[i] = new PlayerSkin(rows[i].getString("skin_value"), rows[i].getString("signature"));
            }
            return skins;
        }, "SELECT signature, skin_value FROM player_skin ORDER BY RAND() LIMIT 100");
    }

    private <T> T execute(Function<StatementResult, T> function, String query, Object... params) {
        Statement statement = this.database.prepareStatement(query);
        StatementResult result = statement.execute(params);
        statement.close();
        return function.apply(result);
    }
}
