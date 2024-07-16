package app.revanced.patches.reddit.layout.navigation.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.reddit.layout.navigation.fingerprints.BottomNavScreenHandlerFingerprint.indexOfGetItems
import app.revanced.patches.reddit.layout.navigation.fingerprints.BottomNavScreenHandlerFingerprint.indexOfSetSelectedItemType
import app.revanced.util.getReference
import app.revanced.util.getTargetIndexWithMethodReferenceName
import app.revanced.util.indexOfFirstInstruction
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.Method
import org.jf.dexlib2.iface.reference.MethodReference

internal object BottomNavScreenHandlerFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L", "L", "Z", "Landroid/view/ViewGroup;", "L"),
    customFingerprint = { methodDef, _ ->
        indexOfGetItems(methodDef) >= 0
                && indexOfSetSelectedItemType(methodDef) >= 0
    }
) {
    fun indexOfGetItems(methodDef: Method) =
        methodDef.indexOfFirstInstruction {
            val reference = getReference<MethodReference>()?.toString()
            reference != null && reference.endsWith("getItems()Ljava/util/List;")
        }
    fun indexOfSetSelectedItemType(methodDef: Method) =
        methodDef.getTargetIndexWithMethodReferenceName("setSelectedItemType")
}