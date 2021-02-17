package douyimian.fengli;

import org.bukkit.plugin.java.*;
import java.util.logging.*;
import org.bukkit.entity.*;
import java.io.*;
import org.bukkit.configuration.file.*;
import org.bukkit.command.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class Fengli extends JavaPlugin implements Listener {
    public Logger logger;
    public HashMap<Player, Boolean> at;
    public List<Player> cooldown;
    public String prefix;

    public Fengli() {
        this.logger = Logger.getLogger("Minecraft");
        this.at = new HashMap<>();
        this.cooldown = new ArrayList<>();
        this.prefix = ChatColor.AQUA + "[@] " + ChatColor.YELLOW + ">>> ";
    }

    public void onEnable() {
        this.getLogger().info(String.valueOf(this.prefix) + ChatColor.GREEN + "插件魔改-二创 Fengli");
        this.getServer().getPluginManager().registerEvents(this, this);
        File configFile = new File(this.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (!configFile.exists()) {
            config.set("sound", 1);
            config.set("cooldown", 10);
            try {
                config.save(configFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.getLogger().info(String.valueOf(this.prefix) + ChatColor.GREEN + "插件已加载完成!");
    }

    public void onDisable() {
        this.getLogger().info(String.valueOf(this.prefix) + ChatColor.GREEN + "正在卸载中....");
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String lable, final String[] args) {
        if (lable.equalsIgnoreCase("at")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 0) {
                    player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "AT-@插件 使用帮助");
                    player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "/at help : 提示");
                    player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "/at disable : 打开免打扰功能");
                    player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "/at enable : 关闭免打扰功能");
                    return true;
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "Help:请在输入人名字之后输入空格或者按tab补全");
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("disable")) {
                        player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "你打开了免打扰功能");
                        this.at.put(player, false);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("enable")) {
                        player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "你关闭了免打扰功能");
                        this.at.put(player, true);
                        return true;
                    }
                }
            }
            else {
                sender.sendMessage("[@] >>> 只有玩家才能用@");
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void chat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        Player player = event.getPlayer();
        int sum = 0;
        if (msg.contains("@")) {
            if (!this.cooldown.contains(player)) {
                List<Player> ap = new ArrayList<>();
                ap.addAll(Bukkit.getServer().getOnlinePlayers());
                int sound = this.getConfig().getInt("sound");
                int cooldownsec = this.getConfig().getInt("cooldown");
                if (player.hasPermission("at.cooldown") || player.isOp()) {
                    cooldownsec = 0;
                }
                if (msg.contains("@全体玩家")) {
                    if (player.hasPermission("at.use.all") || player.isOp()) {
                        for (Player target : ap) {
                            if (!this.at.containsKey(target) || this.at.get(target)) {
                                target.sendMessage(ChatColor.AQUA + "[@] " + ChatColor.YELLOW + ">>> " + ChatColor.GREEN + player.getDisplayName() + ChatColor.AQUA + " @了你");

                                int test = msg.length() - ("@全体玩家").length();
                                if (test <= 0){
                                    target.sendTitle(ChatColor.AQUA + "有全体消息", ChatColor.AQUA + player.getDisplayName() + ChatColor.AQUA + " @了全体成员");
                                }else {
                                    String m = msg.substring(5);
                                    target.sendTitle(ChatColor.AQUA + m, ChatColor.AQUA + player.getDisplayName() + ChatColor.AQUA + " @了全体成员");
                                }

                                switch (sound) {
                                    case 1: {
                                        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                        continue;
                                    }
                                    case 2: {
                                        target.playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                                        continue;
                                    }
                                    case 3: {
                                        target.playSound(target.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1.0f, 1.0f);
                                        continue;
                                    }
                                    case 4: {
                                        target.playSound(target.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0f, 1.0f);
                                        continue;
                                    }
                                    case 5: {
                                        target.playSound(target.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
                                        continue;
                                    }
                                    default: {
                                        player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "@插件配置有误");
                                    }
                                }
                            }
                        }
                        msg = msg.replaceAll("@全体玩家", ChatColor.AQUA + "@全体玩家" + ChatColor.RESET);
                        ++sum;
                    }
                    else {
                        player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "你没有权限@全体玩家！");
                    }
                }
                else if (player.hasPermission("at.use") || player.isOp()) {
                    for (Player target : ap) {
                        if (msg.endsWith("@" + target.getName()) || msg.contains("@" + target.getName() + " ")) {
                            msg = msg.replaceAll("@" + target.getName(), ChatColor.AQUA + "@" + target.getName() + ChatColor.RESET);
                            if (!this.at.containsKey(target) || this.at.get(target)) {
                                target.sendMessage(ChatColor.AQUA + "[@] " + ChatColor.YELLOW + ">>> " + ChatColor.GREEN + player.getDisplayName() + ChatColor.AQUA + " @了你");

                                String m = msg.substring(("@" + target.getName()).length()+2);
                                if (!m.isEmpty()) {
                                    target.sendTitle(ChatColor.AQUA + m, ChatColor.AQUA + player.getDisplayName() + ChatColor.AQUA + " @了你");
                                }else {
                                    target.sendTitle(ChatColor.AQUA + "有人@你", ChatColor.AQUA + player.getDisplayName() + ChatColor.AQUA + " @了你");
                                }

                                switch (sound) {
                                    case 1: {
                                        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                        break;
                                    }
                                    case 2: {
                                        target.playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                                        break;
                                    }
                                    case 3: {
                                        target.playSound(target.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1.0f, 1.0f);
                                        break;
                                    }
                                    case 4: {
                                        target.playSound(target.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0f, 1.0f);
                                        break;
                                    }
                                    case 5: {
                                        target.playSound(target.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
                                        break;
                                    }
                                    default: {
                                        player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "请联系op,@插件配置有误");
                                        break;
                                    }
                                }
                            }
                        }
                        ++sum;
                    }
                    if (sum == 0) {
                        player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "玩家不在线或不存在");
                    }
                }
                else {
                    player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "你没有权限使用@");
                }
                if (sum != 0) {
                    event.setMessage(msg);
                    this.cooldown.add(player);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            Fengli.this.cooldown.remove(player);
                        }
                    },(cooldownsec * 20L));
                }
            }
            else {
                final int cooldownsec2 = this.getConfig().getInt("cooldown");
                player.sendMessage(String.valueOf(this.prefix) + ChatColor.GREEN + "冷却中，冷却时间为" + cooldownsec2 + "秒");
            }
        }
    }

    @EventHandler
    public void onTabComplete(PlayerChatTabCompleteEvent event) {
        final String uncomplete = event.getChatMessage();
        if (uncomplete.contains("@") && !uncomplete.endsWith(" ")) {
            final List<Player> allplayers = new ArrayList<>();
            allplayers.addAll(Bukkit.getServer().getOnlinePlayers());
            final String at = uncomplete.substring(uncomplete.lastIndexOf("@") + 1);
            final List<String> fit = new ArrayList<>();
            for (final Player p : allplayers) {
                if (p.getName().toLowerCase().startsWith(at.toLowerCase())) {
                    if (uncomplete.contains(" ")) {
                        fit.add(uncomplete.substring(uncomplete.lastIndexOf(" ") + 1, uncomplete.lastIndexOf("@")) + "@" + p.getName());
                    }
                    else {
                        fit.add(uncomplete.substring(0, uncomplete.lastIndexOf("@")) + "@" + p.getName());
                    }
                }
            }
            event.getTabCompletions().clear();
            event.getTabCompletions().addAll(fit);
        }
    }
}