package de.mixin;

import de.CondensedData;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityTrackerEntry.class, priority = 1500)
public class NetworkThrottlerMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (de.CondensedData.CONFIG == null) return;

        try {
            Object obj = this;
            EntityTrackerEntryAccessor accessor = (EntityTrackerEntryAccessor) obj;
            Entity entity = accessor.getTrackedEntity();

            if (entity == null || entity.getWorld() == null) return;

            int distance = de.CondensedData.CONFIG.throttleDistance;
            int interval = de.CondensedData.CONFIG.throttleInterval;

            Entity player = entity.getWorld().getClosestPlayer(entity, -1.0);

            if (player != null) {
                double distSq = entity.squaredDistanceTo(player);
                if (distSq > (distance * distance)) {
                    if (entity.getWorld().getTime() % interval != 0) {
                        ci.cancel();
                    }
                }
            }
        } catch (Throwable t) {
        }
    }
}