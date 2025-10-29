import json
from datetime import date, timedelta

# 5个城市及其车站
cities = {
    "上海": ["上海虹桥", "上海"],
    "北京": ["北京南", "北京西"],
    "杭州": ["杭州东", "杭州"],
    "广州": ["广州南", "广州东"],
    "深圳": ["深圳北", "深圳"]
}

# 车次前缀
train_prefixes = ["G", "D", "C", "T", "K"]

# 生成日期范围
start_date = date(2025, 10, 20)
dates = [start_date + timedelta(days=i) for i in range(6)]  # 10月20-25日

trains = []
train_id_counter = 1

# 为每对城市生成数据
city_list = list(cities.keys())
for i, from_city in enumerate(city_list):
    for to_city in city_list:
        if from_city == to_city:
            continue

        # 为每个日期生成5个车次
        for day_index, travel_date in enumerate(dates):
            for train_index in range(5):
                # 生成车次号
                prefix = train_prefixes[train_index % len(train_prefixes)]
                train_number = f"{prefix}{100 + train_id_counter}"

                # 选择车站
                from_station = cities[from_city][train_index % len(cities[from_city])]
                to_station = cities[to_city][train_index % len(cities[to_city])]

                # 生成时间
                dep_hour = 6 + train_index * 2
                dep_minute = (train_index * 15) % 60
                dep_time = f"{dep_hour:02d}:{dep_minute:02d}"

                # 计算到达时间和时长
                base_duration = 180 + (i * 30) + (train_index * 20)  # 基础时长（分钟）
                arr_hour = dep_hour + base_duration // 60
                arr_minute = (dep_minute + base_duration % 60) % 60
                if arr_minute < dep_minute:
                    arr_hour += 1
                arr_time = f"{arr_hour:02d}:{arr_minute:02d}"

                duration_hours = base_duration // 60
                duration_minutes = base_duration % 60
                duration_str = f"{duration_hours}时{duration_minutes}分"

                # 生成座位和价格
                base_price = 200 + (i * 50) + (train_index * 30)
                available_seats = {
                    "二等座": (10 - train_index * 2) if train_index < 4 else 0,
                    "一等座": (5 - train_index) if train_index < 4 else 0,
                    "商务座": (3 - train_index) if train_index < 3 else 0
                }

                prices = {
                    "二等座": base_price,
                    "一等座": base_price * 1.6,
                    "商务座": base_price * 3.0
                }

                # 特性标签
                features = []
                if train_index == 0:
                    features.append("直达有票")
                if train_index < 3:
                    features.append("车内换座两次即可")
                if train_index == 2:
                    features.append("同车换乘")

                train = {
                    "trainId": f"train_{from_city}_{to_city}_{day_index}_{train_index}",
                    "trainNumber": train_number,
                    "trainType": "复兴号·智能动车" if prefix == "G" else ("动车" if prefix == "D" else "高速列车"),
                    "departureCity": from_city,
                    "arrivalCity": to_city,
                    "departureStation": from_station,
                    "arrivalStation": to_station,
                    "departureDate": travel_date.strftime("%Y-%m-%d"),
                    "departureTime": dep_time,
                    "arrivalTime": arr_time,
                    "duration": duration_str,
                    "availableSeats": available_seats,
                    "prices": prices,
                    "hasDiscount": train_index % 2 == 0,
                    "discountAmount": 10.0 if train_index % 2 == 0 else 0.0,
                    "features": features
                }

                trains.append(train)
                train_id_counter += 1

# 写入JSON文件
with open("app/src/main/assets/data/trains_list.json", "w", encoding="utf-8") as f:
    json.dump(trains, f, ensure_ascii=False, indent=2)

print(f"生成了 {len(trains)} 条火车票数据")
print(f"城市组合: {len(city_list) * (len(city_list) - 1)} 对")
print(f"日期范围: {dates[0]} 到 {dates[-1]}")
print(f"每个城市对每天车次: 5")
