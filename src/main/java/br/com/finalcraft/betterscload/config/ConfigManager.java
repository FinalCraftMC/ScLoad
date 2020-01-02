package br.com.finalcraft.betterscload.config;

import br.com.finalcraft.betterscload.config.data.BlockRemap;
import br.com.finalcraft.evernifecore.config.Config;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class ConfigManager {

    public static Config mainConfig;

    public static Config getMainConfig(){
        return mainConfig;
    }

    public static int blocksPerTick = 10000;
    public static int tickInterval = 2;
    public static boolean fastplace = true;
    public static String schematicDirName;

    public static HashMap<String,HashMap<String, BlockRemap>> remmaping2DMap = new HashMap<>();

    public static void initialize(JavaPlugin instance){
        mainConfig          = new Config(instance,"config.yml"      ,false);

        blocksPerTick       = mainConfig.getOrSetDefaultValue("Settings.blockspertick",5000);
        tickInterval        = mainConfig.getOrSetDefaultValue("Settings.tickInterval",2);
        fastplace           = mainConfig.getOrSetDefaultValue("Settings.fastplace",true);
        schematicDirName    = mainConfig.getOrSetDefaultValue("Settings.schematicDirName", getSchematicDirectory());

        if (mainConfig.getKeys("Remmaping").isEmpty()){
            mainConfig.setDefaultValue("Remmaping.example", Arrays.asList("1:1 - 35:1","1:2 - 35:2","6 - 36","7 - 37"));
        }

        remmaping2DMap.clear();
        for (String remapName : mainConfig.getKeys("Remmaping")) {
            HashMap<String,BlockRemap> remmapingMap = new HashMap<>();
            for (String remapValue : mainConfig.getStringList("Remmaping." + remapName)) {
                try{
                    BlockRemap blockRemap = new BlockRemap(remapValue);
                    remmapingMap.put(blockRemap.getOriginIdentifier(),blockRemap);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            remmaping2DMap.put(remapName.toLowerCase(),remmapingMap);
        }

        mainConfig.save();
    }

    public static HashMap<String, BlockRemap> selectedMap;

    public static boolean applyRemap(BaseBlock baseBlock){
        if (selectedMap !=null){
            BlockRemap blockRemap = selectedMap.get(baseBlock.getType() + ":" + baseBlock.getData());
            if (blockRemap == null){
                blockRemap = selectedMap.get(baseBlock.getType() + ":-1");
            }
            if (blockRemap != null){
                blockRemap.applyRemap(baseBlock);
                return true;
            }
        }
        return false;
    }

    private static String getSchematicDirectory() {
        Plugin we = Bukkit.getPluginManager().getPlugin("WorldEdit");
        String dir = we.getDataFolder() + File.separator + "schematics";
        File sd = new File(dir);
        if (!sd.exists()) sd.mkdirs();
        return dir;
    }
}
