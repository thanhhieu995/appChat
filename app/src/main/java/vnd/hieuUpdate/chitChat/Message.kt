package vnd.hieuUpdate.chitChat

data class Message (
    var message: String? = null,
    var senderId: String? = null,
    var receiveId: String? = null,
    var time: String?= null,
    var noAvatarMessage: Boolean = false,
    var seen: Boolean = false,
    var avatarSendUrl: String? = null,
    var avatarReceiveUrl: String? = null,
    var hadImage: Boolean = false
) {
}