package edu.yu.cs.intro.doomGame;

import java.util.*;

/**
 * Plays through a given game scenario. i.e. tries to kill all the monsters in
 * all the rooms and thus complete the game, using the given set of players
 */
public class GameBot {
    private SortedSet<Room> rooms;
    private SortedSet<Player> players;

    private Set<Room> compRooms = new HashSet<>();

    /**
     * Create a new "GameBot", i.e. a program that automatically "plays the game"
     * 
     * @param rooms   the set of rooms in this game
     * @param players the set of players the bot can use to try to complete all
     *                rooms
     */
    public GameBot(SortedSet<Room> rooms, SortedSet<Player> players) {
        this.rooms = rooms;
        this.players = players;
    }

    /**
     * Try to complete killing all monsters in all rooms using the given set of
     * players. It could take multiple passes through the set of rooms to complete
     * the task of killing every monster in every room. This method should call
     * #passThroughRooms in some loop that tracks whether all the rooms have been
     * completed OR we have reached a point at which no progress can be made. If we
     * are "stuck", i.e. we haven't completed all rooms but calls to
     * #passThroughRooms are no longer increasing the number of rooms that have been
     * completed, return false to indicate that we can't complete the game. As long
     * as the number of completed rooms continues to rise, keep calling
     * #passThroughRooms.
     *
     * Throughout our attempt/logic to play the game, we rely on and take advantage
     * of the fact that Room, Monster, and Player all implement Comparable, and the
     * sets we work with are all SortedSets
     *
     * @return true if all rooms were completed, false if not
     */
    public boolean play() {
        Map<Room, Integer> roomToInitDL = new HashMap<>();

        for (Room r : getAllRooms()) {
            roomToInitDL.put(r, r.getDangerLevel());
        }

        while (!getIncompletedRooms().isEmpty()) {
            passThroughRooms();
            if (!updateroomToInitDL(roomToInitDL)) {
                return false;
            }
        }

        return true;
    }

    private boolean updateroomToInitDL(Map<Room, Integer> roomToInitDL) {
        boolean updated = false;

        for (Room r : roomToInitDL.keySet()) {
            if (r.getDangerLevel() != roomToInitDL.get(r)) {
                roomToInitDL.put(r, r.getDangerLevel());
                updated = true;
            }
        }

        return updated;
    }

    /**
     * Pass through the rooms, killing any monsters that can be killed, and thus
     * attempt to complete the rooms
     * 
     * @return the set of rooms that were completed in this pass
     */
    protected Set<Room> passThroughRooms() {
        Set<Room> completedRooms = new HashSet<>();
        for (Player p : getLivePlayers()) {
            for (Room r : getAllRooms()) { // What about uncopmplteed rooms?
                for (Monster mon : r.getLiveMonsters()) {
                    System.out.println("passThroughRooms mon: " + mon.getMonsterType());
                    System.out.println("mon is alive: " + r.getLiveMonsters().contains(mon));
                    if (r.getLiveMonsters().contains(mon) && canKill(p, mon, r)) {
                        killMonster(p, r, mon);
                    }
                }
                if (r.getLiveMonsters().isEmpty()) {
                    reapCompletionRewards(p, r);
                    completedRooms.add(r);
                    compRooms.add(r);
                }
            }
        }
        return completedRooms;
    }

    // for every room that is not completed,
    // for every living monster in that room
    // See if any of your players can kill the monster. If so, have the capable
    // player kill it.
    // The player that causes the room to be completed by killing a monster reaps
    // the rewards for completing that room.
    // Return the set of completed rooms.

    /**
     * give the player the weapons, ammunition, and health that come from completing
     * the given room
     * 
     * @param player
     * @param room
     */
    protected void reapCompletionRewards(Player player, Room room) {
        Map<Weapon, Integer> ammo = room.getAmmoWonUponCompletion();
        int health = room.getHealthWonUponCompletion();
        Set<Weapon> weapons = room.getWeaponsWonUponCompletion();

        player.changeHealth(health);
        for (Weapon w : ammo.keySet()) {
            player.addAmmunition(w, ammo.get(w));
        }
        for (Weapon w : weapons) {
            player.addWeapon(w);
        }
    }

    /**
     * Have the given player kill the given monster in the given room. Assume that
     * #canKill was already called to confirm that player's ability to kill the
     * monster
     * 
     * @param player
     * @param room
     * @param monsterToKill
     */
    protected void killMonster(Player player, Room room, Monster monsterToKill) {
        // Call getAllProtectorsInRoom to get a sorted set of all the monster's
        // protectors in this room
        SortedSet<Monster> protectors = getAllProtectorsInRoom(monsterToKill, room);
        // Player must kill the protectors before it can kill the monster, so kill all
        // the protectors

        // first via a recursive call to killMonster on each one.
        for (Monster mon : protectors) {
            killMonster(player, room, mon);
        }
        // Reduce the player's health by the amount given by
        // room.getPlayerHealthLostPerEncounter().
        player.changeAmmunitionRoundsForWeapon(monsterToKill.getMonsterType().weaponNeededToKill,
                (monsterToKill.getMonsterType().ammunitionCountNeededToKill * -1)); // Makes it negative number?

        player.changeHealth((room.getPlayerHealthLostPerEncounter() * -1));
        // DO I REMOVE AMMO!?!??!?!?!??!?!?!?!

        // Attack (and thus kill) the monster with the kind of weapon, and amount of
        // ammunition, needed to kill it.
        monsterToKill.attack(monsterToKill.getMonsterType().weaponNeededToKill,
                monsterToKill.getMonsterType().ammunitionCountNeededToKill);
    }

    /**
     * @return a set of all the rooms that have been completed
     */
    public Set<Room> getCompletedRooms() {
        /*
         * Set<Room> completedRooms = new HashSet<>(); for (Room r : getAllRooms()) { if
         * (r.getLiveMonsters().isEmpty()) { completedRooms.add(r); } } return
         * completedRooms;
         */
        return compRooms;
    }

    private SortedSet<Room> getIncompletedRooms() {
        SortedSet<Room> incomplete = new TreeSet<>();
        for (Room r : getAllRooms()) {
            if (!r.getLiveMonsters().isEmpty()) {
                incomplete.add(r);
            }
        }
        return incomplete;
    }

    /**
     * @return an unmodifiable collection of all the rooms in the came
     * @see java.util.Collections#unmodifiableSortedSet(SortedSet)
     */
    public SortedSet<Room> getAllRooms() {
        return this.rooms;
    }

    /**
     * @return a sorted set of all the live players in the game
     */
    protected SortedSet<Player> getLivePlayers() {
        SortedSet<Player> alivePlayers = new TreeSet<>();
        for (Player i : this.players) {
            if (!i.isDead()) {
                alivePlayers.add(i);
            }
        }
        return alivePlayers;
    }

    /**
     * @param weapon
     * @param ammunition
     * @return a sorted set of all the players that have the given wepoan with the
     *         given amount of ammunition for it
     */
    protected SortedSet<Player> getLivePlayersWithWeaponAndAmmunition(Weapon weapon, int ammunition) {
        SortedSet<Player> playersWithWandA = new TreeSet<>();
        for (Player p : getLivePlayers()) {
            if (p.hasWeapon(weapon) && p.getAmmunitionRoundsForWeapon(weapon) >= ammunition) {
                playersWithWandA.add(p);
            }
        }
        return playersWithWandA;
    }

    /**
     * Get the set of all monsters that would need to be killed first before you
     * could kill this one. Remember that a protector may itself be protected by
     * other monsters, so you will have to recursively check for protectors
     * 
     * @param monster
     * @param room
     * @return
     */
    protected static SortedSet<Monster> getAllProtectorsInRoom(Monster monster, Room room) {
        return getAllProtectorsInRoom(new TreeSet<Monster>(), monster, room); // this is a hint about how to handle
                                                                              // canKill as well
    }

    private static SortedSet<Monster> getAllProtectorsInRoom(SortedSet<Monster> protectors, Monster monster,
            Room room) {
        if (monster.getProtectedBy() == null) {
            return protectors;
        }
        for (Monster mon : room.getLiveMonsters()) {
            if (monster.getProtectedBy().ordinal() == mon.getMonsterType().ordinal()) {
                protectors.add(mon);
                getAllProtectorsInRoom(protectors, mon, room);
            }
        }
        return protectors;
    }

    /**
     * Can the given player kill the given monster in the given room?
     *
     * @param player
     * @param monster
     * @param room
     * @return
     * @throws IllegalArgumentException if the monster is not located in the room or
     *                                  is dead
     */
    protected static boolean canKill(Player player, Monster monster, Room room) {
        int playerHealth = player.getHealth();
        Set<Monster> alreadyMarkedByCanKill = new HashSet<>();
        SortedMap<Weapon, Integer> roundsUsedPerWeapon = new TreeMap<>();

        if (!room.getLiveMonsters().contains(monster)) {
            System.out.println("monster not alive: " + monster.getMonsterType());
            throw new IllegalArgumentException();
        }

        // Going into the room exposes the player to all the monsters in the room. If
        // the player's health is
        // not > room.getPlayerHealthLostPerEncounter(), you can return immediately.
        if (playerHealth <= room.getPlayerHealthLostPerEncounter()) {
            return false;
        }

        // Call the private canKill method, to determine if this player can kill this
        // monster.
        boolean canKill = canKill(player, monster, room, roundsUsedPerWeapon, alreadyMarkedByCanKill);

        // Before returning from this method, reset the player's health to what it was
        // before you called the private canKill
        player.setHealth(playerHealth);

        return canKill;
    }

    /**
     *
     * @param player
     * @param monster
     * @param room
     * @param roundsUsedPerWeapon
     * @return
     */
    private static boolean canKill(Player player, Monster monster, Room room,
            SortedMap<Weapon, Integer> roundsUsedPerWeapon, Set<Monster> alreadyMarkedByCanKill) {

        if (!room.getLiveMonsters().contains(monster)) {
            System.out.println("monster not alive (inside private canKill): " + monster.getMonsterType());
            throw new IllegalArgumentException();
        }

        // Check what weapon is needed to kill the monster, see if player has it. If
        // not, return false.
        if (!player.hasWeapon(monster.getMonsterType().weaponNeededToKill)) {
            /* TODO -- CHECK IF WEAPON IS GREATER THEN */
            /* TODO -- REFUND PLAYER AMMO */
            System.out.println("player does not have correct weapon, returing false");
            return false;
        }
        // Check what protects the monster. If the monster is protected, the player can
        // only kill this monster if it can kill its protectors as well.
        // Make recursive calls to canKill to see if player can kill its protectors.
        // Be sure to remove all members of alreadyMarkedByCanKill from the set of
        // protectors before you recursively call canKill on the protectors.
        if (!getAllProtectorsInRoom(monster, room).isEmpty()) {
            for (Monster mon : getAllProtectorsInRoom(monster, room)) {
                System.out.println("monster type: " + monster.getMonsterType());
                System.out.println("protector type: " + mon.getMonsterType());

                if (alreadyMarkedByCanKill.contains(mon)) {
                    continue;
                }

                if (!canKill(player, mon, room, roundsUsedPerWeapon, alreadyMarkedByCanKill)) {
                    System.out.println("cannot kill protectors, returing false");
                    return false;
                }
            }
        }
        // If all the recursive calls to canKill on all the protectors returned true:
        // Check what amount of ammunition is needed to kill the monster, and see if
        // player has it after we subtract
        // from his total ammunition the number stored in roundsUsedPerWeapon for the
        // given weapon, if any.
        System.out.println("weapon being used: " + monster.getMonsterType().weaponNeededToKill);
        System.out.println("amount of ammo player has: "
                + player.getAmmunitionRoundsForWeapon(monster.getMonsterType().weaponNeededToKill));
        System.out.println("ammo needed to kill this monster: " + monster.getMonsterType().ammunitionCountNeededToKill);
        System.out.println("monster type: " + monster.getMonsterType());
        if (player.getAmmunitionRoundsForWeapon(
                monster.getMonsterType().weaponNeededToKill) < monster.getMonsterType().ammunitionCountNeededToKill) {
            System.out.println("player does not have enough ammo, returing false");
            return false;
        }

        player.changeAmmunitionRoundsForWeapon(monster.getMonsterType().weaponNeededToKill,
                monster.getMonsterType().ammunitionCountNeededToKill * -1);

        // add how much ammunition will be used up to kill this monster to
        // roundsUsedPerWeapon
        roundsUsedPerWeapon.put(monster.getMonsterType().weaponNeededToKill,
                monster.getMonsterType().ammunitionCountNeededToKill);

        // Add up the playerHealthLostPerExposure of all the live monsters, and see if
        // when that is subtracted from the player if his health is still > 0. If not,
        // return false.
        if (player.getHealth() - room.getPlayerHealthLostPerEncounter() <= 0) {
            System.out.println("player does not have enough health, returing false");
            return false;
        }

        // If health is still > 0, subtract the above total from the player's health
        player.changeHealth(player.getHealth() - room.getPlayerHealthLostPerEncounter());

        // add this monster to alreadyMarkedByCanKill, and return true.
        alreadyMarkedByCanKill.add(monster);
        return true;
    }

}