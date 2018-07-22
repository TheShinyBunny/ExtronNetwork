package com.extron.network.api.command.defaults.trolls;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.SavedBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

public class CommandTrap extends BaseCommand {

    private static final List<Method> methods = new ArrayList<>();
    public static Map<ExtronPlayer,Method> currentTraps = new HashMap<>();

    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        ExtronPlayer target = getOnlinePlayerByName(args.getEntry("player"));
        Method method = notNullOrDefault(ListUtils.firstMatch(methods,m->m.getName().equalsIgnoreCase(args.at(1))),ListUtils.randomItem(methods,m->m != REMOVE));
        if (method != null) {
            Method prev = currentTraps.get(target);
            if (prev != null) {
                prev.remove(target);
            } else if (method == REMOVE) {
                error("This player is not trapped!");
            }
            method.execute(target);
            if (method == REMOVE) {
                currentTraps.remove(target);
                success("Removed trap of %s",target.getName());
            } else {
                currentTraps.put(target, method);
                success("Trapped %s with §6%s trap!",target.getName(),method.getName());
            }
        }
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .onlinePlayer("player")
                .optional()
                .string("method",ValidArgs.getter(ListUtils.convertAndSupply(methods,Method::getName)));
    }

    @Override
    public String getDescription() {
        return null;
    }

    public static void registerTrap(CommandTrap.Method m) {
        methods.add(m);
    }

    public static final Method VOID = new Method("void") {
        private Map<ExtronPlayer,List<SavedBlock>> saved = new HashMap<>();

        @Override
        public void execute(ExtronPlayer p) {
            List<SavedBlock> blocks = new ArrayList<>();
            saved.put(p,blocks);
            int i = p.getLocation().getBlockY();
            Location loc = p.getLocation().clone();
            while (i >= 0) {
                Block b = loc.subtract(0,1,0).getBlock();
                blocks.add(new SavedBlock(b.getState()));
                b.setType(Material.AIR);
                i--;
            }
            p.handle.teleport(new Location(p.handle.getWorld(),loc.getBlockX()+0.5,p.getLocation().getY(),loc.getBlockZ()+0.5));
                /*Main.getTaskManager().addTask(new ExtronTask(()->{
                    for (SavedBlock b : blocks) {
                        b.replaceState();
                    }
                },TaskType.DELAYED,80));*/
        }

        @Override
        public void remove(ExtronPlayer p) {
            if (saved.containsKey(p)) {
                for (SavedBlock b : saved.getOrDefault(p,new ArrayList<>())) {
                    b.replaceState();
                }
                saved.remove(p);
            }
        }
    };

    public static final Method GLASS = new Method("glass") {
        private Map<ExtronPlayer,List<Location>> locations = new HashMap<>();

        @Override
        public void execute(ExtronPlayer p) {
            List<Location> changed = new ArrayList<>();
            locations.put(p,changed);
            for (int i = -2; i < 3; i++) {
                for (int j = -2; j < 4; j++) {
                    for (int k = -2; k < 3; k++) {
                        if (i == -2 || j == -2 || k == -2 || i == 2 || j == 3 || k == 2) {
                            Block b = p.getLocation().clone().add(i, j, k).getBlock();
                            if (b.getType() == Material.AIR) {
                                changed.add(b.getLocation());
                                b.setType(Material.GLASS);
                            }
                        }
                    }
                }
            }

        }

        @Override
        public void remove(ExtronPlayer p) {
            if (locations.containsKey(p)) {
                for (Location loc : locations.getOrDefault(p,new ArrayList<>())) {
                    loc.getBlock().setType(Material.AIR);
                }
            }
            locations.remove(p);
        }
    };

    public static final Method CONFUSE = new Method("confuse") {
        @Override
        public void execute(ExtronPlayer p) {
            Location loc = p.getLocation();
            loc.setYaw((p.getLocation().getYaw() + 180) % 360);
            p.handle.teleport(loc);
        }

        @Override
        public void remove(ExtronPlayer p) {

        }
    };

    public static final Method REMOVE = new Method("remove") {
        @Override
        public void execute(ExtronPlayer p) {
            if (currentTraps.containsKey(p)) {
                currentTraps.get(p).remove(p);
            }
        }

        @Override
        public void remove(ExtronPlayer p) {

        }
    };


    static {
        registerTrap(VOID);
        registerTrap(GLASS);
        registerTrap(REMOVE);
        registerTrap(CONFUSE);
    }

    public static abstract class Method {

        private final String name;

        public Method(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract void execute(ExtronPlayer p);

        public abstract void remove(ExtronPlayer p);
    }

}
