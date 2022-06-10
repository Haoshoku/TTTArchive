package xyz.haoshoku.ttt.manager.dbmanager;

import org.bukkit.entity.Player;
import xyz.haoshoku.ttt.manager.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class TTTDatabase extends Database {

    public TTTDatabase( String host, String port, String database, String username, String password, int id ) {
        super( host, port, database, username, password, 1 );
    }

    public void createTable() {
        if ( !isConnected() ) return;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement
                    ( "CREATE TABLE IF NOT EXISTS `ttt` ( uuid VARCHAR(64), last_name VARCHAR(16), karma INT, detective_pass INT, " +
                            "traitor_pass INT, rank_up INT, kills INT, deaths INT, played INT, wins INT, fail_rate INT, tokens INT )" );
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public void createPlayer( String uuid ) {
        if ( !isConnected() ) return;
        if ( exists( uuid ) ) return;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement
                    ( "INSERT INTO `ttt` ( uuid, last_name, karma, detective_pass, traitor_pass, rank_up, kills, deaths, played, wins, fail_rate, tokens ) VALUES " +
                            "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) " );
            preparedStatement.setString( 1, uuid );
            preparedStatement.setString( 2, "-" );
            preparedStatement.setInt( 3, 100 );
            preparedStatement.setInt( 4, 0 );
            preparedStatement.setInt( 5, 0 );
            preparedStatement.setInt( 6, 1 );

            for ( int i = 7; i < 13; i++ )
                preparedStatement.setInt( i, 0 );

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public boolean exists( String uuid ) {
        if ( !isConnected() ) return false;
        boolean value = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement( "SELECT * FROM `ttt` WHERE `uuid` = ?" );
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

    public int getRanking( String uuid ) {
        if ( !isConnected() )
            return -1;

        if ( !exists( uuid ) )
            return -1;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement( "SELECT * FROM `ttt` ORDER BY `karma` DESC" );
            ResultSet resultSet = preparedStatement.executeQuery();

            int rank = 0;

            while ( resultSet.next() ) {
                rank++;

                if ( resultSet.getString( "uuid" ).equalsIgnoreCase( uuid ) )
                    break;
            }

            resultSet.close();
            preparedStatement.close();
            return rank;
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
        return -1;
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
                PreparedStatement preparedStatement = this.connection.prepareStatement( "UPDATE `ttt` SET `last_name` = ? WHERE `uuid` = ?" );
                preparedStatement.setString( 1, player.getName() );
                preparedStatement.setString( 2, player.getUniqueId().toString() );
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        } );
    }

    public int getDataSync( String uuid, String stats ) {
        if ( !isConnected() )
            return -1;

        if ( !exists( uuid ) ) {
            return 0;
        }

        int value = 0;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement( "SELECT `" + stats + "` FROM `ttt` WHERE `uuid` = ?" );
            preparedStatement.setString( 1, uuid );

            ResultSet resultSet = preparedStatement.executeQuery();

            while ( resultSet.next() )
                value = resultSet.getInt( stats );

            resultSet.close();
            preparedStatement.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        return value;
    }

    public void getDataAsync( String uuid, String stats, Consumer<Integer> consumer ) {
        this.executor.execute( () -> consumer.accept( getDataSync( uuid, stats ) ) );
    }

    public void addStats( String uuid, String stats, int value ) {
        this.getDataAsync( uuid, stats, result -> setStats( uuid, stats, result + value ) );
    }

    public void addStatsSync( String uuid, String stats, int value ) {
        this.setStats( uuid, stats, this.getDataSync( uuid, stats ) + value );
    }

    public void removeStats( String uuid, String stats, int value ) {
        this.getDataAsync( uuid, stats, result -> setStats( uuid, stats, result - value ) );
    }


    public void setStats( String uuid, String stats, int value ) {
        if ( !isConnected() )
            return;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement( "UPDATE `ttt` SET `" + stats + "` = ? WHERE `uuid` = ?" );
            preparedStatement.setInt( 1, value );
            preparedStatement.setString( 2, uuid );
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public void deleteAllStats( String uuid ) {
        if ( !isConnected() )
            return;

        this.executor.execute( () -> {
            setStats( uuid, "karma", 100 );
            setStats( uuid, "kills", 0 );
            setStats( uuid, "rank_up", 1 );
            setStats( uuid, "deaths", 0 );
            setStats( uuid, "played", 0 );
            setStats( uuid, "wins", 0 );
            setStats( uuid, "fail_rate", 0 );
        } );
    }

}
