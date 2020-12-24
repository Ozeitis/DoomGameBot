package edu.yu.cs.intro.doomGame;

/**
 * A specific monster
 */
public class Monster implements Comparable<Monster>{
    //WHAT PARAM GOES HERE FOR MONSTERTYPE?!
    private MonsterType type;
    /**
     * create a monster with no customr protectors; its protectors will be determined by its MonsterType
     * @param type the type of monster to create
     */
    protected Monster(MonsterType type) {
    }
    /**
     * create a monster with a custom protector, i.e. a different protector than the one specified in its MonsterType
     * @param type
     * @param customProtectedBy
     */
    public Monster(MonsterType type, MonsterType customProtectedBy){
    }

    /**
     * set the room that the Monster is located in
     * @param room
     */
    protected void setRoom(Room room){
    }

    public MonsterType getMonsterType(){
        return this.type;
    }

    /**
     * Attack this monster with the given weapon, firing the given number of rounds at it
     * @param weapon
     * @param rounds
     * @return indicates if the monster is dead after this attack
     * @throws IllegalArgumentException if the weapon is one that dones't hurt this monster, if the weapon is null, or if rounds < 1
     * @throws IllegalStateException if the monster is already dead
     */
    protected boolean attack(Weapon weapon, int rounds){
    }

    /**
     * @return is this monster dead?
     */
    public boolean isDead() {
    }

    /**
     * if this monster has its customProtectedBy set, return it. Otherwise, return the protectedBy of this monster's type
     * @return
     */
    public MonsterType getProtectedBy(){
    }

    /**
     * Used to sort a set of monsters into the order in which they must be killed, assuming they are in the same room.
     * If the parameter refers to this monster, return 0
     * If this monster is protected by the other monster's type, return 1
     * If this monster's type protects the other monster, return -1
     * otherwise, return -1
     * @param other the other monster
     * @return see above
     */
    @Override
    public int compareTo(Monster other) {
        return -1;
    }
}