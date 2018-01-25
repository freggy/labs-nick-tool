package de.bergwerklabs.nick;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import de.bergwerklabs.framework.commons.misc.NicknameGenerator;
import de.bergwerklabs.framework.commons.spigot.entity.npc.PlayerSkin;
import de.bergwerklabs.nick.api.NickProfile;

import java.util.*;

/**
 * Created by Yannic Rieger on 03.09.2017.
 * <p>
 * Contains some useful utilities.
 *
 * @author Yannic Rieger
 */
class NickUtil {

    private static PlayerSkin[] skins;
    private static int index;

    static void init() {
        skins = NickPlugin.getInstance().getDao().retrieveRandomSkins();
    }

    /**
     * Gets a unique nickname from the database.
     *
     * @param takenNickNames {@link List} of taken nicknames.
     * @return               a unique nick name which is not contained in takenNickNames.
     */
    static String getUniqueNickName(Set<String> takenNickNames) {
        String nickName;
        do {
            nickName = NicknameGenerator.generate();
        } while (takenNickNames.contains(nickName));
        return nickName;
    }

    /**
     * Gets a random skin from the database.
     *
     * @return a skin wrapped in {@link PlayerSkin}.
     */
    static PlayerSkin getRandomSkin() {
        return skins[index++];
    }

    /**
     * Converts an {@link WrappedGameProfile} to an {@link NickProfile}.
     *
     * @param profile profile to convert.
     * @return        an {@link NickProfile}.
     */
    static NickProfile toNickProfile(WrappedGameProfile profile) {
        WrappedSignedProperty textures = (WrappedSignedProperty) profile.getProperties().get("textures").toArray()[0];
        return new NickProfile(profile.getUUID(), profile.getName(), new PlayerSkin(textures.getValue(), textures.getSignature()));
    }
}
