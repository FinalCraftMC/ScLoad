/*  
 *  ScLoad, Minecraft bukkit plugin
 *  (c)2013, fromgate, fromgate@gmail.com
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

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class SLQueue {
	boolean flag_loaded=false;
	boolean flag_finished=false;

	ScLoad plg;

	World world;
	BukkitWorld worldeditworld;
	Vector pos;
	String filename;

	Map<VChunk,List<VBlock>> chunked;
	List<List<VBlock>> queue;

	public boolean isLoaded(){
		return flag_loaded;
	}

	SLQueue (ScLoad plg, World w, Vector pos, String filename){
		this.plg = plg;
		this.world = w;
		this.pos = pos;
		this.filename = filename;
		this.worldeditworld = new BukkitWorld (w);
	}

	public void start(){
		CuboidClipboard cc = load (filename);
		if (cc == null) return;
		addAndStart (cc);
	}

	public CuboidClipboard load(String filename){
		if (filename.isEmpty()) return null;
		File f = new File (plg.schem_dir+File.separator+filename+".schematic");
		if (!f.exists()) return null;
		SchematicFormat sc = SchematicFormat.getFormat(f);
		if (sc == null) return null;
		CuboidClipboard cc = null;			
		try {
			cc = sc.load(f);
		} catch (Exception e) {
			plg.u.log("Error loading file "+f.getAbsolutePath());
			e.printStackTrace();
			return null;
		}
		return cc;
	}

	public void addAndStart (final CuboidClipboard cc){
		Bukkit.getScheduler().runTaskLaterAsynchronously(plg, new Runnable(){
			@Override
			public void run() {
				addDelayed (pos,cc);
				processQueue();
			}
		}, plg.delay);
	}

	public BaseBlock[][][] getCuboidClipboadData(CuboidClipboard cc){
		try {
			Object obj = cc;
			Field f;
			f = obj.getClass().getDeclaredField("data");
			f.setAccessible(true);
			BaseBlock[][][] blocks = (BaseBlock[][][]) f.get(obj); 
			return blocks;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void addDelayed (Vector pos, CuboidClipboard cc){
		queue = new ArrayList<List<VBlock>>();
		chunked = new HashMap<VChunk,List<VBlock>>();
		List<VBlock> first = new ArrayList<VBlock>(); 
		List<VBlock> last = new ArrayList<VBlock>();
		List<VBlock> fin = new ArrayList<VBlock>(); 
		BaseBlock[][][] blocks = getCuboidClipboadData(cc);

		for (int x = 0; x<cc.getSize().getBlockX();x++)
			for (int y = 0; y<cc.getSize().getBlockY();y++)
				for (int z = 0; z<cc.getSize().getBlockZ();z++){
					BaseBlock bb = blocks[x][y][z];
					BlockVector bv = pos.add(x, y, z).toBlockVector();
					VChunk vch = new VChunk(bv);
					VBlock vb = new VBlock(world, bv, bb);
					if (!chunked.containsKey(vch)) chunked.put(vch, new ArrayList<VBlock>());
					chunked.get(vch).add(vb);
				}

		if (chunked.isEmpty()) return;
		for (VChunk vch : chunked.keySet()){
			for (int i = 0; i<chunked.get(vch).size(); i++){
				VBlock vb = chunked.get(vch).get(i);
				if (BlockType.shouldPlaceFinal(vb.bblock.getType())) fin.add(vb);
				else if (BlockType.shouldPlaceLast(vb.bblock.getType())) last.add(vb);
				else first.add(vb);
			}
		}

		int counter = 0; 

		queue.clear();

		List<VBlock> bpt = new ArrayList<VBlock>();
		if (!first.isEmpty()){
			for (int i = 0; i<first.size();i++){
				VBlock vb = first.get(i);
				counter ++;
				if (counter>=plg.blockpertick){
					queue.add(bpt);
					bpt = new ArrayList<VBlock>();
					counter = 0;
				}
				bpt.add(vb);
			}
			queue.add(bpt);			
		}

		bpt = new ArrayList<VBlock>();
		if (!last.isEmpty()){
			for (int i = 0; i<last.size();i++){
				VBlock vb = last.get(i);
				counter ++;
				if (counter>=plg.blockpertick){
					queue.add(bpt);
					bpt = new ArrayList<VBlock>();
					counter = 0;
				}
				bpt.add(vb);
			}
			queue.add(bpt);			
		}

		bpt = new ArrayList<VBlock>();
		if (!fin.isEmpty()){
			for (int i = 0; i<fin.size();i++){
				VBlock vb = fin.get(i);
				counter ++;
				if (counter>=plg.blockpertick){
					queue.add(bpt);
					bpt = new ArrayList<VBlock>();
					counter = 0;
				}
				bpt.add(vb);
			}
			queue.add(bpt);			
		}		

	}

	public void processQueue(){
		if (queue.isEmpty()) return;
		Bukkit.getScheduler().runTaskLater(plg, new Runnable(){
			@Override
			public void run() {
				placeBlocks(queue.get(0));
				queue.remove(0);
				if (!queue.isEmpty()) processQueue();
				else {
					refreshChunks(chunked.keySet());
				}
			}
		}, plg.delay);
	}

	public void placeBlocks(List<VBlock> blocks){
		if (blocks.isEmpty()) return;
		for (int i = 0; i<blocks.size();i++){
			Block b = world.getBlockAt(blocks.get(i).bvector.getBlockX(), blocks.get(i).bvector.getBlockY(), blocks.get(i).bvector.getBlockZ());
			int id = b.getTypeId();
			if ((BlockType.shouldPlaceFinal(id))||
					(BlockType.shouldPlaceLast(id))||		
					(BlockType.canPassThrough(id))) b.setTypeIdAndData(0, (byte) 0, false);
			else if (BlockType.isContainerBlock(id)) worldeditworld.clearContainerBlockContents(blocks.get(i).bvector);
		}
		if (plg.fastplace)	for (int i = 0; i<blocks.size();i++) blocks.get(i).placeBlockFast();
		else for (int i = 0; i<blocks.size();i++) blocks.get(i).placeBlock();
	}



	public void refreshChunks(final Set<VChunk> vchs){
		Bukkit.getScheduler().runTaskLaterAsynchronously(plg, new Runnable(){
			@Override
			public void run() {
				for (VChunk vch : vchs){
					world.refreshChunk(vch.x, vch.z);
				}			
			}
		}, plg.delay);
	}


}
