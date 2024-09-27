package app.revanced.patches.reddit.misc.openlink

import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.reddit.misc.openlink.ScreenNavigatorMethodResolverPatch.Companion.screenNavigatorMethod
import app.revanced.patches.reddit.utils.annotation.RedditCompatibility
import app.revanced.patches.reddit.utils.integrations.Constants.PATCHES_PATH
import app.revanced.patches.reddit.utils.settings.SettingsBytecodePatch.Companion.updateSettingsStatus
import app.revanced.patches.reddit.utils.settings.SettingsPatch
import app.revanced.util.getInstruction
import app.revanced.util.indexOfFirstStringInstructionOrThrow

@Patch
@Name("Open links externally")
@Description("Adds an option to always open links in your browser instead of in the in-app-browser.")
@DependsOn([SettingsPatch::class, ScreenNavigatorMethodResolverPatch::class])
@RedditCompatibility
@Suppress("unused")
class OpenLinksExternallyPatch : BytecodePatch() {
    companion object {
        private const val INTEGRATIONS_METHOD_DESCRIPTOR =
            "$PATCHES_PATH/OpenLinksExternallyPatch;"
    }

    override fun execute(context: BytecodeContext) {

        screenNavigatorMethod.apply {
            val insertIndex = indexOfFirstStringInstructionOrThrow("uri") + 2

            addInstructions(
                insertIndex, """
                    invoke-static {p1, p2}, $INTEGRATIONS_METHOD_DESCRIPTOR->openLinksExternally(Landroid/app/Activity;Landroid/net/Uri;)Z
                    move-result v0
                    if-eqz v0, :dismiss
                    return-void
                    """, listOf(ExternalLabel("dismiss", getInstruction(insertIndex)))
            )
        }

        updateSettingsStatus("enableOpenLinksExternally")

    }
}