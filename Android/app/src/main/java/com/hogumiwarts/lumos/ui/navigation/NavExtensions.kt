package com.hogumiwarts.lumos.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * 화면 순서를 고려한 네비게이션 확장 함수
 *
 * @param route 이동할 화면의 경로
 * @param routes 화면 순서 목록 (좌에서 우로 나열된 순서)
 */
fun NavController.navigateWithSlideDirection(route: String, routes: List<String?>) {
    val currentRoute = currentDestination?.route ?: return

    // 이미 같은 화면이면 무시
    if (currentRoute == route) return

    // 화면 위치 인덱스 찾기
    val currentIndex = routes.indexOf(currentRoute)
    val targetIndex = routes.indexOf(route)

    // 인덱스를 찾을 수 없다면 기본 네비게이션 사용
    if (currentIndex == -1 || targetIndex == -1) {
        navigate(route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(graph.findStartDestination().id) {
                saveState = true
            }
        }
        return
    }

    // 애니메이션 방향을 적용한 네비게이션
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}

/**
 * 두 화면 사이의 방향 관계 확인 (UI 배치 순서 기준)
 *
 * @param from 출발 화면 경로
 * @param to 도착 화면 경로
 * @param screenOrder 화면 순서 목록
 * @return true면 왼쪽에서 오른쪽, false면 오른쪽에서 왼쪽
 */
fun getNavigationDirection(from: String?, to: String?, screenOrder: List<String>): Boolean {
    if (from == null || to == null) return true

    val fromIndex = screenOrder.indexOf(from)
    val toIndex = screenOrder.indexOf(to)

    if (fromIndex == -1 || toIndex == -1) return true

    // 바텀 네비게이션에서 왼쪽(인덱스가 작은)에서 오른쪽(인덱스가 큰)으로 이동하는지 여부
    return fromIndex < toIndex
}