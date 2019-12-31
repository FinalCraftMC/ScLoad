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

import com.sk89q.worldedit.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SLUtil extends FGUtilCore implements CommandExecutor, Listener {
    ScLoad plg;

    public SLUtil(ScLoad plugin, boolean vcheck, boolean savelng, String language) {
        super(plugin, savelng, language, "scload", "schematic");
        this.plg = plugin;
        fillMSG();
        if (savelng) this.SaveMSG();
        initCommands();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if ((args.length > 0) && checkCmdPerm(sender, args[0])) {
            if (args.length == 1) return executeCmd(sender, args[0]);
            else if (args.length == 2) return executeCmd(sender, args[0], args[1]);
            else if (args.length == 6) return executeCmd(sender, args[0], args[1], args[2], args[3], args[4], args[5]);
        } else printMSG(sender, "cmd_cmdpermerr", 'c');
        return true;
    }

    private boolean executeCmd(CommandSender sender, String cmd, String fn, String wn, String xs, String ys, String zs) {
        if (cmd.equalsIgnoreCase("load")) {
            if (!checkPerFilePermission(sender instanceof Player ? (Player) sender : null, fn))
                return returnMSG(true, sender, "msg_nopermforfile", fn, 'c', '4');

            World w = Bukkit.getWorld(wn);

            if (w == null) {
                printMSG(sender, "msg_unknownworld", 'c', '4', wn);
                return true;
            }

            if (!isIntegerSigned(xs, ys, zs)) {
                printMSG(sender, "msg_wrongcoordinate", 'c', '4', xs, ys, zs);
                return true;
            }
            int x = Integer.parseInt(xs);
            int y = Integer.parseInt(ys);
            int z = Integer.parseInt(zs);
            Vector v = new Vector(x, y, z);
            QueueManager.addQueue(sender, w, v, fn);
            printMSG(sender, "msg_loadstarted", new Location(w, x, y, z), fn);
        } else return false;
        return true;
    }

    private boolean executeCmd(CommandSender sender, String cmd, String arg) {
        if (cmd.equalsIgnoreCase("load")) {
            if (!checkPerFilePermission(sender instanceof Player ? (Player) sender : null, arg))
                return returnMSG(true, sender, "msg_nopermforfile", arg, 'c', '4');

            if (!(sender instanceof Player)) {
                printMSG(sender, "msg_senderisnotplayer", 'c');
                return true;
            }
            Player p = (Player) sender;
            QueueManager.addQueue(p, arg);
            printMSG(p, "msg_loadstarted", p.getLocation(), arg);
        } else if (cmd.equalsIgnoreCase("list")) {
            int page = 1;
            if (isIntegerGZ(arg)) page = Integer.parseInt(arg);
            File f = new File(plg.schem_dir);
            List<String> files = new ArrayList<String>();
            for (String fn : f.list())
                if (fn.endsWith(".schematic")) files.add(fn.substring(0, fn.length() - 10));
            printPage(sender, files, page, "msg_filelist", "", false, 20);

        } else return false;
        return true;
    }

    private boolean executeCmd(CommandSender sender, String cmd) {
        if (cmd.equalsIgnoreCase("cfg")) {
            printConfig(sender, 1, 100, false, true);
        } else if (cmd.equalsIgnoreCase("reload")) {
            plg.reloadCfg();
            printMSG(sender, "msg_reloaded");
        } else if (cmd.equalsIgnoreCase("list")) {
            File f = new File(plg.schem_dir);
            List<String> files = new ArrayList<String>();
            for (String fn : f.list())
                if (fn.endsWith(".schematic")) files.add(fn.substring(0, fn.length() - 10));
            printPage(sender, files, 1, "msg_filelist", "", false, 20);
        } else if (cmd.equalsIgnoreCase("help")) {
            PrintHlpList(sender, 1, 100);
        } else return false;
        return true;
    }

    public void initCommands() {
        addCmd("help", "config", "hlp_thishelp", "/scl help", true);
        addCmd("reload", "config", "hlp_reload", "/scl reload", true);
        addCmd("cfg", "config", "hlp_cfg", "/scl cfg", true);
        addCmd("list", "load", "hlp_list", "/scl list", true);
        addCmd("load", "load", "hlp_load", "/scl load <filename> [<world> <x> <y> <z>]", true);

    }

    public void fillMSG() {
        addMSG("hlp_cfg", "%1% - display current configuration");
        addMSG("hlp_reload", "%1% - reload plugin configuration and reload gadgets");
        addMSG("hlp_list", "%1% - list all availiable schematic files");
        addMSG("hlp_load", "%1% - load structure from the schematic file and build it at the world");
        addMSG("msg_unknownworld", "World %1% was not found");
        addMSG("msg_wrongcoordinate", "Failed to determine location from coordinates:  %1% (%2%,%3%,%4%)");
        addMSG("msg_filenotloaded", "Failed to load file: %1%");
        addMSG("msg_loadstarted", "File %2% was loaded. Starting to build at %1%");
        addMSG("msg_senderisnotplayer", "You need to specify the world and coordinates or run this command as a logged player");
        addMSG("msg_reloaded", "Configuration reloaded");
        addMSG("msg_filelist", "File list (extension not shown)");
        addMSG("cfgmsg_schematic-loader_use-worldedit-folder", "Use WorldEdit folder: %1%");
        addMSG("cfgmsg_schematic-loader_blocks-per-tick", "Number of blocks to place during the single tick: %1%");
        addMSG("cfgmsg_schematic-loader_delay-between-ticks", "Delay between ticks (min = 1): %1%");
        addMSG("cfgmsg_schematic-loader_fast-place", "Place blocks fast (without physics): %1%");
        addMSG("msg_nopermforfile", "You have not enough permissions to load file %1%");
    }


    /*
     *  Это... зачем?!
     */
    public List<Entity> getEntities(Location l1, Location l2) {
        List<Entity> entities = new ArrayList<Entity>();
        if (!l1.getWorld().equals(l2.getWorld())) return entities;
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
        int chX1 = x1 >> 4;
        int chX2 = x2 >> 4;
        int chZ1 = z1 >> 4;
        int chZ2 = z2 >> 4;
        for (int x = chX1; x <= chX2; x++)
            for (int z = chZ1; z <= chZ2; z++) {
                for (Entity e : l1.getWorld().getChunkAt(x, z).getEntities()) {
                    double ex = e.getLocation().getX();
                    double ey = e.getLocation().getY();
                    double ez = e.getLocation().getZ();
                    if ((x1 <= ex) && (ex <= x2) && (y1 <= ey) && (ey <= y2) && (z1 <= ez) && (ez <= z2))
                        entities.add(e);
                }
            }
        return entities;
    }

    private boolean checkPerFilePermission(Player player, String fileName) {
        if (!plg.usePermPerFile) return true;
        if (player == null) return true;
        if (player.hasPermission("schematic.file") || player.hasPermission("schematic.file.*")) return true;
        return (player.hasPermission("schematic.file." + fileName.toLowerCase()));
    }


}
