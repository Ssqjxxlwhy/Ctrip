import subprocess
import json
import os

# 任务16：进入"酒店"页面，城市选择北京，入住时间选择10月22日，退房时间选择10月23日，得到酒店列表
# 检查条件：type="hotel_search", city="北京", checkIn="2025-10-22", checkOut="2025-10-23"

def check_hotel_search_beijing():
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
        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "北京" and
                latest_event.get("checkIn") == "2025-10-22" and
                latest_event.get("checkOut") == "2025-10-23")
    except:
        return False

if __name__ == "__main__":
    print("true" if check_hotel_search_beijing() else "false")
