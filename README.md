# Batchforceload Fabric 模组文档
> 基于Minecraft 1.20.1 / fabric 0.15.11开发，用于服务器管理
## 模组功能
优化原版`/forceload`系列指令（主世界），管理主世界的强制加载区块，允许玩家在世界中批量框选强制加载区块，批量移除强制加载区块，查找强制加载区块。

---
## 指令列表
### 添加强制加载区块
玩家可以在游戏中先后使用 `/batchforceload add pos1 ~ ~ ~`和`/batchforceload add pos2 ~ ~ ~`指令，选取两个区块的坐标，把这两个区块所围成的矩形内的所有区块设置为强制加载区块。
| 调用功能 | 三级参数 | 四级参数 | 作用 |
| --- | --- | --- | --- |
| `/batchforceload add` | `pos1` | `<pos>`(三维坐标) | 选取初始区块坐标 |
| `/batchforceload add` | `pos2` | `<pos>`(三维坐标) | 选取结束区块坐标，并把选取的两个区块所围成的矩形内所有区块设置为强制加载区块 |
---
### 移除强制加载区块
玩家可以在游戏中先后使用 `/batchforceload remove pos1 ~ ~ ~`和`/batchforceload remove pos2 ~ ~ ~`指令，选取两个区块的坐标，把这两个区块所围成的矩形内的所有区块的强制加载状态设置为非强制加载。
| 调用功能 | 三级参数 | 四级参数 | 作用 |
| --- | --- | --- | --- |
| `/batchforceload remove` | `pos1` | `<pos>`(三维坐标) | 选取初始区块坐标 |
| `/batchforceload remove` | `pos2` | `<pos>`(三维坐标) | 选取结束区块坐标，并把选取的两个区块所围成的矩形内所有区块的强制加载状态设置为非强制加载 |
---
### 查找强制加载区块
玩家可以使用 `/batchforceload query`指令，查看世界中所有强制加载区块的数量、坐标、以及玩家所在的区块是否为强制加载区块。
| 调用功能 | 三级参数 | 四级参数 | 作用 |
| --- | --- | --- | --- |
| `/batchforceload query` | `num` | null | 查看世界中所有强制加载区块的数量 |
| `/batchforceload query` | `chunk` | null | 查看世界中所有强制加载区块的坐标 |
| `/batchforceload query` | `ifforceload` | null | 查看玩家所在的区块是否为强制加载区块 |
---
## 配置与储存文件
### 配置文件
该模组配置文件位于`config/batchforceload/config.json`中，配置项：
* `PermissionLevel`用于设置指令的权限等级，默认为`2`，及管理员权限。 
### 储存文件
储存文件位于`config/batchforceload/chunkpos.json`中，储存了所有强制加载区块的坐标。

---
## 注意事项
* 目前该模组仅支持服务端，客户端在测试中。
* 添加强制加载区块会影响服务器性能，请谨慎使用。
* 在使用模组前，请先使用指令`/forceload remove all`移除所有强制加载区块，再使用本模组的指令。
* 原版指令`/forceload query ~ ~`无法检测到使用本模组的指令添加的强制加载区块，请使用本模组的指令`/batchforceload query`查询。
* 在同一纬度中，模组指令与原版指令不能混用，以免造成冲突。
* 该模组的指令`/batchforceload add`和`batchforceload remove`系列指令仅在主世界有效，如果想在其他维度使用，请使用原版`/forceload`系列指令。
* 卸载该模组后，模组配置文件依然保留在`config/batchforceload`文件夹中，请手动删除。
