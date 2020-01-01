package br.com.finalcraft.betterscload.commands;

import br.com.finalcraft.betterscload.BetterScLoad;
import br.com.finalcraft.betterscload.PermissionNodes;
import br.com.finalcraft.betterscload.queue.QueueManager;
import br.com.finalcraft.betterscload.config.ConfigManager;
import br.com.finalcraft.evernifecore.FCBukkitUtil;
import br.com.finalcraft.evernifecore.argumento.MultiArgumentos;
import br.com.finalcraft.evernifecore.config.location.FCLocation;
import br.com.finalcraft.evernifecore.fancytext.FancyText;
import com.sk89q.worldedit.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CMDScLoad implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!FCBukkitUtil.hasThePermission(sender,PermissionNodes.commandScLoad)){
            return true;
        }

        MultiArgumentos argumentos = new MultiArgumentos(args);

        switch (argumentos.get(0).toLowerCase()){
            case "?":
            case "help:":
            case "":
                return help(label,sender,argumentos);
            case "load":
                return load(label,sender,argumentos);
            case "list":
                return list(label,sender,argumentos);
            case "reload":
                return reload(label,sender,argumentos);
        }

        sender.sendMessage("§cErro de parametros, por favor use /" + label + " help");
        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------------------//
    // Command Help
    // -----------------------------------------------------------------------------------------------------------------------------//
    public boolean help(String label, CommandSender sender, MultiArgumentos argumentos){
        sender.sendMessage("§6§m---------------§6( §e§lBetterScLoad§r §6)§m---------------");
        new FancyText("§3§l ▶ §a/" + label + " load <SchematicName> [WorldName xCord yCord zCord]","§bCarrega uma schematic para a sua posição, ou a posição especificada!").setSuggestCommandAction("/" + label + " load").sendTo(sender);
        new FancyText("§3§l ▶ §a/" + label + " list","§bLista todos as Schematics disponíveis!").setSuggestCommandAction("/" + label + " list").sendTo(sender);
        new FancyText("§3§l ▶ §a/" + label + " reload","§bRecarrega as configurações do plugin!").setSuggestCommandAction("/" + label + " reload").sendTo(sender);
        sender.sendMessage("§6§m-----------------------------------------------------");
        return true;
    }


    // -----------------------------------------------------------------------------------------------------------------------------//
    // Command Load
    // -----------------------------------------------------------------------------------------------------------------------------//
    public boolean load(String label, CommandSender sender, MultiArgumentos argumentos){

        if (argumentos.get(1).isEmpty()){
            new FancyText("§3§l ▶ §a/" + label + " load <SchematicName> [WorldName xCord yCord zCord]","§bCarrega uma schematic para a sua posição, ou a posição especificada!").setSuggestCommandAction("/" + label + " load").sendTo(sender);
        }

        String fileName = argumentos.get(1).toString();
        Integer xCord;
        Integer yCord;
        Integer zCord;
        World world;
        if (!argumentos.get(2).isEmpty()){
            world = Bukkit.getWorld(argumentos.get(2).toString());

            if (world == null) {
                sender.sendMessage("§4§l ▶ §cO mundo §e" + argumentos.get(2) + "§c não existe!");
                return true;
            }

            xCord = argumentos.get(3).getInteger();
            yCord = argumentos.get(4).getInteger();
            zCord = argumentos.get(5).getInteger();

            if (xCord == null || yCord == null || zCord == null){
                sender.sendMessage("§4§l ▶ §cVocê precisa especificar as COORDENADAS!!!");
                return true;
            }
        }else {
            if ( !(sender instanceof Player) ){
                sender.sendMessage("§4§l ▶ §cVocê deve especificar o MUNDO e as Coordenadas!");
                return true;
            }

            Player player = (Player) sender;

            world = player.getWorld();
            xCord = player.getLocation().getBlockX();
            yCord = player.getLocation().getBlockY();
            zCord = player.getLocation().getBlockZ();
        }

        Vector vector = new Vector(xCord, yCord, zCord);
        String locationString = "(Mundo: " + world.getName() + ", x=" + xCord + ", y= " + yCord + ", z=" + zCord + ")";
        if (QueueManager.addQueue(sender, world, vector, fileName)){
            sender.sendMessage("§3§l ▶ §aIniciando colagem de " + fileName + ".schematic");
        }else {
            sender.sendMessage("§4§l ▶ §cErro ao tentar colar a schematic " + fileName + ".schematic");
        }
        sender.sendMessage("     §aEm " + locationString);
        return true;
    }


    // -----------------------------------------------------------------------------------------------------------------------------//
    // Command List
    // -----------------------------------------------------------------------------------------------------------------------------//
    public boolean list(String label, CommandSender sender, MultiArgumentos argumentos){
        File schemDirFile = new File(ConfigManager.schematicDirName);
        sender.sendMessage("§b§l Schematic List");
        for (String fileName : schemDirFile.list()) {
            if (fileName.endsWith(".schematic")){
                sender.sendMessage("§3§l ▶ §a" + fileName);
            }
        }
        return true;
    }


    // -----------------------------------------------------------------------------------------------------------------------------//
    // Command Reload
    // -----------------------------------------------------------------------------------------------------------------------------//
    public boolean reload(String label, CommandSender sender, MultiArgumentos argumentos){
        ConfigManager.initialize(BetterScLoad.instance);
        sender.sendMessage("§2§l ▶ §aBetterScLoad recarregado com sucesso!");
        return true;
    }
}
