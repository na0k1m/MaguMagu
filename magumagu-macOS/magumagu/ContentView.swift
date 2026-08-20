//
//  ContentView.swift
//  magumagu
//
//  Created by 김나영 on 8/19/26.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var viewModel = MaguViewModel()
    
    var body: some View {
        NavigationView {
            List(viewModel.items) { item in
                VStack(alignment: .leading, spacing: 8) {
                    Text(item.summary ?? "요약 없음")
                        .font(.headline)
                    
                    HStack {
                        Text(item.category ?? "카테고리 없음")
                            .font(.caption)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color.blue.opacity(0.2))
                            .cornerRadius(8)
                        
                        Text(item.createdAt?.prefix(10) ?? "") // 날짜 앞부분(YYYY-MM-DD)만 자르기
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                }
                .padding(.vertical, 4)
            }
            .navigationTitle("마구마구 서랍장")
            .onAppear {
                viewModel.fetchMagus()
            }
        }
    }
}

#Preview {
    ContentView()
}
