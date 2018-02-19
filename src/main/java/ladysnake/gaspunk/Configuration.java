package ladysnake.gaspunk;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = GasPunk.MOD_ID)
@Mod.EventBusSubscriber(modid = GasPunk.MOD_ID)
public class Configuration {

    @Config.Comment("Disables gas / smoke / vapor clouds checking for a clear path to entities and make them only check straight distance instead")
    public static boolean fastGas = false;

    @Config.Comment("Makes ash require smelting nether wart instead of rotten flesh")
    public static boolean alternativeAshRecipe = false;

    @Config.Comment({"If set to true, gaspunk will connect to an external server to detect if a player was awarded a custom grenade skin during the Modoff event",
    "The only sent information is the player's Universally Unique Identifier"})
    public static boolean allowModoffReward = false;

    public static Client client = new Client();

    public static class Client {
        @Config.Comment({"Enables the use of shaders to render the gas overlay", "won't do anything if renderGasOverlays is set to false"})
        public boolean useShaders = true;

        @Config.Comment({"Display a custom overlay when inside a gas cloud", "combine with useShaders for a dynamically generated overlay"})
        public boolean renderGasOverlays = false;

        @Config.Comment("The maximum amount of particles that should be displayed on the screen at the same time")
        public int maxParticles = 500;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(GasPunk.MOD_ID))
            ConfigManager.sync(GasPunk.MOD_ID, Config.Type.INSTANCE);
    }
}
