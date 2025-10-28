package com.example.Ctrip.home

class OrderFormTabPresenter(
    private val model: OrderFormTabContract.Model
) : OrderFormTabContract.Presenter {
    
    private var view: OrderFormTabContract.View? = null
    private var currentOrderData: OrderFormData? = null
    
    override fun attachView(view: OrderFormTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadOrderFormData(ticketOptionId: String) {
        view?.showLoading()
        
        try {
            val data = model.getOrderFormData(ticketOptionId)
            if (data != null) {
                currentOrderData = data
                view?.showOrderFormData(data)
            } else {
                view?.showError("无法加载订单信息")
            }
        } catch (e: Exception) {
            view?.showError("加载失败：${e.message}")
        } finally {
            view?.hideLoading()
        }
    }
    
    override fun onBackClicked() {
        view?.onBack()
    }
    
    override fun onCustomerServiceClicked() {
        view?.showCustomerService()
    }
    
    override fun onPassengerAddClicked() {
        view?.showPassengerSelector()
    }
    
    override fun onContactEditClicked() {
        view?.showContactEditor()
    }
    
    override fun onContactVerifyClicked() {
        // 处理联系方式验证逻辑
        currentOrderData?.let { data ->
            val updatedContact = data.contactInfo.copy(isVerified = true)
            updateContactInfo(updatedContact.countryCode, updatedContact.phoneNumber)
        }
    }
    
    override fun onInsuranceOptionSelected(insuranceType: String, optionId: String) {
        currentOrderData?.let { data ->
            val updatedData = when (insuranceType) {
                "flight" -> {
                    val updatedOptions = data.insuranceOptions.flightInsurance.options.map { option ->
                        option.copy(isSelected = option.id == optionId)
                    }
                    val updatedFlightInsurance = data.insuranceOptions.flightInsurance.copy(
                        options = updatedOptions,
                        selectedOptionId = optionId
                    )
                    val updatedInsuranceOptions = data.insuranceOptions.copy(
                        flightInsurance = updatedFlightInsurance
                    )
                    data.copy(insuranceOptions = updatedInsuranceOptions)
                }
                "accident" -> {
                    val updatedAccidentInsurance = data.insuranceOptions.accidentInsurance.copy(
                        isSelected = !data.insuranceOptions.accidentInsurance.isSelected
                    )
                    val updatedInsuranceOptions = data.insuranceOptions.copy(
                        accidentInsurance = updatedAccidentInsurance
                    )
                    data.copy(insuranceOptions = updatedInsuranceOptions)
                }
                else -> data
            }
            
            currentOrderData = updatedData
            model.updateInsuranceSelection(insuranceType, optionId)
            view?.updateInsuranceSelection(insuranceType, optionId)
            
            // 更新总价
            val newTotalPrice = model.calculateTotalPrice(updatedData)
            view?.updateTotalPrice(newTotalPrice)
        }
    }
    
    override fun onInsuranceTermsClicked(insuranceType: String) {
        view?.showInsuranceTerms(insuranceType)
    }
    
    override fun onTravelInsuranceToggle() {
        currentOrderData?.let { data ->
            val updatedTravelInsurance = data.insuranceOptions.travelInsurance.copy(
                isExpanded = !data.insuranceOptions.travelInsurance.isExpanded
            )
            val updatedInsuranceOptions = data.insuranceOptions.copy(
                travelInsurance = updatedTravelInsurance
            )
            val updatedData = data.copy(insuranceOptions = updatedInsuranceOptions)
            
            currentOrderData = updatedData
            view?.showOrderFormData(updatedData)
        }
    }
    
    override fun onNextStepClicked() {
        currentOrderData?.let { data ->
            // 验证表单完整性
            if (validateOrderForm(data)) {
                model.saveOrderAction(data)
                view?.navigateToServiceSelect(data)
            } else {
                view?.showError("请完善订单信息")
            }
        } ?: run {
            view?.showError("订单数据异常")
        }
    }
    
    override fun updateContactInfo(countryCode: String, phoneNumber: String) {
        currentOrderData?.let { data ->
            val updatedContact = ContactInfo(
                countryCode = countryCode,
                phoneNumber = phoneNumber,
                isVerified = false
            )
            val updatedData = data.copy(contactInfo = updatedContact)
            
            currentOrderData = updatedData
            model.updateContactInfo(updatedContact)
            model.saveOrderFormData(updatedData)
            view?.showOrderFormData(updatedData)
        }
    }
    
    override fun addPassenger(passenger: Passenger) {
        currentOrderData?.let { data ->
            val updatedPassengers = data.passengers + passenger
            val updatedData = data.copy(passengers = updatedPassengers)
            
            currentOrderData = updatedData
            model.addPassenger(passenger)
            model.saveOrderFormData(updatedData)
            view?.showOrderFormData(updatedData)
            
            // 更新总价（因为乘客数量变化会影响保险费用）
            val newTotalPrice = model.calculateTotalPrice(updatedData)
            view?.updateTotalPrice(newTotalPrice)
        }
    }
    
    override fun removePassenger(passengerId: String) {
        currentOrderData?.let { data ->
            val updatedPassengers = data.passengers.filterNot { it.id == passengerId }
            val updatedData = data.copy(passengers = updatedPassengers)
            
            currentOrderData = updatedData
            model.removePassenger(passengerId)
            model.saveOrderFormData(updatedData)
            view?.showOrderFormData(updatedData)
            
            // 更新总价
            val newTotalPrice = model.calculateTotalPrice(updatedData)
            view?.updateTotalPrice(newTotalPrice)
        }
    }
    
    private fun validateOrderForm(data: OrderFormData): Boolean {
        // 检查是否有乘机人
        if (data.passengers.isEmpty()) {
            return false
        }
        
        // 检查联系方式是否完整
        if (data.contactInfo.phoneNumber.isBlank()) {
            return false
        }
        
        // 检查乘机人信息是否完整
        return data.passengers.all { passenger ->
            passenger.name.isNotBlank() && passenger.idCard.isNotBlank()
        }
    }
}