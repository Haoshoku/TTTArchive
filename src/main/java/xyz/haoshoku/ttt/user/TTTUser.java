package xyz.haoshoku.ttt.user;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import java.util.*;


@Getter @Setter
public class TTTUser {

    private static final Map<UUID, TTTUser> USER_MAP = new HashMap<>();

    public static TTTUser getUser( Player player ) {
        return getUserByUUID( player.getUniqueId() );
    }

    public static TTTUser getUserByUUID( UUID uuid ) {
        if ( !USER_MAP.containsKey( uuid ) )
            USER_MAP.put( uuid, new TTTUser() );

        return USER_MAP.get( uuid );
    }

    public static void deleteUser( Player player ) {
        if ( USER_MAP.containsKey( player.getUniqueId() ) )
            USER_MAP.remove( player.getUniqueId() );
    }

    public static TTTUser[] getUsers() {
        TTTUser[] users = new TTTUser[USER_MAP.size()];

        int count = 0;
        for ( Map.Entry<UUID, TTTUser> entry : USER_MAP.entrySet() ) {
            users[count] = entry.getValue();
            count++;
        }
        return users;
    }


    @Getter @Setter private static Map<Integer, Player> entityIDMap = new HashMap<>();
    @Getter @Setter private static Map<Zombie, Object[]> zombieOfPlayer = new HashMap<>();
    @Getter @Setter private static boolean traitorTrapUsed;
    @Getter @Setter private static int globalDetectivePassUsed;
    @Getter @Setter private static int globalTraitorPassUsed;
    @Getter @Setter private static int aliveSize;
    @Getter @Setter private static int innoDetectiveSize;
    @Getter @Setter private static int traitorSize;
    @Getter @Setter private static List<String> traitorList = new LinkedList<>();

    private Player player;

    private int ranking;

    // Combat
    private Player combatPlayer;
    private long combatTime;

    private boolean rankup;

    private int tokens;

    // Admin Tools
    private boolean buildState;

    private boolean autoNick;

    // Dead or alive
    private boolean alive;
    private boolean spectator;

    // LoginEvent
    private boolean loaded;

    // Roles
    private boolean detective;
    private boolean innocent;
    private boolean traitor;

    private int karma;

    private int detectivePoints;
    private int traitorPoints;

    // NEEDED STATS
    private int detectivePasses;
    private int traitorPasses;

    private boolean innocentTicket;

    private boolean usingDetectivePass;
    private boolean usingTraitorPass;

    private long payloadCooldown;

    private int chestOpened;

    private ItemStack skull;

}
