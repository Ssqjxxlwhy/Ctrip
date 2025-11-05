import subprocess
import os

def clear_click_history():
    """清空 click_history.json 文件"""
    app_package = "com.example.Ctrip"
    file_path = "files/click_history.json"

    adb_path = os.path.join(
        os.environ.get('ANDROID_HOME', 'C:\\Users\\wudel\\AppData\\Local\\Android\\Sdk'),
        'platform-tools', 'adb.exe'
    )

    try:
        # 删除文件
        result = subprocess.run(
            [adb_path, "shell", "run-as", app_package, "rm", file_path],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            encoding='utf-8'
        )

        if result.returncode == 0:
            print("✓ click_history.json 文件已清空")
            return True
        else:
            # 如果文件不存在也算成功
            if "No such file" in result.stderr:
                print("✓ 文件不存在（已经是空的）")
                return True
            else:
                print(f"✗ 清空失败: {result.stderr}")
                return False
    except Exception as e:
        print(f"✗ 错误: {e}")
        return False

if __name__ == "__main__":
    clear_click_history()
