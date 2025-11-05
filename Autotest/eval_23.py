import subprocess
import json
import os

# 任务23：进入 "酒店" 页面，为我检索上海所有酒店中最低的价格
# 检查条件：智能体返回的价格是否是上海所有酒店中的最低价格

# 上海酒店数据（从HotelListTabModel.kt中提取）
SHANGHAI_HOTELS = [
    {"id": "hotel_sh_001", "name": "上海外滩茂悦大酒店", "rating": 4.6, "price": 298},
    {"id": "hotel_sh_002", "name": "上海迪士尼乐园酒店", "rating": 4.9, "price": 888},
    {"id": "hotel_sh_003", "name": "上海虹桥机场华美达酒店", "rating": 4.4, "price": 220},
    {"id": "hotel_sh_004", "name": "上海南京路步行街亚朵酒店", "rating": 4.7, "price": 350},
    {"id": "hotel_sh_005", "name": "上海田子坊创意民宿", "rating": 4.5, "price": 180}
]

def get_expected_min_price():
    """
    计算真实答案：上海所有酒店中的最低价格
    """
    # 找出所有酒店中的最低价格
    min_price = min(hotel["price"] for hotel in SHANGHAI_HOTELS)
    # 找出最低价格对应的酒店（用于调试）
    cheapest_hotel = [h for h in SHANGHAI_HOTELS if h["price"] == min_price][0]

    return min_price, cheapest_hotel

def check_hotel_search_shanghai_min_price(agent_answer):
    """
    检验函数：验证智能体答案是否正确

    参数:
        agent_answer: 智能体生成的答案，可以是：
                     - 数字: 180 或 180.0
                     - 字符串: "180" 或 "¥180"
                     - 包含价格的字典: {"price": 180, "min_price": 180}

    返回:
        True: 验证成功
        False: 验证失败
    """
    # 计算真实答案
    expected_price, _ = get_expected_min_price()

    # 处理不同类型的智能体答案
    try:
        if isinstance(agent_answer, (int, float)):
            agent_price = float(agent_answer)
        elif isinstance(agent_answer, str):
            # 移除货币符号和空格
            clean_str = agent_answer.replace("¥", "").replace("￥", "").replace(",", "").strip()
            agent_price = float(clean_str)
        elif isinstance(agent_answer, dict):
            # 尝试从字典中获取价格
            agent_price = float(agent_answer.get("price", agent_answer.get("min_price", 0)))
        else:
            return False

        # 精确匹配
        return agent_price == expected_price

    except (ValueError, TypeError):
        return False

def check_from_search_params_file():
    """
    从search_params.json文件中检查是否记录了正确的搜索参数
    """
    app_package = "com.example.Ctrip"
    phone_file_path = "files/search_params.json"

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

    # 3. 检查最新记录是否包含指定字段
    try:
        latest_event = data["search_events"][-1]
        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "上海" and
                latest_event.get("calculation") == "min_price" and
                latest_event.get("list_shown") == True)
    except:
        return False

if __name__ == "__main__":
    # 计算并打印预期答案
    expected_price, cheapest_hotel = get_expected_min_price()

    print(f"预期答案：上海所有酒店中最低的价格 = {expected_price}元")
    print(f"（对应酒店：{cheapest_hotel['name']}）")

    # 打印所有上海酒店按价格排序
    print("\n上海所有酒店按价格排序：")
    sorted_hotels = sorted(SHANGHAI_HOTELS, key=lambda x: x["price"])
    for hotel in sorted_hotels:
        print(f"  {hotel['name']} - 价格{hotel['price']}元")

    # 如果要从文件检查，使用：
    file_check_result = check_from_search_params_file()
    print(f"\n从search_params.json文件检查: {'true' if file_check_result else 'false'}")

    # 如果要验证智能体答案，使用：
    # result = check_hotel_search_shanghai_min_price(agent_answer)
    # print("true" if result else "false")
