import subprocess
import json
import os

# 任务31：先订北京飞上海的机票（10月20日，经济舱），再订上海的酒店（入住10月20日，退房10月21日），最后订上海回北京的火车票（10月21日）
# 检查条件：type="multi_booking"且有3个步骤：机票(北京->上海)、酒店(上海)、火车票(上海->北京)，并验证顺序

def check_booking_multi_step():
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
