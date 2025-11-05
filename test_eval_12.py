import json

# 测试数据 - 用户提供的实际JSON
test_data = {
    "search_events": [
        {
            "time": "2025-11-04 12:24:47",
            "type": "flight_search",
            "from": "上海",
            "to": "北京",
            "date": "2025-11-05",
            "cabin": "business",
            "list_shown": True
        }
    ]
}

# 模拟检查逻辑
latest_event = test_data["search_events"][-1]
cabin = latest_event.get("cabin", "")

result = (latest_event.get("type") == "flight_search" and
          cabin in ["economy", "business"] and
          latest_event.get("list_shown") == True)

print(f"测试结果: {'true' if result else 'false'}")
print(f"详细信息:")
print(f"  type: {latest_event.get('type')}")
print(f"  cabin: {cabin}")
print(f"  cabin in ['economy', 'business']: {cabin in ['economy', 'business']}")
print(f"  list_shown: {latest_event.get('list_shown')}")

# 测试 economy 舱位
test_data_economy = {
    "search_events": [
        {
            "time": "2025-11-04 12:24:47",
            "type": "flight_search",
            "from": "上海",
            "to": "北京",
            "date": "2025-11-05",
            "cabin": "economy",
            "list_shown": True
        }
    ]
}

latest_event_economy = test_data_economy["search_events"][-1]
cabin_economy = latest_event_economy.get("cabin", "")

result_economy = (latest_event_economy.get("type") == "flight_search" and
                  cabin_economy in ["economy", "business"] and
                  latest_event_economy.get("list_shown") == True)

print(f"\n经济舱测试结果: {'true' if result_economy else 'false'}")
