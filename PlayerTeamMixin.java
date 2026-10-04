package com.fakebalance.mixin;

import com.fakebalance.BalanceRewriter;
import com.fakebalance.FakeBalanceConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sidebar scoreboard lines are built by PlayerTeam.formatNameForTeam (team prefix + name + suffix).
 * We rewrite the finished line locally, only on this client.
 */
@Mixin(PlayerTeam.class)
public abstract class PlayerTeamMixin {
    @Inject(method = "formatNameForTeam", at = @At("RETURN"), cancellable = true)
    private static void fakebalance$rewrite(Team team, Component name, CallbackInfoReturnable<MutableComponent> cir) {
        if (!FakeBalanceConfig.INSTANCE.enabled) return;
        MutableComponent rewritten = BalanceRewriter.rewrite(cir.getReturnValue());
        if (rewritten != null) cir.setReturnValue(rewritten);
    }
}
