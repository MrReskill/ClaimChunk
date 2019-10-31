package com.claimchunk;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.claimchunk.commands.ChunkCommand;
import com.claimchunk.config.PluginConfig;
import com.claimchunk.listeners.ListenerManager;
import com.claimchunk.sql.BaseSQL;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class Loader extends PluginBase {

    public static Loader getLoader;
    public static Config config;
    public static HashMap<UUID, String> playerHud;

    @Override
    public void onEnable() {
        getLoader = this;
        config = new Config(getDataFolder() + "/config.yml", Config.YAML);
        playerHud = new HashMap<UUID, String>();

        this.getServer().getCommandMap().register("chunk", new ChunkCommand("chunk", this));
        this.checkForDatabase();

        PluginConfig.init();
        ListenerManager.init();

        if(config.getBoolean("claimExpiration.isActive")) {
            try {
                Database.check();
            } catch (Exception e) {
                getLogger().alert(e.getMessage());
            }
        }
        try {
            BaseSQL.createTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForDatabase() {
        try {
            if(BaseSQL.getConnection() == null || BaseSQL.getConnection().isClosed())
            {
                getLogger().critical("There is an error with your database configuration, please check the config.yml");
                getPluginLoader().disablePlugin(this);
            }
        } catch (SQLException e) {
            getLogger().critical("There is an error with your database configuration, please check the config.yml ("+e.getMessage()+")");
            getPluginLoader().disablePlugin(this);
        }
    }

    public static Loader getLoader()
    {
        return getLoader;
    }
}
