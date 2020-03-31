package io.kroom.app.view.activitymain.trackvoteevent.model

data class TrackWithVote (
    val track : TrackModel,
    val score : Int,
    val nb_vote_up : Int,
    val nb_vote_down: Int
)