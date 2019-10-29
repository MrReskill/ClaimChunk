package com.claimchunk.listeners;

import com.claimchunk.Loader;

public class ListenerManager {

   public static void init()
   {
       Loader.getLoader.getServer().getPluginManager().registerEvents(new Join(), Loader.getLoader());
       Loader.getLoader.getServer().getPluginManager().registerEvents(new Quit(), Loader.getLoader());
       Loader.getLoader.getServer().getPluginManager().registerEvents(new ChunkProtection(), Loader.getLoader());
   }

}
