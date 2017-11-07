package de.bergwerklabs.nick.api;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import de.bergwerklabs.framework.commons.spigot.entity.npc.PlayerSkin;

/**
 * Created by Yannic Rieger on 03.09.2017.
 * <p>
 * Data class which contains data about the nicked player.
 *
 * @author Yannic Rieger
 */
public class NickInfo {

    /**
     * Real {@link WrappedGameProfile} of the player before he was nicked.
     */
    public NickProfile getRealGameProfile() {
        return realGameProfile;
    }

    /**
     * Gets the faked {@link WrappedGameProfile}.
     */
    public NickProfile getFakeGameProfile() {
        return fakeGameProfile;
    }

    /**
     * Gets the {@link PlayerSkin} that the nicked player is currently wearing.
     */
    public PlayerSkin getSkin() {
        return skin;
    }

    /**
     * Gets the nickname of the player.
     */
    public String getNickName() {
        return nickName;
    }

    private NickProfile realGameProfile, fakeGameProfile;
    private String nickName;
    private PlayerSkin skin;

    /**
     * @param realGameProfile Real {@link WrappedGameProfile} of the player before he was nicked.
     * @param skin            {@link PlayerSkin} that the nicked player is currently wearing.
     * @param fakeName        Faked name that the player will receive.
     */
    public NickInfo(NickProfile realGameProfile, NickProfile fakeGameProfile, PlayerSkin skin, String fakeName) {
        this.realGameProfile = realGameProfile;
        this.fakeGameProfile = fakeGameProfile;
        this.nickName        = fakeName;
        this.skin            = skin;
    }
}
