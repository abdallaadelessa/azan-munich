//
// Created by Abdullah Essa on 24.05.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import Foundation
import RxSwift
import shared

class HomeViewModel: ObservableObject {

    //region Private Properties

    private let sharedAzanService: SharedAzanService

    private let sharedDateTimeService: SharedDateTimeService

    private let sharedLocalizationService: SharedLocalizationService

    private let sharedSettingsService: SharedSettingsService
    
    private let sharedTrackingService: SharedTrackingService

    private let sideEffectsSubject = PublishSubject<SideEffect>()

    private let disposableBag = CompositeDisposable()

    private var scheduleNextAzanTimeUpdateDisposeKey: CompositeDisposable.DisposeKey?

    private var loadAzanTimesDisposeKey: CompositeDisposable.DisposeKey?

    //endregion

    //region Public Properties

    @Published var state: HomeViewState = HomeViewState()

    lazy var sideEffects: Observable<SideEffect> = sideEffectsSubject

    var strings: SharedStrings {
        sharedLocalizationService.strings
    }

    //endregion

    //region Init & Deinit

    init(
            sharedAzanService: SharedAzanService,
            sharedDateTimeService: SharedDateTimeService,
            sharedLocalizationService: SharedLocalizationService,
            sharedSettingsService: SharedSettingsService,
            sharedTrackingService: SharedTrackingService
    ) {
        self.sharedAzanService = sharedAzanService
        self.sharedDateTimeService = sharedDateTimeService
        self.sharedLocalizationService = sharedLocalizationService
        self.sharedSettingsService = sharedSettingsService
        self.sharedTrackingService = sharedTrackingService
        sharedTrackingService.trackScreen(screen: Screen.home)
        listenToSettingsChanges()
        scheduleNextAzanTimeUpdate()
        loadAzanTimes(date: sharedDateTimeService.getToday()) // SharedDateModel(day: 1, month: 1, year: 2022)
    }

    private func listenToSettingsChanges() {
        sharedSettingsService.eventsFlow.collect(collector: SwiftFlowCollector<SharedSettingsEvent> { [self] event in
            switch (event) {
            case is SharedNotificationsChangedEvent:
                // Update the isEnabled field in the azan list
                let newAzanList: [PlaceHolder<Azan>] = state.azanList.map { (holder: PlaceHolder<Azan>) in
                    holder.mapSuccess { (item: Azan) in
                        item.copy(isEnabled: sharedSettingsService.isAzanEnabled(azanType: item.azanType))
                    }
                }
                state = state.copy(azanList: newAzanList)
            case is SharedLanguageChangedEvent:
                // Reload all the azan times for the current date to get the new localized strings from shared
                state = state.copy(nextAzan: .loading)
                loadAzanTimes(date: state.azanDate.date)
            default:
                ()
            }
        }
        ) { (unit, error) in
            // code which is executed if the Flow object completed
        }
    }

    deinit {
        disposableBag.dispose()
    }

    //endregion

    //region Public Methods

    func goToNextAzanDay() {
        switch (state.nextAzan) {
        case .success(let value):
            if (value.azan.date == state.azanDate.date) {
                return
            }
            loadAzanTimes(date: value.azan.date)
        default:
            return
        }
    }

    func goToDayBefore() {
        let newDay = sharedDateTimeService.getDayBefore(dateModel: state.azanDate.date)
        loadAzanTimes(date: newDay)
    }

    func goToDayAfter() {
        let newDay = sharedDateTimeService.getDayAfter(dateModel: state.azanDate.date)
        loadAzanTimes(date: newDay)
    }

    func reload() {
        loadAzanTimes(date: state.azanDate.date)
    }
    
    func resetNextAzan() {
        updateState { state in
            state.copy(
                nextAzan: PlaceHolder.loading
            )
        }
    }


    //endregion

    //region Main Logic

    private func scheduleNextAzanTimeUpdate() {
        if let key = scheduleNextAzanTimeUpdateDisposeKey {
            disposableBag.remove(for: key)
        }

        // Schedule next azan interval
        scheduleNextAzanTimeUpdateDisposeKey = disposableBag.insert(
                Observable<Int>.interval(RxTimeInterval.seconds(1), scheduler: MainScheduler.instance)
                        .flatMap { _ in
                            self.getNextAzanSingle().asCompletable().catch{ _ in Completable.empty() }
                        }
                        .subscribe()
        )
    }

    private func loadAzanTimes(date: SharedDateModel) {
        if let key = loadAzanTimesDisposeKey {
            disposableBag.remove(for: key)
        }

        updateState { state in
            state.copy(
                    azanDate: AzanDate(
                            date: date,
                            westernFormattedDate: sharedDateTimeService.getWesternFormattedDate(dateModel: date),
                            islamicFormattedDate: sharedDateTimeService.getIslamicFormattedDate(dateModel: date)
                    ),
                    azanList: HomeViewState.getDummyLoadingList()
            )
        }

        loadAzanTimesDisposeKey = disposableBag.insert(getAzanListSingle(date: date).subscribe())
    }

    //endregion

    //region RXSingles

    private func getNextAzanSingle() -> Single<NextAzan> {
        Single<NextAzan>.create(subscribe: { [self]  emitter in

            // Calculate remaining time
            if let oldNextAzanTime = state.nextAzan.getDataOrNull() {
                let dateModel = oldNextAzanTime.azan.date
                let timeModel = oldNextAzanTime.azan.time
                let timeUntil = sharedDateTimeService.getTimeUntil(dateModel: dateModel, timeModel: timeModel)
                if (timeUntil.isInFuture) {
                    let timeUntil = sharedDateTimeService.getTimeUntil(dateModel: dateModel, timeModel: timeModel)
                    let periodText = sharedLocalizationService.getTimeUntilNextAzan(timeUntil: timeUntil)
                    emitter(.success(NextAzan(
                            azan: oldNextAzanTime.azan,
                            periodText: periodText
                    )))
                    return Disposables.create()
                }
            }

            // Load next azan
            sharedAzanService.getNextAzan(completionHandler: { optionalData, optionalErr in
                if let result: Any = optionalData {
                    switch result {
                    case let success as SharedResultSuccess<SharedAzanModel>:
                        // Success
                        let nextAzanTime = toAzanData(model: success.data, isNext: true, isEnabled: false)
                        let timeUntil = sharedDateTimeService.getTimeUntil(dateModel: nextAzanTime.date, timeModel: nextAzanTime.time)
                        let periodText = sharedLocalizationService.getTimeUntilNextAzan(timeUntil: timeUntil)
                        emitter(.success(NextAzan(azan: nextAzanTime, periodText: periodText)))
                    case let error as SharedResultError:
                        // Error
                        emitter(.failure(error.toNSError()))
                    default:
                        ()
                    }
                }
            })
            return Disposables.create()
        }).do(onSuccess: { [self] (nextAzan: NextAzan) in
            // Update State
            updateState { state in
                let newAzanList: [PlaceHolder<Azan>] = self.state.azanList.map { (holder: PlaceHolder<Azan>) in
                    holder.mapSuccess { (item: Azan) in
                        item.copy(isNext: item.id == nextAzan.azan.id)
                    }
                }
                return state.copy(
                        nextAzan: .success(data: nextAzan),
                        azanList: newAzanList
                )
            }
        }).do(onError: { [self] (error: Error) in
            // Update State
            updateState { state in
                return state.copy(nextAzan: .error(message: (error as NSError).domain))
            }
        })
    }

    private func getAzanListSingle(date: SharedDateModel) -> Single<[SharedAzanModel]> {
        let dataSingle: Single<[SharedAzanModel]> = Single<[SharedAzanModel]>.create(subscribe: { [self]  emitter in
            sharedAzanService.getAzanList(dateModel: date, completionHandler: { optionalData, optionalErr in
                if let result: Any = optionalData {
                    switch result {
                    case let success as SharedResultSuccess<AnyObject>:
                        // Success
                        let data = success.data as! [SharedAzanModel]
                        emitter(.success(data))
                    case let error as SharedResultError:
                        // Error
                        emitter(.failure(error.toNSError()))
                    default:
                        ()
                    }
                }
            })
            return Disposables.create()
        })

        let delaySingle: Single<Bool> = Observable.just(true)
                .delay(.milliseconds(300), scheduler: MainScheduler.instance)
                .asSingle()

        return Single.zip(dataSingle, delaySingle).map({ (data: [SharedAzanModel], _: Bool) in data })
                .do(onSuccess: { [self] (azanTimes: [SharedAzanModel]) in
                    // Update State
                    let azanData = azanTimes.map({ (item: SharedAzanModel) -> PlaceHolder<Azan> in
                        let isEnabled = sharedSettingsService.isAzanEnabled(azanType: item.azanType)
                        let isNextAzan: Bool = item.id == state.nextAzan.getDataOrNull()?.azan.id
                        return .success(data: toAzanData(model: item, isNext: isNextAzan, isEnabled: isEnabled))
                    })
                    updateState { state in
                        state.copy(azanList: azanData)
                    }
                })
                .do(onError: { [self] (error: Error) in
                    // Update State
                    updateState { state in
                        return state.copy(azanList: HomeViewState.getDummyErrorList(message: (error as NSError).domain))
                    }
                })
    }

    //endregion

    //region Helpers

    private func updateState(_ updateBlock: (HomeViewState) -> HomeViewState) {
        state = updateBlock(state)
    }

    private func toAzanData(
            model: SharedAzanModel,
            isNext: Bool,
            isEnabled: Bool
    ) -> Azan {
        Azan(
                id: model.id,
                displayName: model.displayName,
                displayTime: model.displayTime,
                azanType: model.azanType,
                date: model.dateModel,
                time: model.timeModel,
                isNext: isNext,
                isEnabled: isEnabled
        )
    }

    //endregion

    //region SideEffects

    class SideEffect {
    }

    //endregion
}
