import subprocess
import json
import os

# 任务32：订10月20日从杭州到北京的最快火车票（5小时内达），住北京中关村亚朵酒店两晚（10.20-10.22），再订10.22北京回杭州的最晚高铁，计算所有费用后判断2000元够不够
# 检查条件：type="complex_booking"且有：火车票(杭州->北京,<=5h)、酒店(中关村亚朵,2晚)、火车票(北京->杭州,最晚)、totalCost、budgetCheck:2000

def check_booking_complex_budget():
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

        # 检查type是否为complex_booking
        if latest_event.get("type") != "complex_booking":
            return False

        # 检查是否有budgetCheck字段且值为2000
        if latest_event.get("budgetCheck") != 2000:
            return False

        # 检查是否有totalCost字段
        if "totalCost" not in latest_event:
            return False

        # 检查steps
        steps = latest_event.get("steps", [])
        if len(steps) < 3:
            return False

        # 验证是否包含必要的预订组件
        has_train_hz_bj = False
        has_hotel_zgc = False
        has_train_bj_hz = False

        for step in steps:
            if (step.get("type") == "train_booking" and
                step.get("from") == "杭州" and
                step.get("to") == "北京"):
                has_train_hz_bj = True
            elif (step.get("type") == "hotel_booking" and
                  "中关村" in step.get("hotelName", "") and
                  "亚朵" in step.get("hotelName", "")):
                has_hotel_zgc = True
            elif (step.get("type") == "train_booking" and
                  step.get("from") == "北京" and
                  step.get("to") == "杭州"):
                has_train_bj_hz = True

        return has_train_hz_bj and has_hotel_zgc and has_train_bj_hz
    except:
        return False

if __name__ == "__main__":
    print("true" if check_booking_complex_budget() else "false")
