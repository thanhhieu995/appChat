package vnd.hieuUpdate.chitChat

import java.io.Serializable

data class User(var name: String? = null,
                var email: String? = null,
                var uid: String? = null,
                var status: String? = null,
                var avatar: String? = null,
                var calling: Boolean = false,
                var acceptCall: Boolean = false,
                var isTyping: Boolean = false,
                var showTyping: Boolean = false,
                var unRead: Int = 0,
                var receiveUid: String? = null,
                var lastMsg: String? = null,
                var listToken: ArrayList<String>? = ArrayList(),
                var sendToUid: String? = null): Serializable {
}