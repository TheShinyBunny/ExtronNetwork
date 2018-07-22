package com.extron.network.api.command.defaults;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.tasks.ExtronRunnable;
import org.bukkit.Material;
import org.bukkit.entity.Firework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandBretty extends BaseCommand {
    @Override
    public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
        if (args.length() == 0) {
            sender.setHealth(0);
        } else if (args.at(0).equals("random")){
            List<KillMethod> methods = Arrays.asList(
                    p -> {
                        p.getWorld().handle.spawnFallingBlock(sender.getLocation().add(0,20,0),Material.ANVIL,(byte)0);
                    },
                    p -> {
                        p.getLocation().getBlock().setType(Material.LAVA);
                    },
                    p -> {
                        List<Firework> fw = new ArrayList<>();
                        new ExtronRunnable() {

                            @Override
                            public void run() {
                                Firework f = sender.getWorld().handle.spawn(sender.getLocation(),Firework.class);
                                fw.add(f);
                                f.detonate();
                            }

                            @Override
                            public void onCancel() {
                                fw.forEach(Firework::remove);
                            }
                        }.repeat(0,20,100);
                    }
            );
            KillMethod m = ListUtils.randomItem(methods);
            if (m != null) {
                m.execute(sender);
            }
        } else {
            sender.kick("0");
        }
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .optional()
                .string("arg");
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"efes","tembel"};
    }

    @FunctionalInterface
    interface KillMethod {
        void execute(ExtronPlayer p);
    }
}
