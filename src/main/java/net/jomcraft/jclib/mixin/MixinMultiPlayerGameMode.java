package net.jomcraft.jclib.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.jomcraft.jclib.ConfigManager;
import net.jomcraft.jclib.JCLib;
import net.jomcraft.jclib.compat.TinkerChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

@Mixin({ MultiPlayerGameMode.class })
public abstract class MixinMultiPlayerGameMode {

	@Inject(method = "startDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
	public boolean startDestroyBlock(BlockPos pos, Direction p_105271_, CallbackInfoReturnable<Boolean> ci) {
		if (ConfigManager.SERVER.swordPatch.get() && cancelSuccess(pos)) {
			ci.setReturnValue(false);
			ci.cancel();
			return false;
		}
		return true;
	}

	@Shadow
	public abstract void attack(Player p_105224_, Entity p_105225_);

	@SuppressWarnings("resource")
	public boolean cancelSuccess(BlockPos pos) {
		ClientLevel level = Minecraft.getInstance().level;
		LocalPlayer player = Minecraft.getInstance().player;
		BlockState state = level.getBlockState(pos);
		if (state.getCollisionShape(level, pos).isEmpty() || state.getDestroySpeed(level, pos) == 0.0F) {
			Item item = player.getMainHandItem().getItem();

			if (item instanceof SwordItem || (JCLib.tconstructLoaded && TinkerChecker.isInstanceOf(item))) {

				double reach = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
				reach = player.isCreative() ? reach : reach - 0.5f;
				Vec3 from = player.getEyePosition(1);
				Vec3 look = player.getViewVector(1);
				Vec3 to = from.add(look.x * reach, look.y * reach, look.z * reach);
				AABB aabb = player.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0D, 1.0D, 1.0D);
				EntityHitResult result = ProjectileUtil.getEntityHitResult(player, from, to, aabb, entity -> !entity.isSpectator() && entity.isAttackable(), reach * reach);

				if (result != null) {
					this.attack(player, result.getEntity());
				}
				return true;
			}
		}
		return false;
	}

	@Inject(method = "continueDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
	public boolean continueDestroyBlock(BlockPos pos, Direction p_105285_, CallbackInfoReturnable<Boolean> ci) {
		if (ConfigManager.SERVER.swordPatch.get() && cancelSuccess(pos)) {
			ci.setReturnValue(false);
			ci.cancel();
			return false;
		}

		return true;
	}

}