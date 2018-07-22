package com.extron.network.api.command.defaults.admin;

import com.extron.network.api.Main;
import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.ParameterDispatcher;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.players.ExtronPlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.concurrent.atomic.AtomicBoolean;

public class CommandCreateWorld extends BaseCommand {
    private boolean inferName;

    public CommandCreateWorld(boolean inferName) {
        this.inferName = inferName;
    }

    @Override
    public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
        String name = data.at(0);
        String params = data.at(1);
        if (params == null) {
            nullOrSuccess(Main.createWorld(name,World.Environment.NORMAL,WorldType.NORMAL,false),"An error occurred while creating a world!","World created successfully!");
            return;
        }
        AtomicBoolean isVoid = new AtomicBoolean(false);
        ParameterDispatcher<WorldCreator> dispatcher = new ParameterDispatcher.Builder<>(new WorldCreator(name))
                .setSeparator(",",ParameterDispatcher.Escape.quote())
                .addSplitter(":")
                .addSplitter("=")
                .createKey("seed")
                    .setValueProcessorException((s,c)->tryParseLong(s, c::seed,"Invalid seed '" + s + "'!"))
                    .create()
                .createKey("environment")
                    .setAliases("env","dim")
                    .setValueProcessorException((s,c)->getEnumValueException(World.Environment.class,"environment",s.toUpperCase(), c::environment))
                    .create()
                .createKey("type")
                    .setValueProcessorException((s,c)->getEnumValueException(WorldType.class,"world type",s.toUpperCase(),c::type))
                    .create()
                .createKey("generator")
                    .setValueProcessorException((s,c)->getException(()->c.generator(s)))
                    .create()
                .createKey("settings")
                    .setValueProcessor((s,c)->c.generatorSettings(s))
                    .create()
                .createKey("nostruct")
                    .setAliases("no_structures")
                    .setHasNoValue()
                    .ifKeyPresent(c->c.generateStructures(true))
                    .create()
                .createKey("void")
                    .setHasNoValue()
                    .ifKeyPresent(c-> isVoid.set(true))
                    .create()
                .build();
        WorldCreator creator = dispatcher.dispatch(params);
        nullOrSuccess(Main.createWorld(name,creator,isVoid.get()),"An error occurred while trying to create this world!","World created successfully!");
    }


    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .string("world name")
                .optional()
                .params();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getName() {
        return inferName ? super.getName() : "create";
    }
}
