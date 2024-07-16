package app.revanced.patches.reddit.layout.navigation.annotation

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

@Compatibility([Package("com.reddit.frontpage", arrayOf("2024.25.3"))])
@Target(AnnotationTarget.CLASS)
internal annotation class NavigationButtonsCompatibility

