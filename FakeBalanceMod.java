package com.fakebalance;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeBalanceMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("fakebalance");

    @Override
    public void onInitializeClient() {
        FakeBalanceConfig.load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("fakebal")
                // /fakebal  -> toggle on/off
                .executes(ctx -> {
                    FakeBalanceConfig c = FakeBalanceConfig.INSTANCE;
                    c.enabled = !c.enabled;
                    FakeBalanceConfig.save();
                    ctx.getSource().sendFeedback(Component.literal("Fake balance display: " + (c.enabled ? "ON" : "OFF")));
                    return 1;
                })
                // /fakebal set 16B
                .then(ClientCommandManager.literal("set")
                    .then(ClientCommandManager.argument("value", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            FakeBalanceConfig.INSTANCE.displayedBalance = StringArgumentType.getString(ctx, "value");
                            FakeBalanceConfig.save();
                            ctx.getSource().sendFeedback(Component.literal("Displayed balance set to: " + FakeBalanceConfig.INSTANCE.displayedBalance));
                            return 1;
                        })))
                // /fakebal label money|balance   (regex for the text before the number on your scoreboard)
                .then(ClientCommandManager.literal("label")
                    .then(ClientCommandManager.argument("regex", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            FakeBalanceConfig.INSTANCE.labelRegex = StringArgumentType.getString(ctx, "regex");
                            FakeBalanceConfig.INSTANCE.invalidate();
                            FakeBalanceConfig.save();
                            ctx.getSource().sendFeedback(Component.literal("Label regex set to: " + FakeBalanceConfig.INSTANCE.labelRegex));
                            return 1;
                        })))
            ));
    }
}
