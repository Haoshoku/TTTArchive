package xyz.haoshoku.ttt.manager.dbmanager;

import org.bukkit.entity.Player;
import xyz.haoshoku.ttt.manager.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class NickDatabase extends Database {

    public NickDatabase( String host, String port, String database, String username, String password, int id ) {
        super( host, port, database, username, password, 2 );
    }

    @Override
    public void createPlayer( String uuid ) {
        if ( this.isConnected() ) {
            if ( this.exists( uuid ) ) return;
            try {
                PreparedStatement preparedStatement = this.connection.prepareStatement(
                        "INSERT INTO `nick` ( UUID, NAME, STATE, NICKNAME ) " +
                                "VALUES ( ?, ?, ?, ? )" );
                preparedStatement.setString( 1, uuid );
                preparedStatement.setString( 2, "null" );
                preparedStatement.setBoolean( 3, false );
                preparedStatement.setString( 4, "none" );
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
    }

    public void updateLastName( Player player ) {
        if ( !isConnected() )
            return;

        this.executor.execute( () -> {
            if ( !exists( player.getUniqueId().toString() ) ) {
                this.createPlayer( player.getUniqueId().toString() );
                return;
            }

            try {
                PreparedStatement preparedStatement = this.connection.prepareStatement( "UPDATE `nick` SET `NAME` = ? WHERE `uuid` = ?" );
                preparedStatement.setString( 1, player.getName() );
                preparedStatement.setString( 2, player.getUniqueId().toString() );
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        } );
    }

    @Override
    public boolean exists( String uuid ) {
        if ( !isConnected() ) return false;
        boolean value = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement( "SELECT * FROM `nick` WHERE `UUID` = ?" );
            preparedStatement.setString( 1, uuid );
            ResultSet resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() ) {
                value = true;
                break;
            }

            resultSet.close();
            preparedStatement.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return value;
    }

    public void setNickState( String uuid, boolean value ) {
        if ( !this.isConnected() ) return;

        if ( !this.exists( uuid ) ) return;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement( "UPDATE `nick` SET `STATE` = ? WHERE `UUID` = ?" );
            preparedStatement.setBoolean( 1, value );
            preparedStatement.setString( 2, uuid );
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public boolean getNickStateSync( String uuid ) {
        if ( !this.isConnected() ) return false;
        boolean value = false;
        if ( !this.exists( uuid ) ) return false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement( "SELECT `STATE` FROM `nick` WHERE `UUID` = ?" );
            preparedStatement.setString( 1, uuid );
            ResultSet resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() )
                value = resultSet.getBoolean( "STATE" );
            resultSet.close();
            preparedStatement.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return value;
    }

    public void getNickStateAsync( String uuid, Consumer<Boolean> consumer ) {
        this.executor.execute( () -> consumer.accept( this.getNickStateSync( uuid ) ) );
    }

    @Override
    public void createTable() {
        if ( !isConnected() ) return;
        try {
            PreparedStatement preparedStatement =  this.connection.prepareStatement( "CREATE TABLE IF NOT EXISTS `nick` (UUID VARCHAR(64), NAME VARCHAR(16), STATE BOOL, NICKNAME VARCHAR(16))" );
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }
}
