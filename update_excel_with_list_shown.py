import openpyxl
from openpyxl.styles import Alignment

# 打开Excel文件
wb = openpyxl.load_workbook('Autotest/任务设计&检验逻辑.xlsx')
ws = wb.active

# 定义任务8-25的正确检验方法描述（都需要包含list_shown=true）
updates = {
    9: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", checkIn:"2025-10-20", checkOut:"2025-10-21", list_shown:true}',
    10: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", rooms:1, adults:1, children:1, list_shown:true}',
    11: '检查search_params.json中的搜索记录是否包含{type:"flight_search", from:"广州", to:"深圳", list_shown:true}',
    12: '检查search_params.json中的搜索记录是否包含{type:"flight_search", date:"2025-10-22", list_shown:true}',
    13: '检查search_params.json中的搜索记录cabin字段是否为"经济舱"或"商务舱"或"头等舱"，且list_shown:true',
    14: '检查search_params.json中的搜索记录是否包含{type:"train_search", from:"北京", to:"上海", list_shown:true}',
    15: '检查search_params.json中的搜索记录是否包含{type:"train_search", date:"2025-10-24", list_shown:true}',
    16: '检查search_params.json中的搜索记录是否包含{type:"train_search", ticketType:"学生票", list_shown:true}',
    17: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", city:"北京", checkIn:"2025-10-22", checkOut:"2025-10-23", list_shown:true}',
    18: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", city:"上海", rooms:2, adults:2, children:0, list_shown:true}（日期动态）',
    19: '检查search_params.json中的搜索记录是否包含{type:"flight_search", from:"北京", to:"深圳", date:"2025-10-25", list_shown:true}',
    20: '检查search_params.json中的搜索记录是否包含{type:"flight_search", from:"成都", to:"上海", date:"2025-10-20", cabin:"头等舱", list_shown:true}',
    21: '检查search_params.json中的搜索记录是否包含{type:"train_search", from:"北京", to:"上海", date:"2025-10-22", list_shown:true}',
    22: '检查search_params.json中的搜索记录是否包含{type:"train_search", from:"长沙", to:"天津", date:"2026-01-18", ticketType:"学生票", list_shown:true}',
    23: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", city:"北京", sortBy:"rating", limit:5, list_shown:true}',
    24: '检查search_params.json中的搜索记录是否包含{type:"hotel_search", city:"上海", sortBy:"price_asc", list_shown:true}',
    25: '检查search_params.json中的搜索记录是否包含{type:"flight_search", from:"北京", to:"广州", date:"2025-10-21", calculation:"average_price_top3", list_shown:true}',
    26: '检查search_params.json中的搜索记录是否包含{type:"train_search", from:"广州", to:"杭州", date:"2025-10-22", timeRange:"14:00-17:00", list_shown:true}',
}

# 更新检验方法列（第3列，C列）
count = 0
for row_num, new_value in updates.items():
    cell = ws.cell(row=row_num, column=3)
    cell.value = new_value
    cell.alignment = Alignment(wrap_text=True, vertical='top')
    count += 1

# 保存文件
wb.save('Autotest/任务设计&检验逻辑.xlsx')
print(f"Excel文件已更新！")
print(f"共更新了 {count} 行（任务8-25）")
print("所有搜索类任务的检验方法现在都包含 list_shown:true 检查")
