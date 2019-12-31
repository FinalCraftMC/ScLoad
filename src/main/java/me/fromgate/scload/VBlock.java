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
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import org.bukkit.World;

public class VBlock {
    BukkitWorld world;
    BlockVector bvector;
    BaseBlock bblock;
    int hash;

    public VBlock(World w, BlockVector bv, BaseBlock bb) {
        this.world = new BukkitWorld(w);
        this.bvector = bv;
        this.bblock = bb;
        this.hash = calcHashCode();
    }

    public void placeBlockFast() {
        try {
            world.setBlock(bvector, bblock, false);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    public void placeBlock() {

        try {
            world.setBlock(bvector, bblock, true);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    public int getTypeId() {
        return bblock.getType();
    }


    public int calcHashCode() {
        int prime = 31;
        int result = bvector.getBlockX() >> 4;
        if (result < 0) result = result * (-1) * prime;
        result = result * prime + (bvector.getBlockZ() >> 4);
        if (result < 0) result = result * (-1) * prime;
        return result;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof VBlock)) return false;
        if (world == null) return false;
        VBlock other = (VBlock) obj;
        if (other.world == null) return false;
        if (!world.getWorld().equals(other.world.getWorld())) return false;
        if (bvector == null) return false;
        if (other.bvector == null) return false;
        if (bvector.getBlockX() != other.bvector.getBlockX()) return false;
        if (bvector.getBlockY() != other.bvector.getBlockY()) return false;
        return (bvector.getBlockZ() == other.bvector.getBlockZ());
    }
}
