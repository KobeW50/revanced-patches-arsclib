package app.revanced.patches.reddit.ad.general.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

internal object NewAdPostFingerprint : MethodFingerprint(
    returnType = "L",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    opcodes = listOf(Opcode.INVOKE_VIRTUAL),
    strings = listOf(
        "chain",
        "feedElement"
    ),
    customFingerprint = { _, classDef -> classDef.sourceFile == "AdElementConverter.kt" },
)