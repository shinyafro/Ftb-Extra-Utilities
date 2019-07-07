package net.dirtcraft.ftbextrautilities;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.google.inject.Inject;
import net.minecraft.util.text.ITextComponent;
import org.slf4j.Logger;
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

    private static FtbExtraUtilities instance;
    @Inject
    private Logger logger;

    public static FtbExtraUtilities getInstance() {
        return instance;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        CommandSpec getId = CommandSpec.builder()
                .arguments(GenericArguments.string(Text.of("name")))
                .executor((src, args)->{
                    //noinspection OptionalGetWithoutIsPresent
                    String team = args.<String>getOne("name").get();
                    if (team.equals("'")) {
                        throw new CommandException( Text.of("invalid argumant!"));
                    }
                    if (team.contains("'")){
                        String s = team.split("'")[0];
                        Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
                        Optional<User> user = userStorage.get().get(s);
                        if (!user.isPresent()) {
                            throw new CommandException (Text.of("Failed to find player!"));
                        }
                        UUID uuid = user.get().getUniqueId();
                        ForgePlayer target = Universe.get().getPlayer(uuid);
                        if (target == null) throw new CommandException (Text.of("Failed to find player"));
                        src.sendMessage(Text.of(target.team.getID()));
                    } else {
                        Collection<ForgeTeam> teams = Universe.get().getTeams();
                        for (ForgeTeam t3am : teams) {
                            if (t3am.getTitle().getUnformattedComponentText().equals(team))
                                src.sendMessage(Text.of(t3am.getID()));
                        }
                    }
                    return CommandResult.success();
                })
                .build();

        CommandSpec getPlayers = CommandSpec.builder()
                .arguments(GenericArguments.string(Text.of("id")))
                .executor((src,args)->{
                    String team = args.<String>getOne("id").get();
                    ForgeTeam fteam = Universe.get().getTeam(team);
                    for (ForgePlayer p : fteam.getMembers()){
                        src.sendMessage(Text.of(p.getName()));
                    }
                    return CommandResult.success();
                })
                .build();

        CommandSpec main = CommandSpec.builder()
                .arguments(GenericArguments.string(Text.of("teamname")))
                .child(getId, "getid", "id")
                .child(getPlayers, "getplayers", "players")
                //.executor(new main())
                .build();
        Sponge.getCommandManager().register(this, main, "teams");
    }
}
