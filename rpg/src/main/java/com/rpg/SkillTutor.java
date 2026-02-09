package com.rpg;

import java.util.List;

public class SkillTutor {
    private final String name;
    private final String description;
    private final List<SkillLesson> lessons;

    public SkillTutor(String name, String description, List<SkillLesson> lessons) {
        this.name = name;
        this.description = description;
        this.lessons = List.copyOf(lessons);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<SkillLesson> getLessons() {
        return lessons;
    }
}
