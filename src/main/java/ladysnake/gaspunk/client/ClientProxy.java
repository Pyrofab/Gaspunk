package ladysnake.gaspunk.client;

import ladylib.LadyLib;
import ladysnake.gaspunk.CommonProxy;
import ladysnake.gaspunk.GasPunk;
import ladysnake.gaspunk.api.IGasParticleType;
import ladysnake.gaspunk.api.customization.GrenadeSkins;
import ladysnake.gaspunk.client.particle.ParticleGasSmoke;
import ladysnake.gaspunk.client.render.entity.LayerBelt;
import ladysnake.gaspunk.init.ModItems;
import ladysnake.gaspunk.item.ItemGasTube;
import ladysnake.gaspunk.util.SpecialRewardChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    private int particleCount = 0;
    private Property selectedSkin;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void postInit() {
        super.postInit();
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(((stack, tintIndex) -> tintIndex == 0
                ? ItemGasTube.getContainedGas(stack).getBottleColor()
                : Color.WHITE.getRGB()), ModItems.GAS_TUBE, ModItems.GRENADE);
        if (Loader.isModLoaded("baubles"))
            Minecraft.getMinecraft().getRenderManager().getSkinMap().forEach((s, render) -> render.addLayer(new LayerBelt()));
    }

    @Override
    public void makeSmoke(World world, double x, double y, double z, int color, int amount, int radX, int radY, IGasParticleType texture) {
        if (!world.isRemote) return;
        float b = (color & 0xFF) / 255F;
        float g = (color >> 8 & 0xFF) / 255F;
        float r = (color >> 16 & 0xFF) / 255F;
        float a = (color >> 24 & 0xFF) / 255F;
        // no need to spawn invisible particles
        if (a == 0) return;

        for (int i = 0; i < amount; i++) {
            particleCount += world.rand.nextInt(3);
            if (particleCount % (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 1 : 2 * Minecraft.getMinecraft().gameSettings.particleSetting) == 0) {
                double posX = x + world.rand.nextGaussian() * radX % radX;
                double posY = y + world.rand.nextGaussian() * radY % radY;
                double posZ = z + world.rand.nextGaussian() * radX % radX;
                ParticleGasSmoke particle = new ParticleGasSmoke(world, posX, posY, posZ, r, g, b, a, (float) (55 + 20 * world.rand.nextGaussian()));
                particle.setTexture(texture.getParticleTexture());
                LadyLib.getParticleManager().addParticle(particle);
            }
        }
    }

    @Override
    public void onSpecialRewardsRetrieved() {
        UUID profileID = Minecraft.getMinecraft().getSession().getProfile().getId();
        // If the profile has been rewarded with one or more custom skins,
        // add the config option to choose which one will appear on new grenades
        if (SpecialRewardChecker.isSpecialPerson(profileID)) {
            List<GrenadeSkins> awardedSkins = SpecialRewardChecker.getRewards(profileID);
            // list of valid skins this player can have
            String[] skinNames = awardedSkins.stream()
                    .map(GrenadeSkins::getDisplayName)
                    .toArray(String[]::new);
            // this guy has a special skin, don't hide it by default
            String defaultSkin = awardedSkins.stream()
                    .filter(g -> g != GrenadeSkins.NONE)
                    .findAny()
                    .orElse(GrenadeSkins.NONE).getDisplayName();
            selectedSkin = config.get(
                    "general",
                    "specialGrenadeSkin",
                    defaultSkin,
                    "The kind of diffuser you will craft (grenade skins are determined by the diffuser used)",
                    skinNames
            );
        }
    }

    @Override
    public GrenadeSkins getSelectedSkin() {
        return getGrenadeSkinProperty()
                .map(Property::getString)
                .map(GrenadeSkins::fromDisplayName)
                .orElse(GrenadeSkins.NONE);
    }

    /**
     * @return an optional containing the config property corresponding to the selected grenade skin,
     * or an empty optional if the user doesn't have any alternative skin
     */
    public static Optional<Property> getGrenadeSkinProperty() {
        if (GasPunk.proxy instanceof ClientProxy)
            return Optional.ofNullable(((ClientProxy) GasPunk.proxy).selectedSkin);
        return Optional.empty();
    }
}
