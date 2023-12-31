package cc.mewcraft.betonquest.brewery.event;

import cc.mewcraft.betonquest.variable.GenericVariable;
import com.dre.brewery.recipe.BRecipe;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class GiveBrewEventFactory implements EventFactory {
    private final BetonQuestLogger log;
    private final Server server;
    private final BukkitScheduler scheduler;
    private final Plugin plugin;

    public GiveBrewEventFactory(final BetonQuestLogger log, final Server server, final BukkitScheduler scheduler, final Plugin plugin) {
        this.log = log;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        int amount = instruction.getInt();
        int quality = instruction.getInt();
        if (quality < 0 || quality > 10) {
            throw new InstructionParseException("Brew quality must be between 0 and 10!");
        }
        GenericVariable<BRecipe> recipe = new GenericVariable<>(
                instruction.next().replace("_", " "),
                instruction.getPackage(),
                recipeName -> {
                    BRecipe recipe0 = null;
                    for (final BRecipe r : BRecipe.getAllRecipes()) {
                        if (r.hasName(recipeName)) {
                            recipe0 = r;
                            break;
                        }
                    }
                    if (recipe0 == null) {
                        throw new QuestRuntimeException("There is no brewing recipe with the name " + "!");
                    } else {
                        return recipe0;
                    }
                });

        return new PrimaryServerThreadEvent(
                new OnlineProfileRequiredEvent(
                        log, new GiveBrewEvent(amount, quality, recipe), instruction.getPackage()
                ),
                server,
                scheduler,
                plugin
        );
    }
}
