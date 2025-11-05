import subprocess
import json
import os

# 任务26：选上海的酒店，入住日期选10月22日，退房10月25日，客房数和入住人数默认，得到酒店列表后，点击第一个酒店，然后选择第一个房型预订
# 检查条件：type="hotel_booking", city="上海", checkIn="2025-10-22", checkOut="2025-10-25", hotelIndex=0, roomIndex=0

def check_booking_hotel_shanghai():
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
        return (latest_event.get("type") == "hotel_booking" and
                latest_event.get("city") == "上海" and
                latest_event.get("checkIn") == "2025-10-22" and
                latest_event.get("checkOut") == "2025-10-25" and
                latest_event.get("hotelIndex") == 0 and
                latest_event.get("roomIndex") == 0)
    except:
        return False

if __name__ == "__main__":
    print("true" if check_booking_hotel_shanghai() else "false")
