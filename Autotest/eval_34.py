import subprocess
import json
import os

# 任务34：订5张10月20日广州到北京的最早火车票，再订北京3家不同的酒店
# 检查条件：type="complex_batch_booking"且有：trainBooking(5张,最早) 和 hotelBooking(3家,不同)

def check_booking_complex_batch():
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

        # 检查type是否为complex_batch_booking
        if latest_event.get("type") != "complex_batch_booking":
            return False

        # 检查trainBooking
        train_booking = latest_event.get("trainBooking", {})
        if not (train_booking.get("from") == "广州" and
                train_booking.get("to") == "北京" and
                train_booking.get("quantity") == 5):
            return False

        # 检查hotelBooking
        hotel_booking = latest_event.get("hotelBooking", {})
        if not (hotel_booking.get("city") == "北京" and
                hotel_booking.get("quantity") == 3):
            return False

        return True
    except:
        return False

if __name__ == "__main__":
    print("true" if check_booking_complex_batch() else "false")
