/*
 *  BetterScLoad, Minecraft bukkit plugin
 *  (c)2013-2019, fromgate (fromgate@gmail.com) and Exemplive
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
 *  along with BetterScLoad.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package br.com.finalcraft.betterscload.queue;

import br.com.finalcraft.betterscload.BetterScLoad;
import br.com.finalcraft.betterscload.config.ConfigManager;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.SchematicReader;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.io.Closer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

@SuppressWarnings("all")
public class SLQueue {
    private JavaPlugin plg() {
        return BetterScLoad.instance;
    }

    boolean flag_loaded = false;
    boolean flag_finished = false;

    World world;
    BukkitWorld worldeditworld;
    Vector pos;
    BlockArrayClipboard bac;


    Map<VChunk, List<VBlock>> chunked;
    List<List<VBlock>> queue;
    List<List<VBlock>> undo;
    List<Entity> entities;

    private boolean active = false;
    private boolean finished = false;

    public boolean isLoaded() {
        return flag_loaded;
    }

    SLQueue(World world, Vector vector, String filename) throws FileNotFoundException {
        this.world = world;
        this.worldeditworld = new BukkitWorld(world);
        bac = load(filename);
        if (bac == null) {
            throw new FileNotFoundException("Schematic file not found.");
        }
        this.pos = calculatePosWithOffset(vector, bac);
    }

    public Vector calculatePosWithOffset(Vector origin, BlockArrayClipboard bac){
        Vector offset = bac.getMinimumPoint().subtract(bac.getOrigin());
        return origin.add(offset);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isFinished() {
        return finished;
    }

    public void start() {
        active = true;
        fillEntities();
        addAndStart(bac);
    }

    public BlockArrayClipboard load(String filename) {
        if (filename.isEmpty()) return null;
        File schematicFile = null;
        // If the file does not exist in root schematics folder, check its subdirectories
        // By default, FAWE separates schematics per-UUID folder but this can be disabled through
        // configuration.
        for (File file : new File(ConfigManager.schematicDirName).listFiles()) {
        	if (file.getName().startsWith(filename + ".")) {
                schematicFile = file;
                break;
            }
        }
        if (schematicFile == null) {
        	for (File file : new File(ConfigManager.schematicDirName).listFiles()) {
            	if (!file.isDirectory())
            		continue;
                for (File schematic : file.listFiles()) {
                    if (schematic.getName().startsWith(filename + ".")) {
                        schematicFile = schematic;
                        break;
                    }
                }
            }
        }
        if (schematicFile == null || !schematicFile.exists()) return null;

        BlockArrayClipboard bac = null;
        try(Closer closer = Closer.create()) {
            FileInputStream fis = closer.register(new FileInputStream(schematicFile));
            BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
            SchematicReader reader = (SchematicReader)ClipboardFormat.SCHEMATIC.getReader(bis);
            bac = (BlockArrayClipboard) reader.read(worldeditworld.getWorldData());
        } catch (IOException e) {
            plg().getLogger().warning("Error loading file " + schematicFile.getAbsolutePath());
            e.printStackTrace();
        }
        return bac;
    }

    public void addAndStart(final BlockArrayClipboard bac) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plg(), new Runnable() {
            @Override
            public void run() {
                addDelayed(pos, bac);
                processQueue();
            }
        }, ConfigManager.tickInterval);
    }

    public void fillEntities() {
        Vector dim = bac.getDimensions();
        Location loc1 = new Location(world, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        Location loc2 = new Location(world, pos.getBlockX() + dim.getBlockX(), pos.getBlockY() + dim.getBlockY(), pos.getBlockZ() + dim.getBlockZ());
        entities = getEntities(loc1, loc2);
    }

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

    public void addDelayed(Vector origin, BlockArrayClipboard bac) {
        queue = new ArrayList<List<VBlock>>();
        chunked = new HashMap<VChunk, List<VBlock>>();
        List<VBlock> first = new ArrayList<VBlock>();
        List<VBlock> last = new ArrayList<VBlock>();
        List<VBlock> fin = new ArrayList<VBlock>();
        Vector dimensions = bac.getDimensions();
        for (int x = 0; x < dimensions.getBlockX(); x++)
            for (int y = 0; y < dimensions.getBlockY(); y++)
                for (int z = 0; z < dimensions.getBlockZ(); z++) {
                    if (origin.getBlockY() + y < 0) continue;
                    if (origin.getBlockY() + y >= world.getMaxHeight()) continue;

                    BaseBlock bb = bac.getBlock(new Vector(x, y, z).add(bac.getMinimumPoint()));
                    BlockVector bv = origin.add(x, y, z).toBlockVector();
                    VChunk vch = new VChunk(bv);
                    VBlock vb = new VBlock(world, bv, bb);
                    if (!chunked.containsKey(vch)) chunked.put(vch, new ArrayList<VBlock>());
                    chunked.get(vch).add(vb);
                }

        if (chunked.isEmpty()) return;
        for (VChunk vch : chunked.keySet()) {
            for (int i = 0; i < chunked.get(vch).size(); i++) {
                VBlock vb = chunked.get(vch).get(i);
                if (BlockType.shouldPlaceFinal(vb.bblock.getType())) fin.add(vb);
                else if (BlockType.shouldPlaceLast(vb.bblock.getType())) last.add(vb);
                else first.add(vb);
            }
        }

        int counter = 0;

        queue.clear();

        List<VBlock> bpt = new ArrayList<VBlock>();
        if (!first.isEmpty()) {
            for (int i = 0; i < first.size(); i++) {
                VBlock vb = first.get(i);
                counter++;
                if (counter >= ConfigManager.blocksPerTick) {
                    queue.add(bpt);
                    bpt = new ArrayList<VBlock>();
                    counter = 0;
                }
                bpt.add(vb);
            }
            queue.add(bpt);
        }

        bpt = new ArrayList<VBlock>();
        if (!last.isEmpty()) {
            for (int i = 0; i < last.size(); i++) {
                VBlock vb = last.get(i);
                counter++;
                if (counter >= ConfigManager.blocksPerTick) {
                    queue.add(bpt);
                    bpt = new ArrayList<VBlock>();
                    counter = 0;
                }
                bpt.add(vb);
            }
            queue.add(bpt);
        }

        bpt = new ArrayList<VBlock>();
        if (!fin.isEmpty()) {
            for (int i = 0; i < fin.size(); i++) {
                VBlock vb = fin.get(i);
                counter++;
                if (counter >= ConfigManager.blocksPerTick) {
                    queue.add(bpt);
                    bpt = new ArrayList<VBlock>();
                    counter = 0;
                }
                bpt.add(vb);
            }
            queue.add(bpt);
        }

    }

    public void processQueue() {
        if (queue.isEmpty()) return;
        Bukkit.getScheduler().runTaskLater(plg(), new Runnable() {
            @Override
            public void run() {
                placeBlocks(queue.get(0));
                queue.remove(0);
                if (!queue.isEmpty()) processQueue();
                else {
                    finished = true;
                    active = false;
                    if (ConfigManager.fastplace) refreshChunks(chunked.keySet());
                }
            }
        }, ConfigManager.tickInterval);
    }

    @SuppressWarnings("deprecation")
    public void placeBlocks(List<VBlock> blocks) {
        if (blocks.isEmpty()) return;
        for (int i = 0; i < blocks.size(); i++) {
            Block b = world.getBlockAt(blocks.get(i).bvector.getBlockX(), blocks.get(i).bvector.getBlockY(), blocks.get(i).bvector.getBlockZ());
            int id = b.getTypeId();
            if ((BlockType.shouldPlaceFinal(id)) ||
                    (BlockType.shouldPlaceLast(id)) ||
                    (BlockType.canPassThrough(id))) b.setTypeIdAndData(0, (byte) 0, (!ConfigManager.fastplace));
            else if (BlockType.isContainerBlock(id)) worldeditworld.clearContainerBlockContents(blocks.get(i).bvector);
        }
        if (ConfigManager.fastplace) {
            for (int i = 0; i < blocks.size(); i++){
                blocks.get(i).placeBlockFast();
            }
        }
        else {
            for (int i = 0; i < blocks.size(); i++){
                blocks.get(i).placeBlock();
            }
        }
    }

    /** [NOTE FROM EXEMPLIVE]: I don't asynchronous chunk refresh is safe? **/
    public void refreshChunks(final Set<VChunk> vchs) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plg(), new Runnable() {
            @Override
            public void run() {
                for (VChunk vch : vchs) {
                    world.refreshChunk(vch.x, vch.z);
                }
            }
        }, ConfigManager.tickInterval);
    }


}
