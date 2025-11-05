import os
import re

# 需要恢复list_shown检查的文件
files_to_restore = range(8, 26)  # eval_8.py 到 eval_25.py

for file_num in files_to_restore:
    file_path = f"Autotest/eval_{file_num}.py"

    if not os.path.exists(file_path):
        continue

    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 更新注释：添加list_shown=true
    content = re.sub(
        r'(# 检查条件：[^#\n]+)(\))',
        r'\1, list_shown=true\2',
        content
    )

    # 如果注释中已经有list_shown，不要重复添加
    content = re.sub(
        r', list_shown=true, list_shown=true',
        r', list_shown=true',
        content
    )

    # 更新代码逻辑：在return语句的最后一个条件后添加list_shown检查
    # 查找 return (xxx and xxx) 的模式
    pattern = r'(return \([^)]+\))'

    def add_list_shown(match):
        return_stmt = match.group(1)
        # 如果已经有list_shown检查，不添加
        if 'list_shown' in return_stmt:
            return return_stmt
        # 在最后的 ) 之前添加检查
        return return_stmt[:-1] + ' and\n                latest_event.get("list_shown") == True)'

    content = re.sub(pattern, add_list_shown, content)

    # 更新注释中的描述
    content = re.sub(
        r'# 3\. 检查最新记录是否包含指定字段',
        '# 3. 检查最新记录是否包含指定字段且显示了列表',
        content
    )

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"已恢复 eval_{file_num}.py 的 list_shown 检查")

print(f"\n✅ 共恢复了 {len(list(files_to_restore))} 个文件的 list_shown 检查")
