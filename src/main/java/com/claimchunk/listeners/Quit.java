package com.claimchunk.listeners;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import com.claimchunk.Database;

public class Quit implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws Exception {
    }

}
