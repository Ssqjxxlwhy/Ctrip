import os

# 定义每个文件正确的return语句
correct_returns = {
    8: '''    # 3. 检查最新记录是否包含指定字段且显示了列表
    try:
        latest_event = data["search_events"][-1]
        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("checkIn") == "2025-10-20" and
                latest_event.get("checkOut") == "2025-10-21" and
                latest_event.get("list_shown") == True)
    except:
        return False''',

    9: '''    # 3. 检查最新记录是否包含指定字段且显示了列表
    try:
        latest_event = data["search_events"][-1]
        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("rooms") == 1 and
                latest_event.get("adults") == 1 and
                latest_event.get("children") == 1 and
                latest_event.get("list_shown") == True)
    except:
        return False''',

    10: '''    # 3. 检查最新记录是否包含指定字段且显示了列表
    try:
        latest_event = data["search_events"][-1]
        return (latest_event.get("type") == "flight_search" and
                latest_event.get("from") == "广州" and
                latest_event.get("to") == "深圳" and
                latest_event.get("list_shown") == True)
    except:
        return False''',

    11: '''    # 3. 检查最新记录是否包含指定字段且显示了列表
    try:
        latest_event = data["search_events"][-1]
        return (latest_event.get("type") == "flight_search" and
                latest_event.get("date") == "2025-10-22" and
                latest_event.get("list_shown") == True)
    except:
        return False''',

    12: '''    # 3. 检查最新记录是否包含指定字段且显示了列表
    try:
        latest_event = data["search_events"][-1]
        cabin = latest_event.get("cabin", "")
        return (latest_event.get("type") == "flight_search" and
                cabin in ["经济舱", "商务舱", "头等舱"] and
                latest_event.get("list_shown") == True)
    except:
        return False''',
}

# 继续添加其他文件...
for num in range(13, 26):
    # 这里需要根据每个任务的具体要求来编写
    pass

print("定义完成，现在需要批量替换")
print("由于文件众多，建议逐个检查和修复")
