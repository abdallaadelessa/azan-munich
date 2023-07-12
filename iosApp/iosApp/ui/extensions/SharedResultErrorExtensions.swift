//
//  SharedResultErrorExtensions.swift
//  iosApp
//
//  Created by Abdullah Essa on 09.07.21.
//  Copyright © 2021 orgName. All rights reserved.
//
import Foundation
import shared

public extension SharedResultError {
    
    func toNSError() -> NSError {
        let code = Int(id)
        return NSError(domain:message,code:code)
    }
}
