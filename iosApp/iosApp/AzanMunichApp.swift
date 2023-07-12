import shared
import SwiftUI
import UIKit
import BackgroundTasks
import WidgetKit

@main
struct AzanMunichApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    private(set) static var sharedApp: SharedApp! = nil

    var body: some Scene {
        WindowGroup {
            ContentView().onAppear {
                AzanMunichApp.sharedApp!.onViewCreated(view: self)
            }
        }
    }

    class AppDelegate: NSObject, UIApplicationDelegate {

        //region UIApplicationDelegate
    
        var jobScheduler : AzanTimesJobScheduler {
            AzanTimesJobScheduler(
                    self,
                    sharedApp.notificationSchedulerService,
                    sharedApp.logService
            )
        }

        func application(
                _ application: UIApplication,
                didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
        ) -> Bool {
            appCodeInjector()
            initSharedApp()
            scheduleAzanTimesNotifications()
            activateAzanTimesJob()
            return true
        }

        //endregion

        //region Main Functions

        private func appCodeInjector() {
            // For using appcode
            #if DEBUG
            var injectionBundlePath = "/Applications/InjectionIII.app/Contents/Resources"
            #if targetEnvironment(macCatalyst)
            injectionBundlePath = "\(injectionBundlePath)/macOSInjection.bundle"
            #elseif os(iOS)
            injectionBundlePath = "\(injectionBundlePath)/iOSInjection.bundle"
            #endif
            Bundle(path: injectionBundlePath)?.load()
            #endif
        }

        private func initSharedApp() {
            var isDebug: Bool = false
            #if DEBUG
            isDebug = true
            #endif
            let platformInfo = SharedPlatformInfo.Ios.init(isDebug:isDebug)
            platformInfo.nativeDelegate = NativeDelegateImpl()
            let sharedApp = SharedApp(platformInfo: platformInfo)
            sharedApp.onCreate()
            AzanMunichApp.sharedApp = sharedApp
        }

        private func scheduleAzanTimesNotifications(){
            sharedApp.notificationSchedulerService.scheduleNotifications(
                    completionHandler: { (result: KotlinUnit?, error: Error?) in
                        // do nothing
                    }
            )
        }
        
        private func activateAzanTimesJob() {
            jobScheduler.registerJob()
            jobScheduler.scheduleNextJob()
        }
        
        //endregion

    }
    
    private class NativeDelegateImpl : SharedPlatformInfoIosNativeDelegate{
        func refreshWidgets() {
            WidgetCenter.shared.reloadAllTimelines()
        }
    }
    
}
