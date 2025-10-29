package com.example.Ctrip.home

import java.time.LocalDate

class TrainMateListTabPresenter(private val model: TrainMateListTabContract.Model) : TrainMateListTabContract.Presenter {

    private var view: TrainMateListTabContract.View? = null
    private var allTrains: List<TrainTicket> = emptyList()
    private var currentDepartureCity: String = ""
    private var currentArrivalCity: String = ""
    private var currentDate: LocalDate = LocalDate.now()
    private var currentFilterOptions: TrainFilterOptions = TrainFilterOptions(null, null, false, false, false)
    private var currentSortType: SortType = SortType.DEFAULT

    override fun attachView(view: TrainMateListTabContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadTrainList(departureCity: String, arrivalCity: String, departureDate: LocalDate) {
        view?.showLoading()
        currentDepartureCity = departureCity
        currentArrivalCity = arrivalCity
        currentDate = departureDate

        try {
            val trains = model.getTrainList(departureCity, arrivalCity, departureDate)
            allTrains = trains

            // 更新日期选项
            val dateOptions = model.getDateOptions(departureDate)
            view?.updateDateOptions(dateOptions)

            // 更新出行方式选项
            val transportOptions = model.getTransportOptions()
            view?.updateTransportOptions(transportOptions)

            // 应用筛选和排序
            applyFilterAndSort()
        } catch (e: Exception) {
            view?.showError("加载火车票失败: ${e.message}")
            view?.hideLoading()
        }
    }

    override fun onDateSelected(date: LocalDate) {
        currentDate = date
        loadTrainList(currentDepartureCity, currentArrivalCity, date)
    }

    override fun onTransportSelected(transportId: String) {
        // 切换到其他出行方式（飞机、智能中转）
        // 这里可以根据需要实现
    }

    override fun onFilterChanged(filterOptions: TrainFilterOptions) {
        currentFilterOptions = filterOptions
        view?.updateFilterInfo(filterOptions)
        applyFilterAndSort()
    }

    override fun onSortChanged(sortType: SortType) {
        currentSortType = sortType
        applyFilterAndSort()
    }

    override fun onTrainClicked(trainId: String) {
        view?.navigateToTrainDetail(trainId)
    }

    override fun onBackClicked() {
        view?.navigateBack()
    }

    override fun onSwapCities() {
        // 交换出发地和目的地
        val temp = currentDepartureCity
        currentDepartureCity = currentArrivalCity
        currentArrivalCity = temp
        loadTrainList(currentDepartureCity, currentArrivalCity, currentDate)
    }

    override fun onShowFilter() {
        view?.showFilterDialog()
    }

    override fun onShareClicked() {
        view?.showShareDialog()
    }

    override fun onGrabTicketClicked() {
        view?.showGrabTicketInfo()
    }

    override fun onNewCustomerGiftClicked() {
        view?.showNewCustomerGift()
    }

    private fun applyFilterAndSort() {
        var trains = allTrains

        // 应用筛选
        trains = model.filterTrains(trains, currentFilterOptions)

        // 应用排序
        trains = model.sortTrains(trains, currentSortType)

        if (trains.isNotEmpty()) {
            view?.showTrainList(trains)
        } else {
            view?.showError("未找到符合条件的车次")
        }

        view?.hideLoading()
    }
}
