package Commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create(); // 格式化 JSON
    private static final Path CONFIG_PATH = Paths.get("config/batchforceload/config.json");

    // 全局配置
    public int PermissionLevel = 2; // 权限等级

    public static ModConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                // 读取文件并解析为 ModConfig 对象
                return GSON.fromJson(new FileReader(CONFIG_PATH.toFile()), ModConfig.class);
            } else {
                ModConfig config = new ModConfig(); // 如果文件不存在，使用默认值创建配置
                config.save();
                return config;
            }
        } catch (IOException e) {
            System.err.println("Failed to load forceload config file.");
            return new ModConfig(); // 发生错误时返回默认配置
        }
    }

    // 保存配置文件
    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent()); // 如果不存在，创建父目录
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(this, writer); // 将当前对象序列化为 JSON 并写入文件
            }
        } catch (IOException e) {
            System.err.println("Failed to save forceload config file.");
        }
    }
}
