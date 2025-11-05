import subprocess
import json
import os

# 任务27：进入"机票"页面，选出发地"武汉"、目的地"深圳"，选择日期11月10号，舱型默认，得到航班列表后，预订第一架航班
# 检查条件：type="flight_booking", from="武汉", to="深圳", date="2025-11-10", flightIndex=0

def check_booking_flight_wh_sz():
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

    # 3. 检查最新预订记录是否包含指定字段
    try:
        booking_events = data.get("booking_events", [])
        if not booking_events:
            return False
        latest_event = booking_events[-1]
        return (latest_event.get("type") == "flight_booking" and
                latest_event.get("from") == "武汉" and
                latest_event.get("to") == "深圳" and
                latest_event.get("date") == "2025-11-10" and
                latest_event.get("flightIndex") == 0)
    except:
        return False

if __name__ == "__main__":
    print("true" if check_booking_flight_wh_sz() else "false")
