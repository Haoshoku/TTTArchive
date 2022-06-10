package xyz.haoshoku.ttt.manager;

import lombok.Getter;
import xyz.haoshoku.ttt.TTTPlugin;
import xyz.haoshoku.ttt.user.TTTUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class FileManager {

    private File file;
    @Getter private FileConfiguration configuration;
    private String fileName;

    public FileManager( String file ) {
        this.fileName = file;
        this.file = new File( TTTPlugin.getPlugin().getDataFolder(), file );
        this.configuration = YamlConfiguration.loadConfiguration( this.file );
    }

    public String getColoredValueFromKey( String key ) {
        String configValue = this.configuration.getString( key );
        if ( configValue == null ) {
            return "§cError: Config entry §e" + key + " §cdoes not exist. Create it or recreate it by deleting §e" + this.fileName;
        }
        if ( TTTPlugin.getPlugin().getPrefix() != null )
            configValue = configValue.replace( "%prefix%", TTTPlugin.getPlugin().getPrefix() );
        configValue = configValue.replace( "%line%", "\n" );
        configValue = configValue.replace( "%newLine%", "\n" );
        configValue = configValue.replace( "%newline%", "\n" );
        try {
            return ChatColor.translateAlternateColorCodes( '&', new String( configValue.getBytes(), "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {

        }
        return ChatColor.translateAlternateColorCodes( '&', configValue );
    }

    public String getScoreboardLine( Player player, String key ) {
        TTTUser user = TTTUser.getUser( player );
        String configValue = this.getColoredValueFromKey( key );
        configValue = configValue.replace( "%player%", player.getName() );
        configValue = configValue.replace( "%karma%", String.valueOf( user.getKarma() ) );
        configValue = configValue.replace( "%d_points%", String.valueOf( user.getDetectivePoints() ) );
        configValue = configValue.replace( "%t_points%", String.valueOf( user.getTraitorPoints() ) );
        configValue = configValue.replace( "%map%", String.valueOf( TTTPlugin.getPlugin().getManager().getVoting().getWinnerMap() ) );
        configValue = configValue.replace( "%ranking%", String.valueOf( user.getRanking() ) );

        int count = 0;
        for ( TTTUser users : TTTUser.getUsers() ) {
            if ( users.isAlive() )
                count++;
        }

        configValue = configValue.replace( "%alive%", String.valueOf( count ) );
        configValue = configValue.replace( "%online%", String.valueOf( Bukkit.getOnlinePlayers().size() ) );
        return configValue;
    }



}
