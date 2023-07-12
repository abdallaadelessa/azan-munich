//
//
//  Created by Abdullah Essa on 13.03.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import WidgetKit
import SwiftUI
import shared

struct DayWidgetProvider: TimelineProvider {
    
    private var sharedApp: SharedApp { Singleton.sharedApp }
    
    func placeholder(in context: Context) -> DayWidgetTimelineEntry {
        DayWidgetTimelineEntry(
            date: Date(),
            title: sharedApp.localizationService.strings.dayWidgetTitle,
            item: SharedIosDayWidgetData.TimelineItem(
                startAfterNowInSeconds: 0,
                nextAzanModel: SharedAzanModel(
                    id:"",
                    azanType: SharedAzanType.fajr,
                    displayName: "",
                    displayTime: "",
                    dateModel: SharedDateModel(day: 0,month: 0,year:0),
                    timeModel: SharedTimeModel(hour: 0,minute: 0, second: 0)
                ),
                items: [
                    SharedIosDayWidgetData.TimelineItemItem(
                        displayName: "FAJR",
                        displayTime: "05:14",
                        isNextAzan: false,
                        azanType: SharedAzanType.fajr
                    ),
                    SharedIosDayWidgetData.TimelineItemItem(
                        displayName: "DHUH",
                        displayTime: "13:24",
                        isNextAzan: false,
                        azanType: SharedAzanType.dhuhr
                    ),
                    SharedIosDayWidgetData.TimelineItemItem(
                        displayName: "ASR",
                        displayTime: "16:50",
                        isNextAzan: false,
                        azanType: SharedAzanType.asr
                    ),
                    SharedIosDayWidgetData.TimelineItemItem(
                        displayName: "MAGH",
                        displayTime: "19:42",
                        isNextAzan: true,
                        azanType: SharedAzanType.maghrib
                    ),
                    SharedIosDayWidgetData.TimelineItemItem(
                        displayName: "ISHA",
                        displayTime: "21:18",
                        isNextAzan: false,
                        azanType: SharedAzanType.isha
                    ),
                ]
            )
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (DayWidgetTimelineEntry) -> ()) {
        completion(
            DayWidgetTimelineEntry(
                date: Date(),
                title: sharedApp.localizationService.strings.dayWidgetTitle,
                item: SharedIosDayWidgetData.TimelineItem(
                    startAfterNowInSeconds: 0,
                    nextAzanModel: SharedAzanModel(
                        id:"",
                        azanType: SharedAzanType.fajr,
                        displayName: "",
                        displayTime: "",
                        dateModel: SharedDateModel(day: 0,month: 0,year:0),
                        timeModel: SharedTimeModel(hour: 0,minute: 0, second: 0)
                    ),
                    items: [
                        SharedIosDayWidgetData.TimelineItemItem(
                            displayName: "FAJR",
                            displayTime: "05:14",
                            isNextAzan: false,
                            azanType: SharedAzanType.fajr
                        ),
                        SharedIosDayWidgetData.TimelineItemItem(
                            displayName: "DHUH",
                            displayTime: "13:24",
                            isNextAzan: false,
                            azanType: SharedAzanType.dhuhr
                        ),
                        SharedIosDayWidgetData.TimelineItemItem(
                            displayName: "ASR",
                            displayTime: "16:50",
                            isNextAzan: false,
                            azanType: SharedAzanType.asr
                        ),
                        SharedIosDayWidgetData.TimelineItemItem(
                            displayName: "MAGH",
                            displayTime: "19:42",
                            isNextAzan: true,
                            azanType: SharedAzanType.maghrib
                        ),
                        SharedIosDayWidgetData.TimelineItemItem(
                            displayName: "ISHA",
                            displayTime: "21:18",
                            isNextAzan: false,
                            azanType: SharedAzanType.isha
                        ),
                    ]
                )
            )
        )
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        sharedApp.widgetsDataService
            .getIosDayWidgetData(
                completionHandler: { (optionalData: SharedIosDayWidgetData?, optionalError: Error?) in

                    if let data = optionalData {
                        var entries: [DayWidgetTimelineEntry] = []
                        
                        let nowDate = Date()
                        
                        data.timeline.forEach { item in
                            let startDate = Calendar.current.date(byAdding: .second, value: Int(item.startAfterNowInSeconds), to: nowDate)!
                            entries.append(
                                DayWidgetTimelineEntry(
                                    date: startDate,
                                    title: data.title,
                                    item: item
                                )
                            )
                        }
                        
                        let nextUpdateDate = Calendar.current.date(byAdding: .hour, value: Int(data.nextUpdateInHours), to: nowDate)!
                        
                        completion(Timeline(entries: entries, policy: .after(nextUpdateDate)))
                        
                    }else{
                        // should never happen
                    }
                    
                }
            )
    }
}

 struct DayWidgetTimelineEntry: TimelineEntry {
    let date: Date
    let title: String
    let item: SharedIosDayWidgetData.TimelineItem
}

 struct DayWidgetEntryView : View {
    var entry: DayWidgetProvider.Entry
    
    @ViewBuilder
    var body: some View {
        ZStack{
            DayWidgetColors.BG.ignoresSafeArea()
            VStack{
                HStack(alignment: .lastTextBaseline){
                    Image("ic_next_prayer")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 30, height: 30)
                        .padding(.trailing,8)
                    Text(entry.title).font(.title2)
                }.padding(.vertical,18)
                HStack(alignment: .center){
                    ForEach(entry.item.items, id: \.self) { item in
                        renderItem(item)
                        if(item.azanType != SharedAzanType.isha){
                            Divider().frame(width: 0.5).background(DayWidgetColors.TEXT)
                        }
                    }
                }
                .padding(.horizontal,8)
            }
        }
    }
     
     private func renderItem(_ item: SharedIosDayWidgetData.TimelineItemItem) -> some View {
         let displayName : String = item.displayNameShort
         let displayTime : String = item.displayTime
        
        let textColor: Color = {
            if item.isNextAzan {
                return DayWidgetColors.TEXT_SELECTED
            } else {
                return DayWidgetColors.TEXT
            }
        }()
         
        return VStack(alignment: .center, spacing: 10) {
                Text(displayTime).font(.subheadline)
                    .foregroundColor(textColor)
                Text(displayName.uppercased()).font(.caption)
                    .foregroundColor(textColor)
         }.frame(
            minWidth: 0,
            maxWidth: .infinity,
            minHeight: 0,
            maxHeight: .infinity,
            alignment: .center
          )
     }
}

struct DayWidgetColors{
    static var BG: Color {
        Color("color_background")
    }
    static var TEXT: Color {
        Color("color_text")
    }
    static var TEXT_SELECTED: Color {
        Color("widget_text_selected")
    }
}

struct DayWidget: Widget {
    let kind: String = "daywidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: DayWidgetProvider()) { entry in
            DayWidgetEntryView(entry: entry)
        }
        .configurationDisplayName(Singleton.sharedApp.localizationService.strings.dayWidgetAppName)
        .description(Singleton.sharedApp.localizationService.strings.dayWidgetAppDesc)
        .supportedFamilies([.systemMedium])
    }
}
