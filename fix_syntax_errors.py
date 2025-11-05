import re
import os

# 需要修复的文件列表
files_to_fix = range(8, 26)

for file_num in files_to_fix:
    file_path = f"Autotest/eval_{file_num}.py"

    if not os.path.exists(file_path):
        print(f"文件不存在: {file_path}")
        continue

    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    # 找到检查逻辑的部分并修复
    new_lines = []
    in_return_block = False
    return_block_lines = []
    indent_count = 0

    for i, line in enumerate(lines):
        # 找到return语句的开始
        if '    # 3. 检查最新记录' in line:
            # 修改注释
            new_lines.append('    # 3. 检查最新记录是否包含指定字段且显示了列表\n')
            continue

        if 'return (latest_event.get("type"' in line and 'and' in line:
            # 这是错误的return语句开始
            in_return_block = True
            return_block_lines = [line]
            indent_count = len(line) - len(line.lstrip())
            continue

        if in_return_block:
            return_block_lines.append(line)
            # 检查是否到达return语句的结束
            if ')' in line and 'except:' not in line:
                # 重建正确的return语句
                # 提取所有条件
                full_return = ''.join(return_block_lines)

                # 移除错误的语法
                full_return = full_return.replace('return (latest_event.get("type" and\n                latest_event.get("list_shown") == True) ==', 'return (latest_event.get("type") ==')

                # 确保最后有list_shown检查
                if 'list_shown' not in full_return or 'list_shown") == True)' not in full_return:
                    # 在最后一个)之前添加list_shown检查
                    full_return = full_return.rstrip()
                    if full_return.endswith(')'):
                        full_return = full_return[:-1] + ' and\n                latest_event.get("list_shown") == True)'

                new_lines.append(full_return + '\n')
                in_return_block = False
                return_block_lines = []
                continue

        new_lines.append(line)

    # 写回文件
    with open(file_path, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)

    print(f"已修复: eval_{file_num}.py")

print("\n修复完成！")
