package com.example.projetotwilio

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.twilio.video.*
import com.twilio.video.quickstart.kotlin.CameraCapturerCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    private val TAG = "VideoActivity"
    private val CAMERA_MIC_PERMISSION_REQUEST_CODE = 1
    private val cameraCapturerCompat by lazy {
        CameraCapturerCompat(this, getAvailableCameraSource())
    }
    private var room: Room? = null
    private var localParticipant: LocalParticipant? = null
    var accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTS2RlOTMxMGNjODc4ZmQ4MDg1OWEwOTkyMDZmYjJiOTc5LTE1ODg4NTgxNzEiLCJpc3MiOiJTS2RlOTMxMGNjODc4ZmQ4MDg1OWEwOTkyMDZmYjJiOTc5Iiwic3ViIjoiQUMyYzA1N2U0NGY3ZjQ2Zjk4ZTAxMzdiNmRjMjNjYjhmNSIsImV4cCI6MTU4ODg2MTc3MSwiZ3JhbnRzIjp7ImlkZW50aXR5IjoiZ2FicmllbCIsInZpZGVvIjp7fX19.8adrnT_uyhZk97LNT7DP_zVcxwORU9ILPrF2pl79DM8"
    private var localAudioTrack: LocalAudioTrack? = null
    private var localVideoTrack: LocalVideoTrack? = null
    private var participantIdentity: String? = null
    private lateinit var localVideoView: VideoRenderer

    private val participantListener = object : RemoteParticipant.Listener {
        override fun onAudioTrackPublished(remoteParticipant: RemoteParticipant,
                                           remoteAudioTrackPublication: RemoteAudioTrackPublication) {
            Log.d(TAG, "onAudioTrackPublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                    "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteAudioTrackPublication.trackName}]")
            txtStatus.text = "onAudioTrackAdded"
        }

        override fun onAudioTrackUnpublished(remoteParticipant: RemoteParticipant,
                                             remoteAudioTrackPublication: RemoteAudioTrackPublication) {
            Log.d(TAG, "onAudioTrackUnpublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                    "enabled=${remoteAudioTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteAudioTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteAudioTrackPublication.trackName}]")
            txtStatus.text = "onAudioTrackRemoved"
        }

        override fun onDataTrackPublished(remoteParticipant: RemoteParticipant,
                                          remoteDataTrackPublication: RemoteDataTrackPublication) {
            Log.d(TAG, "onDataTrackPublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                    "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteDataTrackPublication.trackName}]")
            txtStatus.text = "onDataTrackPublished"
        }

        override fun onDataTrackUnpublished(remoteParticipant: RemoteParticipant,
                                            remoteDataTrackPublication: RemoteDataTrackPublication) {
            Log.d(TAG, "onDataTrackUnpublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                    "enabled=${remoteDataTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteDataTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteDataTrackPublication.trackName}]")
            txtStatus.text = "onDataTrackUnpublished"
        }

        override fun onVideoTrackPublished(remoteParticipant: RemoteParticipant,
                                           remoteVideoTrackPublication: RemoteVideoTrackPublication) {
            Log.d(TAG, "onVideoTrackPublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                    "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteVideoTrackPublication.trackName}]")
            txtStatus.text = "onVideoTrackPublished"
        }

        override fun onVideoTrackUnpublished(remoteParticipant: RemoteParticipant,
                                             remoteVideoTrackPublication: RemoteVideoTrackPublication) {
            Log.d(TAG, "onVideoTrackUnpublished: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                    "enabled=${remoteVideoTrackPublication.isTrackEnabled}, " +
                    "subscribed=${remoteVideoTrackPublication.isTrackSubscribed}, " +
                    "name=${remoteVideoTrackPublication.trackName}]")
            txtStatus.text = "onVideoTrackUnpublished"
        }

        override fun onAudioTrackSubscribed(remoteParticipant: RemoteParticipant,
                                            remoteAudioTrackPublication: RemoteAudioTrackPublication,
                                            remoteAudioTrack: RemoteAudioTrack) {
            Log.d(TAG, "onAudioTrackSubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                    "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                    "name=${remoteAudioTrack.name}]")
            txtStatus.text = "onAudioTrackSubscribed"
        }

        override fun onAudioTrackUnsubscribed(remoteParticipant: RemoteParticipant,
                                              remoteAudioTrackPublication: RemoteAudioTrackPublication,
                                              remoteAudioTrack: RemoteAudioTrack) {
            Log.d(TAG, "onAudioTrackUnsubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrack: enabled=${remoteAudioTrack.isEnabled}, " +
                    "playbackEnabled=${remoteAudioTrack.isPlaybackEnabled}, " +
                    "name=${remoteAudioTrack.name}]")
            txtStatus.text = "onAudioTrackUnsubscribed"
        }

        override fun onAudioTrackSubscriptionFailed(remoteParticipant: RemoteParticipant,
                                                    remoteAudioTrackPublication: RemoteAudioTrackPublication,
                                                    twilioException: TwilioException) {
            Log.d(TAG, "onAudioTrackSubscriptionFailed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteAudioTrackPublication: sid=${remoteAudioTrackPublication.trackSid}, " +
                    "name=${remoteAudioTrackPublication.trackName}]" +
                    "[TwilioException: code=${twilioException.code}, " +
                    "message=${twilioException.message}]")
            txtStatus.text = "onAudioTrackSubscriptionFailed"
        }

        override fun onDataTrackSubscribed(remoteParticipant: RemoteParticipant,
                                           remoteDataTrackPublication: RemoteDataTrackPublication,
                                           remoteDataTrack: RemoteDataTrack) {
            Log.d(TAG, "onDataTrackSubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                    "name=${remoteDataTrack.name}]")
            txtStatus.text = "onDataTrackSubscribed"
        }

        override fun onDataTrackUnsubscribed(remoteParticipant: RemoteParticipant,
                                             remoteDataTrackPublication: RemoteDataTrackPublication,
                                             remoteDataTrack: RemoteDataTrack) {
            Log.d(TAG, "onDataTrackUnsubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrack: enabled=${remoteDataTrack.isEnabled}, " +
                    "name=${remoteDataTrack.name}]")
            txtStatus.text = "onDataTrackUnsubscribed"
        }

        override fun onDataTrackSubscriptionFailed(remoteParticipant: RemoteParticipant,
                                                   remoteDataTrackPublication: RemoteDataTrackPublication,
                                                   twilioException: TwilioException) {
            Log.d(TAG, "onDataTrackSubscriptionFailed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteDataTrackPublication: sid=${remoteDataTrackPublication.trackSid}, " +
                    "name=${remoteDataTrackPublication.trackName}]" +
                    "[TwilioException: code=${twilioException.code}, " +
                    "message=${twilioException.message}]")
            txtStatus.text = "onDataTrackSubscriptionFailed"
        }

        override fun onVideoTrackSubscribed(remoteParticipant: RemoteParticipant,
                                            remoteVideoTrackPublication: RemoteVideoTrackPublication,
                                            remoteVideoTrack: RemoteVideoTrack) {
            Log.d(TAG, "onVideoTrackSubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                    "name=${remoteVideoTrack.name}]")
            txtStatus.text = "onVideoTrackSubscribed"
            addRemoteParticipantVideo(remoteVideoTrack)
        }

        override fun onVideoTrackUnsubscribed(remoteParticipant: RemoteParticipant,
                                              remoteVideoTrackPublication: RemoteVideoTrackPublication,
                                              remoteVideoTrack: RemoteVideoTrack) {
            Log.d(TAG, "onVideoTrackUnsubscribed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrack: enabled=${remoteVideoTrack.isEnabled}, " +
                    "name=${remoteVideoTrack.name}]")
            txtStatus.text = "onVideoTrackUnsubscribed"
            removeParticipantVideo(remoteVideoTrack)
        }

        override fun onVideoTrackSubscriptionFailed(remoteParticipant: RemoteParticipant,
                                                    remoteVideoTrackPublication: RemoteVideoTrackPublication,
                                                    twilioException: TwilioException) {
            Log.d(TAG, "onVideoTrackSubscriptionFailed: " +
                    "[RemoteParticipant: identity=${remoteParticipant.identity}], " +
                    "[RemoteVideoTrackPublication: sid=${remoteVideoTrackPublication.trackSid}, " +
                    "name=${remoteVideoTrackPublication.trackName}]" +
                    "[TwilioException: code=${twilioException.code}, " +
                    "message=${twilioException.message}]")
            txtStatus.text = "onVideoTrackSubscriptionFailed"

        }

        override fun onAudioTrackEnabled(remoteParticipant: RemoteParticipant,
                                         remoteAudioTrackPublication: RemoteAudioTrackPublication) {
        }

        override fun onVideoTrackEnabled(remoteParticipant: RemoteParticipant,
                                         remoteVideoTrackPublication: RemoteVideoTrackPublication) {
        }

        override fun onVideoTrackDisabled(remoteParticipant: RemoteParticipant,
                                          remoteVideoTrackPublication: RemoteVideoTrackPublication) {
        }

        override fun onAudioTrackDisabled(remoteParticipant: RemoteParticipant,
                                          remoteAudioTrackPublication: RemoteAudioTrackPublication) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        localVideoView = videoView
        requestPermissionForCameraAndMicrophone()

        btnCreate.setOnClickListener {
            connectToRoom(txtRoomName.text.toString())
        }
    }

    private fun removeParticipantVideo(videoTrack: VideoTrack) {
        videoTrack.removeRenderer(videoView)
    }

    private fun requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this,
                R.string.permissions_needed,
                Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                CAMERA_MIC_PERMISSION_REQUEST_CODE)
        }
    }

    private fun getAvailableCameraSource(): CameraCapturer.CameraSource {
        return if (CameraCapturer.isSourceAvailable(CameraCapturer.CameraSource.FRONT_CAMERA))
            CameraCapturer.CameraSource.FRONT_CAMERA
        else
            CameraCapturer.CameraSource.BACK_CAMERA
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            var cameraAndMicPermissionGranted = true

            for (grantResult in grantResults) {
                cameraAndMicPermissionGranted = cameraAndMicPermissionGranted and
                        (grantResult == PackageManager.PERMISSION_GRANTED)
            }

            if (cameraAndMicPermissionGranted) {
                createAudioAndVideoTracks()
            } else {
                Toast.makeText(this,
                    R.string.permissions_needed,
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createAudioAndVideoTracks() {
        // Share your microphone
        localAudioTrack = LocalAudioTrack.create(this, true)

        // Share your camera
        localVideoTrack = LocalVideoTrack.create(this,
            true,
            cameraCapturerCompat.videoCapturer)
    }

    override fun onResume() {
        super.onResume()

        localVideoTrack = if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            LocalVideoTrack.create(this,
                true,
                cameraCapturerCompat.videoCapturer)
        } else {
            localVideoTrack
        }
        localVideoTrack?.addRenderer(localVideoView)
    }

    private fun checkPermissionForCameraAndMicrophone(): Boolean {
        val resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED
    }

    private var roomListener = object : Room.Listener {
        override fun onRecordingStopped(room: Room) {
            TODO("Not yet implemented")
        }

        override fun onParticipantDisconnected(room: Room, remoteParticipant: RemoteParticipant) {
            txtStatus.text = "${remoteParticipant.identity} desconectado"
        }

        override fun onRecordingStarted(room: Room) {
            TODO("Not yet implemented")
        }

        override fun onConnectFailure(room: Room, twilioException: TwilioException) {
            txtStatus.text = "Falha ao conectar"
            Log.d(TAG, twilioException.toString())
        }

        override fun onReconnected(room: Room) {
            TODO("Not yet implemented")
        }

        override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
            addRemoteParticipant(remoteParticipant)
        }

        override fun onConnected(room: Room) {
            localParticipant = room.localParticipant
            txtStatus.text = "Conectado a sala ${room.name}"

            room.remoteParticipants.firstOrNull()?.let { addRemoteParticipant(it) }
        }

        override fun onDisconnected(room: Room, twilioException: TwilioException?) {
            Log.d(TAG,"disconected")
        }

        override fun onReconnecting(room: Room, twilioException: TwilioException) {
            TODO("Not yet implemented")
        }

    }

    private fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        if (thumbView.visibility == View.VISIBLE) {
            return
        }
        participantIdentity = remoteParticipant.identity
        txtStatus.text = "Participant $participantIdentity joined"

        remoteParticipant.remoteVideoTracks.firstOrNull()?.let { remoteVideoTrackPublication ->
            if (remoteVideoTrackPublication.isTrackSubscribed) {
                remoteVideoTrackPublication.remoteVideoTrack?.let { addRemoteParticipantVideo(it) }
            }
        }

        remoteParticipant.setListener(participantListener)
    }

    private fun addRemoteParticipantVideo(videoTrack: VideoTrack) {
        moveLocalVideoToThumbnailView()
        videoView.mirror = true
        videoTrack.addRenderer(videoView)
    }

    private fun moveLocalVideoToThumbnailView() {
        if (thumbView.visibility == View.GONE) {
            thumbView.visibility = View.VISIBLE
            with(localVideoTrack) {
                this?.removeRenderer(videoView)
                this?.addRenderer(thumbView)
            }
            localVideoView = thumbView
            thumbView.mirror = cameraCapturerCompat.cameraSource ==
                    CameraCapturer.CameraSource.FRONT_CAMERA
        }
    }



    private fun connectToRoom(roomName: String) {
        val connectOptionsBuilder = ConnectOptions.Builder(accessToken)
            .roomName(roomName)

        /*
         * Add local audio track to connect options to share with participants.
         */
        localAudioTrack?.let { connectOptionsBuilder.audioTracks(listOf(it)) }

        /*
         * Add local video track to connect options to share with participants.
         */
        localVideoTrack?.let { connectOptionsBuilder.videoTracks(listOf(it)) }

        room = Video.connect(this, connectOptionsBuilder.build(), roomListener)
    }
}
