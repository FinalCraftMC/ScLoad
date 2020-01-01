package br.com.finalcraft.betterscload.config;

import br.com.finalcraft.betterscload.BetterScLoad;
import br.com.finalcraft.evernifecore.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {

    public static Config mainConfig;

    public static Config getMainConfig(){
        return mainConfig;
    }

    public static int blocksPerTick = 5000;
    public static int tickInterval = 2;
    public static boolean fastplace = true;
    public static String schematicDirName;

    public static HashMap<Integer,Integer> remmapignIDS = new HashMap<>();

    public static void initialize(JavaPlugin instance){
        mainConfig  = new Config(instance,"config.yml"      ,false);

        blocksPerTick       = mainConfig.getOrSetDefaultValue("Settings.blockspertick",5000);
        tickInterval        = mainConfig.getOrSetDefaultValue("Settings.tickInterval",2);
        fastplace           = mainConfig.getOrSetDefaultValue("Settings.fastplace",true);
        schematicDirName    = mainConfig.getOrSetDefaultValue("Settings.schematicDirName", getSchematicDirectory());

        List<String> remaps = mainConfig.getOrSetDefaultValue("Remmaping", new ArrayList<String>());

        remmapignIDS.clear();
        for (String remap : remaps) {
            try{
                Integer origin = Integer.parseInt(remap.split("-")[0]);
                Integer target = Integer.parseInt(remap.split("-")[1]);
                remmapignIDS.put(origin,target);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        mainConfig.saveIfNewDefaults();
    }

    public static boolean hasRemapMap(){
        return !remmapignIDS.isEmpty();
    }

    public static int getRemmapedID(int origin){
        return remmapignIDS.getOrDefault(origin,origin);
    }

    private static String getSchematicDirectory() {
        Plugin we = Bukkit.getPluginManager().getPlugin("WorldEdit");
        String dir = we.getDataFolder() + File.separator + "schematics";
        File sd = new File(dir);
        if (!sd.exists()) sd.mkdirs();
        return dir;
    }
}
