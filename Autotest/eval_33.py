import subprocess
import json
import os

# 任务33：订5张10月20日深圳到北京的火车票（要求5小时内到达）
# 检查条件：type="batch_booking", bookingType="train", from="深圳", to="北京", date="2025-10-20", quantity=5, constraint="duration<=5h"

def check_booking_batch_train():
    app_package = "com.example.Ctrip"
    phone_file_path = "files/booking_history.json"

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

    # 3. 检查最新预订记录
    try:
        booking_events = data.get("booking_events", [])
        if not booking_events:
            return False
        latest_event = booking_events[-1]

        return (latest_event.get("type") == "batch_booking" and
                latest_event.get("bookingType") == "train" and
                latest_event.get("from") == "深圳" and
                latest_event.get("to") == "北京" and
                latest_event.get("date") == "2025-10-20" and
                latest_event.get("quantity") == 5 and
                latest_event.get("constraint") == "duration<=5h")
    except:
        return False

if __name__ == "__main__":
    print("true" if check_booking_batch_train() else "false")
