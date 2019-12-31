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

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class QueueManager {

    private static Map<String, SLQueue> qman = new HashMap<>();

    public static boolean addQueue(Player player, String fileName) {
        if (qman.containsKey(player.getName()) && qman.get(player.getName()).isActive()) return false;
        return addQueue (player, player.getWorld(),
                new Vector(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()),
                fileName);
    }

    public static boolean addQueue(CommandSender sender, World world, Vector v, String fileName) {
        if (qman.containsKey(sender.getName()) && qman.get(sender.getName()).isActive()) return false;
        SLQueue slQueue = null;
        try {
            slQueue = new SLQueue(world, v, fileName);
        } catch (FileNotFoundException e) {
            sender.sendMessage(ChatColor.RED + "Schematic file not found.");
            return false;
        }
        qman.put(sender.getName(), slQueue);
        startNext();
        return true;
    }


    public static void startNext() {
        SLQueue q = getNext();
        if (q == null) return;
        q.start();
    }

    public static SLQueue getNext() {
        if (isActive()) return null;
        for (SLQueue q : qman.values())
            if (!q.isFinished()) return q;
        return null;
    }

    public static boolean isActive() {
        for (SLQueue q : qman.values())
            if (q.isActive()) return true;
        return false;
    }


}
