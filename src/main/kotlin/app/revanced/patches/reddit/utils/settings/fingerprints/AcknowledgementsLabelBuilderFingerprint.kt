package app.revanced.patches.reddit.utils.settings.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

internal object AcknowledgementsLabelBuilderFingerprint : MethodFingerprint(
    returnType = "Z",
    parameters = listOf("Landroidx/preference/Preference;"),
    strings = listOf("onboardingAnalytics"),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.startsWith("Lcom/reddit/screen/settings/preferences/")
    }
)