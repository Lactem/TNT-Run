package net.galaxygaming.tntrun;

import java.util.Map;

import net.galaxygaming.dispenser.game.GameBase;
import net.galaxygaming.selection.Selection;
import net.galaxygaming.util.LocationUtil;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TNTRun extends GameBase {
	private Location spawn;
	private Selection arena;
	
	private void setSpawn(Location spawn) {
		this.spawn = spawn;
		getConfig().set("spawn", LocationUtil.serializeLocation(spawn));
		saveConfig();
	}
	
	private void setArena(Selection arena) {
		this.arena = arena;
		getConfig().set("arena", arena.serialize());
	}

	public Location getSpawn() {
		return spawn;
	}
	
	public Selection getArena() {
		return arena;
	}
	
	@Override
	public boolean setComponent(String componentName, Location location) {
		if (componentName.equalsIgnoreCase("spawn")) {
			setSpawn(location);
			return true;
		}
		return false;
	}

	
	@Override
	public boolean setComponent(String componentName, Selection selection) {
		if (componentName.equalsIgnoreCase("arena")) {
			setArena(selection);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLoad() {
		spawn = LocationUtil.deserializeLocation(getConfig().getString("spawn"));
		arena = Selection.deserialize((Map<String, Object>) getConfig().get("arena"));
		addComponent("arena");
		addComponent("spawn");
	}

	@Override
	public void onStart() {
		// TODO: Add 5 second grace before chaos commences
	}

	@Override
	public void onTick() {
		// TODO: Handle deaths either in events or by checking something here? idk
	}

	@Override
	public void onEnd() {
		// Check winner and do stuff
	}

	@Override
	public void onPlayerLeave(Player player) {
	    // TOOD: Remove any metadata that we added
	    
		super.onPlayerLeave(player);
	}

	@Override
	public boolean isFinished() {
	    // TODO: Check how many players left and decide whether game is finished
	    
		return super.isFinished();
	}

	@Override
	public boolean isSetup() {
		return spawn != null && arena != null;
	}
	
	public boolean isPlayerSpectating(Player player) {
	    return getMetadata(player, "spectator").asBoolean();
	}
}