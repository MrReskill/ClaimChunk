package com.claimchunk.listeners;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import com.claimchunk.Database;

public class Join implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws Exception {
        Database.makePlayer(e.getPlayer());
    }

}
