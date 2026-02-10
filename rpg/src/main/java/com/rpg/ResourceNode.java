package com.rpg;

public class ResourceNode {
    private final String name;
    private final SkillType skillType;
    private final String resourceItem;

    public ResourceNode(String name, SkillType skillType, String resourceItem) {
        this.name = name;
        this.skillType = skillType;
        this.resourceItem = resourceItem;
    }

    public String getName() {
        return name;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public String getResourceItem() {
        return resourceItem;
    }
}
