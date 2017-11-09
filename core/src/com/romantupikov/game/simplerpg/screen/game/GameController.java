package com.romantupikov.game.simplerpg.screen.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.romantupikov.game.simplerpg.SimpleRpgGame;
import com.romantupikov.game.simplerpg.assets.RegionsNames;
import com.romantupikov.game.simplerpg.entity.Unit;
import com.romantupikov.game.simplerpg.factory.EffectFactory;
import com.romantupikov.game.simplerpg.factory.EntityFactory;
import com.romantupikov.game.simplerpg.factory.SkillFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hvitserk on 01-Nov-17.
 */

public class GameController implements Observable {
    private final float STATUS_UPDATE = 1f;
    private final float AI_UPDATE = 1f;

    private final SimpleRpgGame game;
    private final AssetManager assetManager;
    private final Viewport viewport;

    private InputHandler inputHandler;

    private List<Observer> observers;

    private EntityFactory entityFactory;
    private SkillFactory skillFactory;
    private EffectFactory effectFactory;

    private Unit selectedUnit;
    private Unit selectedEnemy;

    private Array<Unit> enemyParty = new Array<Unit>();
    private Array<Unit> playerParty = new Array<Unit>();

    private boolean gameOver = false;
    private float statusUpdateTimer;
    private float aiTimer;

    public GameController(SimpleRpgGame game, Viewport viewport) {
        this.game = game;
        assetManager = game.getAssetManager();
        this.viewport = viewport;
        observers = new ArrayList<Observer>();
        init();
    }

    private void init() {
        inputHandler = new InputHandler(this, viewport);
        game.addInputProcessor(new GestureDetector(inputHandler));

        effectFactory = new EffectFactory(assetManager);
        skillFactory = new SkillFactory(effectFactory);
        entityFactory = new EntityFactory(assetManager, skillFactory);

        // TODO: 06-Nov-17 перекнуть в EntityFactory
        // == UNDER HEAVY CONSTRUCTION ==
        // == player party ==
        Unit dwarf1 = entityFactory.createDummyUnit(RegionsNames.DWARF_RUNEMASTER, "Dwarf1");
        dwarf1.setPosition(1f, 1f);
        dwarf1.getAttributes().setMoveSpeed(1f);
        dwarf1.getAttributes().setAttackDelay(0.5f);
        dwarf1.getAttributes().setCastDelay(0.5f);
        dwarf1.getAttributes().setAttackRange(5f);
        dwarf1.getAttributes().setIntelligence(5f);
        dwarf1.addSkill(skillFactory.createHealSkill(dwarf1));
        this.selectedUnit = dwarf1;
        playerParty.add(dwarf1);

        Unit dwarf = entityFactory.createDummyUnit(RegionsNames.DWARF_BASE, "Dwarf2");
        dwarf.setPosition(6f, 6f);
        dwarf.getAttributes().setMoveSpeed(3f);
        dwarf.getAttributes().setAttackDelay(0.5f);
        dwarf.getAttributes().setCastDelay(0.5f);
        dwarf.getAttributes().setAttackRange(5f);
        dwarf.getAttributes().setIntelligence(5f);
        dwarf.addSkill(skillFactory.createHealSkill(dwarf));
        playerParty.add(dwarf);

        Unit dwarf2 = entityFactory.createDummyUnit(RegionsNames.DWARF_MACE, "Dwarf3");
        dwarf2.setPosition(3f, 3f);
        dwarf2.getAttributes().setMoveSpeed(2f);
        dwarf2.getAttributes().setAttackDelay(0.5f);
        dwarf2.getAttributes().setCastDelay(0.5f);
        dwarf2.getAttributes().setAttackRange(5f);
        dwarf2.getAttributes().setIntelligence(5f);
        dwarf2.addSkill(skillFactory.createHealSkill(dwarf2));
        playerParty.add(dwarf2);

        // == enemy party ==
        Unit goblin = entityFactory.createDummyUnit(RegionsNames.GOBLIN_NINJA, "Goblin");
        goblin.setPosition(8f, 3f);
        goblin.getAttributes().setAttackDelay(5f);
        enemyParty.add(goblin);
    }

    public void update(float delta) {
        if (gameOver) {
            return;
        }

        updateUnits(delta);
        updateStatus(delta);
        simpleAI(delta);
    }

    private void updateUnits(float delta) {
        selectedUnit.handleInput(inputHandler);

        for (int i = 0; i < playerParty.size; i++) {
            Unit unit = playerParty.get(i);
            unit.update(delta);
        }

        for (int i = 0; i < enemyParty.size; i++) {
            Unit enemy = enemyParty.get(i);
            enemy.update(delta);
        }
    }

    private void updateStatus(float delta) {
        statusUpdateTimer += delta;
        if (statusUpdateTimer >= STATUS_UPDATE) {
            statusUpdateTimer = 0f;
            playerParty.sort();
        }
    }

    private void simpleAI(float delta) {
        aiTimer += delta;
        if (aiTimer >= AI_UPDATE) {
            aiTimer = 0f;
            for (int i = 0; i < enemyParty.size; i++) {
                Unit unit = enemyParty.get(i);
                Unit target = playerParty.first();
                unit.setTarget(target);
                unit.activateSkill(0);
            }
        }
    }

    // == getters/setters ==

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(Unit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    public Unit getSelectedEnemy() {
        return selectedEnemy;
    }

    public void setSelectedEnemy(Unit selectedEnemy) {
        this.selectedEnemy = selectedEnemy;
    }

    public Array<Unit> getEnemyParty() {
        return enemyParty;
    }

    public Array<Unit> getPlayerParty() {
        return playerParty;
    }

    // == override methods ==

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.getControllerUpdate();
        }
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public EffectFactory getEffectFactory() {
        return effectFactory;
    }
}
