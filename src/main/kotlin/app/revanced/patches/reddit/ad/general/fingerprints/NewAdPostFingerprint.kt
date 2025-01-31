package app.revanced.patches.reddit.ad.general.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.reddit.ad.general.fingerprints.NewAdPostFingerprint.indexOfAddArrayList
import app.revanced.util.getReference
import app.revanced.util.indexOfFirstInstruction
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.iface.reference.MethodReference

internal object NewAdPostFingerprint : MethodFingerprint(
    returnType = "L",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf(
        "chain",
        "feedElement",
        "android_feed_freeform_render_variant",
    ),
    customFingerprint = { methodDef, _ ->
        indexOfAddArrayList(methodDef, 0) >= 0
    }
) {
    fun indexOfAddArrayList(methodDef: Method, index: Int) =
        methodDef.indexOfFirstInstruction(index) {
            getReference<MethodReference>()?.toString() == "Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z"
        }
}