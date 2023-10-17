package app.revanced.patches.youtube.misc.quic

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.misc.quic.fingerprints.CronetEngineBuilderFingerprint
import app.revanced.patches.youtube.misc.quic.fingerprints.ExperimentalCronetEngineBuilderFingerprint
import app.revanced.patches.youtube.utils.settings.SettingsPatch
import app.revanced.util.integrations.Constants.MISC_PATH

@Patch(
    name = "Disable QUIC protocol",
    description = "Disable CronetEngine's QUIC protocol.",
    dependencies = [SettingsPatch::class],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.24.37",
                "18.25.40",
                "18.27.36",
                "18.29.38",
                "18.30.37",
                "18.31.40",
                "18.32.39",
                "18.33.40",
                "18.34.38",
                "18.35.36",
                "18.36.39",
                "18.37.36"
            ]
        )
    ]
)
@Suppress("unused")
object QUICProtocolPatch : BytecodePatch(
    setOf(
        CronetEngineBuilderFingerprint,
        ExperimentalCronetEngineBuilderFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        arrayOf(
            CronetEngineBuilderFingerprint,
            ExperimentalCronetEngineBuilderFingerprint
        ).forEach {
            it.result?.mutableMethod?.addInstructions(
                0, """
                    invoke-static {p1}, $MISC_PATH/QUICProtocolPatch;->disableQUICProtocol(Z)Z
                    move-result p1
                    """
            ) ?: throw it.exception
        }

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "SETTINGS: DISABLE_QUIC_PROTOCOL"
            )
        )

        SettingsPatch.updatePatchStatus("disable-quic-protocol")

    }
}