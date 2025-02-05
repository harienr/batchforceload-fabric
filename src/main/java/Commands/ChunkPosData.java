package Commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.math.ChunkPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ChunkPosData {
    private static final Gson GSON = new Gson(); // 用于 JSON 序列化和反序列化
    private static final Path SAVE_PATH = Paths.get("config/batchforceload/chunkpos.json");
    private static final Set<ChunkPos> loadedChunks = new HashSet<>(); // 存储区块坐标

    public static void load() {
        try {
            if (Files.exists(SAVE_PATH)){
                String json = Files.readString(SAVE_PATH);
                Set<ChunkPos> savedChunks = GSON.fromJson(json, new TypeToken<Set<ChunkPos>>() {}.getType()); // 反序列化为 Set<ChunkPos>
                loadedChunks.addAll(savedChunks);
            }
        } catch (IOException e) {
            System.out.println("Failed to load forceload data.");
        }
    }

    // 保存当前强制加载的区块坐标
    public static void save() {
        try {
            String json = GSON.toJson(loadedChunks); // 将 Set<ChunkPos> 序列化为 JSON 字符串
            Files.createDirectories(SAVE_PATH.getParent()); // 创建父目录（如果不存在）
            Files.writeString(SAVE_PATH, json);
        } catch (IOException e) {
            System.out.println("Failed to save forceload data.");
        }
    }

    // 添加区块坐标
    public static void addChunk(ChunkPos pos) {
        loadedChunks.add(pos);
        save();
    }

    // 移除区块坐标
    public static void removeChunk(ChunkPos pos) {
        loadedChunks.remove(pos);
        save();
    }

    // 获取所有已强制加载的区块
    public static Set<ChunkPos> getLoadedChunks() {
        return new HashSet<>(loadedChunks); // 返回副本
    }
}
