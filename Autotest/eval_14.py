import subprocess
import json
import os

# 任务14：进入"火车票"页面，选择日期10月24日，得到车次列表
# 检查条件：type="train_search", date="2025-10-24"

def check_train_search_date():
    app_package = "com.example.Ctrip"
    phone_file_path = "files/search_params.json"

    # 获取ADB路径
    adb_path = os.path.join(
        os.environ.get('ANDROID_HOME', 'C:\\Users\\wudel\\AppData\\Local\\Android\\Sdk'),
        'platform-tools', 'adb.exe'
    )

    # 1. 通过ADB获取文件内容
    try:
        result = subprocess.run(
            [adb_path, "exec-out", "run-as", app_package, "cat", phone_file_path],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            universal_newlines=True,
            encoding='utf-8',
            check=True
        )
        file_content = result.stdout
    except subprocess.CalledProcessError:
        return False
    except Exception:
        return False

    # 2. 解析JSON内容
    try:
        data = json.loads(file_content)
    except json.JSONDecodeError:
        return False
    except Exception:
        return False

    # 3. 检查最新记录是否包含指定字段且显示了列表
    try:
        latest_event = data["search_events"][-1]
        return (latest_event.get("type") == "train_search" and
                latest_event.get("date") == "2025-10-24")
    except:
        return False

if __name__ == "__main__":
    print("true" if check_train_search_date() else "false")
