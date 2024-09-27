package app.revanced.patches.reddit.layout.recentlyvisited

import app.revanced.patcher.BytecodeContext
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.reddit.layout.recentlyvisited.fingerprints.CommunityDrawerPresenterConstructorFingerprint
import app.revanced.patches.reddit.layout.recentlyvisited.fingerprints.CommunityDrawerPresenterConstructorFingerprint.indexOfHeaderItem
import app.revanced.patches.reddit.layout.recentlyvisited.fingerprints.CommunityDrawerPresenterFingerprint
import app.revanced.patches.reddit.utils.annotation.RedditCompatibility
import app.revanced.patches.reddit.utils.integrations.Constants.PATCHES_PATH
import app.revanced.patches.reddit.utils.settings.SettingsBytecodePatch.Companion.updateSettingsStatus
import app.revanced.patches.reddit.utils.settings.SettingsPatch
import app.revanced.util.alsoResolve
import app.revanced.util.getInstruction
import app.revanced.util.indexOfFirstInstructionOrThrow
import app.revanced.util.indexOfFirstInstructionReversedOrThrow
import app.revanced.util.resultOrThrow
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.ReferenceInstruction

@Patch
@Name("Hide Recently Visited shelf")
@Description("Adds an option to hide the Recently Visited shelf in the sidebar.")
@DependsOn([SettingsPatch::class])
@RedditCompatibility
@Suppress("unused")
class RecentlyVisitedShelfPatch : BytecodePatch(
    listOf(CommunityDrawerPresenterConstructorFingerprint)
) {
    companion object {
        private const val INTEGRATIONS_METHOD_DESCRIPTOR =
            "$PATCHES_PATH/RecentlyVisitedShelfPatch;" +
                    "->" +
                    "hideRecentlyVisitedShelf(Ljava/util/List;)Ljava/util/List;"
    }

    override fun execute(context: BytecodeContext) {

        val recentlyVisitedReference =
            CommunityDrawerPresenterConstructorFingerprint.resultOrThrow().let {
                with (it.mutableMethod) {
                    val recentlyVisitedFieldIndex = indexOfHeaderItem(this)
                    val recentlyVisitedObjectIndex =
                        indexOfFirstInstructionOrThrow(recentlyVisitedFieldIndex, Opcode.IPUT_OBJECT)

                    getInstruction<ReferenceInstruction>(recentlyVisitedObjectIndex).reference.toString()
                }
            }

        CommunityDrawerPresenterFingerprint.alsoResolve(
            context, CommunityDrawerPresenterConstructorFingerprint
        ).let {
            it.mutableMethod.apply {
                val recentlyVisitedObjectIndex =
                    indexOfFirstInstructionOrThrow {
                        (this as? ReferenceInstruction)?.reference?.toString() == recentlyVisitedReference
                    }

                arrayOf(
                    indexOfFirstInstructionOrThrow(recentlyVisitedObjectIndex, Opcode.INVOKE_STATIC),
                    indexOfFirstInstructionReversedOrThrow(recentlyVisitedObjectIndex, Opcode.INVOKE_STATIC)
                ).forEach { staticIndex ->
                    val insertRegister =
                        getInstruction<OneRegisterInstruction>(staticIndex + 1).registerA

                    addInstructions(
                        staticIndex + 2, """
                                invoke-static {v$insertRegister}, $INTEGRATIONS_METHOD_DESCRIPTOR
                                move-result-object v$insertRegister
                                """
                    )
                }
            }
        }

        updateSettingsStatus("enableRecentlyVisitedShelf")

    }
}
