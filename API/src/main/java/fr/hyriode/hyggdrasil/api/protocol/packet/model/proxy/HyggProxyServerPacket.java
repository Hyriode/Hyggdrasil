package fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 13:53
 */
public class HyggProxyServerPacket extends HyggPacket {

    public enum Type {
        ADD, REMOVE
    }

    private final Type type;
    private final String name;
    private final int port;

    public HyggProxyServerPacket(Type type, String name, int port) {
        this.type = type;
        this.name = name;
        this.port = port;
    }

    public HyggProxyServerPacket(Type type, String name) {
        this(type, name, 25565);
    }

    public Type getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public int getPort() {
        return this.port;
    }

}
