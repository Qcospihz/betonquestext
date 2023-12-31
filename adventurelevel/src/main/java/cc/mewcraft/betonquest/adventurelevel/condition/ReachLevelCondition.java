package cc.mewcraft.betonquest.adventurelevel.condition;

import cc.mewcraft.adventurelevel.data.PlayerData;
import cc.mewcraft.adventurelevel.level.category.LevelCategory;
import cc.mewcraft.adventurelevel.plugin.AdventureLevelProvider;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

public class ReachLevelCondition extends Condition {
    private final LevelCategory category;
    private final Expression evaluator;

    // usage: alevel <category> <operator><level>
    public ReachLevelCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);

        category = instruction.getEnum(LevelCategory.class);
        evaluator = new Expression("level" + instruction.next());
    }

    @Override protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        PlayerData data = AdventureLevelProvider.get().getPlayerDataManager().load(profile.getPlayerUUID());
        if (!data.complete()) {
            return false;
        }

        int level = data.getLevel(category).getLevel();

        try {
            return evaluator.with("level", level).evaluate().getBooleanValue();
        } catch (EvaluationException | ParseException e) {
            throw new QuestRuntimeException(e.getMessage());
        }
    }
}
