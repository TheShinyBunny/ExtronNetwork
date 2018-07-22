package com.extron.network.api.event;

import com.extron.network.api.Main;
import com.extron.network.api.entity.ExtronEntity;
import com.extron.network.api.entity.ExtronFallingBlock;
import com.extron.network.api.game.CreatorButton;
import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.players.PlayerList;
import com.extron.network.api.utils.ChatType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.PlayerInventory;

public class EventManager implements Listener {

	private static boolean registered;

	public static boolean callEvent(ExtronEvent e) {
		System.out.println("calling event: " + e.getClass().getSimpleName());
		Bukkit.getPluginManager().callEvent(e);
		return !e.isCancelled();
	}
	
	public static void registerEvents(Listener listener) {
		if (listener == null) throw new NullPointerException("Event Listener can not be null!");
		Bukkit.getPluginManager().registerEvents(listener,Main.INSTANCE);
	}

	private static ExtronPlayer getPlayer(PlayerEvent e) {
		return ExtronPlayer.of(e.getPlayer());
	}

	public static void register() {
		if (registered) return;
		registered = true;
		EventManager m = new EventManager();
		registerEvents(m);
	}

	@EventHandler
	public void command(PlayerCommandPreprocessEvent e) {
		ExtronPlayer p = getPlayer(e);
		e.setCancelled(Main.getCommandManager().handle(p,e.getMessage()));
		p.dropItems = false; // FIXME: 7/5/2018 depend on current game
	}

	@EventHandler
	public void consoleCommand(ServerCommandEvent e) {
		if (Main.getCommandManager().handle(e.getSender(),e.getCommand())) {
			e.setCommand("");
		}
	}

	@EventHandler
	public void preLogin(PlayerLoginEvent e) {
		ExtronPlayer p = getPlayer(e);
		if (p == null) {
			return;
		}
		if (p.isBanned()) {
			if (p.getBan().hasExpired()) {
				p.getBan().remove();
			} else {
				e.disallow(PlayerLoginEvent.Result.KICK_BANNED,p.getBan().getBanKickMessage());
			}
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		ExtronPlayer p = getPlayer(e);
		if (p == null) {
			p = Main.createPlayer(e.getPlayer());
		}
		if (p == null) {
			return;
		}
		PlayerList.playerJoined(p);
		e.getPlayer().getInventory().clear();
		p.changeWorld(Main.getLobby());
		p.invulnerable = true;
		p.setHealth(20);
		p.setCollisionRule(false);
		p.enableFlight(true);
		p.clearPotionEffects();
		Main.showPlayerHolograms(p);
		Main.updateMainScoreboard();
		p.getScoreboardManager().updateScoreboard(Main.getMainScoreboard());
		p.getStatistics().increment(Main.getStatistic("times_login"));
		p.sendTabList(ChatColor.GREEN + "ExtronNetwork" + ChatColor.AQUA + " [Alpha v1.5]", ChatColor.YELLOW + "Website: " + ChatColor.GOLD + "www.ExtronWeb.com");
		p.sendActionBar(ChatColor.GREEN + "" + ChatColor.BOLD + "Welcome to " + ChatColor.LIGHT_PURPLE + "Extron " + ChatColor.YELLOW + "Network");
		e.setJoinMessage("");
	}

	@EventHandler
	public void quit(PlayerQuitEvent e) {
		ExtronPlayer p = getPlayer(e);
		if (p.getWorld() != null) {
			p.getWorld().getPlayers().remove(p);
		}
		PlayerList.playerLeft(p);
		if (p.getCurrentGame() != null) {
			p.getCurrentGame().onPlayerLeave(p);
			p.setCurrentGame(null);
		}
		Main.hidePlayerHolograms(p);
		Main.updateMainScoreboard();
		e.setQuitMessage("");
	}

	@EventHandler
	public void kick(PlayerKickEvent e) {
		PlayerQuitEvent ev = new PlayerQuitEvent(e.getPlayer(),e.getLeaveMessage());
		quit(ev);
	}

	@EventHandler
	public void clickInventory(InventoryClickEvent e) {
		ExtronPlayer p = ExtronPlayer.of((Player) e.getWhoClicked());
		if (e.getClickedInventory() instanceof PlayerInventory) {
			e.setCancelled(p.getInventory().inventoryClicked(e.getSlot(),e.getCurrentItem(),e.getClick()));
			return;
		}
		if (p.getOpenInventory() == null) {
			return;
		}
		e.setCancelled(p.getOpenInventory().inventoryClicked(e.getSlot(),e.getCurrentItem(),e.getClick()));
	}

	@EventHandler
	public void closeInventory(InventoryCloseEvent e) {
		ExtronPlayer p = ExtronPlayer.of((Player) e.getPlayer());
		if (p.getOpenInventory() != null) {
			p.closeInventory();
		}
	}

	@EventHandler
	public void interact(PlayerInteractEvent e) {
		ExtronPlayer p = getPlayer(e);
		if (e.getAction() == Action.PHYSICAL) {
			if (e.getClickedBlock().getType() == Material.WOOD_PLATE || e.getClickedBlock().getType() == Material.STONE_PLATE || e.getClickedBlock().getType() == Material.IRON_PLATE || e.getClickedBlock().getType() == Material.GOLD_PLATE) {
				if (e.getClickedBlock().getData() == 0) {
					p.getWorld().onStepPressurePlate(p, e.getClickedBlock().getLocation());
				}
			}
		}
		if (e.getItem() == null) {
			return;
		}
		e.setCancelled(p.getInventory().interactWithItem(p.getSelectedSlot(),e.getItem(),null,e.getClickedBlock(),e.getAction()));

		/*if (p.getCurrentGame() != null) {
			e.setCancelled(!p.getCurrentGame().onPlayerInteract(p,e.getItem(),e.getClickedBlock(),e));
		}*/
	}

	@EventHandler
	public void entityInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() == null) return;
		ExtronPlayer p = getPlayer(e);
		e.setCancelled(p.getInventory().interactWithItem(p.getSelectedSlot(),p.handle.getItemInHand(),e.getRightClicked(),null, Action.RIGHT_CLICK_AIR));
		if (!e.isCancelled()) {
			if (p.pet != null && p.pet.getEntity().equals(e.getRightClicked())) {
				p.pet.mountOwner();
			}
		}
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		ExtronPlayer p = getPlayer(e);
		e.setCancelled(true);
		if (p.isMuted() && p.chat == ChatType.ALL) {
			if (p.getMute().hasExpired()) {
				p.getMute().remove();
			} else {
				p.sendMessage(ChatColor.RED + "You are muted on the server, you can't use the public chat!");
				return;
			}
		}
		p.getStatistics().increment(Main.getStatistic("chat_messages"));
		//ChallengeSystem.getInstance().trigger(new ChatTrigger(p,p.currentChat,e.getMessage()));
		switch (p.chat) {
			case ALL:
				if (p.getCurrentGame() != null) {
					p.getCurrentGame().sendMessage(p, e.getMessage());
				} else {
					p.getWorld().broadcastMessage(p.getDisplayName() + ": " + e.getMessage());
				}
				break;
			case TEAM:
				break;
			case PARTY:
				if (p.getParty() != null) {
					p.getParty().sendMessage(p, e.getMessage());
				} else {
					p.chat = ChatType.ALL;
					p.sendMessage(ChatColor.RED + "You are not in a party anymore so you have moved to the Public Chat.");
				}
				break;
			case PRIVATE:
				if (p.privateMassaging == null || !p.privateMassaging.isOnline()) {
					p.chat = ChatType.ALL;
					p.sendMessage(ChatColor.RED + "You are not in an private conversation anymore so you have moved to the Public Chat.");
				} else {
					p.sendPrivateMessage(e.getMessage());
				}
				break;
		}
	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		ExtronPlayer p = ExtronPlayer.of(e.getEntity());
		if (p.getCurrentGame() != null) {
			p.getCurrentGame().onPlayerDeath(p.getCurrentGame().createDeathFromEvent(e));
			e.setDeathMessage("");
		} else {
			p.teleportToLobby();
			p.setHealth(20);
			e.setKeepInventory(true);
		}
	}

	@EventHandler
	public void damagedEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			// a player is being attacked
			ExtronPlayer p = ExtronPlayer.of((Player) e.getEntity());
			if (p.invulnerable) {
				e.setCancelled(true);
			} else {
				p.setLastDamagedEntity(e);
			}
		}
		if (e.getDamager() instanceof Player) {
			// a player attacked an entity
			ExtronPlayer p = ExtronPlayer.of((Player) e.getDamager());
			if (p.getCurrentGame() == null) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void damaged(EntityDamageEvent e) {
		if (e.isCancelled()) {
			System.out.println("damage event is already cancelled");
		}
		if (e.getEntity() instanceof Player) {
			System.out.println("the player got damaged!");
			ExtronPlayer p = ExtronPlayer.of((Player) e.getEntity());
			if (p.getCurrentGame() == null) {
				if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
					p.handle.teleport(p.getWorld().getSpawnPoint());
					e.setCancelled(true);
				}
			}
			if (p.invulnerable) {
				System.out.println("the player is invulnerable");
				e.setCancelled(true);
			}
			p.setLastDamage(e);
			if (e.isCancelled()) {
				System.out.println("the event was cancelled");
			}
		} else {
			ExtronEntity en = Main.getExtronEntity(e.getEntity());
			if (en != null && en.invulnerable) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void breakBlock(BlockBreakEvent e) {
		ExtronPlayer p = ExtronPlayer.of(e.getPlayer());
		if (p.getMapCreator() != null) {
			p.getMapCreator().setActivePlayer(p);
			e.setCancelled(p.getMapCreator().breakBlock(e.getBlock()));
            if (p.getInventory().getHeldButton() != null) {
                if (p.getInventory().getHeldButton() instanceof CreatorButton) {
                    e.setCancelled(((CreatorButton) p.getInventory().getHeldButton()).blockBreak(e.getBlock()));
                }
            }
			return;
		}
		if (p.getCurrentGame() == null) {
			p.getInventory().interactWithItem(p.getSelectedSlot(),e.getPlayer().getItemInHand(),null,e.getBlock(), Action.LEFT_CLICK_BLOCK);
			e.setCancelled(e.getPlayer().getGameMode() != GameMode.CREATIVE);
		}
	}

	@EventHandler
	public void placeBlock(BlockPlaceEvent e) {
		ExtronPlayer p = ExtronPlayer.of(e.getPlayer());
		if (p.getMapCreator() != null) {
			p.getMapCreator().setActivePlayer(p);
			e.setCancelled(p.getMapCreator().placedBlock(e.getBlock()));
			if (p.getInventory().getHeldButton() != null) {
			    if (p.getInventory().getHeldButton() instanceof CreatorButton) {
			        e.setCancelled(((CreatorButton) p.getInventory().getHeldButton()).blockPlace(e.getBlockAgainst(),e.getBlockPlaced()));
                }
            }
			return;
		}
		if (p.getCurrentGame() == null) {
			boolean cancel = p.getInventory().interactWithItem(p.getSelectedSlot(),e.getItemInHand(),null,e.getBlockPlaced(), Action.RIGHT_CLICK_BLOCK);
			if (cancel && p.handle.getGameMode() != GameMode.CREATIVE) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void pickup(PlayerPickupItemEvent e) {
		if (getPlayer(e).getCurrentGame() == null) {
			e.setCancelled(!getPlayer(e).pickupItems);
		}
	}

	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		ExtronPlayer p = getPlayer(e);
		if (p.getCurrentGame() == null) {
			e.setCancelled(!p.dropItems);
		}
	}

	@EventHandler
	public void target(EntityTargetLivingEntityEvent e) {
		if (e.getTarget() instanceof Player) {
			ExtronPlayer p = Main.getPlayer((Player) e.getTarget());
			if (p.getCurrentGame() == null) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void entityChangeBlock(EntityChangeBlockEvent e) {
		ExtronEntity entity = Main.getExtronEntity(e.getEntity());
		if (e.getEntity() instanceof FallingBlock) {
			if (entity instanceof ExtronFallingBlock) {
				e.setCancelled(!((ExtronFallingBlock) entity).onLand());
			}
		}
	}

	@EventHandler
	public void exitVehicle(VehicleExitEvent e) {
		if (e.getExited() instanceof Player) {
			ExtronPlayer p = ExtronPlayer.of((Player) e.getExited());
			if (p.pet != null && p.pet.isBeingRidden()) {
				p.pet.onPlayerDismount();
			}
		}
	}
}
