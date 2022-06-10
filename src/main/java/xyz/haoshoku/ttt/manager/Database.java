package xyz.haoshoku.ttt.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Database {

    protected Connection connection;
    protected ExecutorService executor;

    private String host, port, database, username, password;
    private int id;

    public Database( String host, String port, String database, String username, String password, int id ) {
        this.executor = Executors.newCachedThreadPool();
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.id = id;
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection( "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password );
            this.createTable();
            Bukkit.getConsoleSender().sendMessage( "[TTT] §aConnected to MySQL servers [" + this.id + "]" );
        } catch ( SQLException e ) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage( "[TTT] §4This plugin requires MySQL connection for saving data [" + this.id + "]" );
        }
    }

    public void disconnect() {
        if ( isConnected() ) {
            try {
                this.connection.close();
                this.connection = null;
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return this.connection != null;
    }



    public abstract void createPlayer( String uuid );

    public abstract void updateLastName( Player player );

    public abstract boolean exists( String uuid );

    public abstract void createTable();
}
