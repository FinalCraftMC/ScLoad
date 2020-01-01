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


package br.com.finalcraft.betterscload.queue;

import com.sk89q.worldedit.BlockVector;

public class VChunk {
    int x;
    int z;

    public VChunk(BlockVector bv) {
        x = bv.getBlockX() >> 4;
        z = bv.getBlockZ() >> 4;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof VChunk))
            return false;
        VChunk other = (VChunk) obj;
        if (x != other.x)
            return false;
        if (z != other.z)
            return false;
        return true;
    }


}
