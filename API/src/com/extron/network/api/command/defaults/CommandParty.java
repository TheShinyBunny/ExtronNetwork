package com.extron.network.api.command.defaults;

import com.extron.network.api.Main;
import com.extron.network.api.command.Command;
import com.extron.network.api.command.CommandTree;
import com.extron.network.api.command.argument.ExecuteData;
import com.extron.network.api.command.argument.ExpectedArgs;
import com.extron.network.api.command.argument.ValidArgs;
import com.extron.network.api.party.CombinationRequest;
import com.extron.network.api.party.CombinationType;
import com.extron.network.api.party.Party;
import com.extron.network.api.party.PartyMemberRole;
import com.extron.network.api.permission.Permission;
import com.extron.network.api.players.ExtronPlayer;
import com.extron.network.api.utils.FunctionUtils;
import com.extron.network.api.utils.ListUtils;
import com.extron.network.api.utils.TextUtils;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.function.Consumer;

public class CommandParty extends CommandTree {


    @Override
    public void addSubCommands(Consumer<Command> c) {
        c.accept(new Command() {
            @Override
            public String getName() {
                return "invite";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                ExtronPlayer p = getPlayerByName(args.getEntry("player"));
                invite(sender,p);
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .onlinePlayer("player");
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "accept";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                ExtronPlayer p = getPlayerByName(args.getEntry("player"));
                p.getParty().accept(sender);
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("player",ValidArgs.getBySender(Party::getInvitesOfPlayer));
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "list";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                listNames(sender,"Players in your party: ",sender.getParty().getAllPlayerNames(),FunctionUtils.noChange(),true);
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Permission getPermission() {
                return Permission.PARTY_ROLE.of(PartyMemberRole.MEMBER);
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "alldead";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                List<ExtronPlayer> alive = ListUtils.filter(sender.getParty().getAllPlayers(),p->p.getCurrentGame() != null && !p.getCurrentGame().getSpectators().contains(p));
                // FIXME: 7/1/2018 change that! there should be "alivePlayers" and "isAlive(ExtronPlayer)" in non-competitive games as well!
                if (alive.isEmpty()) {
                    success("All of your party members are not alive in-game. Feel free to warp the party to a new game.");
                } else {
                    listNames(sender,ChatColor.RED + "The following players are still alive in-game: " + ChatColor.RESET, alive, ExtronPlayer::getName,true);
                }
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public Permission getPermission() {
                return Permission.PARTY_ROLE.of(PartyMemberRole.LEADER);
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "leave";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                sender.getParty().leave(sender);
                sender.leaveParty();
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Permission getPermission() {
                return Permission.PARTY_ROLE.of(PartyMemberRole.MEMBER);
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "disband";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                for (ExtronPlayer p : sender.getParty().getAllPlayers()) {
                    if (p == sender) {
                        p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n" + ChatColor.RED + "You have disbanded the party.\n" + ChatColor.AQUA + TextUtils.LINE);
                    } else {
                        p.sendMessage(ChatColor.AQUA + TextUtils.LINE + "\n" + sender.getRealDisplayName() + ChatColor.RED + " have disbanded the party.\n" + ChatColor.AQUA + TextUtils.LINE);
                    }
                }
                sender.getParty().disband();
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Permission getPermission() {
                return Permission.PARTY_ROLE.of(PartyMemberRole.LEADER);
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "remove";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                ExtronPlayer remove = getPlayerByName(args.getEntry("player"));
                if (sender.equals(remove)) {
                    error("You can't remove yourself from the party! Use /party leave.");
                }
                if (sender.getParty() == remove.getParty()) {
                    performConditioned(sender.getParty().getRoleOf(sender).getPos() > sender.getParty().getRoleOf(remove).getPos(),
                            ()->sender.getParty().remove(remove,sender),
                            "You are not allowed to remove party members of a higher role!");
                } else {
                    error("This player is not in your party!");
                }
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("player",ValidArgs.getBySender(Party::getAllPlayersInPartyOf));
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Permission getPermission() {
                return Permission.PARTY_ROLE.of(PartyMemberRole.MANAGER);
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "setleader";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                ExtronPlayer newLeader = getPlayerByName(args.getEntry("player"));
                if (!sender.getParty().getAllPlayers().contains(newLeader)) {
                    error("This player is not in your party!");
                }
                if (sender.getParty().getLeader().equals(sender)) {
                    error("You are already the leader!");
                }
                sender.getParty().setLeader(newLeader,sender);
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("player",ValidArgs.getBySender(Party::getAllNonLeaderNames));
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Permission getPermission() {
                return Permission.PARTY_ROLE.of(PartyMemberRole.LEADER);
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "promote";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                ExtronPlayer promote = getPlayerByName(args.getEntry("player"));
                if (!sender.getParty().getAllPlayers().contains(promote)) {
                    error("This player is not in your party!");
                }
                if (sender.equals(promote)) {
                    error("You can't promote yourself!");
                }
                performConditioned(sender.getParty().getRoleOf(sender).getPos() > sender.getParty().getRoleOf(promote).getPos(),
                        ()->sender.getParty().promote(promote,sender),
                        "You are not allowed to promote party members of a higher role!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("player",ValidArgs.getBySender(Party::getAllNonLeaderNames));
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Permission getPermission() {
                return Permission.PARTY_ROLE.of(PartyMemberRole.MANAGER);
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "demote";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                ExtronPlayer demote = getPlayerByName(args.getEntry("player"));
                if (!sender.getParty().getAllPlayers().contains(demote)) {
                    error("This player is not in your party!");
                }
                if (sender.equals(demote)) {
                    error("You can't demote yourself!");
                }
                performConditioned(sender.getParty().getRoleOf(demote) == PartyMemberRole.MANAGER,
                        ()->sender.getParty().demote(demote,sender),
                        "You can only demote managers!");
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .string("player",ValidArgs.getBySender(Party::getAllManagerNames));
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Permission getPermission() {
                return Permission.PARTY_ROLE.of(PartyMemberRole.LEADER);
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "combine";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                CombinationType type = getEnumValue(args.getEntry("method"),CombinationType.class);
                ExtronPlayer other = getPlayerByName(args.getEntry("player"));
                if (sender.equals(other)) {
                    error("You can't combine the party with your own party!");
                }
                if (other.getParty() == null || !other.getParty().getLeader().equals(other)) {
                    error("This player must be a party leader!");
                }
                if (type == CombinationType.ACCEPT) {
                    for (CombinationRequest r : Main.getPartyCombinations()) {
                        if (r.getSender().equals(other.getParty()) && r.getReceiver().equals(sender.getParty())) {
                            other.getParty().acceptCombine(r);
                            return;
                        }
                    }
                    error("This player did not request you to combine the party!");
                } else {
                    sender.getParty().requestCombine(other.getParty(),type);
                }
            }

            @Override
            public ExpectedArgs getArguments() {
                return ExpectedArgs.create()
                        .enumValue("method",CombinationType.class)
                        .onlinePlayer("player");
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Permission getPermission() {
                return Permission.PARTY_ROLE.of(PartyMemberRole.LEADER);
            }
        });
        c.accept(new Command() {
            @Override
            public String getName() {
                return "all-invite";
            }

            @Override
            public void execute(ExtronPlayer sender, ExecuteData args) throws Exception {
                sender.getParty().toggleAllInvite(sender);
            }

            @Override
            public ExpectedArgs getArguments() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public Permission getPermission() {
                return Permission.PARTY_ROLE.of(PartyMemberRole.LEADER);
            }
        });
    }

    private void invite(ExtronPlayer sender, ExtronPlayer invited) throws Exception{
        isNull(invited,"This player does not exist!");
        performConditioned(!sender.equals(invited), (Runnable) null,"You can't invite yourself to a party!");
        if (invited.isOnline()) {
            performConditioned(!Party.getInvitesOfPlayer(invited).contains(sender.getName()), (String) null, "You have already invited this player!");
            performConditioned(Party.getInvitesOfPlayer(sender).contains(invited.getName()) && invited.getParty() != null, () -> invited.getParty().accept(sender), (String) null);
            performConditionedNull(sender.getParty(), () -> performConditioned(sender.getParty().canPlayerInvite(sender),
                    () -> sender.getParty().invite(sender, invited),
                    () -> sender.sendMessage(ChatColor.RED + "You are not allowed to invite other players to this party!")
            ), () -> {
                Party p = new Party(sender);
                sender.setParty(p);
                p.invite(sender, invited);
            });
        } else {
            error("This player is not online!");
        }
    }

    @Override
    public String getDefaultSubCommandToUse() {
        return "invite";
    }

    @Override
    public boolean addHelpSubCommand() {
        return true;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String[] getAliases() {
        return new String[] {"p"};
    }
}
