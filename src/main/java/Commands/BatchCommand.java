package Commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Objects;
import java.util.Set;

public class BatchCommand {
    public static BlockPos Pos1 = null; // start 坐标
    public static BlockPos Pos2 = null; // end 坐标

    // 用于设置start坐标
    public static int SavePos1(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        //检查是否在主世界
        ServerWorld world = context.getSource().getWorld();
        if (world.getRegistryKey() == ServerWorld.OVERWORLD)
        {
            Pos1 = BlockPosArgumentType.getLoadedBlockPos(context, "pos1");
            context.getSource().sendMessage(Text.literal("已选取起始坐标").formatted(Formatting.GREEN));
        }
        else {
            context.getSource().sendMessage(Text.literal("请在主世界执行该命令").formatted(Formatting.RED));
        }
        return 1;
    }

    // 用于设置end坐标
    public static int SavePos2(CommandContext<ServerCommandSource> context, boolean AddForceLoad) throws CommandSyntaxException {
        //检查是否在主世界
        ServerWorld world = context.getSource().getWorld();
        if (world.getRegistryKey() != ServerWorld.OVERWORLD){
            context.getSource().sendMessage(Text.literal("请在主世界执行该命令").formatted(Formatting.RED));
            return 0;
        }

        if (Pos1 != null) {
            //已设置start坐标时
            Pos2 = BlockPosArgumentType.getLoadedBlockPos(context, "pos2");

            if (AddForceLoad) {
                AddForceLoad(context);
            }
            else {
                RemoveForceLoad(context);
            }
        }
        else {//未设置start坐标时
            context.getSource().sendError(Text.literal("请先设置 start 坐标").formatted(Formatting.RED));
        }
        return 1;
    }

    // 启动服务器时加载强制加载区块
    public static void LoadForceLoad(MinecraftServer server, Set<ChunkPos> forceLoadChunks) {
        // 获取主世界
        ServerWorld world = server.getWorld(ServerWorld.OVERWORLD);
        if (world == null) return;

        ServerChunkManager chunkManager = world.getChunkManager();
        forceLoadChunks.forEach(chunkPos -> chunkManager.setChunkForced(chunkPos, true));
    }

    // 强制加载区块
    public static void AddForceLoad(CommandContext<ServerCommandSource> context){
        ServerCommandSource source = context.getSource();
        BlockPos pos1 = Pos1;
        BlockPos pos2 = Pos2;

        //重置Pos
        Pos1 = null;
        Pos2 = null;

        //获取坐标
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        ServerWorld world = source.getWorld();
        ServerChunkManager chunkManager = world.getChunkManager();

        //获取区块坐标
        int chunkMinX = minX >> 4;
        int chunkMinZ = minZ >> 4;
        int chunkMaxX = maxX >> 4;
        int chunkMaxZ = maxZ >> 4;

        int totalChunks = 0;
        for (int x = chunkMinX; x <= chunkMaxX; x++) {
            for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
                chunkManager.setChunkForced(new ChunkPos(x, z), true);// 强制加载区块
                ChunkPosData.addChunk(new ChunkPos(x, z)); //保存区块坐标到数据文件中
                totalChunks++;
            }
        }
        source.sendMessage(Text.literal("已添加 " + totalChunks + " 个强制加载区块").formatted(Formatting.GREEN));
    }

    // 删除强制加载区块
    public static void RemoveForceLoad(CommandContext<ServerCommandSource> context) {

        ServerCommandSource source = context.getSource();
        BlockPos pos1 = Pos1;
        BlockPos pos2 = Pos2;

        //重置Pos
        Pos1 = null;
        Pos2 = null;

        //获取坐标
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        ServerWorld world = source.getWorld();
        ServerChunkManager chunkManager = world.getChunkManager();

        //获取区块坐标
        int chunkMinX = minX >> 4;
        int chunkMinZ = minZ >> 4;
        int chunkMaxX = maxX >> 4;
        int chunkMaxZ = maxZ >> 4;

        int totalChunks = 0;
        for (int x = chunkMinX; x <= chunkMaxX; x++) {
            for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
                chunkManager.setChunkForced(new ChunkPos(x, z), false);// 删除强制加载区块
                ChunkPosData.removeChunk(new ChunkPos(x, z)); // 从数据文件中移除区块坐标
                totalChunks++;
            }
        }
        source.sendMessage(Text.literal("已移除 " + totalChunks + " 个强制加载区块").formatted(Formatting.GREEN));
    }

    // 查看已保存的强制加载区块坐标
    public static int QueryForceLoadChunk(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String forceloadChunk = ChunkPosData.getLoadedChunks().toString(); // 加载已保存的区块坐标
        source.sendMessage(Text.literal("已添加的强加载区块坐标: " + forceloadChunk).formatted(Formatting.GREEN));
        return 1;
    }

    // 查看已保存的强制加载区块数量
    public static int QueryForceLoadNum(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        String forceloadNum = String.valueOf(ChunkPosData.getLoadedChunks().size()); // 加载已保存的区块数量
        source.sendMessage(Text.literal("已添加的强加载区块数量: " + forceloadNum).formatted(Formatting.GREEN));
        return 1;
    }

    // 查看玩家所处区块是否被强制加载
    public static int QueryPlayerChunk(CommandContext<ServerCommandSource> context) {
        BlockPos pos = Objects.requireNonNull(context.getSource().getEntity()).getBlockPos();

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        // 判断玩家所处区块是否被强制加载
        if (ChunkPosData.getLoadedChunks().contains(new ChunkPos(chunkX, chunkZ))) {
            context.getSource().sendMessage(Text.literal("该区块被强制加载").formatted(Formatting.GREEN));
        }else {
            context.getSource().sendMessage(Text.literal("该区块未被强制加载").formatted(Formatting.RED));
        }
        return 1;
    }
}
