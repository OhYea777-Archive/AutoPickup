package com.ohyea777.autopickup;

import com.ohyea777.autopickup.config.AutoPickupConfig;
import com.ohyea777.autopickup.config.ConfigLoader;
import com.ohyea777.autopickup.events.AutoPickupListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoPickup extends JavaPlugin {

    private static AutoPickup instance;

    private AutoPickupConfig config;
    private AutoPickupListener listener;

    @Override
    public void onEnable() {
        instance = this;
        config = ConfigLoader.reload();
        listener = new AutoPickupListener();

        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(listener);
    }

    public static AutoPickup getInstance() {
        return instance;
    }

    public AutoPickupConfig getActualConfig() {
        return config;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completion = new ArrayList<String>();

        if (args.length == 0) Collections.addAll(completion, "about", "reload");
        else if (args.length == 1) {
            if ("about".startsWith(args[0].toLowerCase())) completion.add("about");
            else if ("reload".startsWith(args[0].toLowerCase())) completion.add("reload");
        }

        return completion;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("a")) {
                sender.sendMessage(getActualConfig().formatWithPrefix("%prefix% %k%Plugin version%s%: %v%" + getDescription().getVersion() + "%k%!"));
                sender.sendMessage(getActualConfig().formatWithPrefix("%prefix% %k%Plugin author%s%: %v%OhYea777%k%!"));

                return true;
            } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
                if (sender.hasPermission("autopickup.reload")) {
                    config = ConfigLoader.reload();

                    sender.sendMessage(getActualConfig().getReload());
                } else sender.sendMessage(getActualConfig().getPermissionDenied());

                return true;
            }
        }

        sender.sendMessage(getActualConfig().formatWithPrefix("%prefix% %k%For info about the plugin do%s%: %v%/ap about%k%!"));
        sender.sendMessage(getActualConfig().formatWithPrefix("%prefix% %k%To reload the config do%s%: %v%/ap reload%k%!"));

        return true;
    }

}
