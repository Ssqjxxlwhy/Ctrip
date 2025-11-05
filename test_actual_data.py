import json

# 用户提供的实际数据
actual_json = '''{
  "search_events": [
    {
      "time": "2025-11-04 12:24:47",
      "type": "flight_search",
      "from": "上海",
      "to": "北京",
      "date": "2025-11-05",
      "cabin": "business",
      "list_shown": true
    }
  ]
}'''

data = json.loads(actual_json)
latest_event = data["search_events"][-1]
cabin = latest_event.get("cabin", "")

result = (latest_event.get("type") == "flight_search" and
          cabin in ["economy", "business"] and
          latest_event.get("list_shown") == True)

print("true" if result else "false")
