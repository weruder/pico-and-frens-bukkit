package io.github.weruder.lightning;

//import com.avaje.ebean.Expr;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public final class Main extends JavaPlugin implements Listener {
    //Define these dye colors up here so that we can use a nice name instead of a magic number
    //This is a good practice for code readability.

    public final byte RED_DYE = (byte) 1;
    public final byte GREEN_DYE = (byte) 2;
    public final byte BLUE_DYE = (byte) 4;
    public final byte CYAN_DYE = (byte) 6;
    public final byte LIGHT_BLUE_DYE = (byte) 12;
    public final byte ORANGE_DYE = (byte) 14;

    public final byte OAK_SAPLING = (byte) 0;
    public final byte SPRUCE_SAPLING = (byte) 1;
    public final byte BIRCH_SAPLING = (byte) 2;
    public final byte JUNGLE_SAPLING = (byte) 3;
    public final byte ACACIA_SAPLING = (byte) 4;
    public final byte DARK_OAK_SAPLING = (byte) 5;

    public final byte ORANGE_STAINED_GLASS = (byte) 1;

    public final byte CHISELED_STONE = (byte) 3;
    public final Random rand = new Random();
    public final int TICKS_PER_SECOND = 20;

    public Map<UUID, Block> CaneBlocks = new HashMap<>();
    public Map<UUID, Location> PlayerTeleportLocations = new HashMap<>();
    public Map<UUID, Boolean> UnthankfulList = new HashMap<>();
    public final Object[] playerList = Bukkit.getOnlinePlayers().toArray();
    public World heck;

    @Override
    //Whenever we enable the plugin for the first time, this method will be called
    public void onEnable() {
        //We need to make sure that our plugin manager knows that we're listening for events, so we call this method
        getServer().getPluginManager().registerEvents(this, this);

        /*
         * YEE EXPLOSIVE MOD
         * // adds a recipe for two flowers + water glass = tnt
         */
        ItemStack aggro = new ItemStack(Material.TNT, 1);
        ShapelessRecipe recipe = new ShapelessRecipe(aggro)
                .addIngredient(2, Material.RED_ROSE)
                .addIngredient(1, Material.WATER_BUCKET);
        Bukkit.addRecipe(recipe);
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                //While the server is running
                //Bukkit.broadcastMessage("Test");
                while (true) {
                    //Bukkit.broadcastMessage("While loop");
                    //for each of the online players        
                    for (int i = 0; i < playerList.length; i++) {
                        //if Player i is wearing a gold helmet
                        if (((Player)playerList[i]).getEquipment().getHelmet() != null && ((Player)playerList[i]).getEquipment().getHelmet().getType() == Material.GOLD_HELMET) {
                            //Bukkit.broadcastMessage("for loop; " + (playerList[i].getEquipment().getHelmet().getDurability()));
                            //playerList[i].getEquipment().getHelmet().setDurability((short) (playerList[i].getEquipment().getHelmet().getDurability() + (short) 1));
                        }
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        );
    }

    @EventHandler
    //Whenever a player logs in, this event will be called
    public void onLogin(PlayerLoginEvent event) {
        //We're going to write out to the console that the player has logged in.
        getLogger().log(Level.INFO, "Player " + event.getPlayer().getName() + " is logging in!");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        //Do a check for null first, that way you can ensure that you don't cause a null reference exeption when calling .getType()
        if (player.getEquipment().getBoots() != null) {
            if (player.getEquipment().getBoots().getType() == Material.GOLD_BOOTS) {
                final Material standingOn = player.getLocation().add(0, -1, 0).getBlock().getType();  // gets the material of block the player is standing on and holds it in variable standingOn of type Material
                if (standingOn != Material.AIR && standingOn != Material.POWERED_RAIL && standingOn != Material.WATER && standingOn != Material.LAVA) {
                    player.getLocation().getBlock().setType(Material.POWERED_RAIL);
                }
            }
        }
        /**
         * BUNNY HOOD
         */
        if (player.getEquipment().getHelmet() != null) {
            if (player.getEquipment().getHelmet().getType() == Material.GOLD_HELMET) {
                //final Material standingOn = player.getLocation().add(0, -1, 0).getBlock().getType();  // gets the material of block the player is standing on and holds it in variable standingOn of type Material
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, TICKS_PER_SECOND * 3, 2));

                //if (standingOn != Material.AIR && standingOn != Material.POWERED_RAIL && standingOn != Material.WATER && standingOn != Material.LAVA) 
                //{
                //player.getLocation().getBlock().setType(Material.POWERED_RAIL);
                //}
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.LONG_GRASS && rand.nextInt(6) == 1) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.SAPLING, 1, (short) rand.nextInt(6)));
        }
    }
    
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent event) {
        //Grab the player object from the event. The final keyword lets us use it inside of scheduleSyncDelayedTask
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        ItemStack heldItem = player.getItemInHand();
        Block targetBlock = player.getTargetBlock((Set<Material>)null, 200);

        if (heldItem.getType() == Material.INK_SACK) {
            //Cut down on repeated calls to getData().getData(), just store the number for later.
            final byte heldDyeColor = heldItem.getData().getData();

            /**
             * MAGNET GLOVES
             */
            if (heldDyeColor == RED_DYE || heldDyeColor == BLUE_DYE) {
                //If we left click anything, we want to change the glove color
                if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    if (heldDyeColor == RED_DYE) {
                        player.setItemInHand(new ItemStack(Material.INK_SACK, 1, BLUE_DYE));
                    } else if (heldDyeColor == BLUE_DYE) {
                        player.setItemInHand(new ItemStack(Material.INK_SACK, 1, RED_DYE));
                    }
                } //Otherwise, we're doing a right click, and want to be pulled or pushed
                else if (targetBlock.getType() == Material.IRON_BLOCK) { //we prioritize iron block over right clicking players
                    Location playerLoc = player.getLocation();
                    final Vector distanceToBlock = playerLoc.getDirection();
                    //If we're using the South glove, reverse the polarity
                    if (heldDyeColor == BLUE_DYE) {
                        distanceToBlock.multiply(-1);
                    }
                    //To prevent the player from falling due to gravity, we enable flying on them temporarily
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    //We also add a little bit of Y velocity to counteract gravity as well
                    distanceToBlock.add(new Vector(0, 0.075, 0));
                    //By setting the velocity instead of adding to it, we can ensure the player won't be affected by
                    //their current velocity when they use the gloves.
                    player.setVelocity(distanceToBlock);
                    //This plays our Magnet sound effect. Remove this line to mute the sound.
                    world.playSound(playerLoc, Sound.ENTITY_ENDERMEN_HURT, 3F, 1F);

                    //In 10 ticks, run this code
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                        public void run() {
                            player.setVelocity(distanceToBlock);
                            //Reset the player's fall distance so they don't die
                            player.setFallDistance(0f);
                            //world.playSound(player.getLocation(), Sound.ENDERMAN_HIT, 3F, 1F);
                            player.setFlying(false);
                            //If we're in Creative mode, this line would disable flying entirely.
                            //We only want to do this if we're in Survival mode.
                            if (player.getGameMode() == GameMode.SURVIVAL) {
                                player.setAllowFlight(false);
                            }
                        }
                    }, 10);
                }
                //else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
           /**
            * MAGNET GLOVE STEALING
            * Steal a player's item when right clicking them with the magnet glove
            */
                //wait 1 second in case the player didn't mean to steal an item
                  //  Bukkit.broadcastMessage("before sleep"); //temp
                //try {
                  //  Thread.sleep(1000);
                //} catch (InterruptedException ex) {
                  //  Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                //}
               // Bukkit.broadcastMessage("Check 1"); //temp
                //final int ATTACK_REACH = 100; // meters you can reach the target from
                //Location playerPosition = player.getEyeLocation();
                //Vector3D playerDirection = new Vector3D(playerPosition.getDirection());
 
                //Vector3D playerStart = new Vector3D(playerPosition);
                //Vector3D playerEnd = playerStart.add(playerDirection.multiply(ATTACK_REACH));
                //Player steal = null;
                 // Get nearby players
                //for (Player stealTarget : player.getWorld().getPlayers()) {
                    // Bounding box of the given target player
                  //  Vector3D stealTargetPos = new Vector3D(stealTarget.getLocation());
                    //Vector3D min = stealTargetPos.add(-0.5, 0, -0.5);
                    //Vector3D max = stealTargetPos.add(0.5, 1.67, 0.5);
                    
                     //if (stealTarget != player && hasIntersection(playerStart, playerEnd, min, max)) {
                       // if (steal == null || 
                         //       steal.getLocation().distanceSquared(playerPosition) > 
                           //     stealTarget.getLocation().distanceSquared(playerPosition)) {
 
                            //steal = stealTarget;
                            //Bukkit.broadcastMessage("Check 2");
                          // get the current item in the target player's hand, starts a while loop that checks
                          // to see if player and target have intersection, player is still right clicking, etc.
                          // If they don't have intersection during one of the checks, the magnet glove is removed
                          // from the player's inventory. If the player is continuously right clicking and in
                          // intersection with target for 5 seconds, the item is given to the player. Bonus:
                          // if player's inventory is full, the itemstack is flung aside from the target.
                          // Second bonus: items on the ground could move toward or away from the player using a magnet glove.
      
                            //ItemStack targetItemInHand = stealTarget.getItemInHand();
                            // check first to see if the target has anything in their hand, and exit if they don't
                            //if (targetItemInHand.getType() == Material.AIR) {
                              //  player.sendMessage(stealTarget.getDisplayName() + " is a   P O O R B O Y");
                                //return;
                            //}
                              //     player.sendMessage("Attempting to steal " + stealTarget.getDisplayName() + "'s " + targetItemInHand + "..." );
                                //   int counter = 0;
                                  // while ((heldDyeColor == RED_DYE || heldDyeColor == BLUE_DYE) && event.getAction().equals(Action.RIGHT_CLICK_AIR) && hasIntersection(playerStart, playerEnd, min, max)) {
                                    //   counter++;
                               // try {
                                 //   Thread.sleep(1000);
                                //} catch (InterruptedException ex) {
                                  //  Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                               // }
                               //        player.sendMessage(Integer.toString(counter));
                                 //  }
                                   //        if (counter == 5) // After a certain time of counting up
                                     //      {
                                       //        player.getInventory().addItem(targetItemInHand);
                                         //      stealTarget.getInventory().remove(targetItemInHand);
                                           //    player.sendMessage("You got it bruh.");
                                             //  stealTarget.sendMessage("Got hacked, lol.");
                                               //return;
                                           //}
                                            //player.sendMessage(stealTarget.getDisplayName() + " got away! And your glove broke.");
                                       // remove one rose red from the player's inventory, or if they somehow turned it into lapis it will take one of those.
                                            //if (player.getInventory().contains(Material.INK_SACK, RED_DYE)) {
                                              //  player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, RED_DYE));
                                            //}
                                            //else if (player.getInventory().contains(Material.INK_SACK, BLUE_DYE)) {
                                              //  player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, BLUE_DYE));
                                            //}
                                            
                                   //play the magnet glove sound on loop while right clicking
                               //Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()  {
                               //@Override
                               //public void run () {
                                 //  world.playSound(player.getLocation(), Sound.ENDERMAN_HIT, 3F, 1F);
                                //}
                            //}, 10);
                //}
            //}
           //}
             //                   }
            }

            /**
             * HOOKSWITCH
             */
            if (heldDyeColor == CYAN_DYE && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
                Location playerLoc = player.getLocation();
                Location blockLoc = targetBlock.getLocation();
                //We need to get the data for the block before we change it, so we create a few variables.
                Material blockMaterial = targetBlock.getType();
                byte blockType = targetBlock.getData();
                //This line makes it so that you only swap with Chiseled Stone Bricks. If you replace it with the
                //following line, it will swap it with most blocks outside of liquids, air, and bedrock
                //if (!targetBlock.isEmpty() && !targetBlock.isLiquid() && blockMaterial != Material.BEDROCK)
                if (blockMaterial == Material.SMOOTH_BRICK && blockType == CHISELED_STONE) {
                    world.playSound(playerLoc, Sound.ENTITY_ENDERMEN_DEATH, 3F, 1F);
                    targetBlock.setType(Material.AIR);
                    Location newPlayerLoc = new Location(world, blockLoc.getX(), blockLoc.getY() + 1, blockLoc.getZ(), playerLoc.getYaw() + 180f, playerLoc.getPitch());
                    player.teleport(newPlayerLoc);
                    playerLoc.getWorld().spawnFallingBlock(playerLoc.add(0, 1, 0), blockMaterial, blockType);
                }
            }
            
            
            /**
             * HOOKSWITCH PT2
             * Switch players around with the hookswitch
             */
            
            if (heldDyeColor == CYAN_DYE && event.getAction().equals(Action.RIGHT_CLICK_AIR)){
                final int ATTACK_REACH = 100;
                Location observerPos = player.getEyeLocation();
                Vector3D observerDir = new Vector3D(observerPos.getDirection());
 
                Vector3D observerStart = new Vector3D(observerPos);
                Vector3D observerEnd = observerStart.add(observerDir.multiply(ATTACK_REACH));
                Entity warp = null;
                 // Get nearby entities
                for (Entity target : player.getWorld().getEntities()) {
                    // Bounding box of the given player
                    Vector3D targetPos = new Vector3D(target.getLocation());
                    Vector3D minimum = targetPos.add(-0.5, 0, -0.5);
                    Vector3D maximum = targetPos.add(0.5, 1.67, 0.5);
                    
                     if (target != player && hasIntersection(observerStart, observerEnd, minimum, maximum)) {
                        if (warp == null || 
                                warp.getLocation().distanceSquared(observerPos) > 
                                target.getLocation().distanceSquared(observerPos)) {
 
                            warp = target;
                            
               // Get the location of both entities and teleport each other. Play the same sfx as part 1.
                if (warp != null) {
                    Location playerLocation = player.getLocation();
                    Location targetLocation = target.getLocation();
                    player.teleport(targetLocation);
                    world.playSound(targetLocation, Sound.ENTITY_ENDERMEN_DEATH, 3F, 1F);
                    target.teleport(playerLocation);
                    world.playSound(playerLocation, Sound.ENTITY_ENDERMEN_DEATH, 3F, 1F);
                }
            }
          }
        }
     }
            /**
             * SHINE
             */
            if (heldDyeColor == LIGHT_BLUE_DYE && ((Entity) player).isOnGround()) {
                Entity expBubble = player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
                List<Entity> nearbyEntities = expBubble.getNearbyEntities(2.0f, 2.0f, 2.0f);
                expBubble.remove();
                for (int i = 0; i < nearbyEntities.size(); i++) {
                    Entity entity = nearbyEntities.get(i);
                    if (entity != player) {
                        //entity.setFallDistance(50.0f);
                        world.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_SCREAM, 3F, 1F);
                        Vector distanceToPlayer = (entity.getLocation().toVector().add(player.getLocation().toVector().multiply(-1)));
                        //Creates a vector using the enemy's location subtracting the player's location to get the displacement from yourself to the enemy.
                        Vector normalizedDistance = (distanceToPlayer.normalize());
                        //Ensures the magnitude of the vector will be 1.
                        entity.setVelocity(new Vector(normalizedDistance.getX(), 1, normalizedDistance.getZ()));
                        //Sets the enemy's velocity to send it (away) up and at an angle relative to the player.
                    }
                }

            }

            /**
             * CANE OF SOMARIA
             */
            if (heldDyeColor == ORANGE_DYE && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
                Location playerLoc = player.getLocation();
                world.playSound(playerLoc, Sound.ENTITY_ENDERMEN_AMBIENT, 3F, 1F);
                boolean spawnBlock = true;
                if (CaneBlocks.containsKey(player.getUniqueId())) {
                    Block oldBlock = CaneBlocks.get(player.getUniqueId());
                    oldBlock.setType(Material.AIR);

                    if (targetBlock.getLocation() == oldBlock.getLocation()) {
                        spawnBlock = false;
                    }
                }
                if (spawnBlock) {
                    BlockFace face = null;
                    List<Block> blocks = player.getLastTwoTargetBlocks((Set<Material>)null, 10);
                    if (blocks.size() > 1) {
                        face = blocks.get(1).getFace(blocks.get(0));
                    }
                    Block newBlock = targetBlock.getRelative(face);
                    newBlock.setType(Material.SMOOTH_BRICK);
                    newBlock.setData(CHISELED_STONE);
                    CaneBlocks.put(player.getUniqueId(), newBlock);
                }
            }
        }

        if (heldItem.getType() == Material.SAPLING && event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            //Cut down on repeated calls to getData().getData(), just store the number for later.
            byte heldSaplingType = heldItem.getData().getData();

            /**
             * GALE SEED
             */
            if (heldSaplingType == OAK_SAPLING) {
                world.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 3F, 1F);

                player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, OAK_SAPLING));

                if (PlayerTeleportLocations.containsKey(player.getUniqueId())) {
                    player.teleport(PlayerTeleportLocations.remove(player.getUniqueId()));
                } else {
                    PlayerTeleportLocations.put(player.getUniqueId(), player.getLocation());
                    player.teleport(new Location(world, 655.5, 107.0, 497.5));
                }
                world.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 3F, 1F);
            }

            /**
             * PEGASUS SEED
             */
            if (heldSaplingType == BIRCH_SAPLING) {
                player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, BIRCH_SAPLING));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, TICKS_PER_SECOND * 10, 4));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, TICKS_PER_SECOND * 10, 2));
            }

            /**
             * EMBER SEED
             */
            if (heldSaplingType == ACACIA_SAPLING) {
                player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, ACACIA_SAPLING));
                Fireball fire = player.getWorld().spawn(event.getPlayer().getLocation().add(new Vector(0.0D, 1.0D, 0.0D)), Fireball.class);
                fire.setFireTicks(0);
                fire.setShooter(player);
            }

            /**
             * MYSTERY SEED
             */
            if (heldSaplingType == SPRUCE_SAPLING) {
                player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, SPRUCE_SAPLING));
                player.getInventory().addItem(new ItemStack(Material.RED_ROSE, 64));
                world.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_AMBIENT, 3F, 1F);
            }

            /**
             * SCENT SEED
             */
            if (heldSaplingType == JUNGLE_SAPLING) {
                player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, JUNGLE_SAPLING));
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 30, 4));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 30, 0));
            }

            /**
             * GASHA SEED
             */
            if (heldSaplingType == DARK_OAK_SAPLING && playerList.length > 1) {
                player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, DARK_OAK_SAPLING));
                Player P1 = (Player)playerList[new Random().nextInt(playerList.length)];
                while (player.getDisplayName().equals(P1.getDisplayName()) && playerList.length > 1) {
                    P1 = (Player)playerList[new Random().nextInt(playerList.length)];
                }

                int nuRando = new Random().nextInt(99);

                if (nuRando > -1 && nuRando <= 20) {
                    double resultHP = (P1.getHealth() - 2.0);
                    if (resultHP < 1) {
                        resultHP = 1;
                    }
                    P1.setHealth(resultHP);
                    P1.sendMessage(ChatColor.AQUA + "A Gasha seed exploded!");
                } else if (nuRando >= 21 && nuRando <= 25) {
                    double resultHP = (P1.getHealth() - 5.0);
                    if (resultHP < 1) {
                        resultHP = 1;
                    }
                    P1.setHealth(resultHP);
                    P1.sendMessage(ChatColor.AQUA + "The Gasha seed is aggressive!");
                } else if (nuRando >= 26 && nuRando <= 30) {
                    P1.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                } else if (nuRando >= 31 && nuRando <= 35) {
                    P1.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                } else if (nuRando >= 36 && nuRando <= 40) {
                    P1.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
                } else if (nuRando >= 41 && nuRando <= 60) // 20% chance of Gasha Seed
                {
                    P1.getInventory().addItem(new ItemStack(Material.SAPLING, 1, DARK_OAK_SAPLING));
                } else if (nuRando >= 61 && nuRando <= 69) {
                    P1.getInventory().addItem(new ItemStack(Material.GOLD_HOE));
                } else if (nuRando == 70) {
                    ItemStack coolRod = new ItemStack(Material.FISHING_ROD);
                    ItemMeta godRod = coolRod.getItemMeta();
                    godRod.setDisplayName(ChatColor.DARK_RED + "Canne à pêche de Boatdad");
                    godRod.addEnchant(Enchantment.LUCK, 3, true);
                    godRod.addEnchant(Enchantment.DURABILITY, 3, true);
                    godRod.addEnchant(Enchantment.LURE, 3, true);
                    coolRod.setItemMeta(godRod);
                    P1.getInventory().addItem(coolRod);
                } else if (nuRando >= 71 && nuRando <= 80) {
                    P1.getInventory().addItem(new ItemStack(Material.BOAT));
                    P1.sendMessage(ChatColor.AQUA + "You have received a blessing from BOATDAD!");
                } else if (nuRando >= 81 && nuRando <= 99) {
                    Firework f1 = P1.getWorld().spawn(P1.getLocation(), Firework.class);
                    FireworkMeta fm1 = f1.getFireworkMeta();

                    FireworkEffect e1 = FireworkEffect.builder().withColor(Color.BLUE).with(Type.BALL).build();

                    fm1.addEffect(e1);
                    fm1.setPower(0);
                    f1.setFireworkMeta(fm1);

                    P1.getWorld().spawn(P1.getLocation(), ThrownExpBottle.class);

                    P1.sendMessage(ChatColor.AQUA + "A Gasha seed exploded!");
                }

                if (nuRando >= 26 && nuRando <= 70) {
                    P1.sendMessage(ChatColor.AQUA + "Special gift from " + player.getDisplayName() + "!");
                    player.sendMessage(ChatColor.WHITE + "Thanks fam. ~" + P1.getDisplayName());
                }

            }
        }

        /**
         * SEED GLASS STAINING
         */
        Block rightClickBlock = event.getClickedBlock();
        byte heldSaplingType = heldItem.getData().getData();
        if (heldItem.getType() == Material.SAPLING && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && ((rightClickBlock.getType() == Material.GLASS)
                || (rightClickBlock.getType() == Material.STAINED_GLASS_PANE)
                || (rightClickBlock.getType() == Material.STAINED_GLASS)
                || (rightClickBlock.getType() == Material.THIN_GLASS))) {
            if (heldSaplingType == JUNGLE_SAPLING) {
                while (rightClickBlock.getType() == Material.GLASS) {
                    rightClickBlock.setType(Material.STAINED_GLASS);
                }
                while (rightClickBlock.getType() == Material.THIN_GLASS) {
                    rightClickBlock.setType(Material.STAINED_GLASS_PANE);
                }
                rightClickBlock.setData(ORANGE_STAINED_GLASS);
                world.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_AMBIENT, 3F, 1F);
            }
        }

        /**
         * ROC'S CAPE
         */
        //Check if we have leather in our hand, and if we're on the ground. The player's isOnGround method is deprecated,
        //so we typecast it as an Entity to get access to the method.
        if (heldItem.getType() == Material.LEATHER && ((Entity) player).isOnGround()) {
            Vector current_velocity = player.getVelocity();
            //We set the player's velocity to their current velocity plus a vector of 1 in the y direction.
            //This will send the player in the same direction they were going, but up into the air as well
            player.setVelocity(current_velocity.add(new Vector(0, 1, 0)));
            //Ask the server to run this code in 10 ticks
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                public void run() {
                    Vector current_velocity = player.getVelocity();
                    //Take the direction the player is looking in, and multiply the Y component by 0
                    //so that the player will only glide in the x and z axes, not get a super jump upwards
                    Vector recoil_velocity = player.getLocation().getDirection().multiply(new Vector(1, 0, 1));
                    player.setVelocity(current_velocity.add(recoil_velocity));
                }
            }, 10); //Change this number if you want to make the wait time longer or shorter
        }

        /*
         if(event.getAction().equals(Action.RIGHT_CLICK_AIR))
         {
         if(player.getItemInHand().getType() == Material.STICK)
         {
         Fireball fire = player.getWorld().spawn(event.getPlayer().getLocation().add(new Vector(0.0D, 1.0D, 0.0D)), Fireball.class);
         fire.setFireTicks(0);
         fire.setShooter(player);
         }
         }*/
    }
    

    @EventHandler
    public void playerHitPlayerEvent(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack heldItem = player.getItemInHand();
            if (heldItem.getType() == Material.SAPLING) {
                @SuppressWarnings("deprecation")
                byte heldSaplingType = heldItem.getData().getData();

                /**
                 * GALE SEED
                 */
                if (heldSaplingType == OAK_SAPLING) {
                    player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, OAK_SAPLING));
                    victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 3F, 1F);
                    victim.teleport(victim.getLocation().add(rand.nextInt(10) * (Math.random() < 0.5 ? -1 : 1), rand.nextInt(10), rand.nextInt(10) * (Math.random() < 0.5 ? -1 : 1)));
                }

                /**
                 * EMBER SEED
                 */
                if (heldSaplingType == ACACIA_SAPLING) {
                    player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, ACACIA_SAPLING));
                    victim.setFireTicks(100);
                }

                /**
                 * PEGASUS SEED
                 */
                if (heldSaplingType == BIRCH_SAPLING) {
                    player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, BIRCH_SAPLING));

                    if (victim instanceof LivingEntity) {
                        ((LivingEntity) victim).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 7));
                    }
                }

                /**
                 * MYSTERY SEED
                 */
                if (heldSaplingType == SPRUCE_SAPLING) {
                    player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, SPRUCE_SAPLING));
                    int selector = rand.nextInt(4);
                    if (selector == 0) {
                        victim.teleport(victim.getLocation().add(rand.nextInt(10) * (Math.random() < 0.5 ? -1 : 1), rand.nextInt(10), rand.nextInt(10) * (Math.random() < 0.5 ? -1 : 1)));
                    } else if (selector == 1) {
                        if (victim instanceof LivingEntity) {
                            ((LivingEntity) victim).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 7));
                        }
                    } else if (selector == 2) {
                        victim.setFireTicks(100);
                    } else if (selector == 3) {
                        if (victim instanceof LivingEntity) {
                            ((LivingEntity) victim).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 4));
                            ((LivingEntity) victim).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 0));
                        }
                    }
                    victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ENDERMEN_AMBIENT, 3F, 1F);
                }

                /**
                 * SCENT SEED
                 */
                if (heldSaplingType == JUNGLE_SAPLING) {
                    player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, JUNGLE_SAPLING));
                    if (victim instanceof LivingEntity) {
                        ((LivingEntity) victim).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 4));
                        ((LivingEntity) victim).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 0));
                    }
                }

                /**
                 * GASHA SEED
                 */
                if (heldSaplingType == DARK_OAK_SAPLING) {
                    player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, DARK_OAK_SAPLING));
                    if (victim instanceof Player) {
                        final Player sickBoy = ((Player) victim);
                        sickBoy.getInventory().addItem(new ItemStack(Material.RED_ROSE, 1));
                        if (UnthankfulList.containsKey(sickBoy.getUniqueId()) == false) {
                            Bukkit.broadcastMessage(ChatColor.AQUA + sickBoy.getDisplayName() + " has the poppy virus!");
                            sickBoy.sendMessage("You got the poppy virus! Good luck and breathing will come to you only if you say \"Thank Pico\".");
                            UnthankfulList.put(sickBoy.getUniqueId(), Boolean.TRUE);
                        }

                        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
                            @Override
                            public void run() {
                                while (UnthankfulList.containsKey(sickBoy.getUniqueId())) {
                                    sickBoy.getInventory().addItem(new ItemStack(Material.RED_ROSE, 1));
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * Santa's naughty list checks chat to see if rudeboi has thanked Pico
     */
    @EventHandler
    public void goodBoy(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (e.getMessage().equalsIgnoreCase("Thank Pico") && UnthankfulList.containsKey(p.getUniqueId())) {
            UnthankfulList.remove(p.getUniqueId());
            p.sendMessage(ChatColor.BLUE + "The Gasha seed compels you to get a good night's rest after such a horrible illness.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            Firework f1 = p.getWorld().spawn(p.getLocation(), Firework.class);
            FireworkMeta fm1 = f1.getFireworkMeta();
            FireworkEffect e1 = FireworkEffect.builder().withColor(Color.BLUE).with(Type.BALL).build();
            fm1.addEffect(e1);
            fm1.setPower(0);
            f1.setFireworkMeta(fm1);
            p.sendMessage(ChatColor.DARK_BLUE + "The Gasha seed flew away!");

        }
    }

    /**
     * "Cheaters not using Gale Seeds" check This is used to check if a teleport
     * is used going somewhere other than the Nexon and removing the player from
     * the hashmap. This is meant for cases where a chat command or command
     * block is used, but this monitors all teleports regardless of cause.
     */
    @EventHandler
    public void onTeleport(PlayerTeleportEvent tp) {
        Player moveBoy = tp.getPlayer();
        World worl = moveBoy.getWorld();
        Location nexon = new Location(worl, 655.5, 107.0, 497.5);
        if (tp.getTo().getChunk() != nexon.getChunk() && PlayerTeleportLocations.containsKey(moveBoy.getUniqueId())) {
            PlayerTeleportLocations.remove(moveBoy.getUniqueId());
        }
    }

    //From https://github.com/MinecraftForge/MCPBukkit/blob/master/src/org/bukkit/craftbukkit/event/CraftEventFactory.java
    public static EntityDamageEvent callEntityDamageEvent(Entity damager, Entity damagee, DamageCause cause, double damage) {
        EntityDamageEvent event;
        if (damager != null) {
            event = new EntityDamageByEntityEvent(damager, damagee, cause, damage);
        } else {
            event = new EntityDamageEvent(damagee, cause, damage);
        }
        callEvent(event);
        if (!event.isCancelled()) {
            event.getEntity().setLastDamageCause(event);
        }
        return event;
    }

    //From https://github.com/MinecraftForge/MCPBukkit/blob/master/src/org/bukkit/craftbukkit/event/CraftEventFactory.java
    public static <T extends Event> T callEvent(T event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    
     private boolean hasIntersection(Vector3D p1, Vector3D p2, Vector3D min, Vector3D max) {
        final double epsilon = 0.0001f;
 
        Vector3D d = p2.subtract(p1).multiply(0.5);
        Vector3D e = max.subtract(min).multiply(0.5);
        Vector3D c = p1.add(d).subtract(min.add(max).multiply(0.5));
        Vector3D ad = d.abs();
 
        if (Math.abs(c.x) > e.x + ad.x)
            return false;
        if (Math.abs(c.y) > e.y + ad.y)
            return false;
        if (Math.abs(c.z) > e.z + ad.z)
            return false;
 
        if (Math.abs(d.y * c.z - d.z * c.y) > e.y * ad.z + e.z * ad.y + epsilon)
            return false;
        if (Math.abs(d.z * c.x - d.x * c.z) > e.z * ad.x + e.x * ad.z + epsilon)
            return false;
        if (Math.abs(d.x * c.y - d.y * c.x) > e.x * ad.y + e.y * ad.x + epsilon)
            return false;
 
        return true;
    }
    
    // Need to specify which world rather than getting current world. same applies to Gale seed portion

    /*
     *   SUBROSIA MOD
     * // Look into loading the world on server start
     */
    /*
     @EventHandler
     public void onBed(PlayerBedEnterEvent be) {
     final Player sleepBoy = be.getPlayer();
     heck = Bukkit.getWorld("Subrosia");
     //Location inception = new Location(heck, 0, 0, 0);
     if (sleepBoy.isSleeping() && sleepBoy.getWorld() == heck)
     {
     //inception = be.getBed().getLocation();
     be.getBed().breakNaturally();
     sleepBoy.getInventory().addItem(new ItemStack(Material.IRON_SPADE, 1));
     sleepBoy.sendMessage(ChatColor.DARK_RED + "Subrosian tradition dictates that one must not sleep, only dig for ore chunks. Get to work.");
     }
     try {
     Thread.sleep(1000); // Wait one second after getting in bed and progress
     } catch (InterruptedException ex) {
     Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
     }
     if (heck == null) //heck returns null if the world is not loaded yet, so we need to find a way to load it on server start.
     {
     sleepBoy.sendMessage(ChatColor.DARK_RED + "You can hear a rumbling sound from far away, as though a drill has pierced the heavens.");
     WorldCreator subrosia = new WorldCreator("Subrosia");
     subrosia.type(WorldType.AMPLIFIED);
     subrosia.environment(World.Environment.NORMAL);
     subrosia.generateStructures(true);

     heck = subrosia.createWorld(); //Create amped world with the name subrosia. This only needs to happen once in our case.
     heck.setDifficulty(Difficulty.PEACEFUL);
     heck.setSpawnLocation(30, 200, 50);
     sleepBoy.sendMessage(ChatColor.DARK_RED + "The rumbling has stopped.");
     } else {
     // teleport the player to subrosia
     sleepBoy.setGameMode(GameMode.CREATIVE);
     sleepBoy.teleport(new Location(Bukkit.getWorld("Subrosia"), 30, 200, 50));
     Bukkit.broadcastMessage(ChatColor.DARK_RED + "Welcome to Subrosia!");
     }

     Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
     public World world = Bukkit.getWorld("world");
     public World heck = Bukkit.getWorld("Subrosia");

     @Override
     public void run() {
     if (sleepBoy.getWorld() == heck && world.getTime() <= 12000) {
     try
     {
     sleepBoy.teleport(sleepBoy.getBedSpawnLocation());
     }
     finally
     {
     if (sleepBoy.getBedSpawnLocation() == null)
     {
     sleepBoy.teleport(world.getSpawnLocation())()); 
     } 
     } 
     sleepBoy.setGameMode(GameMode.SURVIVAL); 
     sleepBoy.sendMessage(ChatColor.WHITE + "W A K E B O Y S"); 
     } 
     } 
     }, 600); 
     }*/
}
