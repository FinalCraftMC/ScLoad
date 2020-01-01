/*
 *  BetterScLoad, Minecraft bukkit plugin
 *  (c)2013-2017, fromgate, fromgate@gmail.com
 *  http://dev.bukkit.org/server-mods/schematic/
 *
 *  This file is part of BetterScLoad.
 *
 *  BetterScLoad is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  BetterScLoad is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with BetterScLoad.  If not, see <http://www.gnorg/licenses/>.
 *
 */

package br.com.finalcraft.betterscload;

import br.com.finalcraft.betterscload.commands.CommandRegisterer;
import br.com.finalcraft.betterscload.config.ConfigManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class BetterScLoad extends JavaPlugin {

    public static BetterScLoad instance;


    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.initialize(this);
        CommandRegisterer.registerCommands(this);
    }

    private String getSchematicDirectory() {
        String dir = getDataFolder() + File.separator + "schematics";
        Plugin we = this.getServer().getPluginManager().getPlugin("WorldEdit");
        if (we != null) dir = we.getDataFolder() + File.separator + "schematics";
        File sd = new File(dir);
        if (!sd.exists()) sd.mkdirs();
        return dir;
    }


}
