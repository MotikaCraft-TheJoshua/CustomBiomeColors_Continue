package me.arthed.custombiomecolors.nms;

import me.arthed.custombiomecolors.utils.ReflectionUtils;
import me.arthed.custombiomecolors.utils.objects.BiomeColors;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

public class NmsBiome {

    private final Biome biomeBase;

    public NmsBiome(Biome biomeBase) {
        this.biomeBase = biomeBase;
    }

    public Biome getBiomeBase() {
        return this.biomeBase;
    }

    public BiomeColors getBiomeColors() {
        try {
            BiomeSpecialEffects biomeFog = (BiomeSpecialEffects) ReflectionUtils.getPrivateObject(this.biomeBase, "specialEffects");
            assert biomeFog != null;
            return new BiomeColors()
                .setGrassColor(ReflectionUtils.getPrivateOptionalInteger(biomeFog, "grassColorOverride"))
                .setFoliageColor(ReflectionUtils.getPrivateOptionalInteger(biomeFog, "foliageColorOverride"))
                .setWaterColor(ReflectionUtils.getPrivateInteger(biomeFog, "waterColor"))
                .setWaterFogColor(ReflectionUtils.getPrivateInteger(biomeFog, "waterFogColor"))
                .setSkyColor(ReflectionUtils.getPrivateInteger(biomeFog, "skyColor"))
                .setFogColor(ReflectionUtils.getPrivateInteger(biomeFog, "fogColor"));
        } catch (NoSuchFieldException exception) {
            System.err.println("Field not found in BiomeSpecialEffects for 1.21.1: " + exception.getMessage() + ". Possible fields: grassColorOverride, foliageColorOverride, etc.");
            exception.printStackTrace();
        }
        return null;
    }

    public NmsBiome cloneWithDifferentColors(NmsServer nmsServer, BiomeKey newBiomeKey, BiomeColors biomeColors) {
        ResourceKey<Biome> customBiomeKey = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(newBiomeKey.key, newBiomeKey.value));
        Biome.BiomeBuilder customBiomeBuilder = new Biome.BiomeBuilder()
            .generationSettings(biomeBase.getGenerationSettings())
            .mobSpawnSettings(biomeBase.getMobSettings())
            .hasPrecipitation(biomeBase.hasPrecipitation())
            .temperature(biomeBase.climateSettings.temperature())
            .downfall(biomeBase.climateSettings.downfall())
            .temperatureAdjustment(biomeBase.climateSettings.temperatureModifier());

        BiomeSpecialEffects.Builder customBiomeColors = new BiomeSpecialEffects.Builder();
        customBiomeColors.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
            .waterColor(biomeColors.getWaterColor())
            .waterFogColor(biomeColors.getWaterFogColor())
            .skyColor(biomeColors.getSkyColor())
            .fogColor(biomeColors.getFogColor());
        if (biomeColors.getGrassColor() != 0) {
            customBiomeColors.grassColorOverride(biomeColors.getGrassColor());
        }
        if (biomeColors.getFoliageColor() != 0) {
            customBiomeColors.foliageColorOverride(biomeColors.getFoliageColor());
        }

        customBiomeBuilder.specialEffects(customBiomeColors.build());
        Biome customBiome = customBiomeBuilder.build();
        nmsServer.registerBiome(customBiome, customBiomeKey);

        return new NmsBiome(customBiome);
    }

    public boolean equals(Object object) {
        return object instanceof NmsBiome && ((NmsBiome) object).getBiomeBase().equals(this.biomeBase);
    }
}
