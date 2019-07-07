package net.dirtcraft.ftbextrautilities;

import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.google.inject.Inject;
import net.minecraft.util.text.ITextComponent;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.List;

@Plugin(
        id = "ftbextrautilities",
        name = "FtbExtraUtilities",
        description = "adds special server admin commands",
        url = "https://github.com/shinyafro",
        authors = {
                "shinyafro"
        },
        dependencies = {
                @Dependency(id = "ftblibfml c")
        }
)
public class FtbExtraUtilities {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        CommandSpec getTeamPlayers = CommandSpec.builder()
                .arguments(GenericArguments.string(Text.of("teamname")))
                .executor(((src, args) -> {
                    String team = args.<String>getOne("teamname").get();
                    Collection<ForgeTeam> teams = Universe.get().getTeams();
                    for (ForgeTeam t3am : teams){
                        String s = t3am.getTitle().getUnformattedComponentText();
                        System.out.println(s);
                        System.out.println(t3am.getDesc());
                        System.out.println(t3am.getUIDCode());
                        System.out.println(t3am.getID());
                        s = t3am.getSettings().getID();
                        System.out.println(s);
                    }
                    System.out.println(Universe.get().getTeam("teanname")==null?"y":"n");
                    return CommandResult.success();
                }))
                .build();
        Sponge.getCommandManager().register(this, getTeamPlayers, "getteamplayers");
    }
}
