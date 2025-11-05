import subprocess
import json
import os

# 任务35：订10月15日北京飞上海的6张经济舱机票
# 检查条件：type="batch_booking", bookingType="flight", from="北京", to="上海", date="2025-10-15", cabin="经济舱", quantity=6

def check_booking_batch_flight():
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
                latest_event.get("bookingType") == "flight" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "上海" and
                latest_event.get("date") == "2025-10-15" and
                latest_event.get("cabin") == "经济舱" and
                latest_event.get("quantity") == 6)
    except:
        return False

if __name__ == "__main__":
    print("true" if check_booking_batch_flight() else "false")
