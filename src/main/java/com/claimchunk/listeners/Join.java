package com.claimchunk.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import com.claimchunk.Database;
import com.claimchunk.Loader;

import java.util.Objects;

public class Join implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws Exception {
        Database.makePlayer(e.getPlayer());
        Loader.playerHud.put(e.getPlayer().getUniqueId(), "null");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) throws Exception {
        Player p = e.getPlayer();
        if(Loader.playerHud.containsKey(e.getPlayer().getUniqueId()))
        {
            if(Database.getClaimOwner(p) != null)
            {
                if(Loader.playerHud.get(e.getPlayer().getUniqueId()).equals("null"))
                {
                    Loader.playerHud.put(e.getPlayer().getUniqueId(), Database.getClaimOwner(p));
                    p.sendMessage("§6§lClaimChunk>§r§a You are now in §2"+Database.getClaimOwner(p)+"§a's claim");
                }
                else
                {
                    if(!(Objects.requireNonNull(Database.getClaimOwner(p)).equals(Loader.playerHud.get(p.getUniqueId()))))
                    {
                        p.sendMessage("§6§lClaimChunk>§r§a You are now in §2"+Database.getClaimOwner(p)+"§a's claim");
                        Loader.playerHud.put(e.getPlayer().getUniqueId(), Database.getClaimOwner(p));
                    }
                }
            } else {
                if(Database.getClaimOwner(p) == null && !Loader.playerHud.get(e.getPlayer().getUniqueId()).equals("null"))
                {
                    p.sendMessage("§6§lClaimChunk>§r§c You are leaving §4"+Loader.playerHud.get(e.getPlayer().getUniqueId())+"§c's claim");
                    Loader.playerHud.put(e.getPlayer().getUniqueId(), "null");
                }
            }
        }
    }

}
