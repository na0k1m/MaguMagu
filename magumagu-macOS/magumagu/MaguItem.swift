//
//  MaguItem.swift
//  magumagu
//
//  Created by 김나영 on 8/19/26.
//

import Foundation
struct MaguItem: Identifiable, Codable {
    let id: Int
    let category: String?
    let format: String?
    let tags: [String]?
    let summary: String?
    let extractedText: String?
    let originalText: String?
    let originalImageUrl: String?
    let createdAt: String?
}
