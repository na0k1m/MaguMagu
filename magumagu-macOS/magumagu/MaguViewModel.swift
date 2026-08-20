//
//  MaguViewModel.swift
//  magumagu
//
//  Created by 김나영 on 8/19/26.
//


import Foundation
internal import Combine

class MaguViewModel: ObservableObject {
    // 화면에 그려질 데이터 목록
    @Published var items: [MaguItem] = []
    
    // GET
    func fetchMagus() {
        guard let url = URL(string: "http://localhost:8080/api/magu") else { return }
        
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let data = data {
                do {
                    // JSON 데이터를 우리가 만든 MaguItem 배열로 변환
                    let decodedData = try JSONDecoder().decode([MaguItem].self, from: data)
                    
                    // UI를 업데이트하는 코드는 반드시 메인 스레드에서 실행
                    DispatchQueue.main.async {
                        self.items = decodedData
                    }
                } catch {
                    print("JSON 파싱 에러: \(error)")
                }
            }
        }.resume()
    }
}
