# Project Map

This document helps other AI agents (and humans) quickly understand the RPG prototype layout and where to extend it.

## Entry point

- `com.rpg.Main`
  - Demonstrates the game loop, combat preview, animation preview, and world overview.

## Core gameplay models

- `SkillType` + `Skill`
  - Skill catalog and leveling data.
- `SkillAction`
  - Actions available per skill (harvest, craft, etc.).
- `Player`
  - Holds skill XP/levels and current stats.
- `GameEngine`
  - Executes turns and applies actions chosen by agents.

## AI agents

- `AiAgent` (interface)
- `BasicPlannerAgent`
  - Picks actions to balance skill progression.

## Combat

- `CombatSystem`
  - First-person directional parry resolution.
- `CombatDirection`
  - Four-direction parry/attack mapping.
- `Combatant`, `Npc`, `CombatResult`
  - Shared combat stats + outcome payloads.

## Magic

- `MagicSchool`
  - Witchcraft, fire, air, water, earth, arcane, healing, illusions, buffs, debuffs.
- `MagicSpellCatalog`
  - Brainstormed spell list with numbers and effects.
- `Spell`
  - Spell definitions + effect data.

## Animations

- `SkillAnimation` + `AnimationStep`
  - Step-by-step animation data for crafting/harvesting.
- `SkillAnimationCatalog`
  - One example animation per skill.

## World data

- `GameWorld`
  - Aggregates skills, spells, animations, and areas.
- `Area`
  - Lightweight location container.
- `ResourceNode`
  - Gatherable resource spots.

## Where to extend

- Add new skills in `SkillType` and seed actions in `GameWorld`.
- Add new spell schools in `MagicSchool` and populate `MagicSpellCatalog`.
- Add more areas/resources in `GameWorld` or new factory methods.
- Expand the AI in `BasicPlannerAgent` or implement new `AiAgent` variants.
