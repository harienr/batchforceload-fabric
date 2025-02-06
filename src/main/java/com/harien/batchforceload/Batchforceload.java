package com.harien.batchforceload;

import net.fabricmc.api.ModInitializer;
import Commands.BatchCommand;
import Commands.ChunkPosData;
import Commands.ModConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;

public class Batchforceload implements ModInitializer {

    public static ModConfig CONFIG;

    @Override
    public void onInitialize() {

        ChunkPosData.load(); // 加载已保存的强制加载区块坐标
        CONFIG = ModConfig.load(); // 加载全局配置

        //在服务器启动时加载已保存的强制加载区块坐标
        ServerLifecycleEvents.SERVER_STARTED.register(server -> BatchCommand.LoadForceLoad(server, ChunkPosData.getLoadedChunks()));

        //注册指令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            //用于设置强加载区块
            dispatcher.register(CommandManager.literal("batchforceload")
                    .requires(source -> source.hasPermissionLevel(CONFIG.PermissionLevel)) // 权限检查

                    .then(CommandManager.literal("add")//设置Pos1坐标
                            .then(CommandManager.literal("start")
                                    .then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos())
                                            .executes(BatchCommand::SavePos1)
                                    )
                            )
                            .then(CommandManager.literal("end")
                                    .then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos())
                                            .executes(context -> BatchCommand.SavePos2(context, true))
                                    )
                            )
                    )
            );

            //用于删除强加载区块
            dispatcher.register(CommandManager.literal("batchforceload")
                    .requires(source -> source.hasPermissionLevel(CONFIG.PermissionLevel)) // 权限检查

                    .then(CommandManager.literal("remove")//设置Pos1坐标
                            .then(CommandManager.literal("start")
                                    .then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos())
                                            .executes(BatchCommand::SavePos1)
                                    )
                            )
                            .then(CommandManager.literal("end")
                                    .then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos())
                                            .executes(context -> BatchCommand.SavePos2(context, false))
                                    )
                            )
                    )
            );

            //用于查看已保存的强制加载区块坐标
            dispatcher.register(CommandManager.literal("batchforceload")
                    .requires(source -> source.hasPermissionLevel(CONFIG.PermissionLevel))
                    .then(CommandManager.literal("query")
                            .then(CommandManager.literal("chunk")//查看强制加载区块坐标
                                    .executes(BatchCommand::QueryForceLoadChunk)
                            )
                            .then(CommandManager.literal("num")//查看强制加载区块数量
                                    .executes(BatchCommand::QueryForceLoadNum)
                            )
                            .then(CommandManager.literal("ifforceload")//查看玩家所在区块是否被强制加载
                                    .executes(BatchCommand::QueryPlayerChunk)
                            )
                    )
            );
        });
    }
}
