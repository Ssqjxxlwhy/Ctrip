package com.example.Ctrip.home

// 乘客类型
enum class PassengerType {
    ADULT,      // 成人票
    STUDENT,    // 学生票
    CHILD       // 儿童票
}

// 证件类型
enum class IDType {
    ID_CARD,    // 二代身份证
    PASSPORT,   // 护照
    OTHER       // 其他
}

// 乘客信息
data class TrainPassenger(
    val name: String,
    val passengerType: PassengerType,
    val idType: IDType,
    val idNumber: String,
    val phone: String
)

// 车厢信息
data class CarriageInfo(
    val number: String,          // 车厢编号，如"01", "02"
    val tag: String = "",        // 标签，如"出站快", "就餐近", "餐车"
    val isSelected: Boolean = false
)

// 座位位置类型
enum class SeatPosition {
    WINDOW,     // 靠窗
    AISLE,      // 过道
    NEAR_DOOR,  // 靠车门
    FAR_DOOR    // 远离车门
}

// 座位选择信息
data class SeatSelectionInfo(
    val position: SeatPosition,
    val label: String,      // 如"A", "B", "C"等
    val description: String, // 如"上下车更方便", "远离卫生间"
    val price: Int,          // 选座价格
    val isSelected: Boolean = false
)

// 订单确认数据
data class InfoConfirmData(
    val trainNumber: String,
    val departureCity: String,
    val arrivalCity: String,
    val departureStation: String,
    val arrivalStation: String,
    val departureDate: String,          // 如"10-30"
    val departureTime: String,          // 如"06:37"
    val arrivalTime: String,            // 如"12:38"
    val duration: String,               // 如"6时01分"
    val seatType: String,               // 如"二等座"
    val ticketPrice: Int,               // 票价
    val insuranceName: String,          // 保险名称，如"携程全能保障"
    val insurancePrice: Int,            // 保险价格
    val currentUser: TrainPassenger,    // 当前用户信息
    val availableCarriages: List<CarriageInfo>,  // 可选车厢
    val availableSeats: List<SeatSelectionInfo>, // 可选座位
    val newCustomerDiscount: Int = 10,  // 新客优惠金额
    val points: Int = 2880,             // 可得积分
    val pointsValue: Double = 28.8,     // 积分价值
    val isStudentTicket: Boolean = false, // 是否学生票
    val originalPrice: Int = 0          // 原价（用于显示学生票折扣对比）
)

interface InfoConfirmTabContract {

    interface View {
        fun showConfirmData(data: InfoConfirmData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateBack()
        fun navigateToLogin()
        fun navigateToPayment(confirmData: InfoConfirmData)
        fun showPassengerPicker()
        fun showPhoneEditor()
        fun showSeatDiagram()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadConfirmData(
            trainId: String,
            ticketOption: TrainTicketOption,
            selectedSeatType: String
        )
        fun onBackClicked()
        fun onLoginClicked()
        fun onAddPassengerClicked()
        fun onPhoneEditClicked()
        fun onCarriageSelected(carriageNumber: String)
        fun onSeatSelected(position: SeatPosition)
        fun onConfirmOrderClicked()
        fun onShowDetailClicked()
        fun onSeatDiagramClicked()
        fun saveOrderAction(data: InfoConfirmData)
    }

    interface Model {
        fun getConfirmData(
            trainId: String,
            ticketOption: TrainTicketOption,
            selectedSeatType: String
        ): InfoConfirmData?
        fun getCurrentUser(): TrainPassenger
        fun getCarriages(): List<CarriageInfo>
        fun getSeatOptions(): List<SeatSelectionInfo>
        fun saveOrderAction(data: InfoConfirmData)
    }
}
