//
//  MutableValue.swift
//  iosApp
//
//  Created by LocalAdmin on 27.03.2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

func mutableValue<T: AnyObject>(_ initialValue: T) -> MutableValue<T> {
    return MutableValueBuilderKt.MutableValue(initialValue: initialValue) as! MutableValue<T>
}
