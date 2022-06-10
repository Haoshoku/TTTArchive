package xyz.haoshoku.ttt.manager;

import lombok.Getter;
import xyz.haoshoku.ttt.TTTPlugin;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebsiteManager {

    @Getter private boolean enabled;

    public WebsiteManager() {
        this.enabled = true;
    }

    public void connect() {
        Bukkit.getScheduler().runTaskAsynchronously( TTTPlugin.getPlugin(), () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL( "https://haoshoku.xyz/ttt/1.0.1/index.php" ).openConnection();
                BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
                String line = reader.readLine();

                if ( line.equalsIgnoreCase( "false" ) ) {
                    this.enabled = false;
                    Bukkit.getConsoleSender().sendMessage( "§cThis version has been deactivated for several reasons" );
                    Bukkit.getConsoleSender().sendMessage( "§cReasons could be: §eToo many bugs, too outdated, unexpected" );
                    Bukkit.getConsoleSender().sendMessage( "§cPlease join &ehttps://haoshoku.xyz/go/discord for further information" );
                    Bukkit.getScheduler().runTask( TTTPlugin.getPlugin(), () -> Bukkit.getPluginManager().disablePlugin( TTTPlugin.getPlugin() ) );
                }

            } catch ( IOException e ) {}
        } );
    }

}
