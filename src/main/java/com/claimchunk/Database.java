package com.claimchunk;

import cn.nukkit.IPlayer;
import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import com.claimchunk.sql.BaseSQL;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Database extends BaseSQL  {

    public static void makePlayer(Player player) throws Exception{
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT uuid FROM joined_players WHERE uuid = '"+player.getUniqueId()+"';";
        ResultSet rs;
        rs = st.executeQuery(sql);
        long last_login = System.currentTimeMillis();
        if(!rs.next()){
            sql = "INSERT INTO joined_players(uuid, last_in_game_name, last_join_time_ms, receive_alerts) VALUES ('"+player.getUniqueId()+"', '"+player.getName()+"', '"+last_login+"', '0')";
            st.execute(sql);
        } else {
            sql = "UPDATE joined_players SET last_in_game_name = '"+ player.getName()+"', last_join_time_ms = '"+last_login+"' WHERE uuid = '"+ player.getUniqueId()+"';";
            st.execute(sql);
        }
        getConnection().close();
    }

    public static void check() throws Exception{
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT * FROM joined_players";
        ResultSet rs;
        rs = st.executeQuery(sql);
        HashMap<UUID, Long> map = new HashMap<>();
        while (rs.next())
        {
            map.put(UUID.fromString(rs.getString("uuid")), rs.getLong("last_join_time_ms"));
        }
        for(Map.Entry<UUID, Long> m : map.entrySet())
        {
            long last_login = m.getValue();
            long actual_time = System.currentTimeMillis();
            Double seconds = Loader.config.getDouble("claimExpiration.timeInSecond");
            long expiration = actual_time + TimeUnit.SECONDS.toMillis(seconds.intValue());
            if(expiration < last_login) {
                UUID uuid = m.getKey();
                removeAllClaim(uuid);
                Loader.getLoader().getLogger().info("A claim of "+uuid+" was removed due to inactivity, you can disable the inactivity mode in the config by set isActive to false.");
            }
        }
        getConnection().close();
    }

    public static boolean makeClaim(Player player) throws Exception{
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT owner_uuid FROM claimed_chunks WHERE chunk_x_pos = '"+player.getChunkX()+"' AND chunk_z_pos = '"+player.getChunkZ()+"' AND world_name = '"+ player.getLevel().getName()+"';";
        ResultSet rs;
        rs = st.executeQuery(sql);
        if(!rs.next()){
            sql = "INSERT INTO claimed_chunks(world_name, chunk_x_pos, chunk_z_pos, tnt_enabled, owner_uuid) VALUES ('"+player.getLevel().getName()+"', '"+player.getChunkX()+"', '"+player.getChunkZ()+"', '0', '"+player.getUniqueId()+"')";
            st.execute(sql);
            getConnection().close();
            return true;
        }
        getConnection().close();
        return false;
    }

    public static boolean addPlayerInClaim(UUID owner, int id, UUID player) throws Exception{
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT chunk_id FROM access_granted WHERE chunk_id = '"+id+"' AND other_uuid = '"+player+"';";
        ResultSet rs;
        rs = st.executeQuery(sql);
        if(!rs.next()){
            sql = "INSERT INTO access_granted(chunk_id, owner_uuid, other_uuid) VALUES ('"+id+"', '"+owner+"', '"+player+"')";
            st.execute(sql);
            getConnection().close();
            return true;
        }
        getConnection().close();
        return false;
    }
    public static void removePlayerFromClaim(int id, UUID player) throws Exception {
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT * FROM access_granted WHERE chunk_id = '"+id+"' AND other_uuid = '"+player+"';";
        ResultSet rs;
        rs = st.executeQuery(sql);
        if(rs.next()){
            sql = "DELETE FROM access_granted WHERE chunk_id = '"+id+"' AND other_uuid = '"+player+"';";
            st.execute(sql);
        }
        getConnection().close();
    }
    public static boolean isGranted(int id, UUID player) throws Exception{
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT chunk_id FROM access_granted WHERE chunk_id = '"+id+"' AND other_uuid = '"+player+"';";
        ResultSet rs;
        rs = st.executeQuery(sql);
        if(!rs.next()){
            getConnection().close();
            return true;
        }
        getConnection().close();
        return false;
    }


    public static void removeClaim(Player player) throws Exception {
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT owner_uuid FROM claimed_chunks WHERE chunk_x_pos = '"+player.getChunkX()+"' AND chunk_z_pos = '"+player.getChunkZ()+"' AND world_name = '"+ player.getLevel().getName()+"';";
        ResultSet rs;
        rs = st.executeQuery(sql);
        if(rs.next()){
            sql = "DELETE FROM claimed_chunks WHERE chunk_x_pos = '"+player.getChunkX()+"' AND chunk_z_pos = '"+player.getChunkZ()+"' AND world_name = '"+ player.getLevel().getName()+"';";
            st.execute(sql);
        } else {
            player.sendMessage("§cAn error was occurred, please contact an administrator.");
        }
        getConnection().close();
    }

    public static void removeAllClaim(Player player) throws Exception {
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "DELETE FROM claimed_chunks WHERE owner_uuid = '"+ player.getUniqueId()+"';";
        st.execute(sql);
        getConnection().close();
    }
    public static void removeAllClaim(UUID player) throws Exception {
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "DELETE FROM claimed_chunks WHERE owner_uuid = '"+ player+"';";
        st.execute(sql);
        getConnection().close();
    }
    public static String getClaimOwner(Player player) throws Exception {
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT owner_uuid FROM claimed_chunks WHERE chunk_x_pos = '"+player.getChunkX()+"' AND chunk_z_pos = '"+player.getChunkZ()+"' AND world_name = '"+ player.getLevel().getName()+"';";
        ResultSet rs;
        rs = st.executeQuery(sql);
        if(rs.next()){
            UUID uuid = UUID.fromString(rs.getString("owner_uuid"));

            IPlayer op = Server.getInstance().getOfflinePlayer(uuid);
            return op.getName();
        }
        getConnection().close();
        return null;
    }

    public static Integer getClaimId(Player player) throws Exception {
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT id FROM claimed_chunks WHERE chunk_x_pos = '"+player.getChunkX()+"' AND chunk_z_pos = '"+player.getChunkZ()+"' AND world_name = '"+ player.getLevel().getName()+"';";
        ResultSet rs;
        rs = st.executeQuery(sql);
        if(rs.next()){
            return rs.getInt("id");
        }
        getConnection().close();
        return null;
    }

    public static Integer getClaimId(Block player) throws Exception {
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT * FROM claimed_chunks WHERE chunk_x_pos = '"+player.getChunkX()+"' AND chunk_z_pos = '"+player.getChunkZ()+"' AND world_name = '"+ player.getLevel().getName()+"';";
        ResultSet rs;
        rs = st.executeQuery(sql);
        if(rs.next()){
            return rs.getInt("id");
        }
        getConnection().close();
        return null;
    }

    public static String getClaimOwner(Block player) throws Exception {
        openConnection();
        Statement st = getConnection().createStatement();
        String sql = "SELECT owner_uuid FROM claimed_chunks WHERE chunk_x_pos = '"+player.getChunkX()+"' AND chunk_z_pos = '"+player.getChunkZ()+"' AND world_name = '"+ player.getLevel().getName()+"';";
        ResultSet rs;
        rs = st.executeQuery(sql);
        if(rs.next()){
            UUID uuid = UUID.fromString(rs.getString("owner_uuid"));

            IPlayer op = Server.getInstance().getOfflinePlayer(uuid);
            return op.getName();
        }
        getConnection().close();
        return null;
    }
}
