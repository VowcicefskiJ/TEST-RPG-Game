package com.rpg;

import java.util.List;

public class SkillLesson {
    private final SkillType skillType;
    private final String overview;
    private final List<String> starterItems;

    public SkillLesson(SkillType skillType, String overview, List<String> starterItems) {
        this.skillType = skillType;
        this.overview = overview;
        this.starterItems = List.copyOf(starterItems);
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public String getOverview() {
        return overview;
    }

    public List<String> getStarterItems() {
        return starterItems;
    }
}
