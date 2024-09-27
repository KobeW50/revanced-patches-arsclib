package app.revanced.patches.reddit.layout.recentlyvisited.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.reddit.layout.recentlyvisited.fingerprints.CommunityDrawerPresenterConstructorFingerprint.indexOfHeaderItem
import app.revanced.util.getReference
import app.revanced.util.indexOfFirstInstruction
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.iface.reference.FieldReference

internal object CommunityDrawerPresenterConstructorFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    strings = listOf("matureFeedFeatures", "communityDrawerSettings"),
    customFingerprint = { methodDef, _ ->
        indexOfHeaderItem(methodDef) >= 0
    }
) {
    fun indexOfHeaderItem(methodDef: Method) =
        methodDef.indexOfFirstInstruction {
            getReference<FieldReference>()?.name == "RECENTLY_VISITED"
        }
}