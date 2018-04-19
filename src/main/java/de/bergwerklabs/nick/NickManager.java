package de.bergwerklabs.nick;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.ImmutableSet;
import de.bergwerklabs.nick.api.NickApi;
import de.bergwerklabs.nick.api.NickInfo;
import de.bergwerklabs.nick.api.NickProfile;
import de.bergwerklabs.nick.api.event.NickAction;
import de.bergwerklabs.nick.api.event.NickEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by Yannic Rieger on 03.09.2017.
 *
 * <p>Provides methods for nicking players.
 *
 * @author Yannic Rieger
 */
class NickManager implements NickApi {

  Map<UUID, NickInfo> nickedPlayers = new ConcurrentHashMap<>();
  private Set<String> takenNickNames = new HashSet<>();
  private Map<UUID, NickProfile> usedProfiles = new HashMap<>();

  @Override
  public boolean isNicked(Player player) {
    return nickedPlayers.containsKey(player.getUniqueId());
  }

  @Override
  public String getRealName(Player player) {
    return this.nickedPlayers.get(player.getUniqueId()).getRealGameProfile().getName();
  }

  @Override
  public Set<NickInfo> getNickedPlayerInfos() {
    return ImmutableSet.copyOf(nickedPlayers.values());
  }

  @Override
  public NickInfo getNickInfo(Player player) {
    return nickedPlayers.get(player.getUniqueId());
  }

  @Override
  public void removeNick(Player player) {
    NickInfo info = this.nickedPlayers.get(player.getUniqueId());
    this.nickedPlayers.remove(player.getUniqueId());
    String realName = info.getRealGameProfile().getName();

    player.setDisplayName(realName);
    player.setCustomName(realName);

    this.resendPlayerInfo(player);
    Bukkit.getPluginManager().callEvent(new NickEvent(player, info, NickAction.REMOVE));
  }

  @Override
  public NickInfo nickPlayer(Player player) {
    UUID uuid = player.getUniqueId();
    NickProfile fake =
        this.usedProfiles.get(uuid) == null
            ? this.createFakeProfile(uuid)
            : this.usedProfiles.get(uuid);
    this.usedProfiles.putIfAbsent(uuid, fake);

    player.setDisplayName(fake.getName());
    player.setCustomName(fake.getName());

    NickProfile real = NickUtil.toNickProfile(WrappedGameProfile.fromPlayer(player));

    NickInfo info = new NickInfo(real, fake, fake.getSkin(), fake.getName());
    this.nickedPlayers.put(player.getUniqueId(), info);
    this.takenNickNames.add(info.getNickName());

    this.resendPlayerInfo(player);

    Bukkit.getPluginManager().callEvent(new NickEvent(player, info, NickAction.NICKED));
    return info;
  }

  @Override
  public boolean canNick(Player player) {
    return player.hasPermission(
        "bergwerklabs.nick"); /*&& NickPlugin.getInstance().getTop3().contains(player.getUniqueId());*/
  }

  @Override
  public boolean isPartiedWithNickedPlayer(Player player) {
    /*
    Optional<Party> partyOptional = PartyApi.getParty(player.getUniqueId());
    return partyOptional.map(party -> party.getMembers().stream()
                                           .filter(member -> Bukkit.getPlayer(member) != null)
                                           .map(Bukkit::getPlayer)
                                           .anyMatch(this::isNicked)).orElse(false); */
    return true;
  }

  /**
   * Re-sends PlayerInfo packet to update player name and skin. This method internally calls {@link
   * Player#hidePlayer(Player)} and {@link Player#showPlayer(Player)} in that specific order.
   *
   * @param player Player that has been nicked.
   */
  private void resendPlayerInfo(Player player) {
    Bukkit.getScheduler().callSyncMethod(NickPlugin.getInstance(), () -> {
          List<Player> others = Bukkit.getOnlinePlayers().stream()
                      .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                      .collect(Collectors.toList());
              others.forEach(p -> p.hidePlayer(player));
              others.forEach(p -> p.showPlayer(player));
              return null;
            });
  }

  private NickProfile createFakeProfile(UUID uuid) {
    // test 4
    return new NickProfile(uuid, NickUtil.getUniqueNickName(this.takenNickNames), NickUtil.getRandomSkin());
  }
}
