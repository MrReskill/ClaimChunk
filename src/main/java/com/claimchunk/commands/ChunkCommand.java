package com.claimchunk.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.plugin.Plugin;
import com.claimchunk.Database;
import com.claimchunk.Loader;
import me.onebone.economyapi.EconomyAPI;

public class ChunkCommand extends PluginCommand {

    public ChunkCommand(String name, Plugin owner) {
        super(name, owner);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player p = (Player)sender;

        if((args.length == 0) || (args.length > 0 && args[0].equalsIgnoreCase("help")))
        {
            sender.sendMessage("§6§lClaimChunk commands:");
            sender.sendMessage("§6§l- §r§e/chunk help");
            sender.sendMessage("§6§l- §r§e/chunk claim");
            sender.sendMessage("§6§l- §r§e/chunk addplayer <player>");
            sender.sendMessage("§6§l- §r§e/chunk removeplayer <player>");
            sender.sendMessage("§6§l- §r§e/chunk unclaim");
            sender.sendMessage("§6§l- §r§e/chunk unclaimall");
            if(sender instanceof Player && ((Player)sender).isOp())
            {
                sender.sendMessage("§6§e- §r§f/chunk adminunclaim");
                sender.sendMessage("§6§e- §r§f/chunk adminunclaimall <player>");
            }
        }
        else
        {
            switch(args[0])
            {
                case "adminunclaimall":
                    if(args.length == 2)
                    {
                        if(p.isOp()) {
                            if (Server.getInstance().getPlayer(args[1]) != null) {
                                try {
                                    Database.removeAllClaim(Server.getInstance().getPlayer(args[1]));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                sender.sendMessage("§6§lClaimChunk>§c§l (ADMIN)§r§a Claims of §f"+args[1]+"§a was removed");
                            } else {
                                sender.sendMessage("§6§lClaimChunk>§c§l (ADMIN)§r§c This player is not online");
                            }
                        } else {
                            sender.sendMessage("§6§lClaimChunk>§c§l (ADMIN)§r§c You didn't have the permission to use this command");
                        }
                    } else {
                        sender.sendMessage("§6§lClaimChunk>§c§l (ADMIN)§r§c Use /chunk adminunclaimall <player>");
                    }
                    break;
                case "adminunclaim":
                    if(p.isOp())
                    {
                        try {
                            if(Database.getClaimOwner(p) != null)
                            {
                                Database.removeClaim(p);
                                sender.sendMessage("§6§lClaimChunk>§c§l (ADMIN)§r§a You have unclaim the area.");
                            } else {
                                sender.sendMessage("§6§lClaimChunk>§c§l (ADMIN)§r§c This area does not belong to anyone");
                            }
                        } catch (Exception e) {
                            Loader.getLoader.getLogger().alert(e.getMessage());
                        }
                    } else {
                        sender.sendMessage("§6§lClaimChunk>§c§l (ADMIN)§r§c You didn't have the permission to use this command");
                    }
                    break;
                case "unclaimall":
                    try {
                        Database.removeAllClaim(p);
                    } catch (Exception e) {
                        Loader.getLoader.getLogger().alert(e.getMessage());
                    }
                    sender.sendMessage("§6§lClaimChunk>§r§a You have removed all your claim..");
                    break;
                case "unclaim":
                    try {
                        if(Database.getClaimOwner(p) != null)
                        {
                            if(p.getName().equalsIgnoreCase(Database.getClaimOwner(p)))
                            {
                                Database.removeClaim(p);
                                sender.sendMessage("§6§lClaimChunk>§r§a You have unclaim the area.");
                            }
                            else
                            {
                                sender.sendMessage("§6§lClaimChunk>§r§c This area is not yours");
                            }
                        } else {
                            sender.sendMessage("§6§lClaimChunk>§r§c This area does not belong to anyone");
                        }
                    } catch (Exception e) {
                        Loader.getLoader.getLogger().alert(e.getMessage());
                    }
                    break;
                case "info":
                    try {
                        if(Database.getClaimOwner(p) != null)
                        {
                            String ownerName = Database.getClaimOwner(p);
                            sender.sendMessage("§6§lClaimChunk§r§a");
                            sender.sendMessage("§6§l-§r§a Chunk of §2"+ownerName);
                            sender.sendMessage("§6§l-§r§a ID: §2"+p.getChunkX()+":"+p.getChunkZ() + " ("+Database.getClaimId(p)+")");
                        } else {
                            sender.sendMessage("§6§lClaimChunk>§r§c This area does not belong to anyone");
                        }
                    } catch (Exception e) {
                        Loader.getLoader.getLogger().alert(e.getMessage());
                    }
                    break;
                case "claim":
                    try {
                        if(Database.getClaimOwner(p) != null)
                        {
                            String ownerName = Database.getClaimOwner(p);
                            sender.sendMessage("§6§lClaimChunk>§r§c This area is already claimed by§f "+ownerName);
                        } else {
                            if(Loader.config.getBoolean("economyapi.isActive"))
                            {
                                if(EconomyAPI.getInstance().myMoney(p) >= Loader.config.getDouble("economyapi.price"))
                                {
                                    EconomyAPI.getInstance().reduceMoney(p, Loader.config.getDouble("economyapi.price"));
                                    Database.makeClaim(p);
                                    sender.sendMessage("§6§lClaimChunk>§r§a You have claimed this area.");
                                } else {
                                    sender.sendMessage("§6§lClaimChunk>§r§c You must have "+Loader.config.getDouble("economyapi.price") + "$ to claim this area.");
                                }
                            } else {
                                Database.makeClaim(p);
                                sender.sendMessage("§6§lClaimChunk>§r§a You have claimed this area.");
                            }
                        }
                    } catch (Exception e) {
                        Loader.getLoader.getLogger().alert(e.getMessage());
                    }

                    break;
                default:
                    sender.sendMessage("§6§lClaimChunk>§r§c Please, use /chunk help to see available commands.");
                    break;
                case "removeplayer":
                    try {
                        if(Database.getClaimOwner(p) != null & p.getName().equalsIgnoreCase(Database.getClaimOwner(p)))
                        {
                            if(args.length == 2)
                            {
                                if(Server.getInstance().getPlayer(args[1]) != null)
                                {
                                    Server.getInstance().getPlayer(args[1]).sendMessage("§6§lClaimChunk>§r§c You are now ungranted from a claim of §f§i"+p.getName());
                                    sender.sendMessage("§6§lClaimChunk>§r §f§i"+args[1]+"§c §ais now ungranted from one of your claim.");
                                    Database.removePlayerFromClaim(Database.getClaimId(p), Server.getInstance().getPlayer(args[1]).getUniqueId());
                                } else {
                                    sender.sendMessage("§6§lClaimChunk>§r§c This player is not online");
                                }
                            } else {
                                sender.sendMessage("§6§lClaimChunk>§r§c Use /chunk addplayer <player>");
                            }
                        } else {
                            sender.sendMessage("§6§lClaimChunk>§r§c This area is not yours");
                        }
                        break;
                    } catch (Exception e) {
                        Loader.getLoader.getLogger().alert("error: " +e.getMessage());
                    }
                    break;
                case "addplayer":
                    try {
                        if(Database.getClaimOwner(p) != null & p.getName().equalsIgnoreCase(Database.getClaimOwner(p)))
                        {
                            if(args.length == 2)
                            {
                                if(Server.getInstance().getPlayer(args[1]) != null)
                                {
                                    Server.getInstance().getPlayer(args[1]).sendMessage("§6§lClaimChunk>§r§a You are now granted in a claim of §f§i"+p.getName());
                                    sender.sendMessage("§6§lClaimChunk>§r §f§i"+args[1]+"§r §ais now granted in one of your claim.");
                                    Database.addPlayerInClaim(p.getUniqueId(), Database.getClaimId(p), Server.getInstance().getPlayer(args[1]).getUniqueId());
                                }
                                else {
                                    sender.sendMessage("§6§lClaimChunk>§r§c This player is not online");
                                }
                            } else {
                                sender.sendMessage("§6§lClaimChunk>§r§c Use /chunk addplayer <player>");
                            }
                        } else {
                            sender.sendMessage("§6§lClaimChunk>§r§c This area is not yours");
                        }
                        break;
                    } catch (Exception e) {
                        Loader.getLoader.getLogger().alert("error: " +e.getMessage());
                    }
            }
        }
        return false;
    }
}

