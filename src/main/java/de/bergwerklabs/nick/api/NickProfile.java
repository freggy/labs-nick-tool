package de.bergwerklabs.nick.api;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import de.bergwerklabs.framework.commons.spigot.entity.npc.PlayerSkin;

import java.util.UUID;

/**
 * Created by Yannic Rieger on 07.11.2017.
 * <p>
 * Abstraction of {@link WrappedGameProfile}.
 *
 * @author Yannic Rieger
 */
public class NickProfile {

    public PlayerSkin getSkin() {
        return skin;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    private UUID uuid;
    private String name;
    private PlayerSkin skin;

    public NickProfile(UUID uuid, String name, PlayerSkin skin) {
        this.uuid = uuid;
        this.name = name;
        this.skin = skin;
    }

    public WrappedGameProfile toWrappedGameProfile() {
        WrappedGameProfile profile = new WrappedGameProfile(this.uuid, this.name);
        this.skin.inject(profile);
        return profile;
    }
}
