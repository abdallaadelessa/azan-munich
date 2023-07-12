//
// Created by Abdullah Essa on 06.06.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

import shared
import SwiftUI
import UIKit
import BackgroundTasks

class AzanTimesJobScheduler {
    private let jobId = "com.alifwyaa.azanmunich.ios.azan.times.job"

    private let appDelegate: UIApplicationDelegate
    private let notificationSchedulerService: SharedNotificationSchedulerService
    private let logService: SharedLogService

    init(
        _ appDelegate: UIApplicationDelegate,
        _ notificationSchedulerService: SharedNotificationSchedulerService,
        _ logService: SharedLogService
    ) {
        self.appDelegate = appDelegate
        self.notificationSchedulerService = notificationSchedulerService
        self.logService = logService
    }


    //region Job Scheduler

    func registerJob() {
        BGTaskScheduler.shared.register(forTaskWithIdentifier: jobId, using: DispatchQueue.main) { task in
            self.handleJob(task: task as! BGAppRefreshTask)
        }
    }

    func scheduleNextJob() {
        do {
            let timeFromNowInSeconds = notificationSchedulerService.scheduleTimeFromNowInSeconds
            let request = BGAppRefreshTaskRequest(identifier: jobId)
            request.earliestBeginDate = Date(timeIntervalSinceNow: timeFromNowInSeconds)
            try BGTaskScheduler.shared.submit(request)
            logService.m(message: "scheduleBGRefreshJob after \(timeFromNowInSeconds) seconds")
        } catch {
            logService.e(message: "Could not schedule next azan times refresh job: \(error.localizedDescription)", report: true)
        }
    }

    private func handleJob(task: BGAppRefreshTask) {
        scheduleNextJob()
        
        task.expirationHandler = {
            task.setTaskCompleted(success: false)
        }

        notificationSchedulerService.scheduleNotifications(
                completionHandler: { (result: KotlinUnit?, error: Error?) in
                    task.setTaskCompleted(success: error == nil)
                }
        )
    }

    //endregion
    
    
    //region Test
    
    private func test(_ text:String) {
        notificationSchedulerService.testNotification(
            text: text,
            completionHandler: { (result: KotlinUnit?, error: Error?) in
                // do nothing
            }
        )
    }
    
    //endregion
    

}
