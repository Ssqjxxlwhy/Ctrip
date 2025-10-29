import json

# Load data
with open("app/src/main/assets/data/trains_list.json", "r", encoding="utf-8") as f:
    data = json.load(f)

cities = ["上海", "北京", "杭州", "广州", "深圳"]
dates = ["2025-10-20", "2025-10-21", "2025-10-22", "2025-10-23", "2025-10-24", "2025-10-25"]

print("Data Coverage Verification")
print("=" * 50)

issues = []
for from_city in cities:
    for to_city in cities:
        if from_city == to_city:
            continue
        for date in dates:
            trains = [t for t in data if
                     t['departureCity'] == from_city and
                     t['arrivalCity'] == to_city and
                     t['departureDate'] == date]
            if len(trains) != 5:
                issues.append(f"{from_city} -> {to_city} on {date}: {len(trains)} trains")

total_routes = len(cities) * (len(cities) - 1) * len(dates)
print(f"Total routes checked: {total_routes}")
print(f"Expected trains per route: 5")
print(f"Total expected trains: {total_routes * 5}")
print(f"Total trains in data: {len(data)}")
print(f"\nIssues found: {len(issues)}")

if not issues:
    print("\n✓ All routes have exactly 5 trains!")
else:
    print("\n✗ Issues found:")
    for issue in issues[:10]:
        print(f"  - {issue}")

# Verify default search
default_trains = [t for t in data if
                 t['departureCity'] == "上海" and
                 t['arrivalCity'] == "北京" and
                 t['departureDate'] == "2025-10-20"]
print(f"\nDefault search (上海 -> 北京, 2025-10-20): {len(default_trains)} trains")
