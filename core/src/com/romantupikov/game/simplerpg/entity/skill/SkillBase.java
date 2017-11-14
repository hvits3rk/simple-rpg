package com.romantupikov.game.simplerpg.entity.skill;

import com.romantupikov.game.simplerpg.entity.Unit;
import com.romantupikov.game.simplerpg.entity.effect.Effect;

/**
 * Created by hvitserk on 07-Nov-17.
 */

abstract class SkillBase implements Skill {
    protected String name;
    protected Unit unit;
    protected Effect[] subEffects;

    protected SkillBase(Unit unit,
                        Effect... subEffects) {
        this.unit = unit;
        this.subEffects = subEffects;
    }
}
