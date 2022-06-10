package xyz.haoshoku.ttt.manager;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import xyz.haoshoku.ttt.TTTPlugin;

import java.util.*;

public class MapVoting {

    private List<String> mapList;
    @Getter private Map<String, Integer> mapVotes;
    private Map<UUID, String> playerVotes;

    @Getter private String winnerMap;

    public MapVoting() {
        this.mapList = new ArrayList<>();
        this.mapVotes = new HashMap<>();
        this.playerVotes = new HashMap<>();
    }

    public void registerMaps() {
        for ( String string : TTTPlugin.getPlugin().getManager().getSettingManager().getConfiguration().getStringList( "settings.maps" ) )
            this.mapList.add( string );

        for ( int i = 0; i < this.mapList.size(); i++ ) {
            String map = this.mapList.get( i );
            Bukkit.createWorld( new WorldCreator( map ) );
            this.mapVotes.put( map, 0 );
        }
    }

    public void addPlayerVote( Player player, String map ) {
        removePlayerVote( player );

        if ( !this.mapVotes.containsKey( map ) ) {
            Bukkit.broadcastMessage( TTTPlugin.getPlugin().getPrefix() + " §cError: Map §4" + map + " §cdoes not exist" );
            return;
        }

        player.sendMessage( TTTPlugin.getPlugin().getManager().getMessageManager().getColoredValueFromKey( "ttt.map_vote" ).replace( "%map%", map ) );
        this.playerVotes.put( player.getUniqueId(), map );
        this.mapVotes.put( map, this.mapVotes.get( map ) + 1 );
    }

    public void removePlayerVote( Player player ) {
        if ( this.playerVotes.containsKey( player.getUniqueId() ) ) {
            String votedMap = this.playerVotes.get( player.getUniqueId() );
            this.mapVotes.put( votedMap, this.mapVotes.get( votedMap ) - 1 );
            this.playerVotes.remove( player.getUniqueId() );
        }
    }

    public void calculateMapWinner() {
        String map = null;
        int currentValue = 0;

        for ( Map.Entry<String, Integer> entry : this.mapVotes.entrySet() ) {
            String key = entry.getKey();
            int value = entry.getValue();

            if ( value >= currentValue ) {
                map = key;
                currentValue = value;
            }
        }

        if ( map == null || currentValue == 0 ) {
            String winnerMap = this.mapList.get( new Random().nextInt( this.mapList.size() ) );
            this.winnerMap = winnerMap;
        } else
            this.winnerMap = map;
    }

}
