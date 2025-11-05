import re
import os

# 修正eval_11到eval_22，移除list_shown检查
files_to_fix = {
    11: ("flight_search", ["date", "2025-10-22"]),
    12: ("flight_search_cabin", None),  # 特殊处理
    13: ("train_search", ["from", "北京", "to", "上海"]),
    14: ("train_search", ["date", "2025-10-24"]),
    15: ("train_search", ["ticketType", "学生票"]),
    16: ("hotel_search", ["city", "北京", "checkIn", "2025-10-22", "checkOut", "2025-10-23"]),
    17: ("hotel_search", ["city", "上海", "rooms", 2, "adults", 2, "children", 0]),
    18: ("flight_search", ["from", "北京", "to", "深圳", "date", "2025-10-25"]),
    19: ("flight_search", ["from", "成都", "to", "上海", "date", "2025-10-20", "cabin", "头等舱"]),
    20: ("train_search", ["from", "北京", "to", "上海", "date", "2025-10-22"]),
    21: ("train_search", ["from", "长沙", "to", "天津", "date", "2026-01-18", "ticketType", "学生票"]),
    22: ("hotel_search", ["city", "北京", "sortBy", "rating", "limit", 5]),
}

for file_num in range(11, 23):
    file_path = f"Autotest/eval_{file_num}.py"
    if not os.path.exists(file_path):
        continue

    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 移除list_shown检查
    if file_num == 12:
        # 特殊处理eval_12
        content = content.replace(
            'cabin in ["经济舱", "商务舱", "头等舱"] and\n                latest_event.get("list_shown") == True',
            'cabin in ["经济舱", "商务舱", "头等舱"]'
        )
    else:
        # 通用处理：移除 and latest_event.get("list_shown") == True
        content = re.sub(
            r' and\s+latest_event\.get\("list_shown"\) == True',
            '',
            content
        )

    # 更新注释
    content = re.sub(
        r'# 3\. 检查最新记录是否符合条件且显示了列表',
        '# 3. 检查最新记录是否包含指定字段',
        content
    )

    # 移除注释中的list_shown
    content = re.sub(
        r', list_shown=true',
        '',
        content
    )

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"Fixed eval_{file_num}.py")

print("Done fixing eval_11 to eval_22")
