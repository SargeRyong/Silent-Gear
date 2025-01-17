package net.silentchaos512.gear.api.data.trait;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.data.DataGenerators;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public abstract class TraitsProviderBase implements DataProvider {
    protected final DataGenerator generator;
    protected final String modId;

    public TraitsProviderBase(DataGenerator generator, String modId) {
        this.generator = generator;
        this.modId = modId;
    }

    @SuppressWarnings({"OverlyLongMethod", "MethodMayBeStatic"})
    public abstract Collection<TraitBuilder> getTraits();

    protected static TraitBuilder bonusDropsTraits(DataResource<ITrait> trait, int maxLevel, float chance, float multiplier, Ingredient ingredient) {
        return new TraitBuilder(trait, maxLevel, ApiConst.BONUS_DROPS_TRAIT_ID)
                .extraData(json -> {
                    json.addProperty("base_chance", chance);
                    json.addProperty("bonus_multiplier", multiplier);
                    json.add("ingredient", ingredient.toJson());
                });
    }

    protected static TraitBuilder cancelEffectsTrait(DataResource<ITrait> trait, MobEffect... effects) {
        JsonArray array = new JsonArray();
        for (MobEffect effect : effects) {
            array.add(Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(effect)).toString());
        }

        return new TraitBuilder(trait, 1, ApiConst.CANCEL_EFFECTS_TRAIT_ID)
                .extraData(json -> {
                    json.add("effects", array);
                });
    }

    protected static TraitBuilder damageTypeTrait(DataResource<ITrait> trait, int maxLevel, String damageType, int damageBonus) {
        return new TraitBuilder(trait, maxLevel, ApiConst.DAMAGE_TYPE_TRAIT_ID)
                .extraData(json -> {
                    json.addProperty("damage_type", damageType);
                    json.addProperty("damage_bonus", damageBonus);
                });
    }

    protected static TraitBuilder selfRepairTrait(DataResource<ITrait> trait, int maxLevel, float activationChance, int repairAmount) {
        return new TraitBuilder(trait, maxLevel, ApiConst.SELF_REPAIR_TRAIT_ID)
                .extraData(json -> {
                    json.addProperty("activation_chance", activationChance);
                    json.addProperty("repair_amount", repairAmount);
                });
    }

    @Override
    public @NotNull String getName() {
        return "Silent Gear Traits: " + modId;
    }

    @Override
    public void run(@NotNull CachedOutput cache) {
        Path outputFolder = this.generator.getOutputFolder();
        Set<ResourceLocation> entries = Sets.newHashSet();

        //noinspection OverlyLongLambda
        getTraits().forEach(builder -> {
            if (entries.contains(builder.getTraitId())) {
                throw new IllegalStateException("Duplicate trait: " + builder.getTraitId());
            }

            entries.add(builder.getTraitId());
            Path path = outputFolder.resolve(String.format("data/%s/silentgear_traits/%s.json", builder.getTraitId().getNamespace(), builder.getTraitId().getPath()));
            TraitsProviderBase.trySaveStable(cache, builder, path);
        });
    }

    private static void trySaveStable(CachedOutput cache, TraitBuilder builder, Path path) {
        try {
            DataGenerators.saveStable(cache, builder.serialize(), path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
