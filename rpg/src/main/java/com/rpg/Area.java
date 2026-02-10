package com.rpg;

import java.util.List;

public class Area {
    private final String name;
    private final String description;
    private final List<ResourceNode> resources;
    private final List<Npc> monsters;
    private final List<SkillTutor> skillTutors;
    private final AuctionHouse auctionHouse;

    public Area(String name, String description, List<ResourceNode> resources, List<Npc> monsters) {
        this(name, description, resources, monsters, List.of(), null);
    }

    public Area(
            String name,
            String description,
            List<ResourceNode> resources,
            List<Npc> monsters,
            List<SkillTutor> skillTutors,
            AuctionHouse auctionHouse
    ) {
        this.name = name;
        this.description = description;
        this.resources = List.copyOf(resources);
        this.monsters = List.copyOf(monsters);
        this.skillTutors = List.copyOf(skillTutors);
        this.auctionHouse = auctionHouse;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ResourceNode> getResources() {
        return resources;
    }

    public List<Npc> getMonsters() {
        return monsters;
    }

    public List<SkillTutor> getSkillTutors() {
        return skillTutors;
    }

    public AuctionHouse getAuctionHouse() {
        return auctionHouse;
    }
}
