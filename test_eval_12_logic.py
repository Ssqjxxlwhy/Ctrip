import json

# 测试1：公务/头等舱 - 应该返回true
test_business = {
    "search_events": [
        {
            "time": "2025-11-04 12:24:47",
            "type": "flight_search",
            "from": "上海",
            "to": "北京",
            "date": "2025-11-05",
            "cabin": "公务/头等舱",
            "list_shown": True
        }
    ]
}

latest = test_business["search_events"][-1]
result1 = (latest.get("type") == "flight_search" and
           latest.get("cabin") == "公务/头等舱" and
           latest.get("list_shown") == True)

print(f"测试1 - 公务/头等舱: {'true' if result1 else 'false'} (期望: true)")

# 测试2：经济舱 - 应该返回false
test_economy = {
    "search_events": [
        {
            "time": "2025-11-04 12:24:47",
            "type": "flight_search",
            "from": "上海",
            "to": "北京",
            "date": "2025-11-05",
            "cabin": "经济舱",
            "list_shown": True
        }
    ]
}

latest = test_economy["search_events"][-1]
result2 = (latest.get("type") == "flight_search" and
           latest.get("cabin") == "公务/头等舱" and
           latest.get("list_shown") == True)

print(f"测试2 - 经济舱: {'true' if result2 else 'false'} (期望: false)")

# 测试3：list_shown为false - 应该返回false
test_no_list = {
    "search_events": [
        {
            "time": "2025-11-04 12:24:47",
            "type": "flight_search",
            "from": "上海",
            "to": "北京",
            "date": "2025-11-05",
            "cabin": "公务/头等舱",
            "list_shown": False
        }
    ]
}

latest = test_no_list["search_events"][-1]
result3 = (latest.get("type") == "flight_search" and
           latest.get("cabin") == "公务/头等舱" and
           latest.get("list_shown") == True)

print(f"测试3 - 公务/头等舱但list_shown=false: {'true' if result3 else 'false'} (期望: false)")
