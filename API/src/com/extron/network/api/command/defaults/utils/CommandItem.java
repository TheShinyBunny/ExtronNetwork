package com.extron.network.api.command.defaults.utils;

import com.extron.network.api.command.BaseCommand;
import com.extron.network.api.command.ParameterDispatcher;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.NBTContainer;
import net.minecraft.server.v1_8_R1.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CommandItem extends BaseCommand {

    private List<String> itemIds = new ArrayList<>();

    {
        Field[] items = Items.class.getFields();
        for (Field f : items) {
            f.setAccessible(true);
            itemIds.add(f.getName().toLowerCase());
        }
        Field[] blocks = Blocks.class.getFields();
        for (Field f : blocks) {
            f.setAccessible(true);
            if (Item.getItemOf(Block.getByName(f.getName().toLowerCase())) != null) {
                itemIds.add(f.getName().toLowerCase());
            }
        }
    }

    @Override
    public void execute(ExtronPlayer sender, ExecuteData data) throws Exception {
        ifArgEquals(data.at(0),"help",()->this.showHelp(sender));
        String item = data.at(0);
        test(ListUtils.containsIgnoreCase(itemIds,item),true,String.format("Unknown item id %s",data.at(0)));
        System.out.println("data length: " + data.length());
        if (data.length() == 1) {
            sender.executeCommand("give @p " + item);
            return;
        }
        String params = data.at(1);
        AtomicInteger count = new AtomicInteger(1);
        AtomicInteger damage = new AtomicInteger(0);
        ParameterDispatcher.Builder<NBTContainer> builder = new ParameterDispatcher.Builder<>(new NBTContainer())
                .addSplitter(":")
                .addSplitter("=")
                .setSeparator(",")
                .createKey("name")
                    .setValueProcessor((s,nbt)->nbt.setString("display.Name",s))
                    .create()
                .createKey("count")
                    .setValueProcessorException((s,nbt)-> tryParseInt(s, count::set,"Invalid 'count' number!"))
                    .setAliases("amount")
                    .create()
                .createKey("damage")
                    .setValueProcessorException((s,nbt)-> tryParseInt(s, damage::set,"Invalid 'damage' number!"))
                    .setAliases("durability")
                    .create()
                .createKey("glint")
                    .setHasNoValue()
                    .ifKeyPresent(nbt -> {
                        NBTTagCompound c = new NBTTagCompound();
                        c.setShort("id",(short)0);
                        c.setShort("lvl",(short)0);
                        NBTTagList list = new NBTTagList();
                        list.add(c);
                        nbt.setList("ench",list);
                        nbt.setInt("HideFlags",1);
                    })
                    .create()
                .createKey("lore")
                    .setValueProcessorException(ParameterDispatcher.dispatchList("|",(nbt,list)->nbt.setList("display.Lore",list)))
                    .create();
        for (String ench : ListUtils.convertAllArray(Enchantment.getNames(),s->s.substring(s.indexOf(":")+1))) {
            builder.createKey(ench)
                    .setValueProcessor((s,nbt)-> tryParseInt(s,i->setEnchant(nbt,ench,i),"Invalid enchantment '" + ench + "' level!"))
                    .create();
        }
        ParameterDispatcher<NBTContainer> dispatcher = builder.build();
        NBTContainer nbt = dispatcher.dispatch(params);
        String cmd = "give " + sender.getName() + " minecraft:" + item + " " + count + " " + damage + " " + nbt;
        sender.executeCommand(cmd);
    }

    private void setEnchant(NBTContainer nbt, String ench, int lvl) {
        NBTContainer c = new NBTContainer();
        c.setShort("id", (short) Enchantment.getByName(ench).id);
        c.setShort("lvl",(short)lvl);
        NBTTagList enchs = nbt.getList("ench",10);
        enchs.add(c.getTag());
        nbt.setList("ench",enchs);
    }

    private void showHelp(ExtronPlayer sender) {
        printHelpFor(sender,this,0);
    }

    @Override
    public ExpectedArgs getArguments() {
        return ExpectedArgs.create()
                .optional()
                .string("item id",ValidArgs.getter(()->itemIds))
                .params();
    }

    @Override
    public String[] getAliases() {
        return new String[]{"i","giveme"};
    }

    @Override
    public String getDescription() {
        return null;
    }
}
