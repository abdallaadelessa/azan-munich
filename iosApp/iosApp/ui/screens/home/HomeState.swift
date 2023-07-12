import shared

struct HomeViewState {
    let azanDate: AzanDate
    let nextAzan: PlaceHolder<NextAzan>
    let azanList: [PlaceHolder<Azan>]

    init() {
        azanDate = AzanDate(
                date: SharedDateModel(day: 1, month: 1, year: 2021),
                westernFormattedDate: "",
                islamicFormattedDate: ""
        )
        nextAzan = .loading
        azanList = [PlaceHolder<Azan>](repeating: .loading, count: 6)
    }

    init(
            azanDate: AzanDate,
            nextAzan: PlaceHolder<NextAzan>,
            azanList: [PlaceHolder<Azan>]
    ) {
        self.azanDate = azanDate
        self.nextAzan = nextAzan
        self.azanList = azanList
    }

    func copy(
            azanDate: AzanDate? = nil,
            nextAzan: PlaceHolder<NextAzan>? = nil,
            azanList: [PlaceHolder<Azan>]? = nil
    ) -> HomeViewState {
        HomeViewState(
                azanDate: azanDate ?? self.azanDate,
                nextAzan: nextAzan ?? self.nextAzan,
                azanList: azanList ?? self.azanList
        )
    }

    //=========>

    static func getDummyLoadingList() -> [PlaceHolder<Azan>] {
        [PlaceHolder<Azan>](repeating: .loading, count: 6)
    }

    static func getDummyErrorList(message: String) -> [PlaceHolder<Azan>] {
        [PlaceHolder<Azan>](repeating: .error(message: message), count: 6)
    }

}
