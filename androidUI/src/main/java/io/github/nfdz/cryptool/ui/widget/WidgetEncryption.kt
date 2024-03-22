package io.github.nfdz.cryptool.ui.widget

import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource

data class WidgetEncryption(
    val id: String,
    val password: String?,
    val message: String?,
    val algorithm: AlgorithmVersion,
    val source: MessageSource?,
)