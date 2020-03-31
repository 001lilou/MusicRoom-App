package io.kroom.app.view.activitymain.trackvoteevent.model

import io.kroom.app.graphql.TrackVoteEventByIdQuery


data class TrackVoteEvent(
    val id: Int,
    val userMaster: String,
    val name: String,
    val public: Boolean,
    val currentTrack: CurrentTrack?,
    val trackWithVote:List<TrackWithVote?>,
    val scheduleBegin: String?,
    val scheduleEnd:String?,
    val latitude: Double?,
    val longitude: Double?,
    val  userInvited : List<User>

)
/* val trackWithVoteTrackId: Int,
   val trackWithVoteTrackTitle: String,
   val trackWithVoteTrackArtisteId: Int,
   val trackWithVoteTracktArtisteName: String,*/
//   val userInvitedId: Int,
//   val userInvitedUserName: String,
//  val userInvitedEmail: String
//    val currentTrackTitle: String,
//  val locAndSchRestriction: Boolean,