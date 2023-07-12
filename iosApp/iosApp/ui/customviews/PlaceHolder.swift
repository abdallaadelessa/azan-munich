//
// Created by Abdullah Essa on 28.05.21.
// Copyright (c) 2021 orgName. All rights reserved.
//

// swiftlint:disable nesting
enum PlaceHolder<T>: Hashable where T: Hashable {
    func hash(into hasher: inout Hasher) {
        switch self {
        case .loading:
            hasher.combine("loading")
        case .success(let value):
            hasher.combine(value)
        case .error:
            hasher.combine("error")
        }
    }

    // swiftlint:disable pattern_matching_keywords
    static func ==(lhs: PlaceHolder, rhs: PlaceHolder) -> Bool {
        switch (lhs, rhs) {
        case (.loading, .loading), (.error, .error):
            return true
        case (.success(let value1), .success(let value2)):
            return value1 == value2
        default:
            return false
        }
    }

    func isLoading() -> Bool {
        switch (self) {
        case .loading:
            return true
        default:
            return false
        }
    }

    func isError() -> Bool {
        switch (self) {
        case .error:
            return true
        default:
            return false
        }
    }

    func isSuccess() -> Bool {
        switch (self) {
        case .success:
            return true
        default:
            return false
        }
    }

    func getDataOrNull() -> T? {
        switch (self) {
        case .success(let value):
            return value
        default:
            return nil
        }
    }

    func mapSuccess<E>(_ mapper: (T) -> E) -> PlaceHolder<E> {
        var newPlaceHolder: PlaceHolder<E>
        switch (self) {
        case .loading:
            newPlaceHolder = .loading
        case .success(data: let data):
            newPlaceHolder = .success(data: mapper(data))
        case .error(message: let message):
            newPlaceHolder = .error(message: message)
        }
        return newPlaceHolder
    }

    case loading
    case success(data: T)
    case error(message: String)
}
