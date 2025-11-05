import openpyxl
from openpyxl.styles import Alignment

# 打开Excel文件
wb = openpyxl.load_workbook('Autotest/任务设计&检验逻辑.xlsx')
ws = wb.active

# 更新第13行（任务12）的检验方法
# 第3列是检验方法
new_value = '检查search_params.json中的搜索记录cabin字段是否为"公务/头等舱"，且list_shown:true'

cell = ws.cell(row=13, column=3)
cell.value = new_value
cell.alignment = Alignment(wrap_text=True, vertical='top')

# 保存文件
wb.save('Autotest/任务设计&检验逻辑.xlsx')
print(f"Excel文件已更新！")
print(f"任务12的检验方法已改为: {new_value}")
