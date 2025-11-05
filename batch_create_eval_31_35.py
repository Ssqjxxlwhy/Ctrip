import os

# 定义eval_31到eval_35的内容
eval_scripts = {
    31: '''import subprocess
import json
import os

# 任务31：先订北京飞上海的机票（10月20日，经济舱），再订上海的酒店（入住10月20日，退房10月21日），最后订上海回北京的火车票（10月21日）
# 检查条件：type="multi_booking"且有3个步骤：机票(北京->上海)、酒店(上海)、火车票(上海->北京)，并验证顺序

def check_booking_multi_step():
    app_package = "com.example.Ctrip"
    phone_file_path = "files/booking_history.json"

    # 获取ADB路径
    adb_path = os.path.join(
        os.environ.get('ANDROID_HOME', 'C:\\\\Users\\\\wudel\\\\AppData\\\\Local\\\\Android\\\\Sdk'),
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

        # 检查type是否为multi_booking
        if latest_event.get("type") != "multi_booking":
            return False

        # 检查是否有3个步骤
        steps = latest_event.get("steps", [])
        if len(steps) != 3:
            return False

        # 验证第1步：机票(北京->上海)
        step1 = steps[0]
        if not (step1.get("type") == "flight_booking" and
                step1.get("from") == "北京" and
                step1.get("to") == "上海"):
            return False

        # 验证第2步：酒店(上海)
        step2 = steps[1]
        if not (step2.get("type") == "hotel_booking" and
                step2.get("city") == "上海"):
            return False

        # 验证第3步：火车票(上海->北京)
        step3 = steps[2]
        if not (step3.get("type") == "train_booking" and
                step3.get("from") == "上海" and
                step3.get("to") == "北京"):
            return False

        return True
    except:
        return False

if __name__ == "__main__":
    print("true" if check_booking_multi_step() else "false")
''',
    32: '''import subprocess
import json
import os

# 任务32：订10月20日从杭州到北京的最快火车票（5小时内达），住北京中关村亚朵酒店两晚（10.20-10.22），再订10.22北京回杭州的最晚高铁，计算所有费用后判断2000元够不够
# 检查条件：type="complex_booking"且有：火车票(杭州->北京,<=5h)、酒店(中关村亚朵,2晚)、火车票(北京->杭州,最晚)、totalCost、budgetCheck:2000

def check_booking_complex_budget():
    app_package = "com.example.Ctrip"
    phone_file_path = "files/booking_history.json"

    # 获取ADB路径
    adb_path = os.path.join(
        os.environ.get('ANDROID_HOME', 'C:\\\\Users\\\\wudel\\\\AppData\\\\Local\\\\Android\\\\Sdk'),
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
''',
    33: '''import subprocess
import json
import os

# 任务33：订5张10月20日深圳到北京的火车票（要求5小时内到达）
# 检查条件：type="batch_booking", bookingType="train", from="深圳", to="北京", date="2025-10-20", quantity=5, constraint="duration<=5h"

def check_booking_batch_train():
    app_package = "com.example.Ctrip"
    phone_file_path = "files/booking_history.json"

    # 获取ADB路径
    adb_path = os.path.join(
        os.environ.get('ANDROID_HOME', 'C:\\\\Users\\\\wudel\\\\AppData\\\\Local\\\\Android\\\\Sdk'),
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
''',
    34: '''import subprocess
import json
import os

# 任务34：订5张10月20日广州到北京的最早火车票，再订北京3家不同的酒店
# 检查条件：type="complex_batch_booking"且有：trainBooking(5张,最早) 和 hotelBooking(3家,不同)

def check_booking_complex_batch():
    app_package = "com.example.Ctrip"
    phone_file_path = "files/booking_history.json"

    # 获取ADB路径
    adb_path = os.path.join(
        os.environ.get('ANDROID_HOME', 'C:\\\\Users\\\\wudel\\\\AppData\\\\Local\\\\Android\\\\Sdk'),
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
''',
    35: '''import subprocess
import json
import os

# 任务35：订10月15日北京飞上海的6张经济舱机票
# 检查条件：type="batch_booking", bookingType="flight", from="北京", to="上海", date="2025-10-15", cabin="经济舱", quantity=6

def check_booking_batch_flight():
    app_package = "com.example.Ctrip"
    phone_file_path = "files/booking_history.json"

    # 获取ADB路径
    adb_path = os.path.join(
        os.environ.get('ANDROID_HOME', 'C:\\\\Users\\\\wudel\\\\AppData\\\\Local\\\\Android\\\\Sdk'),
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
''',
}

# 写入文件
for num, content in eval_scripts.items():
    file_path = f"Autotest/eval_{num}.py"
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Created/Updated eval_{num}.py")

print("Done creating eval_31 to eval_35!")
