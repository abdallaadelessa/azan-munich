//
//  Shimmer.swift
//
//
//  Created by Joshua Homann on 2/20/21.
//

import SwiftUI

struct ShimmeringView: View {
    private let config: ShimmerConfiguration

    @State
    private var startPoint: UnitPoint

    @State
    private var endPoint: UnitPoint

    init(config: ShimmerConfiguration = .defaultConfig) {
        self.config = config
        _startPoint = .init(wrappedValue: config.initialLocation.start)
        _endPoint = .init(wrappedValue: config.initialLocation.end)
    }

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: config.radius).fill(config.bgColor)
            LinearGradient(gradient: config.gradient, startPoint: startPoint, endPoint: endPoint)
                    .opacity(config.opacity)
                    .blendMode(.screen)
                    .onAppear {
                        withAnimation(Animation.linear(duration: config.duration).repeatForever(autoreverses: false)) {
                            startPoint = config.finalLocation.start
                            endPoint = config.finalLocation.end
                        }
                    }
        }.cornerRadius(config.radius)
    }

    public struct ShimmerConfiguration {
        public let bgColor: Color
        public let gradient: Gradient
        public let initialLocation: (start: UnitPoint, end: UnitPoint)
        public let finalLocation: (start: UnitPoint, end: UnitPoint)
        public let duration: TimeInterval
        public let opacity: Double
        public let radius: CGFloat
        public static let defaultConfig = ShimmerConfiguration(
                bgColor:  Colors.SHIMMER_BG,
                gradient: Gradient(stops: [
                    .init(color: .black, location: 0),
                    .init(color: .white, location: 0.3),
                    .init(color: .white, location: 0.7),
                    .init(color: .black, location: 1),
                ]),
                initialLocation: (start: UnitPoint(x: -1, y: 0.5), end: .leading),
                finalLocation: (start: .trailing, end: UnitPoint(x: 2, y: 0.5)),
                duration: 0.9,
                opacity: 0.6,
                radius: 10
        )
    }
}

class ShimmeringView_Previews: PreviewProvider {
    static var previews: some View {
        HStack {
            ShimmeringView().frame(width: 100, height: 20).padding(24)
            Spacer()
            ShimmeringView().frame(width: 100, height: 20).padding(24)
        }
    }

    #if DEBUG
    @objc
    class func injected() {
        UIApplication.shared.windows.first?.rootViewController =
                UIHostingController(rootView: ShimmeringView_Previews.previews)
    }
    #endif
}
