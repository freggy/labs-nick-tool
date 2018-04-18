package de.bergwerklabs.nick.command;

import de.bergwerklabs.nick.NickPlugin;
import de.bergwerklabs.nick.api.NickInfo;
import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Yannic Rieger on 03.09.2017.
 *
 * <p>Lists all nicked players on the server.
 *
 * @author Yannic Rieger
 */
public class NickListCommand implements CommandExecutor {

  @Override
  public boolean onCommand(
      CommandSender commandSender, Command command, String s, String[] strings) {
    if (!(commandSender instanceof Player)) return false;
    Player player = (Player) commandSender;

    if (!NickPlugin.getInstance().isFunctional()) {
      player.sendMessage(
          "§6>> §eNick §6❘ §cDas Nick-Plugin konnte nicht fehlerfrei gestaret werden. §bBitte wende dich an einen §eDeveloper oder §cModerator.");
      return false;
    }

    if (s.equals("nicklist") || NickPlugin.getInstance().getNickApi().canNick(player)) {
      player.sendMessage("§6>> §eNick §6❘ §7Liste aller zurzeit genickten Spieler:");
      Set<NickInfo> nickInfos = NickPlugin.getInstance().getNickApi().getNickedPlayerInfos();

      if (nickInfos.size() != 0) {
        NickPlugin.getInstance()
            .getNickApi()
            .getNickedPlayerInfos()
            .forEach(
                info -> {
                  player.sendMessage(
                      "§a■ §f"
                          + info.getRealGameProfile().getName()
                          + " §b➟ §f"
                          + info.getNickName());
                });
      } else player.sendMessage("§6>> §eNick §6❘ §cEs sind keine Spieler genickt.");
    }
    return false;
  }
}
