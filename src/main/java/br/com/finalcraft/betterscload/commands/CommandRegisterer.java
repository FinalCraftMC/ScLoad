package br.com.finalcraft.betterscload.commands;

import org.bukkit.plugin.java.JavaPlugin;

public class CommandRegisterer {

    public static void registerCommands(JavaPlugin pluginInstance) {
        pluginInstance.getCommand("betterscload").setExecutor(new CMDScLoad());
    }

}
