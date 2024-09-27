package app.revanced.patches.reddit.layout.navigation

import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.reddit.layout.navigation.annotation.NavigationButtonsCompatibility
import app.revanced.patches.reddit.layout.navigation.fingerprints.BottomNavScreenFingerprint
import app.revanced.patches.reddit.layout.navigation.fingerprints.BottomNavScreenFingerprint.indexOfGetDimensionPixelSize
import app.revanced.patches.reddit.layout.navigation.fingerprints.BottomNavScreenHandlerFingerprint
import app.revanced.patches.reddit.layout.navigation.fingerprints.BottomNavScreenHandlerFingerprint.indexOfGetItems
import app.revanced.patches.reddit.layout.navigation.fingerprints.BottomNavScreenOnGlobalLayoutFingerprint
import app.revanced.patches.reddit.utils.integrations.Constants.PATCHES_PATH
import app.revanced.patches.reddit.utils.settings.SettingsBytecodePatch.Companion.updateSettingsStatus
import app.revanced.patches.reddit.utils.settings.SettingsBytecodePatch.Companion.upward202426
import app.revanced.patches.reddit.utils.settings.SettingsPatch
import app.revanced.util.findClass
import app.revanced.util.getInstruction
import app.revanced.util.indexOfFirstInstructionOrThrow
import app.revanced.util.resultOrThrow
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.FiveRegisterInstruction
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction

@Patch
@Name("Hide navigation buttons")
@Description("Adds options to hide buttons in the navigation bar.")
@DependsOn([SettingsPatch::class])
@NavigationButtonsCompatibility
@Suppress("unused")
class NavigationButtonsPatch : BytecodePatch(
    listOf(
        BottomNavScreenHandlerFingerprint,
        BottomNavScreenFingerprint
    )
) {
    companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR =
            "$PATCHES_PATH/NavigationButtonsPatch;"

        private const val INTEGRATIONS_METHOD_DESCRIPTOR =
            "$PATCHES_PATH/NavigationButtonsPatch;->hideNavigationButtons(Landroid/view/ViewGroup;)V"
    }

    override fun execute(context: BytecodeContext) {

        if (upward202426) {
            println("WARNING: Hide navigation buttons patch is not supported in this version. Use Reddit 2024.25.3 or earlier.")
            return
        }

        val bottomNavScreenFingerprintResult = BottomNavScreenFingerprint.result

        if (bottomNavScreenFingerprintResult != null) {
            bottomNavScreenFingerprintResult.let {
                it.mutableMethod.apply {
                    val startIndex = indexOfGetDimensionPixelSize(this)
                    val targetIndex = indexOfFirstInstructionOrThrow(startIndex, Opcode.NEW_INSTANCE)
                    val targetReference = getInstruction<ReferenceInstruction>(targetIndex).reference.toString()
                    val bottomNavScreenMutableClass =
                        context.findClass(targetReference)!!.mutableClass

                    BottomNavScreenOnGlobalLayoutFingerprint.resolve(context, bottomNavScreenMutableClass)
                }
            }

            BottomNavScreenOnGlobalLayoutFingerprint.resultOrThrow().let {
                it.mutableMethod.apply {
                    val startIndex = it.scanResult.patternScanResult!!.startIndex
                    val targetRegister =
                        getInstruction<FiveRegisterInstruction>(startIndex).registerC

                    addInstruction(
                        startIndex + 1,
                        "invoke-static {v$targetRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->hideNavigationButtons(Landroid/view/ViewGroup;)V"
                    )
                }
            }
        } else {
            // Legacy method.
            BottomNavScreenHandlerFingerprint.resultOrThrow().mutableMethod.apply {
                val targetIndex = indexOfGetItems(this) + 1
                val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

                addInstructions(
                    targetIndex + 1, """
                        invoke-static {v$targetRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->hideNavigationButtons(Ljava/util/List;)Ljava/util/List;
                        move-result-object v$targetRegister
                        """
                )
            }
        }

        updateSettingsStatus("enableNavigationButtons")

    }
}
