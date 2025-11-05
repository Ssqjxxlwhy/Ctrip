import subprocess
import json
import os

# 任务22：进入 "酒店" 页面，为我筛选北京评分最高的前 3 家酒店的平均价
# 检查条件：智能体计算的平均价格是否与真实的平均价格一致

# 北京酒店数据（从HotelListTabModel.kt中提取）
BEIJING_HOTELS = [
    {"id": "hotel_bj_001", "name": "北京一家人民宿(大兴国际机场店)", "rating": 4.7, "price": 104},
    {"id": "hotel_bj_002", "name": "北京兰岩酒店(德胜门鼓楼大街地铁站店)", "rating": 4.5, "price": 151},
    {"id": "hotel_bj_003", "name": "北京旭阳趣舍(北京大兴国际机场店)", "rating": 4.8, "price": 65},
    {"id": "hotel_bj_004", "name": "北京王府井希尔顿酒店", "rating": 4.6, "price": 380},
    {"id": "hotel_bj_005", "name": "北京颐和园如家民宿", "rating": 4.3, "price": 180}
]

def get_expected_average_price_top3_rating():
    """
    计算真实答案：北京评分最高的前3家酒店的平均价格
    """
    # 按评分降序排序
    sorted_hotels = sorted(BEIJING_HOTELS, key=lambda x: x["rating"], reverse=True)
    # 取前3个
    top3 = sorted_hotels[:3]
    # 计算平均价格
    total_price = sum(hotel["price"] for hotel in top3)
    average_price = total_price / 3

    return average_price, top3

def check_hotel_search_beijing_top3_avg_price(agent_answer):
    """
    检验函数：验证智能体答案是否正确

    参数:
        agent_answer: 智能体生成的答案，可以是：
                     - 数字: 183 或 183.0
                     - 字符串: "183" 或 "¥183"
                     - 包含平均价格的字典: {"average_price": 183}

    返回:
        True: 验证成功（允许±1元的误差）
        False: 验证失败
    """
    # 计算真实答案
    expected_avg, _ = get_expected_average_price_top3_rating()

    # 处理不同类型的智能体答案
    try:
        if isinstance(agent_answer, (int, float)):
            agent_avg = float(agent_answer)
        elif isinstance(agent_answer, str):
            # 移除货币符号和空格
            clean_str = agent_answer.replace("¥", "").replace("￥", "").replace(",", "").strip()
            agent_avg = float(clean_str)
        elif isinstance(agent_answer, dict):
            # 尝试从字典中获取平均价格
            agent_avg = float(agent_answer.get("average_price", agent_answer.get("avg", 0)))
        else:
            return False

        # 允许±1元的误差（因为可能有四舍五入）
        return abs(agent_avg - expected_avg) <= 1

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

    # 3. 检查最新记录是否包含指定字段且显示了列表
    try:
        latest_event = data["search_events"][-1]
        return (latest_event.get("type") == "hotel_search" and
                latest_event.get("city") == "北京" and
                latest_event.get("sortBy") == "rating" and
                latest_event.get("limit") == 3 and
                latest_event.get("calculation") == "average_price" and
                latest_event.get("list_shown") == True)
    except:
        return False

if __name__ == "__main__":
    # 计算并打印预期答案
    expected_avg, top3_hotels = get_expected_average_price_top3_rating()

    print(f"预期答案：北京评分最高的前3家酒店的平均价格 = {expected_avg:.2f}元")
    print("\n评分最高的前3家酒店：")
    for i, hotel in enumerate(top3_hotels, 1):
        print(f"  {i}. {hotel['name']} - 评分{hotel['rating']} 价格{hotel['price']}元")

    print(f"\n计算过程：({top3_hotels[0]['price']} + {top3_hotels[1]['price']} + {top3_hotels[2]['price']}) / 3 = {expected_avg:.2f}元")

    # 如果要从文件检查，使用：
    file_check_result = check_from_search_params_file()
    print(f"\n从search_params.json文件检查: {'true' if file_check_result else 'false'}")

    # 如果要验证智能体答案，使用：
    # result = check_hotel_search_beijing_top3_avg_price(agent_answer)
    # print("true" if result else "false")
