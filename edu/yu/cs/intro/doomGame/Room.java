package edu.yu.cs.intro.doomGame;

import java.util.*;

/**
 * A Room in the game, which contains both monsters as well as rewards for the
 * player that completes the room, which is defined as the player who kills the
 * last living monster in the room
 */
public class Room implements Comparable<Room> {
    private SortedSet<Monster> monsters;
    private Set<Weapon> weaponsWonUponCompletion;
    private Map<Weapon, Integer> ammoWonUponCompletion;
    private int healthWonUponCompletion;
    private String name;

    private SortedSet<Monster> killedMonsters = new TreeSet<>();

    /**
     *
     * @param monsters                 the monsters present in this room. This is a
     *                                 set of Monsters, NOT MonsterTypes - can have
     *                                 multiple monsters of a given type in a room
     * @param weaponsWonUponCompletion weapons a player gains when killing the last
     *                                 monster in this room
     * @param ammoWonUponCompletion    ammunition a player gains when killing the
     *                                 last monster in this room
     * @param healthWonUponCompletion  health a player gains when killing the last
     *                                 monster in this room
     * @param name                     the room's name
     */
    public Room(SortedSet<Monster> monsters, Set<Weapon> weaponsWonUponCompletion,
            Map<Weapon, Integer> ammoWonUponCompletion, int healthWonUponCompletion, String name) {
        this.monsters = monsters;
        this.weaponsWonUponCompletion = weaponsWonUponCompletion;
        this.ammoWonUponCompletion = ammoWonUponCompletion;
        this.healthWonUponCompletion = healthWonUponCompletion;
        this.name = name;

    }

    /**
     * Mark the given monster as being dead. Reduce the danger level of this room by
     * monster.getMonsterType().ordinal()+1
     * 
     * @param monster
     */
    protected void monsterKilled(Monster monster) {
        killedMonsters.add(monster);
    }

    /**
     * The danger level of the room is defined as the sum of the ordinal+1 value of
     * all living monsters, i.e. adding up (m.getMonsterType().ordinal() + 1) of all
     * the living monsters
     * 
     * @return the danger level of this room
     */
    public int getDangerLevel() {
        int danger = 0;
        for (Monster mon : getLiveMonsters()) {
            danger += mon.getMonsterType().ordinal() + 1;
        }
        return danger;
    }

    /**
     *
     * @return name of this room
     */
    public String getName() {
        return this.name;
    }

    /**
     * compares based on danger level
     * 
     * @param other
     * @return
     */
    @Override
    public int compareTo(Room other) {
        if (other == this) {
            return 0;
        } else if (this.getDangerLevel() == other.getDangerLevel()) {
            return 0;
        } else if (this.getDangerLevel() > other.getDangerLevel()) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * @return the set of weapons the player who completes the room is rewarded
     *         with. Make sure you don't allow the caller to modify the actual set!
     *         - Oze Note: Collections!
     * @see java.util.Collections#unmodifiableSet(Set)
     */
    public Set<Weapon> getWeaponsWonUponCompletion() {
        return Collections.unmodifiableSet(this.weaponsWonUponCompletion);
    }

    /**
     * @return The set of ammunition the player who completes the room is rewarded
     *         with. Make sure you don't allow the caller to modify the actual set!
     *         - Oze Note: Collections!
     * @see java.util.Collections#unmodifiableSet(Set)
     */
    public Map<Weapon, Integer> getAmmoWonUponCompletion() {
        return Collections.unmodifiableMap(this.ammoWonUponCompletion);
    }

    /**
     *
     * @return The amount of health this room
     */
    public int getHealthWonUponCompletion() {
        return this.healthWonUponCompletion;
    }

    /**
     * @return indicates if all the monsters in the room are dead
     */
    public boolean isCompleted() {
        if (!getLiveMonsters().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * @return The SortedSet of all monsters in the room
     * @see java.util.Collections#unmodifiableSet(Set) // Make collection? LOOK INTO
     *      THIS!
     */
    public SortedSet<Monster> getMonsters() {
        return Collections.unmodifiableSortedSet(this.monsters); // Does this add all monsters?
    }

    /**
     * @return the set of monsters in this room that are alive
     */
    public SortedSet<Monster> getLiveMonsters() {
        SortedSet<Monster> liveMonsters = new TreeSet<>();
        for (Monster mon : getMonsters()) {
            if (!mon.isDead()) {
                liveMonsters.add(mon);
            }
        }
        return liveMonsters;
    }

    /**
     * Every time a player enters a room, he loses health points based on the
     * monster in the room. The amount lost is the sum of the values of
     * playerHealthLostPerExposure of all the monsters in the room
     * 
     * @return the amount of health lost
     * @see MonsterType#playerHealthLostPerExposure - canKill only calculates how
     *      much health the player would lose if he kills the monster, but the
     *      player doesn't actually lose health until he kills a monster. This is
     *      why in the comments at @468 we said to make sure to reset th eplayer
     *      health in the protected canKill method. - no health is lost from just
     *      walking into a room. Think of it this way: you don't get hurt from just
     *      peeking into a room, rather you get huer when you actualyl fight a
     *      monster.
     */
    public int getPlayerHealthLostPerEncounter() {
        int healthLost = 0;
        for (Monster mon : getLiveMonsters()) {
            healthLost += mon.getMonsterType().playerHealthLostPerExposure;
        }
        return healthLost;
    }

    /**
     * @return the set of monsters in this room that are dead
     */
    public SortedSet<Monster> getDeadMonsters() { // where do we keep dead monsters?
        /*
         * SortedSet<Monster> deadMonsters = new TreeSet<>(); for (Monster mon :
         * getMonsters()) { if (mon.isDead()) { deadMonsters.add(mon); } } return
         * killedMonsters;
         */
        return killedMonsters;
    }
}