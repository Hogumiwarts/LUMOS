package com.hogumiwarts.lumos.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * 화면 순서를 고려한 네비게이션 확장 함수
 *
 * @param route 이동할 화면의 경로
 * @param routes 화면 순서 목록 (왼쪽에서 오른쪽으로 나열된 순서)
 */
fun NavController.navigateWithSlideDirection(route: String, routes: List<String>) {
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
    // 이 함수에서는 실제 애니메이션이 적용되지 않고, NavGraph에서 정의된 애니메이션이 사용됨
    // 여기서는 적절한 네비게이션 명령만 수행
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}