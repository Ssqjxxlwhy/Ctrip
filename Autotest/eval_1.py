import subprocess
import json
import os

def check_click_hotel():
    app_package = "com.example.Ctrip"
    phone_file_path = "files/click_history.json"

    # 获取ADB路径
    adb_path = os.path.join(
        os.environ.get('ANDROID_HOME', 'C:\\Users\\wudel\\AppData\\Local\\Android\\Sdk'),
        'platform-tools', 'adb.exe'
    )

    # 1. 通过ADB获取文件内容（指定编码为UTF-8，避免解码错误）
    try:
        # 使用stdout=subprocess.PIPE，配合universal_newlines和encoding参数处理编码
        result = subprocess.run(
            [adb_path, "exec-out", "run-as", app_package, "cat", phone_file_path],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            universal_newlines=True,  # 启用文本模式
            encoding='utf-8',         # 强制指定UTF-8编码解析输出
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

    # 3. 检查最新记录
    try:
        latest_event = data["click_events"][-1]
        return (latest_event["icon"] == "酒店" and
                latest_event["page"] == "酒店预订页面")
    except:
        return False

if __name__ == "__main__":
    print(check_click_hotel())