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
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class QueueManager {
    static ScLoad plg() {
        return ScLoad.instance;
    }

    private static Map<String, SLQueue> qman = new HashMap<String, SLQueue>();

    public static boolean addQueue(Player p, String fname) {
        if (qman.containsKey(p.getName()) && qman.get(p.getName()).isActive()) return false;
        qman.put(p.getName(), new SLQueue(p.getWorld(), new Vector(p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()), fname));
        startNext();
        return true;
    }

    public static boolean addQueue(CommandSender s, World w, Vector v, String fn) {
        if (qman.containsKey(s.getName()) && qman.get(s.getName()).isActive()) return false;
        qman.put(s.getName(), new SLQueue(s, w, v, fn));
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
