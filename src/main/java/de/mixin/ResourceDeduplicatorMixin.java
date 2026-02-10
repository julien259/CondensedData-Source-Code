package de.mixin;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(Identifier.class)
public class ResourceDeduplicatorMixin {

    @Unique
    private static final Map<String, Identifier> POOL = new ConcurrentHashMap<>(2048);

    @Inject(method = "of(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"), cancellable = true)
    private static void getPooledInstance(String namespace, String path, CallbackInfoReturnable<Identifier> cir) {
        String key = namespace + ":" + path;
        Identifier existing = POOL.get(key);

        if (existing != null) {
            cir.setReturnValue(existing);
        }
    }

    @Inject(method = "<init>(Ljava/lang/String;Ljava/lang/String;)V", at = @At("RETURN"))
    private void onInit(String namespace, String path, CallbackInfo ci) {
        String key = namespace + ":" + path;
        POOL.putIfAbsent(key, (Identifier)(Object)this);
    }
}