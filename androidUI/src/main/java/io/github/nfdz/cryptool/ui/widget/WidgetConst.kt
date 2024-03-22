package io.github.nfdz.cryptool.ui.widget

object WidgetConst {
    const val ENCRYPT = "ENCRYPT"
    const val DECRYPT = "DECRYPT"
    const val CLEAR = "clear"
}

class CipherTextChecker(
    var text : String
){
    fun isCipherText(): Boolean {

        // Additional heuristics can be added based on specific encryption formats or algorithms.

        // For example, if you know the ciphertext is Base64 encoded, you can check for its format:

        // You can add more specific checks based on the encryption algorithms or formats you expect.

        return text.matches("[A-Za-z0-9+/_.]+[=]{0,2}".toRegex())
    }
}