//
//  nextprayerwidget.swift
//  nextprayerwidget
//
//  Created by Abdullah Essa on 13.03.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import WidgetKit
import SwiftUI
import shared

struct NextPrayerWidgetProvider: TimelineProvider {
    
    private var sharedApp: SharedApp { Singleton.sharedApp }
    
    func placeholder(in context: Context) -> NextPrayerWidgetTimelineEntry {
        NextPrayerWidgetTimelineEntry(
            date: Date(),
            title: "...",
            item:  SharedIosNextPrayerWidgetData.TimelineItem(
                startAfterNowInSeconds: 60 * 10,
                azanModel: SharedAzanModel(
                    id:"",
                    azanType: SharedAzanType.fajr,
                    displayName: "",
                    displayTime: "",
                    dateModel: SharedDateModel(day: 0,month: 0,year:0),
                    timeModel: SharedTimeModel(hour: 0,minute: 0, second: 0)
                ),
                displayName: "...",
                displayTime: "..."
            )
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (NextPrayerWidgetTimelineEntry) -> ()) {
        completion(
            NextPrayerWidgetTimelineEntry(
                date: Date(),
                title: sharedApp.localizationService.strings.nextPrayer,
                item:  SharedIosNextPrayerWidgetData.TimelineItem(
                    startAfterNowInSeconds: 60 * 10,
                    azanModel: SharedAzanModel(
                        id:"",
                        azanType: SharedAzanType.fajr,
                        displayName: "",
                        displayTime: "",
                        dateModel: SharedDateModel(day: 0,month: 0,year:0),
                        timeModel: SharedTimeModel(hour: 0,minute: 0, second: 0)
                    ),
                    displayName: sharedApp.localizationService.strings.maghrib,
                    displayTime: "18:27"
                )
            )
        )
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {  
        sharedApp.widgetsDataService
            .getIosNextPrayerWidgetData(
                completionHandler: { (optionalData: SharedIosNextPrayerWidgetData?, optionalError: Error?) in
                    
                    if let data = optionalData {
                        var entries: [NextPrayerWidgetTimelineEntry] = []
                        
                        let nowDate = Date()
                        
                        data.timeline.forEach { item in
                            let startDate = Calendar.current.date(byAdding: .second, value: Int(item.startAfterNowInSeconds), to: nowDate)!
                            entries.append(
                                NextPrayerWidgetTimelineEntry(
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

struct NextPrayerWidgetTimelineEntry: TimelineEntry {
    let date: Date
    let title: String
    let item: SharedIosNextPrayerWidgetData.TimelineItem
}

struct NextPrayerWidgetEntryView : View {
    var entry: NextPrayerWidgetProvider.Entry
    
    @ViewBuilder
    var body: some View {
        ZStack{
            NextPrayerWidgetColors.BG.ignoresSafeArea()
            let title : String = entry.title
            let displayName : String = entry.item.displayName
            let displayTime : String = entry.item.displayTime
            VStack{
                Image("ic_next_prayer")
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 40, height: 40)
                Text(title)
                    .font(.caption)
                    .bold()
                Text(displayTime).font(.title)
                    .foregroundColor(NextPrayerWidgetColors.TITLE)
                Text(displayName.uppercased()).font(.subheadline)
                    .foregroundColor(NextPrayerWidgetColors.SUBTITLE)
                    .bold()
            }
        }
    }
}

struct NextPrayerWidgetColors{
    static var BG: Color {
        Color("color_background")
    }
    static var TITLE: Color {
        Color("widget_primary_color")
    }
    static var SUBTITLE: Color {
        Color("widget_primary_color")
    }
}

struct NextPrayerWidget: Widget {
    let kind: String = "nextprayerwidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: NextPrayerWidgetProvider()) { entry in
            NextPrayerWidgetEntryView(entry: entry)
        }
        .configurationDisplayName(Singleton.sharedApp.localizationService.strings.nextPrayerWidgetAppName)
        .description(Singleton.sharedApp.localizationService.strings.nextPrayerWidgetAppDesc)
        .supportedFamilies([.systemSmall])
    }
}

