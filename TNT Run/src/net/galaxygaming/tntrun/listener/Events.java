package net.galaxygaming.tntrun.listener;

import net.galaxygaming.dispenser.game.GameState;
import net.galaxygaming.dispenser.task.GameRunnable;
import net.galaxygaming.dispenser.team.Team;
import net.galaxygaming.tntrun.TNTRun;
import net.galaxygaming.util.FormatUtil;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

// You do not need to register listeners because GameDispenser does that automatically.
public class Events implements Listener {
	
	// Use the optional parameter TNTRun (or whatever the name of your main class is) in
	// events to have them only be called when the player involved in the event is in your game.
    @EventHandler
	public void onPlayerMove(final PlayerMoveEvent event, final TNTRun game) {
		if (game.getState().ordinal() == GameState.ACTIVE.ordinal()) {
			final Block block = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
			if (block.getType() == Material.AIR || block.getType() == Material.GLASS)
				return;
			if (!game.getArena().getSelection().isIn(block.getLocation())) // No griefing outside of reset area
				return;
			
			game.resetTime(event.getPlayer());
			breakBlock(block);
		}
	}
    
    // Announces a player's death and checks for a winner whenever a player in-game dies.
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event, final TNTRun game) {
		Player entity = event.getEntity();
		Team spectatorTeam = game.getSpectatorTeam();
		spectatorTeam.add(entity);
		int length = game.getPlayers().length;
		int players = length - spectatorTeam.getSize();
		game.broadcast("&4" + entity.getDisplayName() + " &ehas fallen out. " + FormatUtil.format(game.getType().getMessages().getMessage("game.playerCount"), players, length));
		event.setDeathMessage("");
		event.getDrops().clear();
		
		if (players == 1) {
			setWinner(game);
		}
    }
    
    // Allows players who die to respawn in creative to spectate.
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event, final TNTRun game) {
    		event.getPlayer().setGameMode(GameMode.CREATIVE);
    }
    
    // Checks if there's only one player left who is not spectating, sets
    // that player as the winner, and ends the game.
    private void setWinner(TNTRun game) {
    		for (Player player : game.getPlayers()) {
    			if (game.getSpectatorTeam().isOnTeam(player))
    				continue;
    			game.setWinner(player.getDisplayName());
    			game.end();
    		}
    }
    
    // Turns a block into glass and then air after a delay.
    private void breakBlock(final Block block) {
		new GameRunnable() {
			@Override
			public void run() {
				block.setType(Material.GLASS);
			}
		}.runTaskLater(5L); // 0.25 seconds
		new GameRunnable() {
			@Override
			public void run() {
				block.setType(Material.AIR);
			}
		}.runTaskLater(10L); // 0.5 seconds
    }
}