import openpyxl
from openpyxl.styles import Alignment

# 打开Excel文件
wb = openpyxl.load_workbook('Autotest/任务设计&检验逻辑.xlsx')
ws = wb.active

# 定义更新的检验方法（从第8行开始，因为前7行是标题行+任务1-7）
# 行号是Excel行号，从1开始计数，第1行是标题，第2行是任务1
updates = {
    # 任务8-22：移除list_shown描述
    9: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", checkIn:"2025-10-20", checkOut:"2025-10-21"}',
    10: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", rooms:1, adults:1, children:1}',
    11: '检查search_params.json中的搜索记录是否包含{type:"flight_search", from:"广州", to:"深圳"}',
    12: '检查search_params.json中的搜索记录是否包含{type:"flight_search", date:"2025-10-22"}',
    13: '检查search_params.json中的搜索记录cabin字段是否为"经济舱"或"商务舱"或"头等舱"',
    14: '检查search_params.json中的搜索记录是否包含{type:"train_search", from:"北京", to:"上海"}',
    15: '检查search_params.json中的搜索记录是否包含{type:"train_search", date:"2025-10-24"}',
    16: '检查search_params.json中的搜索记录是否包含{type:"train_search", ticketType:"学生票"}',
    17: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", city:"北京", checkIn:"2025-10-22", checkOut:"2025-10-23"}',
    18: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", city:"上海", rooms:2, adults:2, children:0}（日期动态）',
    19: '检查search_params.json中的搜索记录是否包含{type:"flight_search", from:"北京", to:"深圳", date:"2025-10-25"}',
    20: '检查search_params.json中的搜索记录是否包含{type:"flight_search", from:"成都", to:"上海", date:"2025-10-20", cabin:"头等舱"}',
    21: '检查search_params.json中的搜索记录是否包含{type:"train_search", from:"北京", to:"上海", date:"2025-10-22"}',
    22: '检查search_params.json中的搜索记录是否包含{type:"train_search", from:"长沙", to:"天津", date:"2026-01-18", ticketType:"学生票"}',
    23: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", city:"北京", sortBy:"rating", limit:5}',

    # 任务23-25：完全更新
    24: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", city:"上海", sortBy:"price_asc"}',
    25: '检查search_params.json中的搜索记录是否包含{type:"flight_search", from:"北京", to:"广州", date:"2025-10-21", calculation:"average_price_top3"}',
    26: '检查search_params.json中的搜索记录是否包含{type:"train_search", from:"广州", to:"杭州", date:"2025-10-22", timeRange:"14:00-17:00"}',

    # 任务26-30：更新为完整检验
    27: '检查booking_history.json中的预订记录是否包含{type:"hotel_booking", city:"上海", checkIn:"2025-10-22", checkOut:"2025-10-25", hotelIndex:0, roomIndex:0}',
    28: '检查booking_history.json中的预订记录是否包含{type:"flight_booking", from:"武汉", to:"深圳", date:"2025-11-10", flightIndex:0}',
    29: '检查booking_history.json中的预订记录是否包含{type:"train_booking", from:"北京", to:"上海", date:"2025-10-23", trainIndex:0}',
    30: '检查booking_history.json中的预订记录是否包含{type:"hotel_booking", city:"上海", checkIn:"2025-10-21", checkOut:"2025-10-25", selection:"cheapest"}',
    31: '检查booking_history.json中的预订记录是否包含{type:"train_booking", from:"北京", to:"上海", date:"2025-10-20", timeRange:"15:00-16:00"}',

    # 任务31-35：更新为复杂检验
    32: '检查booking_history.json中是否包含type:"multi_booking"且有3个步骤：机票(北京->上海)、酒店(上海)、火车票(上海->北京)，并验证顺序',
    33: '检查booking_history.json中是否包含type:"complex_booking"且有：火车票(杭州->北京,<=5h)、酒店(中关村亚朵,2晚)、火车票(北京->杭州,最晚)、totalCost、budgetCheck:2000',
    34: '检查booking_history.json中是否包含{type:"batch_booking", bookingType:"train", from:"深圳", to:"北京", date:"2025-10-20", quantity:5, constraint:"duration<=5h"}',
    35: '检查booking_history.json中是否包含type:"complex_batch_booking"且有：trainBooking(5张,最早) 和 hotelBooking(3家,不同)',
    36: '检查booking_history.json中是否包含{type:"batch_booking", bookingType:"flight", from:"北京", to:"上海", date:"2025-10-15", cabin:"经济舱", quantity:6}',
}

# 更新检验方法列（第3列，C列）
for row_num, new_value in updates.items():
    cell = ws.cell(row=row_num, column=3)
    cell.value = new_value
    cell.alignment = Alignment(wrap_text=True, vertical='top')
    print(f"已更新第{row_num}行：{new_value[:50]}...")

# 保存文件
wb.save('Autotest/任务设计&检验逻辑.xlsx')
print(f"\n✅ Excel文件已更新！共更新了{len(updates)}行")
print("更新的行范围：第9行到第36行（任务8-35）")
