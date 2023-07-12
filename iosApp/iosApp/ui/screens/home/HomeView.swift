import shared
import SwiftUI

struct HomeView: View {
    
    @ObservedObject
    private var viewModel: HomeViewModel = HomeViewModel(
        sharedAzanService: AzanMunichApp.sharedApp!.azanService,
        sharedDateTimeService: AzanMunichApp.sharedApp!.dateTimeService,
        sharedLocalizationService: AzanMunichApp.sharedApp!.localizationService,
        sharedSettingsService: AzanMunichApp.sharedApp!.settingsService,
        sharedTrackingService: AzanMunichApp.sharedApp!.trackingService
    )
    
    var body: some View {
        let screenHorizontalPadding: CGFloat = 24
        let screenVerticalPadding: CGFloat = 16
        ZStack {
            ScrollView(.vertical) {
                GeometryReader { (geometry: GeometryProxy) in
                    VStack(spacing: 15) {
                        createPageHeader(geometry: geometry, viewModel: viewModel)
                        createDateNavigator(geometry: geometry, viewModel: viewModel)
                            .padding(.horizontal, screenHorizontalPadding)
                        createPageItems(geometry: geometry, viewModel: viewModel)
                            .padding(.horizontal, screenHorizontalPadding)
                    }.padding(.vertical, screenVerticalPadding)
                }
            }.foregroundColor(Colors.DEFAULT_ACCENT)
        }
        .navigationBarTitle(viewModel.strings.appName)
    }
    
    //region Next Azan Time
    
    private func createPageHeader(geometry: GeometryProxy, viewModel: HomeViewModel) -> some View {
        let state = viewModel.state
        let nextAzanTime: PlaceHolder<NextAzan> = state.nextAzan
        
        return VStack(alignment: .center, spacing: 5) {
            switch nextAzanTime {
            case .loading, .error:
                ShimmeringView().frame(width: 100, height: 10).background(Colors.DEFAULT_BG)
                ShimmeringView().frame(width: 200, height: 10).background(Colors.DEFAULT_BG).padding(.vertical, 16)
                ShimmeringView().frame(width: 100, height: 10).background(Colors.DEFAULT_BG)
            case .success(let value):
                HStack(alignment: .lastTextBaseline){
                    Image(Drawables.ICON_NEXT_PRAYER)
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 30, height: 30)
                    Text(viewModel.strings.nextPrayer).font(.headline)
                }
                Text(value.azan.displayName + " " + value.azan.displayTime).font(.largeTitle)
                Text(value.periodText).font(.body)
            }
        }.foregroundColor(Colors.DEFAULT_ACCENT).frame(height: 100).onTapGesture {
            viewModel.goToNextAzanDay()
        }
    }
    
    //endregion
    
    
    //region Azan Day
    
    private func createDateNavigator(geometry: GeometryProxy, viewModel: HomeViewModel) -> some View {
        let state = viewModel.state
        let westernFormattedDate = state.azanDate.westernFormattedDate
        let islamicFormattedDate = state.azanDate.islamicFormattedDate
        
        let iconSize: CGFloat = 35
        let iconPadding = EdgeInsets(top: 0, leading: 10, bottom: 0, trailing: 10)
        
        return HStack {
            Image(Drawables.ICON_ARROW_LEFT)
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: iconSize, height: iconSize)
                .padding(iconPadding)
                .onTapGesture {
                    viewModel.goToDayBefore()
                }
            Spacer()
            VStack {
                Text(westernFormattedDate).font(.title2)
                Text(islamicFormattedDate).font(.callout)
            }.padding(iconPadding)
            Spacer()
            Image(Drawables.ICON_ARROW_RIGHT)
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: iconSize, height: iconSize)
                .padding(iconPadding)
                .onTapGesture {
                    viewModel.goToDayAfter()
                }
        }.padding(.vertical, 8)
        .background(Colors.DEFAULT_LIST_ITEM_BG)
        .foregroundColor(Colors.DEFAULT_ACCENT)
        .cornerRadius(40)
        .overlay(RoundedRectangle(cornerRadius: 40).stroke(Color.black, lineWidth: 1))
    }
    
    //endregion
    
    //region Azan Times
    
    private func createPageItems(geometry: GeometryProxy, viewModel: HomeViewModel) -> some View {
        let state = viewModel.state
        let azanData = state.azanList
        
        let verticalPadding: CGFloat = 16
        let textHeight: CGFloat = 16.0
        let cornerRadius: CGFloat = 40
        
        let listItemBgColor =  Colors.DEFAULT_LIST_ITEM_BG
        
        return VStack {
            
            ForEach(azanData, id: \.self) { (item: PlaceHolder<Azan>) in
                let isNext = item.getDataOrNull()?.isNext ?? false
                let isEnabled = item.getDataOrNull()?.isEnabled ?? false
                
                HStack {
                    switch item {
                    case .error(let message):
                        Text(message)
                            .font(.title2)
                            .frame(height: textHeight)
                            .padding(.vertical, verticalPadding)
                            .padding(.leading, verticalPadding)
                        Spacer()
                        Button(viewModel.strings.retry) { viewModel.reload() }
                            .font(.title2)
                            .foregroundColor(Colors.DEFAULT_HIGHLIGHT)
                            .frame(height: textHeight)
                            .padding(.trailing, verticalPadding)
                    case .loading:
                        ShimmeringView()
                            .frame(width: textHeight, height: textHeight)
                            .background(listItemBgColor)
                            .padding(.leading, verticalPadding)
                            .padding(.trailing, 8)
                        ShimmeringView()
                            .frame(width: 50, height: textHeight)
                            .background(listItemBgColor)
                            .padding(.vertical, verticalPadding)
                        Spacer()
                        ShimmeringView()
                            .frame(width: 60, height: textHeight)
                            .background(listItemBgColor)
                            .padding(.trailing, verticalPadding)
                    case .success(let azanData):
                        Image(isEnabled ? Drawables.ICON_AZAN_ON : Drawables.ICON_AZAN_OFF)
                            .renderingMode(.template)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 22, height: 22)
                            .padding(.leading, verticalPadding)
                            .padding(.trailing, 8)
                        Text(azanData.displayName)
                            .font(.title2)
                            .frame(height: textHeight)
                            .padding(.vertical, verticalPadding)
                        Spacer()
                        Text(azanData.displayTime)
                            .font(.title2)
                            .frame(height: textHeight)
                            .padding(.trailing, verticalPadding)
                    }
                }
                .foregroundColor(isNext ? Colors.DEFAULT_ACCENT : Colors.DEFAULT_ACCENT)
                .background(isNext ? Colors.DEFAULT_HIGHLIGHT : listItemBgColor)
                .cornerRadius(cornerRadius).overlay(
                    RoundedRectangle(cornerRadius: cornerRadius).stroke(Color.black, lineWidth: 1)
                )
                
            }.padding(.vertical, 4)
        }
    }
    
    //endregion
    
    //region Public Methods
    
    func reloadAndResetNextAzan(){
        viewModel.resetNextAzan()
        viewModel.reload()
    }
    
    //endregion
    
}

class HomeView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            NavigationLink(destination:HomeView().navigationBarTitleDisplayMode(.inline).navigationBarTitle("Azan Munich")) {
                Text("Hello, World!")
            }
        }
    }
    
    #if DEBUG
    @objc
    class func injected() {
        UIApplication.shared.windows.first?.rootViewController =
            UIHostingController(rootView: HomeView_Previews.previews)
    }
    #endif
}
