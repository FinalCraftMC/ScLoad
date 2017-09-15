/*
 *  ScLoad, Minecraft bukkit plugin
 *  (c)2013-2017, fromgate, fromgate@gmail.com
 *  http://dev.bukkit.org/server-mods/schematic/
 *
 *  This file is part of ScLoad.
 *
 *  ScLoad is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ScLoad is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with ScLoad.  If not, see <http://www.gnorg/licenses/>.
 *
 */

package me.fromgate.scload;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public class ScLoad extends JavaPlugin {
    //Конфигурация
    boolean use_we_folder = true;
    int blockpertick = 5000;
    int delay = 2;
    boolean vcheck = false;
    boolean language_save = false;
    String language = "english";
    boolean fastplace = true;
    boolean usePermPerFile = false;
    boolean undo_enabled = true;


    String schem_dir;
    SLUtil u;
    static ScLoad instance;

    @Override
    public void onEnable() {
        reloadCfg();
        u = new SLUtil(this, vcheck, language_save, language);
        getCommand("scload").setExecutor(u);
        schem_dir = getSchematicDirectory();
        instance = this;

        /* try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
        } */
    }

    public void reloadCfg() {
        reloadConfig();
        language = getConfig().getString("general.language", "english");
        getConfig().set("general.language", language);
        language_save = getConfig().getBoolean("general.language-save", false);
        getConfig().set("general.language-save", language_save);
        vcheck = getConfig().getBoolean("general.check-updates", true);
        getConfig().set("general.check-updates", vcheck);
        use_we_folder = getConfig().getBoolean("schematic-loader.use-worldedit-folder", true);
        getConfig().set("schematic-loader.use-worldedit-folder", use_we_folder);
        blockpertick = getConfig().getInt("schematic-loader.blocks-per-tick", 5000);
        getConfig().set("schematic-loader.blocks-per-tick", blockpertick);
        delay = getConfig().getInt("schematic-loader.delay-between-ticks", 2);
        getConfig().set("schematic-loader.delay-between-ticks", delay);
        fastplace = getConfig().getBoolean("schematic-loader.fast-place", false);
        getConfig().set("schematic-loader.fast-place", fastplace);
        usePermPerFile = getConfig().getBoolean("schematic-loader.use-permission-per-file", false);
        getConfig().set("schematic-loader.use-permission-per-file", usePermPerFile);
        saveConfig();
    }


    private String getSchematicDirectory() {
        String dir = getDataFolder() + File.separator + "schematics";
        if (use_we_folder) {
            Plugin we = this.getServer().getPluginManager().getPlugin("WorldEdit");
            if (we != null) dir = we.getDataFolder() + File.separator + "schematics";
        }
        File sd = new File(dir);
        if (!sd.exists()) sd.mkdirs();
        return dir;
    }


}
