/*
 *  ScLoad, Minecraft bukkit plugin
 *  (c)2012-2017, fromgate, fromgate@gmail.com
 *  http://dev.bukkit.org/server-mods/reactions/
 *
 *  This file is part of ScLoad.
 *
 *  ReActions is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ReActions is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with ScLoad.  If not, see <http://www.gnorg/licenses/>.
 *
 */


package me.fromgate.scload;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public abstract class FGUtilCore {
    JavaPlugin plg;
    //utilities configuration
    private String px = "";
    private String permprefix = "fgutilcore.";
    private String language = "english";
    private String plgcmd = "<command>";
    //Messages + Translation
    YamlConfiguration lng;
    private boolean savelng = false;
    //String lngfile = this.language+".lng";
    protected HashMap<String, String> msg = new HashMap<String, String>();
    private char c1 = 'a'; //color 1 (default for text)
    private char c2 = '2'; //color 2 (default for values)
    protected String msglist = "";
    private boolean colorconsole = false;  // it will be necessary to add methods for configuration "from outside"
    private Set<String> log_once = new HashSet<String>();
    protected HashMap<String, Cmd> cmds = new HashMap<String, Cmd>();
    protected String cmdlist = "";
    PluginDescriptionFile des;
    private Logger log = Logger.getLogger("Minecraft");
    Random random = new Random();
    BukkitTask chId;

    //newupdate-checker
    private boolean project_check_version = true;
    private String project_id = ""; //66204 - PlayEffect
    private String project_name = "";
    private String project_current_version = "";
    private String project_last_version = "";
    //private String project_file_url = "";
    private String project_curse_url = "";
    private String version_info_perm = permprefix + "config"; // who to notify about updates [???]
    private String project_bukkitdev = "";


    public FGUtilCore(JavaPlugin plg, boolean savelng, String lng, String plgcmd, String permissionPrefix) {
        this.permprefix = permissionPrefix.endsWith(".") ? permissionPrefix : permissionPrefix + ".";
        this.plg = plg;
        this.des = plg.getDescription();
        this.language = lng;
        this.InitMsgFile();
        this.initStdMsg();
        this.fillLoadedMessages();
        this.savelng = savelng;
        this.plgcmd = plgcmd;
        this.px = ChatColor.translateAlternateColorCodes('&', "&3[" + des.getName() + "]&f ");
    }


    /*
     * Initializing Standard Messages
     */
    private void initStdMsg() {
        addMSG("msg_outdated", "%1% is outdated!");
        addMSG("msg_pleasedownload", "Please download new version (%1%) from ");
        addMSG("hlp_help", "Help");
        addMSG("hlp_thishelp", "%1% - this help");
        addMSG("hlp_execcmd", "%1% - execute command");
        addMSG("hlp_typecmd", "Type %1% - to get additional help");
        addMSG("hlp_typecmdpage", "Type %1% - to see another page of this help");
        addMSG("hlp_commands", "Command list:");
        addMSG("hlp_cmdparam_command", "command");
        addMSG("hlp_cmdparam_page", "page");
        addMSG("hlp_cmdparam_parameter", "parameter");
        addMSG("cmd_unknown", "Unknown command: %1%");
        addMSG("cmd_cmdpermerr", "Something wrong (check command, permissions)");
        // addMSG("cmd_ccnull", "The file could not be found.");
        addMSG("enabled", "enabled");
        msg.put("enabled", ChatColor.DARK_GREEN + msg.get("enabled"));
        addMSG("disabled", "disabled");
        msg.put("disabled", ChatColor.RED + msg.get("disabled"));
        addMSG("lst_title", "String list:");
        addMSG("lst_footer", "Page: [%1% / %2%]");
        addMSG("lst_listisempty", "List is empty");
        addMSG("msg_config", "Configuration");
        addMSG("cfgmsg_general_check-updates", "Check updates: %1%");
        addMSG("cfgmsg_general_language", "Language: %1%");
        addMSG("cfgmsg_general_language-save", "Save translation file: %1%");
    }


    public void setConsoleColored(boolean colorconsole) {
        this.colorconsole = colorconsole;
    }

    public boolean isConsoleColored() {
        return this.colorconsole;
    }

    public void addCmd(String cmd, String perm, String desc_id, String desc_key) {
        addCmd(cmd, perm, desc_id, desc_key, this.c1, this.c2, false);
    }

    public void addCmd(String cmd, String perm, String desc_id, String desc_key, char color) {
        addCmd(cmd, perm, desc_id, desc_key, this.c1, color, false);
    }

    public void addCmd(String cmd, String perm, String desc_id, String desc_key, boolean console) {
        addCmd(cmd, perm, desc_id, desc_key, this.c1, this.c2, console);
    }

    public void addCmd(String cmd, String perm, String desc_id, String desc_key, char color, boolean console) {
        addCmd(cmd, perm, desc_id, desc_key, this.c1, color, console);
    }

    public void addCmd(String cmd, String perm, String desc_id, String desc_key, char color1, char color2) {
        addCmd(cmd, perm, desc_id, desc_key, color1, color2, false);
    }

    public void addCmd(String cmd, String perm, String desc_id, String desc_key, char color1, char color2, boolean console) {
        String desc = getMSG(desc_id, desc_key, color1, color2);
        cmds.put(cmd, new Cmd(this.permprefix + perm, desc, console));
        if (cmdlist.isEmpty()) cmdlist = cmd;
        else cmdlist = cmdlist + ", " + cmd;
    }

    /*
     * Check permissions and team availability
     */

    public boolean checkCmdPerm(CommandSender sender, String cmd) {
        if (!cmds.containsKey(cmd.toLowerCase())) return false;
        Cmd cm = cmds.get(cmd.toLowerCase());
        if (sender instanceof Player) return (cm.perm.isEmpty() || sender.hasPermission(cm.perm));
        else return cm.console;
    }

    /* Class describing the command:
     * perm - permix postfix
     * desc - command description
     */
    public class Cmd {
        String perm;
        String desc;
        boolean console;

        public Cmd(String perm, String desc) {
            this.perm = perm;
            this.desc = desc;
            this.console = false;
        }

        public Cmd(String perm, String desc, boolean console) {
            this.perm = perm;
            this.desc = desc;
            this.console = console;
        }
    }

    public class PageList {
        private List<String> ln;
        private int lpp = 15;
        private String title_msgid = "lst_title";
        private String footer_msgid = "lst_footer";
        private boolean shownum = false;

        public void setLinePerPage(int lpp) {
            this.lpp = lpp;
        }

        public PageList(List<String> ln, String title_msgid, String footer_msgid, boolean shownum) {
            this.ln = ln;
            if (!title_msgid.isEmpty()) this.title_msgid = title_msgid;
            if (!footer_msgid.isEmpty()) this.footer_msgid = footer_msgid;
            this.shownum = shownum;
        }

        public void addLine(String str) {
            ln.add(str);
        }

        public boolean isEmpty() {
            return ln.isEmpty();
        }

        public void setTitle(String title_msgid) {
            this.title_msgid = title_msgid;

        }

        public void setShowNum(boolean shownum) {
        }

        public void setFooter(String footer_msgid) {
            this.footer_msgid = footer_msgid;
        }

        public void printPage(CommandSender p, int pnum) {
            printPage(p, pnum, this.lpp);
        }

        public void printPage(CommandSender p, int pnum, int linesperpage) {
            if (ln.size() > 0) {

                int maxp = ln.size() / linesperpage;
                if ((ln.size() % linesperpage) > 0) maxp++;
                if (pnum > maxp) pnum = maxp;
                int maxl = linesperpage;
                if (pnum == maxp) {
                    maxl = ln.size() % linesperpage;
                    if (maxp == 1) maxl = ln.size();
                }
                if (maxl == 0) maxl = linesperpage;
                if (msg.containsKey(title_msgid)) printMsg(p, "&6&l" + getMSGnc(title_msgid));
                else printMsg(p, title_msgid);

                String numpx = "";
                for (int i = 0; i < maxl; i++) {
                    if (shownum) numpx = "&3" + Integer.toString(1 + i + (pnum - 1) * linesperpage) + ". ";
                    printMsg(p, numpx + "&a" + ln.get(i + (pnum - 1) * linesperpage));
                }
                if (maxp > 1) printMSG(p, this.footer_msgid, 'e', '6', pnum, maxp);
            } else printMSG(p, "lst_listisempty", 'c');
        }

    }

    public void printPage(CommandSender p, List<String> ln, int pnum, String title, String footer, boolean shownum) {
        PageList pl = new PageList(ln, title, footer, shownum);
        pl.printPage(p, pnum);
    }

    public void printPage(CommandSender p, List<String> ln, int pnum, String title, String footer, boolean shownum, int lineperpage) {
        PageList pl = new PageList(ln, title, footer, shownum);
        pl.printPage(p, pnum, lineperpage);
    }


    /*
     * Various useful procedures
     *
     */

    /* The function checks if a number is included (int)
     * in the list of numbers represented as a string of the form n1, n2, n3, ... nN
     */
    public boolean isIdInList(int id, String str) {
        if (!str.isEmpty()) {
            String[] ln = str.split(",");
            if (ln.length > 0)
                for (int i = 0; i < ln.length; i++)
                    if ((!ln[i].isEmpty()) && ln[i].matches("[0-9]*") && (Integer.parseInt(ln[i]) == id)) return true;
        }
        return false;
    }

    /*
     * The function checks if all array numbers (int) are included.
      * to the list of numbers represented as a string of the form n1, n2, n3, ... nN
     */
    public boolean isAllIdInList(int[] ids, String str) {
        for (int id : ids)
            if (!isIdInList(id, str)) return false;
        return true;
    }


    /*
     * The function checks whether the word (String) is included in the list of words
     * represented as a string of the form n1, n2, n3, ... nN
     */
    public boolean isWordInList(String word, String str) {
        String[] ln = str.split(",");
        if (ln.length > 0)
            for (int i = 0; i < ln.length; i++)
                if (ln[i].equalsIgnoreCase(word)) return true;
        return false;
    }

    /*
     * The function checks whether there is an item (block) with the given id and data in the list,
     * represented as a string of the form id1: data1, id2: data2, MATERIAL_NAME: data
     * Returns false if str is empty.
     */
    public boolean isItemInList(String id, int data, String str) {
        String[] ln = str.split(",");
        if (ln.length > 0)
            for (int i = 0; i < ln.length; i++)
                if (compareItemStr(id, data, ln[i])) return true;
        return false;
    }



    /*public boolean compareItemStr (ItemStack item, String itemstr){
        return compareItemStr (item.getTypeId(), item.getDurability(), item.getAmount(), itemstr);
    }*/

    @Deprecated
    public boolean compareItemStr(String item_id, int item_data, String itemstr) {
        return compareItemStrIgnoreName(item_id, item_data, 1, itemstr);
    }

    
    public boolean compareItemStr(ItemStack item, String str) {
        String itemstr = str;
        String name = "";
        if (itemstr.contains("$")) {
            name = str.substring(0, itemstr.indexOf("$"));
            name = ChatColor.translateAlternateColorCodes('&', name.replace("_", " "));
            itemstr = str.substring(name.length() + 1);
        }
        if (itemstr.isEmpty()) return false;
        if (!name.isEmpty()) {
            String iname = item.hasItemMeta() ? item.getItemMeta().getDisplayName() : "";
            if (!name.equals(iname)) return false;
        }
        return compareItemStrIgnoreName(item.getType().name(), item.getDurability(), item.getAmount(), itemstr); // ;compareItemStr(item, itemstr);
    }


    public boolean compareItemStrIgnoreName(String item_id, int item_data, int item_amount, String itemstr) {
        if (!itemstr.isEmpty()) {
            String id = null;
            int amount = 1;
            int data = -1;
            String[] si = itemstr.split("\\*");
            if (si.length > 0) {
                if ((si.length == 2) && si[1].matches("[1-9]+[0-9]*")) amount = Integer.parseInt(si[1]);
                String ti[] = si[0].split(":");
                if (ti.length > 0) {
                    try {
                        id = Material.getMaterial(ti[0]).name();
                    } catch (Exception e) {
                        logOnce("unknownitem" + ti[0], "Unknown item: " + ti[0]);
                    }
                    if ((ti.length == 2) && (ti[1]).matches("[0-9]*")) data = Integer.parseInt(ti[1]);
                    return ((item_id.equals(id)) && ((data < 0) || (item_data == data)) && (item_amount >= amount));
                }
            }
        }
        return false;
    }

    public void giveItemOrDrop(Player p, ItemStack item) {
        for (ItemStack i : p.getInventory().addItem(item).values())
            p.getWorld().dropItemNaturally(p.getLocation(), i);
    }

    /*
     * Output a message to the user
     */
    public void printMsg(CommandSender p, String msg) {
        String message = ChatColor.translateAlternateColorCodes('&', msg);
        if ((!(p instanceof Player)) && (!colorconsole)) message = ChatColor.stripColor(message);
        p.sendMessage(message);
    }

    /*
     *  Output a message (containing a prefix) to the user
     */
    public void printPxMsg(CommandSender p, String msg) {
        printMsg(p, px + msg);
    }


    /*
     * Broadcast messages that were used when debugging [NOTE FROM EXEMPLIVE: this was only used for fromgate's development, probably]
     */
    public void BC(String msg) {
        plg.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', px + msg));
    }

    public void broadcastMSG(String perm, Object... s) {
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.hasPermission(permprefix + perm)) printMSG(p, s);
    }

    public void broadcastMsg(String perm, String msg) {
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.hasPermission(permprefix + perm)) printMsg(p, msg);
    }

    /*
     * 	public void printMSG (CommandSender p, Object... s){
		String message = getMSG (s);
		if ((!(p instanceof Player))&&(!colorconsole)) message = ChatColor.stripColor(message);
		p.sendMessage(message);
	}
     */

    /*
     * Log a message
     */
    public void log(String msg) {
        log.info(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', px + msg)));
    }

    public void logOnce(String error_id, String msg) {
        if (log_once.contains(error_id)) return;
        log_once.add(error_id);
        log.info(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', px + msg)));
    }


    /*
     * Send a color message to the console
     */
    public void SC(String msg) {
        plg.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', px + msg));
    }



    /*
     * Translation
     *
     */

    /*
     *  Message file initialization
     */
    public void InitMsgFile() {
        try {
            lng = new YamlConfiguration();
            File f = new File(plg.getDataFolder() + File.separator + "language" + File.separator + this.language + ".lng");
            if (!f.exists()) plg.saveResource("language/" + this.language + ".lng", false);
            lng.load(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fillLoadedMessages() {
        if (lng == null) return;
        for (String key : lng.getKeys(true))
            addMSG(key, lng.getString(key));
    }


    /*
     * Added message to list
     * Colors are removed.
     * Options:
     * key - message key
     * txt - message text
     */
    public void addMSG(String key, String txt) {
        msg.put(key, ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', lng.getString(key, txt))));
        if (msglist.isEmpty()) msglist = key;
        else msglist = msglist + "," + key;
    }


    /*
     * Сохранение сообщений в файл
     */
    public void SaveMSG() {
        String[] keys = this.msglist.split(",");
        try {
            File f = new File(plg.getDataFolder() + File.separator + this.language + ".lng");
            if (!f.exists()) f.createNewFile();
            YamlConfiguration cfg = new YamlConfiguration();
            for (int i = 0; i < keys.length; i++)
                cfg.set(keys[i], msg.get(keys[i]));
            cfg.save(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *  getMSG (String id, [char color1, char color2], Object param1, Object param2, Object param3... )
     */
    public String getMSG(Object... s) {
        String str = "&4Unknown message";
        String color1 = "&" + this.c1;
        String color2 = "&" + this.c2;
        if (s.length > 0) {
            String id = s[0].toString();
            str = "&4Unknown message (" + id + ")";
            if (msg.containsKey(id)) {
                int px = 1;
                if ((s.length > 1) && (s[1] instanceof Character)) {
                    px = 2;
                    color1 = "&" + (Character) s[1];
                    if ((s.length > 2) && (s[2] instanceof Character)) {
                        px = 3;
                        color2 = "&" + (Character) s[2];
                    }
                }
                str = color1 + msg.get(id);
                if (px < s.length)
                    for (int i = px; i < s.length; i++) {
                        String f = s[i].toString();
                        if (s[i] instanceof Location) {
                            Location loc = (Location) s[i];
                            f = loc.getWorld().getName() + "[" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "]";
                        }
                        str = str.replace("%" + Integer.toString(i - px + 1) + "%", color2 + f + color1);
                    }

            } else if (this.savelng) {
                addMSG(id, str);
                SaveMSG();
            }
        }
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public void printMSG(CommandSender p, Object... s) {
        String message = getMSG(s);
        if ((!(p instanceof Player)) && (!colorconsole)) message = ChatColor.stripColor(message);
        p.sendMessage(message);
    }

    /*
     * Help message
     */
    public void PrintHLP(Player p) {
        printMsg(p, "&6&l" + this.project_name + " v" + des.getVersion() + " &r&6| " + getMSG("hlp_help", '6'));
        printMSG(p, "hlp_thishelp", "/" + plgcmd + " help");
        printMSG(p, "hlp_execcmd", "/" + plgcmd + " <" + getMSG("hlp_cmdparam_command", '2') + "> [" + getMSG("hlp_cmdparam_parameter", '2') + "]");
        printMSG(p, "hlp_typecmd", "/" + plgcmd + " help <" + getMSG("hlp_cmdparam_command", '2') + ">");
        printMsg(p, getMSG("hlp_commands") + " &2" + cmdlist);
    }


    /*
     * Print help for a specific command
     */
    public void printHLP(Player p, String cmd) {
        if (cmds.containsKey(cmd)) {
            printMsg(p, "&6&l" + this.project_name + " v" + des.getVersion() + " &r&6| " + getMSG("hlp_help", '6'));
            printMsg(p, cmds.get(cmd).desc);
        } else printMSG(p, "cmd_unknown", 'c', 'e', cmd);
    }

    public void PrintHlpList(CommandSender p, int page, int lpp) {
        String title = "&6&l" + this.project_name + " v" + des.getVersion() + " &r&6| " + getMSG("hlp_help", '6');
        List<String> hlp = new ArrayList<String>();
        hlp.add(getMSG("hlp_thishelp", "/" + plgcmd + " help"));
        hlp.add(getMSG("hlp_execcmd", "/" + plgcmd + " <" + getMSG("hlp_cmdparam_command", '2') + "> [" + getMSG("hlp_cmdparam_parameter", '2') + "]"));
        if (p instanceof Player)
            hlp.add(getMSG("hlp_typecmdpage", "/" + plgcmd + " help <" + getMSG("hlp_cmdparam_page", '2') + ">"));

        String[] ks = (cmdlist.replace(" ", "")).split(",");
        if (ks.length > 0) {
            for (String cmd : ks)
                hlp.add(cmds.get(cmd).desc);
        }
        printPage(p, hlp, page, title, "", false, lpp);
    }

    /*
     * Represent a boolean value in text form (enabled / disabled)
     */
    public String EnDis(boolean b) {
        return b ? getMSG("enabled", '2') : getMSG("disabled", 'c');
    }

    public String EnDis(String str, boolean b) {
        String str2 = ChatColor.stripColor(str);
        return b ? ChatColor.DARK_GREEN + str2 : ChatColor.RED + str2;
    }

    /*
     * Print a boolean value
     */
    public void printEnDis(CommandSender p, String msg_id, boolean b) {
        p.sendMessage(getMSG(msg_id) + ": " + EnDis(b));
    }


    /*
     * Additional stuff
     */

    /*
     * Override the permission prefix
     */
    public void setPermPrefix(String ppfx) {
        this.permprefix = ppfx + ".";
        this.version_info_perm = this.permprefix + "config";
    }

    /*
     * Permission compliance check (specify without prefix),
     * given a command
     */
    public boolean equalCmdPerm(String cmd, String perm) {
        return (cmds.containsKey(cmd.toLowerCase())) &&
                ((cmds.get(cmd.toLowerCase())).perm.equalsIgnoreCase(permprefix + perm));
    }

    public Color colorByName(String colorname) {
        Color[] clr = {Color.WHITE, Color.SILVER, Color.GRAY, Color.BLACK,
                Color.RED, Color.MAROON, Color.YELLOW, Color.OLIVE,
                Color.LIME, Color.GREEN, Color.AQUA, Color.TEAL,
                Color.BLUE, Color.NAVY, Color.FUCHSIA, Color.PURPLE};
        String[] clrs = {"WHITE", "SILVER", "GRAY", "BLACK",
                "RED", "MAROON", "YELLOW", "OLIVE",
                "LIME", "GREEN", "AQUA", "TEAL",
                "BLUE", "NAVY", "FUCHSIA", "PURPLE"};
        for (int i = 0; i < clrs.length; i++)
            if (colorname.equalsIgnoreCase(clrs[i])) return clr[i];
        return null;
    }

    /*public ItemStack parseItemStack (String itemstr){
        if (!itemstr.isEmpty()){
            //int id = -1;
            Material m = Material.AIR;
            int amount =1;
            short data =0;
            String [] si = itemstr.split("\\*");
            if (si.length>0){
                if ((si.length==2)&&si[1].matches("[1-9]+[0-9]*")) amount = Integer.parseInt(si[1]);
                String ti[] = si[0].split(":");
                if (ti.length>0){
                    if (ti[0].matches("[0-9]*")) m = Material.getMaterial(Integer.parseInt(ti[0]));//id=Integer.parseInt(ti[0]);
                    else m=Material.getMaterial(ti[0].toUpperCase());
                    if ((ti.length==2)&&(ti[1]).matches("[0-9]*")) data = Short.parseShort(ti[1]);
                    return new ItemStack (m,amount,data);
                }
            }
        }
        return null;
    }*/


    /*
     * Checks if there are any players within a given radius
     */
    public boolean isPlayerAround(Location loc, int radius) {
        for (Player p : loc.getWorld().getPlayers()) {
            if (p.getLocation().distance(loc) <= radius) return true;
        }
        return false;
    }

    /*
     * Same as MSG, but with the colors stripped.
     */
    public String getMSGnc(Object... s) {
        return ChatColor.stripColor(getMSG(s));
    }

    public boolean rollDiceChance(int chance) {
        return (random.nextInt(100) < chance);
    }

    public int tryChance(int chance) {
        return random.nextInt(chance);
    }


    public int getRandomInt(int maxvalue) {
        return random.nextInt(maxvalue);
    }


    /*
     * Check integer list represented as a string
     */
    public boolean isIntegerSigned(String str) {
        return (str.matches("-?[0-9]+[0-9]*"));
    }

    public boolean isIntegerSigned(String... str) {
        if (str.length == 0) return false;
        for (String s : str)
            if (!s.matches("-?[0-9]+[0-9]*")) return false;
        return true;
    }


    public boolean isIntegerGZ(String str) {
        return (str.matches("[1-9]+[0-9]*"));
    }


    public void printConfig(CommandSender p, int page, int lpp, boolean section, boolean usetranslation) {
        List<String> cfgprn = new ArrayList<String>();
        if (!plg.getConfig().getKeys(true).isEmpty())
            for (String k : plg.getConfig().getKeys(true)) {
                Object objvalue = plg.getConfig().get(k);
                String value = objvalue.toString();
                String str = k;
                if ((objvalue instanceof Boolean) && (usetranslation)) value = EnDis((Boolean) objvalue);
                if (objvalue instanceof MemorySection) {
                    if (!section) continue;
                } else str = k + " : " + value;
                if (usetranslation) str = getMSG("cfgmsg_" + k.replaceAll("\\.", "_"), value);
                cfgprn.add(str);
            }
        String title = "&6&l" + this.project_current_version + " v" + des.getVersion() + " &r&6| " + getMSG("msg_config", '6');
        printPage(p, cfgprn, page, title, "", false);
    }
    
    public boolean returnMSG(boolean result, CommandSender p, Object... s) {
        if (p != null) this.printMSG(p, s);
        return result;
    }


}
