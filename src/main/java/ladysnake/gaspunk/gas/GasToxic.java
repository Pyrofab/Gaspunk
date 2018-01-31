package ladysnake.gaspunk.gas;

import ladysnake.gaspunk.gas.core.IBreathingHandler;
import ladysnake.gaspunk.gas.core.IGasType;
import ladysnake.gaspunk.gas.core.ILingeringGas;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class GasToxic extends Gas implements ILingeringGas {
    public GasToxic(IGasType type, int color) {
        super(type, color);
    }

    @Override
    public boolean isToxic() {
        return true;
    }

    @Override
    public void applyLingeringEffect(EntityLivingBase entity, int amplifier) {
        entity.attackEntityFrom(DamageSource.DROWN, 1);
    }

    @Override
    public void applyEffect(EntityLivingBase entity, IBreathingHandler handler, float concentration, boolean firstTick) {
        if (handler.getAirSupply() <= 0) {
            ILingeringGas.super.applyEffect(entity, handler, concentration, firstTick);
            entity.attackEntityFrom(DamageSource.DROWN, 2);
        }
    }
}
