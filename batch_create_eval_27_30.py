import os

# 定义所有eval_27到eval_35的内容
eval_scripts = {
    27: '''import subprocess
import json
import os

# 任务27：进入"机票"页面，选出发地"武汉"、目的地"深圳"，选择日期11月10号，舱型默认，得到航班列表后，预订第一架航班
# 检查条件：type="flight_booking", from="武汉", to="深圳", date="2025-11-10", flightIndex=0

def check_booking_flight_wh_sz():
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
''',
    28: '''import subprocess
import json
import os

# 任务28：进入"火车票"页面，选出发地"北京"、目的地"上海"，选择日期10月23日，得到车次列表后，预订第一班车次
# 检查条件：type="train_booking", from="北京", to="上海", date="2025-10-23", trainIndex=0

def check_booking_train_bj_sh():
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

    # 3. 检查最新预订记录是否包含指定字段
    try:
        booking_events = data.get("booking_events", [])
        if not booking_events:
            return False
        latest_event = booking_events[-1]
        return (latest_event.get("type") == "train_booking" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "上海" and
                latest_event.get("date") == "2025-10-23" and
                latest_event.get("trainIndex") == 0)
    except:
        return False

if __name__ == "__main__":
    print("true" if check_booking_train_bj_sh() else "false")
''',
    29: '''import subprocess
import json
import os

# 任务29：选上海的酒店，入住日期选10月21日，退房10月25日，客房数和入住人数默认，得到酒店列表后，选择价格最低的酒店，然后选择最便宜的房型预订
# 检查条件：type="hotel_booking", city="上海", checkIn="2025-10-21", checkOut="2025-10-25", selection="cheapest"

def check_booking_hotel_cheapest():
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

    # 3. 检查最新预订记录是否包含指定字段
    try:
        booking_events = data.get("booking_events", [])
        if not booking_events:
            return False
        latest_event = booking_events[-1]
        return (latest_event.get("type") == "hotel_booking" and
                latest_event.get("city") == "上海" and
                latest_event.get("checkIn") == "2025-10-21" and
                latest_event.get("checkOut") == "2025-10-25" and
                latest_event.get("selection") == "cheapest")
    except:
        return False

if __name__ == "__main__":
    print("true" if check_booking_hotel_cheapest() else "false")
''',
    30: '''import subprocess
import json
import os

# 任务30：进入"火车票"页面，选出发地"北京"、目的地"上海"，选择日期10月20日，得到车次列表后，预订下午3点到下午4点的任意一班车次
# 检查条件：type="train_booking", from="北京", to="上海", date="2025-10-20", timeRange="15:00-16:00"

def check_booking_train_time():
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

    # 3. 检查最新预订记录是否包含指定字段
    try:
        booking_events = data.get("booking_events", [])
        if not booking_events:
            return False
        latest_event = booking_events[-1]
        return (latest_event.get("type") == "train_booking" and
                latest_event.get("from") == "北京" and
                latest_event.get("to") == "上海" and
                latest_event.get("date") == "2025-10-20" and
                latest_event.get("timeRange") == "15:00-16:00")
    except:
        return False

if __name__ == "__main__":
    print("true" if check_booking_train_time() else "false")
''',
}

# 写入文件
for num, content in eval_scripts.items():
    file_path = f"Autotest/eval_{num}.py"
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Created/Updated eval_{num}.py")

print("Done!")
