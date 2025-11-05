# 自动化测试系统使用说明

## 📋 概述

本目录包含35个自动化检验脚本（eval_1.py ~ eval_35.py），用于验证携程旅行APP的各项功能。

## 📁 文件结构

```
Autotest/
├── eval_1.py ~ eval_35.py    # 35个检验脚本
└── README.md                  # 本说明文档

项目根目录/
├── 任务设计&检验逻辑.xlsx      # 详细的任务设计和检验逻辑文档
├── clear_click_history.bat    # 清空点击历史记录的批处理脚本
└── run_eval_1.bat             # 运行eval_1.py的批处理脚本

Android工具类/
├── app/src/main/java/com/example/Ctrip/utils/
│   ├── ClickHistoryManager.kt        # 点击记录管理器
│   ├── SearchParamsManager.kt        # 搜索参数管理器
│   └── BookingHistoryManager.kt      # 预订历史管理器
```

## 🔧 检验脚本分类

### 1. 点击类任务（eval_1.py ~ eval_6.py）
检查用户点击按钮/图标的行为

**数据文件**: `click_history.json`

**示例**:
- eval_1.py: 检查点击"酒店"图标
- eval_2.py: 检查点击"机票"图标
- eval_3.py: 检查点击"火车票"图标
- eval_4.py: 检查点击"消息"按钮
- eval_5.py: 检查点击"行程"按钮
- eval_6.py: 检查点击"我的"按钮

### 2. 搜索和筛选类任务（eval_7.py ~ eval_25.py）
检查用户的搜索和筛选操作

**数据文件**: `search_params.json`

**子分类**:
- **酒店搜索** (eval_7, 8, 9, 16, 17, 22, 23): 城市、日期、人数、筛选等
- **机票搜索** (eval_10, 11, 12, 18, 19, 24): 出发地、目的地、日期、舱位等
- **火车票搜索** (eval_13, 14, 15, 20, 21, 25): 出发地、目的地、日期、票种等

### 3. 预订类任务（eval_26.py ~ eval_35.py）
检查用户的预订操作

**数据文件**: `booking_history.json`

**子分类**:
- **单次预订** (eval_26-30): 预订酒店、机票或火车票
- **复杂任务** (eval_31-32): 多步骤预订和费用计算
- **批量预订** (eval_33-35): 批量订票

## 🚀 使用方法

### 方法1: 运行单个检验脚本

```bash
python Autotest/eval_1.py
```

### 方法2: 使用批处理文件（Windows）

双击项目根目录下的批处理文件：
```
run_eval_1.bat
```

### 方法3: 在代码中调用

```python
import sys
sys.path.append('Autotest')
from eval_1 import check_click_hotel

result = check_click_hotel()
print("检验结果:", "通过" if result else "失败")
```

## 📝 JSON文件格式

### 1. click_history.json (点击记录)

```json
{
  "click_events": [
    {
      "time": "2025-11-03 15:30:00",
      "icon": "酒店",
      "page": "酒店预订页面"
    }
  ]
}
```

### 2. search_params.json (搜索参数)

```json
{
  "search_events": [
    {
      "time": "2025-11-03 15:35:00",
      "type": "hotel_search",
      "city": "上海",
      "checkIn": "2025-10-20",
      "checkOut": "2025-10-21",
      "rooms": 1,
      "adults": 2,
      "children": 0
    },
    {
      "time": "2025-11-03 15:40:00",
      "type": "flight_search",
      "from": "北京",
      "to": "上海",
      "date": "2025-10-22",
      "cabin": "经济舱"
    },
    {
      "time": "2025-11-03 15:45:00",
      "type": "train_search",
      "from": "广州",
      "to": "深圳",
      "date": "2025-10-23",
      "ticketType": "学生票"
    }
  ]
}
```

### 3. booking_history.json (预订历史)

```json
{
  "booking_events": [
    {
      "time": "2025-11-03 16:00:00",
      "type": "hotel_booking",
      "city": "上海",
      "checkIn": "2025-10-22",
      "checkOut": "2025-10-25",
      "hotelIndex": 0,
      "roomIndex": 0
    },
    {
      "time": "2025-11-03 16:05:00",
      "type": "flight_booking",
      "from": "武汉",
      "to": "深圳",
      "date": "2025-11-10",
      "flightIndex": 0
    }
  ]
}
```

## 🛠️ Android集成

在Android应用中使用工具类记录用户行为：

### 1. 记录点击事件

```kotlin
import com.example.Ctrip.utils.ClickHistoryManager

// 在点击事件处理中
ClickHistoryManager.recordClick(context, "酒店", "酒店预订页面")
```

### 2. 记录搜索参数

```kotlin
import com.example.Ctrip.utils.SearchParamsManager

// 记录酒店搜索
SearchParamsManager.recordHotelSearch(
    context = context,
    city = "上海",
    checkIn = "2025-10-20",
    checkOut = "2025-10-21",
    rooms = 1,
    adults = 2,
    children = 0
)

// 记录机票搜索
SearchParamsManager.recordFlightSearch(
    context = context,
    from = "北京",
    to = "上海",
    date = "2025-10-22",
    cabin = "经济舱"
)

// 记录火车票搜索
SearchParamsManager.recordTrainSearch(
    context = context,
    from = "广州",
    to = "深圳",
    date = "2025-10-23",
    ticketType = "学生票"
)
```

### 3. 记录预订操作

```kotlin
import com.example.Ctrip.utils.BookingHistoryManager

// 记录酒店预订
BookingHistoryManager.recordHotelBooking(
    context = context,
    city = "上海",
    checkIn = "2025-10-22",
    checkOut = "2025-10-25",
    hotelIndex = 0,
    roomIndex = 0
)

// 记录机票预订
BookingHistoryManager.recordFlightBooking(
    context = context,
    from = "武汉",
    to = "深圳",
    date = "2025-11-10",
    flightIndex = 0
)

// 记录火车票预订
BookingHistoryManager.recordTrainBooking(
    context = context,
    from = "北京",
    to = "上海",
    date = "2025-10-23",
    trainIndex = 0
)
```

## 🧹 清空测试数据

### 方法1: 使用批处理脚本

双击 `clear_click_history.bat` 清空点击历史记录

### 方法2: 使用ADB命令

```bash
# 清空点击历史
adb shell "run-as com.example.Ctrip sh -c 'echo \"{\\\"click_events\\\": []}\" > files/click_history.json'"

# 清空搜索参数
adb shell "run-as com.example.Ctrip sh -c 'echo \"{\\\"search_events\\\": []}\" > files/search_params.json'"

# 清空预订历史
adb shell "run-as com.example.Ctrip sh -c 'echo \"{\\\"booking_events\\\": []}\" > files/booking_history.json'"
```

### 方法3: 在代码中清空

```kotlin
// 清空点击历史
ClickHistoryManager.clearHistory(context)

// 清空搜索参数
SearchParamsManager.clearHistory(context)

// 清空预订历史
BookingHistoryManager.clearHistory(context)
```

## 📊 查看测试数据

### 方法1: 使用ADB命令

```bash
# 查看点击历史
adb shell run-as com.example.Ctrip cat files/click_history.json

# 查看搜索参数
adb shell run-as com.example.Ctrip cat files/search_params.json

# 查看预订历史
adb shell run-as com.example.Ctrip cat files/booking_history.json
```

### 方法2: 在代码中查看

```kotlin
// 获取点击历史
val clickHistory = ClickHistoryManager.getHistory(context)
Log.d("Test", "Click History: $clickHistory")

// 获取搜索参数
val searchParams = SearchParamsManager.getHistory(context)
Log.d("Test", "Search Params: $searchParams")

// 获取预订历史
val bookingHistory = BookingHistoryManager.getHistory(context)
Log.d("Test", "Booking History: $bookingHistory")
```

## ⚠️ 注意事项

1. **设备连接**: 运行检验脚本前，确保Android设备或模拟器已连接并启用USB调试
2. **应用安装**: 确保APP（com.example.Ctrip）已安装在设备上
3. **文件权限**: 检验脚本使用`run-as`命令访问应用私有目录，需要正确的权限
4. **数据清空**: 每次测试前建议清空相关的JSON文件，避免旧数据干扰
5. **编码问题**: 所有JSON文件使用UTF-8编码，确保中文字符正确处理

## 🔍 故障排查

### 问题1: "获取文件失败，可能文件不存在或权限不足"

**解决方案**:
- 确认设备已连接：`adb devices`
- 确认应用已安装：`adb shell pm list packages | grep Ctrip`
- 手动测试ADB访问：`adb shell run-as com.example.Ctrip ls files/`

### 问题2: "文件解析失败或文件不存在"

**解决方案**:
- 确认JSON文件已创建并包含有效数据
- 手动查看文件内容：`adb shell run-as com.example.Ctrip cat files/click_history.json`
- 检查JSON格式是否正确

### 问题3: 检验总是返回"失败"

**解决方案**:
- 确认已在应用中执行相应操作
- 检查JSON文件中的数据是否正确记录
- 对比检验条件和实际记录的数据字段

## 📖 参考文档

- **任务设计&检验逻辑.xlsx**: 包含所有35个任务的详细设计和检验逻辑
- **任务设计-脑暴.xlsx**: 原始的任务需求和设计思路

## 📞 支持

如有问题，请检查：
1. Android应用是否正确集成了三个Manager工具类
2. JSON文件格式是否符合规范
3. ADB路径是否正确配置

---

**版本**: 1.0
**创建日期**: 2025-11-03
**最后更新**: 2025-11-03
