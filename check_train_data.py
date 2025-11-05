import json

# 读取火车票数据
with open(r'app\src\main\assets\data\trains_list.json', encoding='utf-8') as f:
    all_trains = json.load(f)

# 筛选广州到杭州10月22日的火车
gz_hz_trains = [t for t in all_trains if t['departureCity'] == '广州' and t['arrivalCity'] == '杭州' and t['departureDate'] == '2025-10-22']

print(f"广州到杭州10月22日的火车共{len(gz_hz_trains)}趟：\n")
for train in gz_hz_trains:
    print(f"{train['trainNumber']} {train['departureTime']}-{train['arrivalTime']}")

# 筛选下午2点到5点（14:00-17:00）出发的车次
print("\n下午2点到5点（14:00-17:00）出发的车次：")
afternoon_trains = []
for train in gz_hz_trains:
    dept_time = train['departureTime']
    hour = int(dept_time.split(':')[0])
    minute = int(dept_time.split(':')[1])
    total_minutes = hour * 60 + minute

    # 14:00-17:00 = 14*60 到 17*60
    if 14*60 <= total_minutes < 17*60:
        afternoon_trains.append(train)
        print(f"{train['trainNumber']} {train['departureTime']}-{train['arrivalTime']}")

print(f"\n符合条件的车次数量：{len(afternoon_trains)}")
