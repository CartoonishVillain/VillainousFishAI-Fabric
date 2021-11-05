package com.cartoonishvillain.villainousfishai.mixin;

import com.cartoonishvillain.villainousfishai.FishAvoidGoal;
import com.cartoonishvillain.villainousfishai.VillainousFishAI;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class FishAIMixin {




	@Inject(at = @At("HEAD"), method = "tick()V")
	private void tick(CallbackInfo info) {
		Mob livingEntity = ((Mob) (Object) this);
		if(livingEntity instanceof AbstractFish && !livingEntity.level.isClientSide && livingEntity.tickCount == 2){
			GoalSelector goalSelector = ((GoalAccessor) livingEntity).accessGoalSelector();
			goalSelector.addGoal(3, new FishAvoidGoal<ItemEntity>(livingEntity, ItemEntity.class, 6.0f, 1.4f, VillainousFishAI::avoidsinking));
			goalSelector.addGoal(3, new FishAvoidGoal<Projectile>(livingEntity, Projectile.class, 6.0f, 1.5f, VillainousFishAI::avoidsinking));
			goalSelector.addGoal(3, new FishAvoidGoal<PrimedTnt>(livingEntity, PrimedTnt.class, 6.0f, 1.8f, VillainousFishAI::avoidsinking));
		}

	}
}


