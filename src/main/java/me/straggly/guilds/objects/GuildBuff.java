package me.straggly.guilds.objects;

import me.straggly.guilds.API;
import org.bukkit.potion.PotionEffectType;

public enum GuildBuff
{
    // TODO: Faster attack speed

    SPEED_BUFF(PotionEffectType.SPEED, 1000000),
    STRENGTH_BUFF(PotionEffectType.INCREASE_DAMAGE, 1000000),
    REGENERATION_BUFF(PotionEffectType.REGENERATION, 1000000),
    MINING_SPEED_BUFF(PotionEffectType.FAST_DIGGING, 1000000),
    RESISTANCE_BUFF(PotionEffectType.DAMAGE_RESISTANCE, 1000000),
    HEALTH_BOOST_BUFF(PotionEffectType.HEALTH_BOOST, 1000000),
    DOLPHINS_GRACE_BUFF(PotionEffectType.DOLPHINS_GRACE, 1000000),
    INTEREST_RATE(null, 1000000)
    ;

    private final PotionEffectType type;
    private final int price;
    GuildBuff(PotionEffectType type, int price){
        this.type = type;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public PotionEffectType getType() {
        return type;
    }

    @Override
    public String toString() {
        return API.formatEnum(name());
    }
}
