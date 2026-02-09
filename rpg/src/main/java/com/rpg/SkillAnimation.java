package com.rpg;

import java.util.List;

public class SkillAnimation {
    private final SkillType skillType;
    private final String title;
    private final String resultItem;
    private final List<String> ingredients;
    private final List<AnimationStep> steps;

    public SkillAnimation(
            SkillType skillType,
            String title,
            String resultItem,
            List<String> ingredients,
            List<AnimationStep> steps
    ) {
        this.skillType = skillType;
        this.title = title;
        this.resultItem = resultItem;
        this.ingredients = List.copyOf(ingredients);
        this.steps = List.copyOf(steps);
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public String getTitle() {
        return title;
    }

    public String getResultItem() {
        return resultItem;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<AnimationStep> getSteps() {
        return steps;
    }
}
