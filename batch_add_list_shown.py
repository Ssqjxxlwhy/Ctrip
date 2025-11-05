import os

# 定义每个文件需要添加list_shown的位置和方式
fixes = {
    9: {
        'old': '''        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("rooms") == 1 and
                latest_event.get("adults") == 1 and
                latest_event.get("children") == 1)''',
        'new': '''        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("rooms") == 1 and
                latest_event.get("adults") == 1 and
                latest_event.get("children") == 1 and
                latest_event.get("list_shown") == True)'''
    },
    10: {
        'old': '''        return (latest_event.get("type") == "flight_search" and
                latest_event.get("from") == "广州" and
                latest_event.get("to") == "深圳")''',
        'new': '''        return (latest_event.get("type") == "flight_search" and
                latest_event.get("from") == "广州" and
                latest_event.get("to") == "深圳" and
                latest_event.get("list_shown") == True)'''
    },
    11: {
        'old': '''        return (latest_event.get("type") == "flight_search" and
                latest_event.get("date") == "2025-10-22")''',
        'new': '''        return (latest_event.get("type") == "flight_search" and
                latest_event.get("date") == "2025-10-22" and
                latest_event.get("list_shown") == True)'''
    },
    12: {
        'old': '''        return (latest_event.get("type") == "flight_search" and
                cabin in ["经济舱", "商务舱", "头等舱"])''',
        'new': '''        return (latest_event.get("type") == "flight_search" and
                cabin in ["经济舱", "商务舱", "头等舱"] and
                latest_event.get("list_shown") == True)'''
    },
    13: {
        'old': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "上海")''',
        'new': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "上海" and
                latest_event.get("list_shown") == True)'''
    },
    14: {
        'old': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("date") == "2025-10-24")''',
        'new': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("date") == "2025-10-24" and
                latest_event.get("list_shown") == True)'''
    },
    15: {
        'old': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("ticketType") == "学生票")''',
        'new': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("ticketType") == "学生票" and
                latest_event.get("list_shown") == True)'''
    },
    16: {
        'old': '''        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "北京" and
                latest_event.get("checkIn") == "2025-10-22" and
                latest_event.get("checkOut") == "2025-10-23")''',
        'new': '''        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "北京" and
                latest_event.get("checkIn") == "2025-10-22" and
                latest_event.get("checkOut") == "2025-10-23" and
                latest_event.get("list_shown") == True)'''
    },
    17: {
        'old': '''        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "上海" and
                latest_event.get("rooms") == 2 and
                latest_event.get("adults") == 2 and
                latest_event.get("children") == 0)''',
        'new': '''        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "上海" and
                latest_event.get("rooms") == 2 and
                latest_event.get("adults") == 2 and
                latest_event.get("children") == 0 and
                latest_event.get("list_shown") == True)'''
    },
    18: {
        'old': '''        return (latest_event.get("type") == "flight_search" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "深圳" and
                latest_event.get("date") == "2025-10-25")''',
        'new': '''        return (latest_event.get("type") == "flight_search" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "深圳" and
                latest_event.get("date") == "2025-10-25" and
                latest_event.get("list_shown") == True)'''
    },
    19: {
        'old': '''        return (latest_event.get("type") == "flight_search" and
                latest_event.get("from") == "成都" and
                latest_event.get("to") == "上海" and
                latest_event.get("date") == "2025-10-20" and
                latest_event.get("cabin") == "头等舱")''',
        'new': '''        return (latest_event.get("type") == "flight_search" and
                latest_event.get("from") == "成都" and
                latest_event.get("to") == "上海" and
                latest_event.get("date") == "2025-10-20" and
                latest_event.get("cabin") == "头等舱" and
                latest_event.get("list_shown") == True)'''
    },
    20: {
        'old': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "上海" and
                latest_event.get("date") == "2025-10-22")''',
        'new': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "上海" and
                latest_event.get("date") == "2025-10-22" and
                latest_event.get("list_shown") == True)'''
    },
    21: {
        'old': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("from") == "长沙" and
                latest_event.get("to") == "天津" and
                latest_event.get("date") == "2026-01-18" and
                latest_event.get("ticketType") == "学生票")''',
        'new': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("from") == "长沙" and
                latest_event.get("to") == "天津" and
                latest_event.get("date") == "2026-01-18" and
                latest_event.get("ticketType") == "学生票" and
                latest_event.get("list_shown") == True)'''
    },
    22: {
        'old': '''        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "北京" and
                latest_event.get("sortBy") == "rating" and
                latest_event.get("limit") == 5)''',
        'new': '''        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "北京" and
                latest_event.get("sortBy") == "rating" and
                latest_event.get("limit") == 5 and
                latest_event.get("list_shown") == True)'''
    },
    23: {
        'old': '''        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "上海" and
                latest_event.get("sortBy") == "price_asc")''',
        'new': '''        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "上海" and
                latest_event.get("sortBy") == "price_asc" and
                latest_event.get("list_shown") == True)'''
    },
    24: {
        'old': '''        return (latest_event.get("type") == "flight_search" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "广州" and
                latest_event.get("date") == "2025-10-21" and
                latest_event.get("calculation") == "average_price_top3")''',
        'new': '''        return (latest_event.get("type") == "flight_search" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "广州" and
                latest_event.get("date") == "2025-10-21" and
                latest_event.get("calculation") == "average_price_top3" and
                latest_event.get("list_shown") == True)'''
    },
    25: {
        'old': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("from") == "广州" and
                latest_event.get("to") == "杭州" and
                latest_event.get("date") == "2025-10-22" and
                latest_event.get("timeRange") == "14:00-17:00")''',
        'new': '''        return (latest_event.get("type") == "train_search" and
                latest_event.get("from") == "广州" and
                latest_event.get("to") == "杭州" and
                latest_event.get("date") == "2025-10-22" and
                latest_event.get("timeRange") == "14:00-17:00" and
                latest_event.get("list_shown") == True)'''
    },
}

# 批量更新文件
count = 0
for file_num, replacement in fixes.items():
    file_path = f"Autotest/eval_{file_num}.py"

    if not os.path.exists(file_path):
        print(f"文件不存在: {file_path}")
        continue

    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 替换内容
    if replacement['old'] in content:
        content = content.replace(replacement['old'], replacement['new'])

        # 同时更新注释
        content = content.replace(
            '# 3. 检查最新记录是否包含指定字段',
            '# 3. 检查最新记录是否包含指定字段且显示了列表'
        )

        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)

        count += 1
        print(f"✓ 已更新 eval_{file_num}.py")
    else:
        print(f"✗ eval_{file_num}.py - 未找到匹配内容")

print(f"\n完成！共更新了 {count}/{len(fixes)} 个文件")
