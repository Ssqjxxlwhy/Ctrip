import openpyxl

wb = openpyxl.load_workbook(r'Autotest\任务设计&检验逻辑.xlsx')
ws = wb.active

print("=" * 80)
for row_num in [1, 23, 24, 25, 26]:
    row_data = []
    for col in range(1, 6):  # 读取前5列
        cell_value = ws.cell(row=row_num, column=col).value
        row_data.append(str(cell_value) if cell_value else "")

    if row_num == 1:
        print(f"标题行: {row_data[0]}, {row_data[1]}, {row_data[2]}")
    else:
        print(f"\n任务{row_data[0]}:")
        print(f"指令内容: {row_data[1]}")
        print(f"检验方法: {row_data[2]}")
        print(f"检验函数: {row_data[3]}")
