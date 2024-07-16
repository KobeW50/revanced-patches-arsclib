package app.revanced.patches.reddit.utils.settings.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags

internal object RedditInternalFeaturesFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    strings = listOf("RELEASE"),
    customFingerprint = { methodDef, _ ->
        !methodDef.definingClass.startsWith("Lcom/")
    }
)