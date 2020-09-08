/*     */ package georgenotfound.netheronlymanhunt;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import net.md_5.bungee.api.ChatMessageType;
/*     */ import net.md_5.bungee.api.chat.TextComponent;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.player.PlayerQuitEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitRunnable;
/*     */ import org.bukkit.scheduler.BukkitTask;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class NetherOnlyManHunt extends JavaPlugin implements Listener, CommandExecutor {
/*     */   private Set<UUID> hunters;
/*     */   private BukkitTask task;
/*     */   private static final char FILLER_CHAR = ' ';
/*     */   private static final char ARROW_LEFT_CHAR = '<';
/*     */   private static final char ARROW_RIGHT_CHAR = '>';
/*     */   private static final char TARGET_CHAR = 'X';
/*     */   
/*     */   public void onEnable() {
/*  31 */     getServer().getPluginManager().registerEvents(this, (Plugin)this);
/*  32 */     this.hunters = new HashSet<>();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
/*  38 */     if (command.getName().equalsIgnoreCase("netherhunter")) {
/*     */       
/*  40 */       if (args.length != 2) {
/*     */         
/*  42 */         sendInvalid(sender);
/*  43 */         return false;
/*     */       } 
/*     */       
/*  46 */       Player player = Bukkit.getPlayer(args[1]);
/*     */       
/*  48 */       if (player == null) {
/*     */         
/*  50 */         sender.sendMessage(ChatColor.RED + "Player not found.");
/*  51 */         return false;
/*     */       } 
/*     */       
/*  54 */       if (args[0].equalsIgnoreCase("add")) {
/*     */         
/*  56 */         this.hunters.add(player.getUniqueId());
/*  57 */         sender.sendMessage(ChatColor.GREEN + player.getName() + " is now a hunter.");
/*     */         
/*  59 */         if (this.hunters.size() == 1)
/*     */         {
/*  61 */           startTask();
/*     */         }
/*  63 */       } else if (args[0].equalsIgnoreCase("remove")) {
/*     */         
/*  65 */         this.hunters.remove(player.getUniqueId());
/*  66 */         sender.sendMessage(ChatColor.GREEN + player.getName() + " is no longer a hunter.");
/*     */       } else {
/*     */         
/*  69 */         sendInvalid(sender);
/*     */       } 
/*     */     } 
/*     */     
/*  73 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void sendInvalid(CommandSender sender) {
/*  78 */     sender.sendMessage(ChatColor.RED + "Invalid usage. Please use:");
/*  79 */     sender.sendMessage(ChatColor.RED + "/netherhunter add <name>");
/*  80 */     sender.sendMessage(ChatColor.RED + "/netherhunter remove <name>");
/*     */   }
/*     */ 
/*     */   
/*     */   private Player getNearestPlayer(Player player) {
/*  85 */     Player nearest = null;
/*  86 */     double distance = Double.MAX_VALUE;
/*     */     
/*  88 */     for (Player onlinePlayer : player.getWorld().getPlayers()) {
/*     */       
/*  90 */       if (onlinePlayer.equals(player) || this.hunters.contains(onlinePlayer.getUniqueId())) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/*  95 */       double distanceSquared = onlinePlayer.getLocation().distanceSquared(player.getLocation());
/*     */       
/*  97 */       if (distanceSquared < distance) {
/*     */         
/*  99 */         distance = distanceSquared;
/* 100 */         nearest = onlinePlayer;
/*     */       } 
/*     */     } 
/*     */     
/* 104 */     return nearest;
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onPlayerQuitEvent(PlayerQuitEvent event) {
/* 110 */     this.hunters.remove(event.getPlayer().getUniqueId());
/*     */     
/* 112 */     if (this.hunters.isEmpty())
/*     */     {
/* 114 */       stopTask();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void startTask() {
/* 122 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 138 */       .task = (new BukkitRunnable() { public void run() { for (UUID hunterUuid : NetherOnlyManHunt.this.hunters) { Player hunter = Bukkit.getPlayer(hunterUuid); Player nearestPlayer = NetherOnlyManHunt.this.getNearestPlayer(hunter); if (nearestPlayer != null) hunter.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(NetherOnlyManHunt.this.generateCompassText(hunter, nearestPlayer.getLocation())));  }  } }).runTaskTimer((Plugin)this, 0L, 2L);
/*     */   }
/*     */ 
/*     */   
/*     */   private void stopTask() {
/* 143 */     if (this.task != null && !this.task.isCancelled())
/*     */     {
/* 145 */       this.task.cancel();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private float getYaw(double x, double y, double z) {
/* 151 */     double yaw = Math.toDegrees(Math.atan(-x / z));
/*     */     
/* 153 */     if (z < 0.0D)
/*     */     {
/* 155 */       yaw += 180.0D;
/*     */     }
/*     */     
/* 158 */     return (float)yaw;
/*     */   }
/*     */ 
/*     */   
/*     */   private Vector getTrajectory(Vector from, Vector to) {
/* 163 */     return to.clone().subtract(from).normalize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 170 */   private static final String PREFIX_COLOR = ChatColor.AQUA + " " + ChatColor.BOLD;
/*     */ 
/*     */   
/*     */   private String generateCompassText(Player player, Location targetLocation) {
/* 174 */     int maxChars = 80;
/* 175 */     int drawChars = 0;
/* 176 */     StringBuilder displayText = new StringBuilder();
/*     */     
/* 178 */     displayText.append(PREFIX_COLOR);
/*     */     
/* 180 */     Vector direction = player.getLocation().getDirection();
/* 181 */     Vector trajectory = getTrajectory(player.getLocation().toVector(), targetLocation.toVector());
/* 182 */     double playerYaw = getYaw(direction.getX(), direction.getY(), direction.getZ());
/* 183 */     double relYaw = getYaw(trajectory.getX(), trajectory.getY(), trajectory.getZ());
/*     */     
/* 185 */     double bearing = relYaw - playerYaw;
/*     */     
/* 187 */     while (bearing < -180.0D)
/*     */     {
/* 189 */       bearing += 360.0D;
/*     */     }
/*     */     
/* 192 */     while (bearing > 180.0D)
/*     */     {
/* 194 */       bearing -= 360.0D;
/*     */     }
/*     */     
/* 197 */     if (bearing < -90.0D) {
/*     */       
/* 199 */       StringBuilder stringBuilder = new StringBuilder(maxChars - 5);
/* 200 */       stringBuilder.append(PREFIX_COLOR);
/* 201 */       stringBuilder.append('<');
/* 202 */       for (int i = 0; i < maxChars - 1; i++)
/*     */       {
/* 204 */         stringBuilder.append(' ');
/*     */       }
/* 206 */       displayText = new StringBuilder(stringBuilder.toString());
/* 207 */       drawChars = maxChars;
/* 208 */     } else if (bearing > 90.0D) {
/*     */       
/* 210 */       StringBuilder stringBuilder = new StringBuilder(maxChars - 5);
/* 211 */       stringBuilder.append(PREFIX_COLOR);
/* 212 */       for (int i = 0; i < maxChars - 1; i++)
/*     */       {
/* 214 */         stringBuilder.append(' ');
/*     */       }
/* 216 */       stringBuilder.append('>');
/* 217 */       displayText = new StringBuilder(stringBuilder.toString());
/* 218 */       drawChars = maxChars;
/*     */     } else {
/*     */       
/* 221 */       double percent = (bearing + 90.0D) / 180.0D;
/* 222 */       while (percent >= drawChars / maxChars) {
/*     */         
/* 224 */         displayText.append(' ');
/* 225 */         drawChars++;
/*     */       } 
/*     */       
/* 228 */       displayText.append('X');
/* 229 */       drawChars++;
/*     */     } 
/*     */     
/* 232 */     while (drawChars < maxChars) {
/*     */       
/* 234 */       displayText.append(' ');
/* 235 */       drawChars++;
/*     */     } 
/*     */     
/* 238 */     return displayText.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\maxry\Downloads\NetherCompass.jar!\georgenotfound\netheronlymanhunt\NetherOnlyManHunt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */