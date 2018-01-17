package de.bergwerklabs.nick.command;

import de.bergwerklabs.framework.commons.spigot.entity.npc.PlayerSkin;
import de.bergwerklabs.nick.NickPlugin;
import de.bergwerklabs.nick.api.NickApi;
import de.bergwerklabs.nick.api.NickInfo;
import de.bergwerklabs.nick.api.NickProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Yannic Rieger on 03.09.2017.
 * <p>
 * Command which nicks a player.
 *
 * @author Yannic Rieger
 */
public class NickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if (!NickPlugin.getInstance().isFunctional()) {
            player.sendMessage("§6>> §eNick §6❘ §cDas Nick-Plugin konnte nicht fehlerfrei gestaret werden. §bBitte wende dich an einen §eDeveloper oder §cModerator.");
            return false;
        }

        if (s.equals("nick") || NickPlugin.getInstance().getNickApi().canNick(player)) {
            //player.sendMessage("§6>> §eNick §6❘ §bNick ist zur Zeit nicht verfügbar.");
            NickApi api = NickPlugin.getInstance().getNickApi();

                if (api.isNicked(player)) {
                    api.removeNick(player);
                    player.sendMessage("§6>> §eNick §6❘ §7Du bist nun §centnickt!");
                    return true;
                }

                NickInfo info = NickPlugin.getInstance().getNickApi().nickPlayer(player);
                player.sendMessage("§6>> §eNick §6❘ §7Dein Nickname lautet nun §b" + info.getNickName());
                player.sendMessage("§6>> §eNick §6❘ §7Führe §b/nick §7aus um ihn zu entfernen.");
                return true;
        }
        return false;
    }
}
