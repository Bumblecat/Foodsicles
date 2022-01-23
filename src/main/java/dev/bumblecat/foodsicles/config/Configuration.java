package dev.bumblecat.foodsicles.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Common CommonSettings = new Common(BUILDER);

    public static void initialize() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BUILDER.build());
    }

    /**
     *
     */
    public static class Common {

        public final ForgeConfigSpec.ConfigValue<ArrayList<String>> objects;
        public final ForgeConfigSpec.BooleanValue reverse;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common settings.").push("Common");

            objects = builder
                    .comment("List of objects to allow or block from the foodsicle.", "must be in format of: <domain>:<object>, for example: minecraft:apple")
                    .define("objects", new ArrayList<>());

            reverse = builder
                    .comment("If true, the list acts as blocklist. objects in the list will not be allowed in the sicle.")
                    .define("blocklist", true);

            builder.pop();
        }
    }
}
