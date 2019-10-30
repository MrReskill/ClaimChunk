package com.claimchunk.sql;

import com.claimchunk.Loader;

import java.sql.*;

public class BaseSQL {

    private static Connection con;
    private static String username;
    private static String password;
    private static String host;
    private static String database;
    private static int port;

    static {
        username = Loader.config.getString("database.username");
        password = Loader.config.getString("database.password");
        host = Loader.config.getString("database.host");
        database = Loader.config.getString("database.name");
        try {
            openConnection();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection()
    {
        return con;
    }

    public static void createTable() throws Exception {

        openConnection();
        if(!existTable("joined_players"))
        {
            Statement st = getConnection().createStatement();
            String sql = "create table joined_players(uuid varchar(36) not null,last_in_game_name varchar(64) not null, chunk_name varchar(64), last_join_time_ms BIGINT(20) NOT NULL, receive_alerts INTEGER NOT NULL)";
            st.executeUpdate(sql);
        }

        if(!existTable("claimed_chunks"))
        {
            Statement st = getConnection().createStatement();
            String sql = "create table claimed_chunks(id integer not null AUTO_INCREMENT,world_name varchar(64) not null, chunk_x_pos INTEGER NOT NULL, chunk_z_pos INTEGER NOT NULL, tnt_enabled INTEGER NOT NULL, owner_uuid varchar(36) NOT NULL, PRIMARY KEY (id))";
            st.executeUpdate(sql);
        }

        if(!existTable("access_granted"))
        {
            Statement st = getConnection().createStatement();
            String sql = "create table access_granted(access_id integer not null AUTO_INCREMENT, chunk_id integer not null, owner_uuid VARCHAR(36) NOT NULL, other_uuid varchar(36) NOT NULL, PRIMARY KEY (access_id))";
            st.executeUpdate(sql);
        }
        getConnection().close();
    }

    public static boolean existTable(String tableName) throws SQLException {
        DatabaseMetaData dbm = con.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        if (tables.next()) {
            return true;
        }
        return false;
    }

    public static void openConnection() throws Exception {
        if (con != null && !con.isClosed()) {
            return;
        }
        String url = "jdbc:mysql://"+host+"/"+database+"?useSSL=false";
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url, username, password);
    }

}