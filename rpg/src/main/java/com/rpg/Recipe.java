package com.rpg;

import java.util.List;
import java.util.Map;

public class Recipe {
    private final String name;
    private final SkillType requiredSkill;
    private final int requiredLevel;
    private final Map<String, Integer> ingredients;
    private final String resultItem;
    private final int resultQuantity;
    private final int experienceReward;
    private final String narrative;

    public Recipe(String name, SkillType requiredSkill, int requiredLevel,
                  Map<String, Integer> ingredients, String resultItem,
                  int resultQuantity, int experienceReward, String narrative) {
        this.name = name;
        this.requiredSkill = requiredSkill;
        this.requiredLevel = requiredLevel;
        this.ingredients = Map.copyOf(ingredients);
        this.resultItem = resultItem;
        this.resultQuantity = resultQuantity;
        this.experienceReward = experienceReward;
        this.narrative = narrative;
    }

    public String getName() { return name; }
    public SkillType getRequiredSkill() { return requiredSkill; }
    public int getRequiredLevel() { return requiredLevel; }
    public Map<String, Integer> getIngredients() { return ingredients; }
    public String getResultItem() { return resultItem; }
    public int getResultQuantity() { return resultQuantity; }
    public int getExperienceReward() { return experienceReward; }
    public String getNarrative() { return narrative; }

    public boolean canCraft(Player player) {
        if (player.getSkill(requiredSkill).getLevel() < requiredLevel) return false;
        Inventory inv = player.getInventory();
        for (var entry : ingredients.entrySet()) {
            if (!inv.hasItem(entry.getKey(), entry.getValue())) return false;
        }
        return true;
    }

    public String craft(Player player) {
        if (!canCraft(player)) return null;
        Inventory inv = player.getInventory();
        for (var entry : ingredients.entrySet()) {
            inv.removeItem(entry.getKey(), entry.getValue());
        }
        inv.addItem(resultItem, resultQuantity);
        player.trainSkill(requiredSkill, experienceReward);
        return narrative;
    }
}
