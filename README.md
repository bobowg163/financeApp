# 财务记账应用

一个使用Kotlin开发的现代化财务记账Android应用，具备完整的记账、统计和数据管理功能。

## 功能特性

### 🎯 核心功能
- **便捷记账**: 支持收入和支出记录，分类管理
- **智能统计**: 提供饼图可视化分析，支持自定义时间范围
- **数据管理**: 完整的数据备份和恢复功能
- **现代UI**: 基于Material Design 3的现代化界面

### 📊 统计分析
- 按分类统计收支情况
- 饼图展示支出/收入分布
- 支持本月、今年、自定义时间范围查询
- 收支趋势分析

### 💾 数据管理
- JSON格式数据导出/导入
- 自动备份功能
- 数据恢复机制
- 本地存储安全

## 技术架构

### 🛠️ 技术栈
- **开发语言**: Kotlin
- **UI框架**: Jetpack Compose
- **数据库**: Room + SQLite
- **注解处理**: KSP (Kotlin Symbol Processing)
- **图表库**: MPAndroidChart
- **架构模式**: MVVM
- **异步处理**: Kotlin Coroutines + Flow

### 🏗️ 项目结构
```
app/src/main/java/com/xiaomi/caiwu/
├── data/
│   ├── entity/          # 数据实体类
│   ├── dao/            # 数据访问对象
│   ├── database/       # 数据库配置
│   └── converter/      # 类型转换器
├── repository/         # 数据仓库层
├── viewmodel/         # 视图模型
├── ui/
│   ├── screen/        # 界面组件
│   └── theme/         # 主题配置
├── util/              # 工具类
└── MainActivity.kt    # 主活动
```

### 📱 界面设计
- **首页**: 显示最近交易记录和月度概览
- **记账**: 快速添加收入/支出记录
- **统计**: 图表分析和分类统计
- **设置**: 数据管理和应用信息

## 数据库设计

### 表结构

#### transactions 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| amount | Double | 金额 |
| type | TransactionType | 交易类型（收入/支出） |
| category | String | 分类名称 |
| note | String | 备注 |
| date | Date | 交易日期 |
| createdAt | Date | 创建时间 |
| updatedAt | Date | 更新时间 |

#### categories 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键，自增 |
| name | String | 分类名称 |
| type | TransactionType | 分类类型 |
| color | String | 颜色代码 |
| icon | String | 图标 |
| isDefault | Boolean | 是否为默认分类 |

## 安装和运行

### 环境要求
- Android Studio Hedgehog 2023.1.1+
- Kotlin 1.9.20+
- Android SDK 34+
- JDK 11+

### 构建步骤
1. 克隆项目到本地
```bash
git clone [repository-url]
cd caiwu
```

2. 在Android Studio中打开项目

3. 同步Gradle依赖
```bash
./gradlew build
```

4. 连接Android设备或启动模拟器

5. 运行应用
```bash
./gradlew installDebug
```

## 核心依赖

```kotlin
// Room 数据库
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Jetpack Compose
implementation(platform("androidx.compose:compose-bom:2023.10.01"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

// 图表库
implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

// 导航
implementation("androidx.navigation:navigation-compose:2.7.5")

// 日期选择
implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")
```

## 使用说明

### 添加记账记录
1. 点击底部导航的"记账"选项卡
2. 选择交易类型（收入/支出）
3. 选择分类（可从预设分类中选择）
4. 输入金额和备注
5. 选择日期
6. 点击"保存"按钮

### 查看统计
1. 点击"统计"选项卡
2. 选择时间范围（本月/今年/自定义）
3. 选择统计类型（收入/支出分类）
4. 查看饼图和详细列表

### 数据备份
1. 进入"设置"页面
2. 点击"导出数据"选择保存位置
3. 或使用"创建自动备份"功能
4. 备份文件采用JSON格式

### 数据恢复
1. 点击"导入数据"选择备份文件
2. 或从自动备份文件列表中选择恢复
3. 确认后将清空现有数据并导入备份

## 开发特色

### KSP集成
- 使用Kotlin Symbol Processing替代KAPT
- 提供更快的编译速度
- 更好的增量编译支持

### Room数据库
- 声明式SQL查询
- 类型安全的数据访问
- 自动迁移机制
- Flow支持响应式编程

### Jetpack Compose
- 声明式UI编程
- 状态管理自动化
- Material Design 3支持
- 导航组件集成

### 图表可视化
- MPAndroidChart集成
- 饼图展示分类分布
- 自定义颜色和样式
- 交互式图表操作

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 作者

**小麦** - 初始开发

---

*此应用仅用于学习和演示目的，请根据实际需求进行调整和完善。*