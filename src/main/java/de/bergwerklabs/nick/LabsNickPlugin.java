package de.bergwerklabs.nick;

import com.comphenix.protocol.ProtocolManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import de.bergwerklabs.framework.commons.spigot.json.JsonUtil;
import de.bergwerklabs.framework.commons.spigot.nms.packet.v1_8.WrapperPlayServerChat;
import de.bergwerklabs.nick.api.NickApi;
import de.bergwerklabs.nick.api.NickInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import de.bergwerklabs.framework.commons.spigot.SpigotCommons;
import de.bergwerklabs.framework.commons.spigot.nms.packet.v1_8.WrapperPlayServerPlayerInfo;
import de.bergwerklabs.nick.command.NickCommand;
import de.bergwerklabs.nick.command.NickListCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Yannic Rieger on 03.09.2017.
 * <p>
 * Main class for the nick plugin.
 *
 * @author Yannic Rieger
 */
public class LabsNickPlugin extends JavaPlugin implements Listener {

    /**
     * Gets the instance of the {@link LabsNickPlugin} object.
     */
    public static LabsNickPlugin getInstance() { return instance; }

    /**
     * Gets the {@link NickApi}.
     */
    public NickApi getNickApi() { return this.manager; }

    private static LabsNickPlugin instance;
    private NickManager manager;
    private boolean removed = false;
    private JsonParser parser = new JsonParser();

    // TODO: listen for text message packets and replace with nick

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getCommand("nick").setExecutor(new NickCommand());
        this.getCommand("nicklist").setExecutor(new NickListCommand());
        this.manager = new NickManager(NickUtil.retrieveNickNames(), NickUtil.retrieveSkins());
        this.getServer().getServicesManager().register(NickApi.class, this.manager, this, ServicePriority.Normal);
        ProtocolManager protocolManager = SpigotCommons.getInstance().getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                if (manager.isNicked(player)) return;

                WrapperPlayServerPlayerInfo packet  = new WrapperPlayServerPlayerInfo(event.getPacket());
                List<PlayerInfoData> playerInfoData = packet.getData();
                List<PlayerInfoData> toNick         = playerInfoData.stream().filter(data -> manager.nickedPlayers.containsKey(data.getProfile().getUUID())).collect(Collectors.toList());

                playerInfoData.removeAll(toNick);

                playerInfoData.addAll(toNick.stream().map(data -> {
                    NickInfo info = manager.nickedPlayers.get(data.getProfile().getUUID());
                    return new PlayerInfoData(info.getFakeGameProfile().toWrappedGameProfile(), data.getLatency(), data.getGameMode(), WrappedChatComponent.fromText(info.getNickName()));
                }).collect(Collectors.toList()));

                packet.setData(playerInfoData);
                event.setPacket(packet.getHandle());
            }
        });

        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                // TODO: check outcome
                WrapperPlayServerChat chat = new WrapperPlayServerChat(event.getPacket());
                Player sentTo = event.getPlayer();

                if (manager.isNicked(sentTo)) { // || partied with nicked guy
                    return;
                }

                NickInfo info = manager.getNickInfo(sentTo);
                JsonObject json = parser.parse(chat.getMessage().getJson()).getAsJsonObject();
                modifyText(json, info.getNickName(), info.getRealGameProfile().getName());
            }
        });
    }

    private void modifyText(JsonObject jsonObject, String fakeName, String realName) {
        jsonObject.add("text", new JsonPrimitive(jsonObject.get("text").getAsString().replace(fakeName, realName)));
        JsonUtil.jsonArrayToJsonObjectList(jsonObject.getAsJsonArray("extra")).forEach(extra -> {
            extra.add("text", new JsonPrimitive(extra.get("text").getAsString().replace(fakeName, realName)));
        });
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent e) {
        /*

        String message = e.getMessage();
        Player player = e.getPlayer();

        if (manager.isNicked(player)) { // || partied with nicked guy
            e.getRecipients().removeAll(this.manager.nickedPlayers.keySet().stream().map(Bukkit::getPlayer).collect(Collectors.toList()));
            player.sendMessage(String.format(e.getFormat(), manager.getRealName(player), message)); // and send to party and other nicked players.
        }

        for (NickInfo info :  this.manager.nickedPlayers.values()) {
            String nick = info.getNickName();
            if (message.contains(nick)) {
                String specialMessage;
                specialMessage = String.format(e.getFormat(), e.getPlayer().getDisplayName(), message.replaceAll(nick, "§o" + info.getRealGameProfile().getName() + "§r"));
                this.manager.nickedPlayers.keySet().forEach(uuid -> Bukkit.getPlayer(uuid).sendMessage(specialMessage));
                e.getRecipients().removeAll(this.manager.nickedPlayers.keySet().stream().map(Bukkit::getPlayer).collect(Collectors.toList()));
                // TODO: send special message to party members of nicked players.
            }
        } */
    }

    @EventHandler
    private void onTabComplete(PlayerChatTabCompleteEvent event) {
        Player player = event.getPlayer();
        Collection<String> matches = event.getTabCompletions();

        if (!this.manager.isNicked(player)) { // TODO: && !isPartiedWith(playerToCheck, potentialMember)
            matches.removeIf(match -> {
                Player p = Bukkit.getPlayer(match);
                return p != null && manager.isNicked(p);
            });

            manager.nickedPlayers.values().forEach(info -> {
                if (info.getNickName().toLowerCase().startsWith(event.getLastToken().toLowerCase())) {
                    matches.add(info.getNickName());
                }
            });
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        this.manager.nickedPlayers.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerLogin(PlayerLoginEvent event) {
        // TODO: check if aut nick is enabled
        Player player = event.getPlayer();
        if (player.getDisplayName().equals("ausderfuture")) return;
        this.manager.nickPlayer(player);
    }
}
