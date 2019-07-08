package net.dirtcraft.ftbextrautilities;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Plugin(
        id = "ftbextrautilities",
        name = "FtbExtraUtilities",
        description = "adds special server admin commands",
        url = "https://github.com/shinyafro",
        authors = {
                "shinyafro"
        },
        dependencies = {
                @Dependency(id = "ftblib")
        }
)
public class FtbExtraUtilities {
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        CommandSpec getId = CommandSpec.builder()
                .arguments(GenericArguments.string(Text.of("name")))
                .executor((src, args)->{
                    if (!args.<String>getOne("name").isPresent()) throw new CommandException(Text.of("You need to specify a name."));
                    String teamName = args.<String>getOne("name").get();
                    if (teamName.equals("'")) throw new CommandException( Text.of("invalid argumant!"));
                    if (teamName.contains("'")){
                        String s = teamName.split("'")[0];
                        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
                        if (!userStorage.isPresent()) throw new CommandException(Text.of("Failed to retrieve user storage service!"));
                        Optional<User> user = userStorage.get().get(s);
                        if (!user.isPresent()) throw new CommandException (Text.of("Failed to find player!"));

                        UUID uuid = user.get().getUniqueId();
                        ForgePlayer teamLeader = Universe.get().getPlayer(uuid);
                        if (teamLeader == null) throw new CommandException (Text.of("Failed to find player"));

                        src.sendMessage(Text.of(teamLeader.team.getID()));
                    } else {
                        Collection<ForgeTeam> teams = Universe.get().getTeams();
                        for (ForgeTeam team : teams) {
                            if (team.getTitle().getUnformattedComponentText().equals(teamName))
                                src.sendMessage(Text.of(team.getID()));
                        }
                    }
                    return CommandResult.success();
                })
                .build();

        CommandSpec getPlayers = CommandSpec.builder()
                .arguments(GenericArguments.string(Text.of("id")))
                .executor((src,args)->{
                    if (!args.<String>getOne("id").isPresent()) throw new CommandException(Text.of("You need to specify an ID."));
                    String teamName = args.<String>getOne("id").get();
                    ForgeTeam forgeTeam = Universe.get().getTeam(teamName);
                    for (ForgePlayer p : forgeTeam.getMembers()){
                        if (p == forgeTeam.owner){
                            src.sendMessage(Text.of("Owner: "+p.getName()));
                        } else {
                            src.sendMessage(Text.of(p.getName()));
                        }
                    }
                    return CommandResult.success();
                })
                .build();

        CommandSpec main = CommandSpec.builder()
                .permission("ftbextrautilities.teams.view")
                .child(getId, "getid", "id")
                .child(getPlayers, "getplayers", "players")
                .build();

        Sponge.getCommandManager().register(this, main, "ftbteams");
    }
}
