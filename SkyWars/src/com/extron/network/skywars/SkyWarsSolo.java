package com.extron.network.skywars;

import com.extron.network.api.ExtronWorld;
import com.extron.network.api.config.Config;
import com.extron.network.api.game.*;
import com.extron.network.api.game.managers.GameManager;
import com.extron.network.api.game.managers.IGameManager;
import com.extron.network.api.hologram.Hologram;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.scoreboard.Scoreboard;
import com.extron.network.api.utils.BlockPos;
import com.extron.network.api.utils.ListUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

import java.util.*;
import java.util.function.Consumer;

public class SkyWarsSolo extends GameMode {
    @Override
    public int getTeamCount() {
        return 6;
    }

    @Override
    public int getPlayersInTeam() {
        return 1;
    }

    @Override
    public GameStartRules getStartRule() {
        return new GameStartRules().setMaxPlayers(5,15);
    }

    @Override
    public boolean hasTeamSelector(GameSettings gameSettings) {
        return false;
    }

    @Override
    public GameManager createManager(ExtronWorld map, GameSettings settings) {
        return new SkyWarsManager(map,settings);
    }

    @Override
    public boolean canPartyJoin() {
        return true;
    }

    @Override
    public void giveWaitItems(ExtronPlayer p, GameManager manager) {

    }

    @Override
    public String getId() {
        return "skywars_solo";
    }

    @Override
    public Scoreboard createScoreboard(IGameManager manager) {
        return new SkyWarsBoard(manager);
    }

    @Override
    public String getName() {
        return "SkyWars Solo";
    }

    @Override
    public DeathMessages getCustomDeathMessages() {
        return null;
    }

    @Override
    public MapCreator getNewMapCreator(ExtronWorld map) {
        return new SoloCreator(map);
    }

    @Override
    public String getShortName() {
        return "Solo";
    }

    @Override
    public boolean multiInstancesInOneMap() {
        return false;
    }

    public class SoloCreator extends MapCreator {
        private Map<BlockPos,Hologram> spawnChests;
        private Map<BlockPos,Hologram> middleChests;
        private Map<Integer,Hologram> spawnMarkers;
        private BlockPos[] spawns;

        public SoloCreator(ExtronWorld map) {
            super(map);
            spawnChests = new HashMap<>();
            middleChests = new HashMap<>();
            spawnMarkers = new HashMap<>();
            spawns = new BlockPos[getTeamCount()];
        }

        @Override
        public void getItems(Consumer<CreatorButton> c) {
            c.accept(new CreatorButton(Material.CHEST,"Spawn Island Chest") {
                @Override
                public boolean blockPlace(Block clicked, Block placed) {
                    BlockPos pos = BlockPos.of(placed);
                    if (spawnChests.containsKey(pos)) {
                        spawnChests.remove(pos).despawn();
                        message(ChatColor.RED + "Removed spawn loot chest!");
                    } else {
                        markSpawnChest(pos);
                        message(ChatColor.GREEN + "Added spawn loot chest at " + pos.toNiceString());
                    }
                    return false;
                }

                @Override
                public boolean blockBreak(Block block) {
                    BlockPos pos = BlockPos.of(block);
                    if (spawnChests.containsKey(pos)) {
                        spawnChests.remove(pos).despawn();
                        message(ChatColor.RED + "Removed spawn loot chest!");
                    }
                    return false;
                }

                @Override
                public boolean blockInteract(Block clicked, Action action) {
                    if (clicked.getType() == Material.CHEST) {
                        return blockPlace(null,clicked);
                    }
                    return false;
                }
            });
            c.accept(new CreatorButton(Material.ENDER_CHEST,"Middle Chest") {

                @Override
                public boolean blockPlace(Block clicked, Block placed) {
                    BlockPos pos = BlockPos.of(placed);
                    Hologram h = middleChests.remove(pos);
                    if (h == null){
                        markMidChest(pos);
                        message(ChatColor.GREEN + "Added middle loot chest at " + pos.toNiceString());
                    } else {
                        message(ChatColor.RED + "Removed middle loot chest!");
                        h.despawn();
                    }
                    return false;
                }

                @Override
                public boolean blockBreak(Block block) {
                    BlockPos pos = BlockPos.of(block);
                    Hologram h = middleChests.remove(pos);
                    if (h != null) {
                        message(ChatColor.RED + "Removed middle loot chest!");
                        h.despawn();
                    }
                    return false;
                }

                @Override
                public boolean blockInteract(Block b, Action action) {
                    if (b.getType() == Material.CHEST) {
                        return blockPlace(null,b);
                    }
                    return false;
                }
            });
            c.accept(new CreatorButton(Material.GRASS,"Team Island Spawn") {
                @Override
                public boolean blockInteract(Block b, Action action) {
                    BlockPos pos = BlockPos.of(b).up();
                    if (ListUtils.arrayContains(spawns,pos)) {
                        int i = ListUtils.arrayIndexOf(spawns,pos);
                        spawns[i] = null;
                        Hologram h = spawnMarkers.remove(i);
                        if (h != null) {
                            h.despawn();
                        }
                        updateSpawnChests();
                        message(ChatColor.RED + "Removed team spawn #" + i);
                    } else {
                        int i = spawnMarkers.size();
                        markIslandSpawn(pos,i);
                        message(ChatColor.GREEN + "Set team spawn #" + i + " to " + pos.toNiceString());
                    }
                    return true;
                }
            });
        }

        private void markMidChest(BlockPos pos) {
            this.middleChests.put(pos,Hologram.create(pos.toLocation(),"Middle Chest"));
        }

        private void markSpawnChest(BlockPos pos) {
            System.out.println("marking spawn chest");
            Hologram h = Hologram.create(pos.toLocation(),"Spawn Island Chest");
            this.spawnChests.put(pos,h);
            if (getNearestSpawn(pos) != -1) {
                h.addLine("of island #" + getNearestSpawn(pos));
            } else {
                h.addLine("of unknown island");
            }
            h.reload();
        }

        private void markIslandSpawn(BlockPos pos, int i) {
            this.spawns[i] = pos;
            Hologram h = Hologram.create(pos.toLocation(),"Spawn of island #" + i);
            h.setArmorStandVisible(true);
            h.reload();
            spawnMarkers.put(i,h);
            updateSpawnChests();
        }

        private void updateSpawnChests() {
            for (Map.Entry<BlockPos,Hologram> e : spawnChests.entrySet()) {
                Hologram h = e.getValue();
                if (getNearestSpawn(e.getKey()) != -1) {
                    h.setLine(1,"of island #" + getNearestSpawn(e.getKey()));
                } else {
                    h.setLine(1,"of unknown island");
                }
                h.reload();
            }
        }

        private int getNearestSpawn(BlockPos pos) {
            BlockPos near = null;
            int id = -1;
            for (int i = 0; i < spawns.length; i++) {
                if (spawns[i] != null) {
                    if (near == null || spawns[i].distanceTo(pos) < near.distanceTo(pos)) {
                        near = spawns[i];
                        id = i;
                    }
                }
            }
            return id;
        }

        @Override
        public void load(Config config) {
            if (config.getStringList("spawn_chests") != null) {
                for (String bp : config.getStringList("spawn_chests")) {
                    System.out.println("loading spawn chest");
                    BlockPos pos = BlockPos.fromString(bp);
                    if (pos != null) {
                        markSpawnChest(pos);
                    }
                }
            }
            if (config.getStringList("mid_chests") != null) {
                for (String bp : config.getStringList("mid_chests")) {
                    System.out.println("loading middle chest");
                    BlockPos pos = BlockPos.fromString(bp);
                    if (pos != null) {
                        markMidChest(pos);
                    }
                }
            }
            if (config.getStringList("island_spawns") != null) {
                int i = 0;
                for (String bp : config.getStringList("island_spawns")) {
                    System.out.println("loading island spawn");
                    BlockPos pos = BlockPos.fromString(bp);
                    if (pos != null) {
                        markIslandSpawn(pos,i);
                        i++;
                    }
                }
            }
        }

        @Override
        public void save(Config config) {
            List<String> schests = new ArrayList<>();
            for (BlockPos pos : spawnChests.keySet()) {
                schests.add(pos.toString());
            }
            config.set("spawn_chests",schests);
            List<String> mid = new ArrayList<>();
            for (BlockPos pos : middleChests.keySet()) {
                mid.add(pos.toString());
            }
            config.set("mid_chests",mid);
            List<String> spawn = new ArrayList<>();
            for (BlockPos pos : spawns) {
                if (pos != null) {
                    spawn.add(pos.toString());
                }
            }
            config.set("island_spawns",spawn);
            spawnChests.values().forEach(Hologram::despawn);
            middleChests.values().forEach(Hologram::despawn);
            spawnMarkers.values().forEach(Hologram::despawn);
        }
    }
}
