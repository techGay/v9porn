/*
 * Copyright (C) 2016 Brian Wernick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flymegoc.exolibrary.widget;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.devbrackets.android.exomedia.ExoMedia;
import com.devbrackets.android.exomedia.core.ListenerMux;
import com.devbrackets.android.exomedia.core.api.VideoViewApi;
import com.devbrackets.android.exomedia.core.exoplayer.ExoMediaPlayer;
import com.devbrackets.android.exomedia.core.listener.MetadataListener;
import com.devbrackets.android.exomedia.core.video.exo.ExoTextureVideoView;
import com.devbrackets.android.exomedia.core.video.mp.NativeTextureVideoView;
import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.listener.OnBufferUpdateListener;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.OnSeekCompletionListener;
import com.devbrackets.android.exomedia.listener.OnVideoSizeChangedListener;
import com.devbrackets.android.exomedia.util.DeviceUtil;
import com.devbrackets.android.exomedia.util.StopWatch;
import com.flymegoc.exolibrary.R;
import com.flymegoc.exolibrary.utils.ActivityUtils;
import com.google.android.exoplayer2.drm.MediaDrmCallback;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;

import java.util.Map;

/**
 * This is a support VideoView that will use the standard VideoView on devices below
 * JellyBean.  On devices with JellyBean and up we will use the ExoPlayer in order to
 * better support HLS streaming and full 1080p video resolutions which the VideoView
 * struggles with, and in some cases crashes.
 * <p>
 * To an external user this view should have the same APIs used with the standard VideoView
 * to help with quick implementations.
 */

public class ExoVideoView extends RelativeLayout {
    private static final String TAG = ExoVideoView.class.getSimpleName();

    @Nullable
    protected ExoVideoControls videoControls;
    protected ImageView previewImageView;

    protected Uri videoUri;
    protected VideoViewApi videoViewImpl;
    protected DeviceUtil deviceUtil = new DeviceUtil();

    protected AudioManager audioManager;
    @NonNull
    protected AudioFocusHelper audioFocusHelper = new AudioFocusHelper();

    protected long positionOffset = 0;
    protected long overriddenDuration = -1;

    protected boolean overridePosition = false;
    protected StopWatch overriddenPositionStopWatch = new StopWatch();

    protected MuxNotifier muxNotifier = new MuxNotifier();
    protected ListenerMux listenerMux;

    protected boolean releaseOnDetachFromWindow = true;
    protected boolean handleAudioFocus = true;

    public ExoVideoView(Context context) {
        super(context);
        setup(context, null);
    }

    public ExoVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ExoVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExoVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (!isInEditMode() && releaseOnDetachFromWindow) {
            release();
        }
    }

    @Override
    public void setOnTouchListener(OnTouchListener listener) {
        videoViewImpl.setOnTouchListener(listener);
        super.setOnTouchListener(listener);
    }

    /**
     * <b><em>WARNING:</em></b> Use of this method may cause memory leaks.
     * <p>
     * Enables or disables the automatic release when the VideoView is detached
     * from the window.  Normally this is expected to release all resources used
     * by calling {@link #release()}.  If <code>releaseOnDetach</code> is disabled
     * then {@link #release()} will need to be manually called.
     *
     * @param releaseOnDetach False to disable the automatic release in {@link #onDetachedFromWindow()}
     */
    public void setReleaseOnDetachFromWindow(boolean releaseOnDetach) {
        this.releaseOnDetachFromWindow = releaseOnDetach;
    }

    /**
     * Stops the playback and releases all resources attached to this
     * VideoView.  This should not be called manually unless
     * {@link #setReleaseOnDetachFromWindow(boolean)} has been set.
     */
    public void release() {
        videoControls = null;
        stopPlayback();
        overriddenPositionStopWatch.stop();

        videoViewImpl.release();
    }

    /**
     * Sets an image that will be visible only when the video is loading.
     *
     * @param drawable The drawable to use for the preview image
     */
    public void setPreviewImage(@Nullable Drawable drawable) {
        if (previewImageView != null) {
            previewImageView.setImageDrawable(drawable);
        }
    }

    /**
     * Sets an image that will be visible only when the video is loading.
     *
     * @param resourceId The resourceId representing the preview image
     */
    public void setPreviewImage(@DrawableRes int resourceId) {
        if (previewImageView != null) {
            previewImageView.setImageResource(resourceId);
        }
    }

    /**
     * Sets an image that will be visible only when the video is loading.
     *
     * @param bitmap The bitmap to use for the preview image
     */
    public void setPreviewImage(@Nullable Bitmap bitmap) {
        if (previewImageView != null) {
            previewImageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Sets an image that will be visible only when the video is loading.
     *
     * @param uri The Uri pointing to the preview image
     */
    public void setPreviewImage(@Nullable Uri uri) {
        if (previewImageView != null) {
            previewImageView.setImageURI(uri);
        }
    }

    /**
     * Gets the preview ImageView for use with image loading libraries.
     *
     * @return the preview ImageView
     */
    public ImageView getPreviewImageView() {
        return previewImageView;
    }

    public void setControls(@Nullable ExoVideoControls controls) {
        if (videoControls != null && videoControls != controls) {
            removeView(videoControls);
        }

        if (controls != null) {
            videoControls = controls;
            controls.setVideoView(this);
            addView(controls);
        }

        //Sets the onTouch listener to show the controls
        TouchListener listener = new TouchListener(getContext());
        setOnTouchListener(videoControls != null ? listener : null);
    }

    /**
     * Requests the {@link ExoVideoControls} to become visible.  This should only be called after
     * {@link #setControls(ExoVideoControls)}.
     */
    public void showControls() {
        if (videoControls != null) {
            videoControls.show();

            if (isPlaying()) {
                videoControls.hideDelayed();
            }
        }
    }

    /**
     * Retrieves the video controls being used by this view.
     * If the controls haven't been specified with {@link #setControls(ExoVideoControls)}
     * or through the XML attribute <code>useDefaultControls</code> this will return
     * null
     *
     * @return The video controls being used by this view or null
     */
    @Nullable
    public ExoVideoControls getVideoControls() {
        return videoControls;
    }

    /**
     * Sets the Uri location for the video to play
     *
     * @param uri The video's Uri
     */
    public void setVideoURI(@Nullable Uri uri) {
        videoUri = uri;
        videoViewImpl.setVideoUri(uri);

        if (videoControls != null) {
            videoControls.showLoading(true);
        }
    }

    /**
     * Sets the Uri location for the video to play
     *
     * @param uri         The video's Uri
     * @param mediaSource MediaSource that should be used
     */
    public void setVideoURI(@Nullable Uri uri, @Nullable MediaSource mediaSource) {
        videoUri = uri;
        videoViewImpl.setVideoUri(uri, mediaSource);

        if (videoControls != null) {
            videoControls.showLoading(true);
        }
    }

    /**
     * Sets the path to the video.  This path can be a web address (e.g. http://) or
     * an absolute local path (e.g. file://)
     *
     * @param path The path to the video
     */
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    /**
     * Retrieves the current Video URI.  If this hasn't been set with {@link #setVideoURI(Uri)}
     * or {@link #setVideoPath(String)} then null will be returned.
     *
     * @return The current video URI or null
     */
    @Nullable
    public Uri getVideoUri() {
        return videoUri;
    }

    /**
     * Sets the {@link MediaDrmCallback} to use when handling DRM for media.
     * This should be called before specifying the videos uri or path
     * <br>
     * <b>NOTE:</b> DRM is only supported on API 18 +
     *
     * @param drmCallback The callback to use when handling DRM media
     */
    public void setDrmCallback(@Nullable MediaDrmCallback drmCallback) {
        videoViewImpl.setDrmCallback(drmCallback);
    }

    /**
     * Sets the volume level for devices that support
     * the ExoPlayer (JellyBean or greater).
     *
     * @param volume The volume range [0.0 - 1.0]
     * @return True if the volume was set
     */
    public boolean setVolume(@FloatRange(from = 0.0, to = 1.0) float volume) {
        return videoViewImpl.setVolume(volume);
    }

    /**
     * Enables or Disables automatic handling of audio focus. By default this is enabled
     * however in instances where a service handles playback of both audio and video it
     * is recommended to disable this and manually handle it in the service for consistency
     *
     * @param handleAudioFocus {@code true} to handle audio focus
     */
    public void setHandleAudioFocus(boolean handleAudioFocus) {
        audioFocusHelper.abandonFocus();
        this.handleAudioFocus = handleAudioFocus;
    }

    /**
     * Stops the current video playback and resets the listener states
     * so that we receive the callbacks for events like onPrepared
     */
    public void reset() {
        stopPlayback();
        setVideoURI(null);
    }

    /**
     * Moves the current video progress to the specified location.
     *
     * @param milliSeconds The time to move the playback to
     */
    public void seekTo(long milliSeconds) {
        if (videoControls != null) {
            videoControls.showLoading(false);
        }

        videoViewImpl.seekTo(milliSeconds);
    }

    /**
     * Returns if a video is currently in playback
     *
     * @return True if a video is playing
     */
    public boolean isPlaying() {
        return videoViewImpl.isPlaying();
    }

    /**
     * Starts the playback for the video specified in {@link #setVideoURI(Uri)}
     * or {@link #setVideoPath(String)}.  This should be called after the VideoView is correctly
     * prepared (see {@link #setOnPreparedListener(OnPreparedListener)})
     */
    public void start() {
        if (!audioFocusHelper.requestFocus()) {
            return;
        }

        videoViewImpl.start();
        setKeepScreenOn(true);

        if (videoControls != null) {
            videoControls.updatePlaybackState(true);
        }
    }

    /**
     * If a video is currently in playback, it will be paused
     */
    public void pause() {
        pause(false);
    }

    /**
     * Pauses the current video in playback, only abandoning the audio focus if
     * <code>transientFocusLoss</code> is <code>false</code>. Calling {@link #pause()} should
     * be used in most cases unless the audio focus is being handled manually
     *
     * @param transientFocusLoss <code>true</code> if the pause is temporary and the audio focus should be retained
     */
    public void pause(boolean transientFocusLoss) {
        if (!transientFocusLoss) {
            audioFocusHelper.abandonFocus();
        }

        videoViewImpl.pause();
        setKeepScreenOn(false);

        if (videoControls != null) {
            videoControls.updatePlaybackState(false);
        }
    }

    /**
     * If a video is currently in playback then the playback will be stopped
     */
    public void stopPlayback() {
        stopPlayback(true);
    }

    /**
     * If the video has completed playback, calling {@code restart} will seek to the beginning of the video, and play it.
     *
     * @return {@code true} if the video was successfully restarted, otherwise {@code false}
     */
    public boolean restart() {
        if (videoUri == null) {
            return false;
        }

        if (videoViewImpl.restart()) {
            if (videoControls != null) {
                videoControls.showLoading(true);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * If a video is currently in playback then the playback will be suspended
     */
    public void suspend() {
        audioFocusHelper.abandonFocus();
        videoViewImpl.suspend();
        setKeepScreenOn(false);

        if (videoControls != null) {
            videoControls.updatePlaybackState(false);
        }
    }

    /**
     * Retrieves the duration of the current audio item.  This should only be called after
     * the item is prepared (see {@link #setOnPreparedListener(OnPreparedListener)}).
     * If {@link #overrideDuration(long)} is set then that value will be returned.
     *
     * @return The millisecond duration of the video
     */
    public long getDuration() {
        if (overriddenDuration >= 0) {
            return overriddenDuration;
        }

        return videoViewImpl.getDuration();
    }

    /**
     * Setting this will override the duration that the item may actually be.  This method should
     * only be used when the item doesn't return the correct duration such as with audio streams.
     * This only overrides the current audio item.
     *
     * @param duration The duration for the current media item or &lt; 0 to disable
     */
    public void overrideDuration(long duration) {
        overriddenDuration = duration;
    }

    /**
     * Retrieves the current position of the audio playback.  If an audio item is not currently
     * in playback then the value will be 0.  This should only be called after the item is
     * prepared (see {@link #setOnPreparedListener(OnPreparedListener)})
     *
     * @return The millisecond value for the current position
     */
    public long getCurrentPosition() {
        if (overridePosition) {
            return positionOffset + overriddenPositionStopWatch.getTimeInt();
        }

        return positionOffset + videoViewImpl.getCurrentPosition();
    }

    /**
     * Sets the amount of time to change the return value from {@link #getCurrentPosition()}.
     * This value will be reset when a new audio item is selected.
     *
     * @param offset The millisecond value to offset the position
     */
    public void setPositionOffset(long offset) {
        positionOffset = offset;
    }

    /**
     * Restarts the audio position to the start if the position is being overridden (see {@link #overridePosition(boolean)}).
     * This will be the value specified with {@link #setPositionOffset(long)} or 0 if it hasn't been set.
     */
    public void restartOverridePosition() {
        overriddenPositionStopWatch.reset();
    }

    /**
     * Sets if the audio position should be overridden, allowing the time to be restarted at will.  This
     * is useful for streaming audio where the audio doesn't have breaks between songs.
     *
     * @param override True if the position should be overridden
     */
    public void overridePosition(boolean override) {
        if (override) {
            overriddenPositionStopWatch.start();
        } else {
            overriddenPositionStopWatch.stop();
        }

        overridePosition = override;
    }

    /**
     * Retrieves the current buffer percent of the video.  If a video is not currently
     * prepared or buffering the value will be 0.  This should only be called after the video is
     * prepared (see {@link #setOnPreparedListener(OnPreparedListener)})
     *
     * @return The integer percent that is buffered [0, 100] inclusive
     */
    public int getBufferPercentage() {
        return videoViewImpl.getBufferedPercent();
    }

    /**
     * Sets the playback speed for this MediaPlayer.
     *
     * @param speed The speed to play the media back at
     * @return True if the speed was set
     */
    public boolean setPlaybackSpeed(float speed) {
        return videoViewImpl.setPlaybackSpeed(speed);
    }

    /**
     * Determines if the current video player implementation supports
     * track selection for audio or video tracks.
     *
     * @return True if tracks can be manually specified
     */
    public boolean trackSelectionAvailable() {
        return videoViewImpl.trackSelectionAvailable();
    }

    /**
     * Changes to the track with <code>trackIndex</code> for the specified
     * <code>trackType</code>
     *
     * @param trackType  The type for the track to switch to the selected index
     * @param trackIndex The index for the track to switch to
     */
    public void setTrack(ExoMedia.RendererType trackType, int trackIndex) {
        videoViewImpl.setTrack(trackType, trackIndex);
    }

    /**
     * Retrieves a list of available tracks to select from.  Typically {@link #trackSelectionAvailable()}
     * should be called before this.
     *
     * @return A list of available tracks associated with each track type
     */
    @Nullable
    public Map<ExoMedia.RendererType, TrackGroupArray> getAvailableTracks() {
        return videoViewImpl.getAvailableTracks();
    }

    /**
     * Sets how the video should be scaled in the view
     *
     * @param scaleType how to scale the videos
     */
    public void setScaleType(@NonNull ScaleType scaleType) {
        videoViewImpl.setScaleType(scaleType);
    }

    /**
     * Measures the underlying {@link VideoViewApi} using the video's aspect ratio if {@code true}
     *
     * @param measureBasedOnAspectRatioEnabled whether to measure using the video's aspect ratio or not
     */
    public void setMeasureBasedOnAspectRatioEnabled(boolean measureBasedOnAspectRatioEnabled) {
        videoViewImpl.setMeasureBasedOnAspectRatioEnabled(measureBasedOnAspectRatioEnabled);
    }

    /**
     * Sets the rotation for the Video
     *
     * @param rotation The rotation to apply to the video
     */
    public void setVideoRotation(@IntRange(from = 0, to = 359) int rotation) {
        videoViewImpl.setVideoRotation(rotation, true);
    }

    /**
     * Sets the listener to inform of VideoPlayer prepared events
     *
     * @param listener The listener
     */
    public void setOnPreparedListener(@Nullable OnPreparedListener listener) {
        listenerMux.setOnPreparedListener(listener);
    }

    /**
     * Sets the listener to inform of VideoPlayer completion events
     *
     * @param listener The listener
     */
    public void setOnCompletionListener(@Nullable OnCompletionListener listener) {
        listenerMux.setOnCompletionListener(listener);
    }

    /**
     * Sets the listener to inform of VideoPlayer buffer update events
     *
     * @param listener The listener
     */
    public void setOnBufferUpdateListener(@Nullable OnBufferUpdateListener listener) {
        listenerMux.setOnBufferUpdateListener(listener);
    }

    /**
     * Sets the listener to inform of VideoPlayer seek completion events
     *
     * @param listener The listener
     */
    public void setOnSeekCompletionListener(@Nullable OnSeekCompletionListener listener) {
        listenerMux.setOnSeekCompletionListener(listener);
    }

    /**
     * Sets the listener to inform of playback errors
     *
     * @param listener The listener
     */
    public void setOnErrorListener(@Nullable OnErrorListener listener) {
        listenerMux.setOnErrorListener(listener);
    }

    /**
     * Sets the listener to inform of ID3 metadata updates
     *
     * @param listener The listener
     */
    public void setId3MetadataListener(@Nullable MetadataListener listener) {
        listenerMux.setMetadataListener(listener);
    }

    /**
     * Sets the listener to inform of video size changes
     *
     * @param listener The listener
     */
    public void setOnVideoSizedChangedListener(@Nullable OnVideoSizeChangedListener listener) {
        muxNotifier.videoSizeChangedListener = listener;
    }

    /**
     * Returns a {@link Bitmap} representation of the current contents of the
     * view. If the surface isn't ready or we cannot access it for some reason then
     * <code>null</code> will be returned instead.
     * <p>
     * <b>NOTE:</b> Only the <code>TextureView</code> implementations support getting the bitmap
     * meaning that if the backing implementation is a <code>SurfaceView</code> then the result
     * will always be <code>null</code>
     *
     * @return A {@link Bitmap} representation of the view or <code>null</code>
     */
    @Nullable
    public Bitmap getBitmap() {
        if (videoViewImpl instanceof TextureView) {
            return ((TextureView) videoViewImpl).getBitmap();
        }

        return null;
    }

    /**
     * Performs the functionality to setup the initial properties including
     * determining the backing implementation and reading xml attributes
     *
     * @param context The context to use for setting up the view
     * @param attrs   The xml attributes associated with this instance
     */
    protected void setup(Context context, @Nullable AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        AttributeContainer attributeContainer = new AttributeContainer(context, attrs);
        initView(context, attributeContainer);
        postInit(attributeContainer);
    }

    /**
     * Performs the initialization of the view including inflating the correct
     * backing layout, linking the implementation, and finding the necessary view
     * references.
     *
     * @param context            The context for the initialization
     * @param attributeContainer The attributes associated with this instance
     */
    protected void initView(Context context, @NonNull AttributeContainer attributeContainer) {
        inflateVideoView(context, attributeContainer);

        previewImageView = findViewById(R.id.exomedia_video_preview_image);
        videoViewImpl = findViewById(R.id.exomedia_video_view);

        muxNotifier = new MuxNotifier();
        listenerMux = new ListenerMux(muxNotifier);

        videoViewImpl.setListenerMux(listenerMux);
    }

    /**
     * Handles any setup that needs to be performed after {@link #initView(Context, AttributeContainer)}
     * is performed.
     *
     * @param attributeContainer The attributes associated with this instance
     */
    protected void postInit(@NonNull AttributeContainer attributeContainer) {
        if (attributeContainer.useDefaultControls) {
            setControls(new ExoVideoControlsMobile(getContext()));
        }

        if (attributeContainer.scaleType != null) {
            setScaleType(attributeContainer.scaleType);
        }

        if (attributeContainer.measureBasedOnAspectRatio != null) {
            setMeasureBasedOnAspectRatioEnabled(attributeContainer.measureBasedOnAspectRatio);
        }
    }

    /**
     * Inflates the video view layout, replacing the {@link ViewStub} with the
     * correct backing implementation.
     *
     * @param context            The context to use for inflating the correct video view
     * @param attributeContainer The attributes for retrieving custom backing implementations.
     */
    protected void inflateVideoView(@NonNull Context context, @NonNull AttributeContainer attributeContainer) {
        View.inflate(context, R.layout.exomedia_video_view_layout, this);
        ViewStub videoViewStub = findViewById(R.id.video_view_api_impl_stub);

        videoViewStub.setLayoutResource(getVideoViewApiImplementation(context, attributeContainer));
        videoViewStub.inflate();
    }

    /**
     * Retrieves the layout resource to use for the backing video view implementation.  By
     * default this uses the Android {@link android.widget.VideoView} on legacy devices with
     * APIs below Jellybean (16) or that don't pass the Compatibility Test Suite [CTS] via
     * {@link NativeTextureVideoView}, and an ExoPlayer backed video view on the remaining devices via
     * {@link ExoTextureVideoView}.
     * <p>
     * In the rare cases that the default implementations need to be extended, or replaced, the
     * user can override the value with the attributes <code>videoViewApiImplLegacy</code>
     * and <code>videoViewApiImpl</code>.
     * <p>
     * <b>NOTE:</b> overriding the default implementations may cause inconsistencies and isn't
     * recommended.
     *
     * @param context            The Context to use when retrieving the backing video view implementation
     * @param attributeContainer The attributes to use for finding overridden video view implementations
     * @return The layout resource for the backing implementation on the current device
     */
    @LayoutRes
    protected int getVideoViewApiImplementation(@NonNull Context context, @NonNull AttributeContainer attributeContainer) {
        boolean useLegacy = !deviceUtil.supportsExoPlayer(context);
        return useLegacy ? attributeContainer.apiImplLegacyResourceId : attributeContainer.apiImplResourceId;
    }

    /**
     * Performs the functionality to stop the progress polling, and stop any other
     * procedures from running that we no longer need.
     */
    protected void onPlaybackEnded() {
        stopPlayback(false);
    }

    /**
     * Stops the video currently in playback, making sure to only clear the surface
     * when requested. This allows us to leave the last frame of a video intact when
     * it plays to completion while still clearing it when the user requests playback
     * to stop.
     *
     * @param clearSurface <code>true</code> if the surface should be cleared
     */
    protected void stopPlayback(boolean clearSurface) {
        audioFocusHelper.abandonFocus();
        videoViewImpl.stopPlayback(clearSurface);
        setKeepScreenOn(false);

        if (videoControls != null) {
            videoControls.updatePlaybackState(false);
        }
    }

    /**
     * A utility used to handle the audio focus for the {@link ExoVideoView}
     * when enabled.
     */
    protected class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
        protected boolean startRequested = false;
        protected boolean pausedForLoss = false;
        protected int currentFocus = 0;

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (!handleAudioFocus || currentFocus == focusChange) {
                return;
            }

            currentFocus = focusChange;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    if (startRequested || pausedForLoss) {
                        start();
                        startRequested = false;
                        pausedForLoss = false;
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (isPlaying()) {
                        pausedForLoss = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (isPlaying()) {
                        pausedForLoss = true;
                        pause(true);
                    }
                    break;
            }
        }

        /**
         * Requests to obtain the audio focus
         *
         * @return True if the focus was granted
         */
        public boolean requestFocus() {
            if (!handleAudioFocus || currentFocus == AudioManager.AUDIOFOCUS_GAIN) {
                return true;
            }

            if (audioManager == null) {
                return false;
            }

            int status = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
                currentFocus = AudioManager.AUDIOFOCUS_GAIN;
                return true;
            }

            startRequested = true;
            return false;
        }

        /**
         * Requests the system to drop the audio focus
         *
         * @return True if the focus was lost
         */
        public boolean abandonFocus() {
            if (!handleAudioFocus) {
                return true;
            }

            if (audioManager == null) {
                return false;
            }

            startRequested = false;
            int status = audioManager.abandonAudioFocus(this);
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status;
        }
    }

    protected class MuxNotifier extends ListenerMux.Notifier {
        @Nullable
        public OnVideoSizeChangedListener videoSizeChangedListener;

        @Override
        public boolean shouldNotifyCompletion(long endLeeway) {
            long position = getCurrentPosition();
            long duration = getDuration();
            return position > 0 && duration > 0 && position + endLeeway >= duration;
        }

        @Override
        public void onExoPlayerError(ExoMediaPlayer exoMediaPlayer, Exception e) {
            stopPlayback();

            if (exoMediaPlayer != null) {
                exoMediaPlayer.forcePrepare();
            }
        }

        @Override
        public void onMediaPlaybackEnded() {
            setKeepScreenOn(false);
            onPlaybackEnded();
        }

        @Override
        public void onSeekComplete() {
            if (videoControls != null) {
                videoControls.finishLoading();
            }
        }

        @Override
        @SuppressWarnings("SuspiciousNameCombination")
        public void onVideoSizeChanged(int width, int height, int unAppliedRotationDegrees, float pixelWidthHeightRatio) {
            //NOTE: Android 5.0+ will always have an unAppliedRotationDegrees of 0 (ExoPlayer already handles it)
            videoViewImpl.setVideoRotation(unAppliedRotationDegrees, false);
            videoViewImpl.onVideoSizeChanged(width, height,pixelWidthHeightRatio);

            if (videoSizeChangedListener != null) {
                videoSizeChangedListener.onVideoSizeChanged(width, height,pixelWidthHeightRatio);
            }
        }

        @Override
        public void onPrepared() {
            if (videoControls != null) {
                videoControls.setDuration(getDuration());
                videoControls.finishLoading();
            }
        }

        @Override
        public void onPreviewImageStateChanged(boolean toVisible) {
            if (previewImageView != null) {
                previewImageView.setVisibility(toVisible ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * Monitors the view click events to show and hide the video controls if they have been specified.
     */
    protected class TouchListener extends GestureDetector.SimpleOnGestureListener implements OnTouchListener {
        private final AudioManager mAudioManager;
        private final int mTouchSlop;
        private GestureDetector gestureDetector;
        private boolean pausedForSeek = false;
        private int stepTime = 0;
        private float stepVolume = 0;
        private float stepLight = 0;
        /**
         * false volume true light
         */
        private boolean isVolume = false;
        private boolean isLight = false;
        private boolean isblockY = false;
        private boolean isblockX = false;
        private float oX;
        private float oY;

        public TouchListener(Context context) {
            gestureDetector = new GestureDetector(context, this);
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (videoControls != null) {
                    videoControls.hideFormatTime();
                    videoControls.hideVolumeLightView();
                }
                if (pausedForSeek) {
                    pausedForSeek = false;
                    stepTime = (int) (videoViewImpl.getCurrentPosition() + stepTime);
                    if (stepTime <= videoViewImpl.getDuration() && stepTime >= 0) {
                        videoViewImpl.seekTo(stepTime);
                    } else if (stepTime < 0) {
                        videoViewImpl.seekTo(0);
                    }
                    start();
                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                stepTime = 0;
                isVolume = false;
                isLight = false;
                pausedForSeek = false;
                isblockY = false;
                isblockX = false;
                oX = event.getX();
                oY = event.getY();
                if (stepVolume == 0) {
                    int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    videoViewImpl.getVolume();
                    stepVolume = ((float) current / max) * 1;
                    Log.d("AAAAA", "max : " + max + "current : " + stepVolume);
                }
                if (stepLight == 0) {
                    stepLight = getScreenBrightness();
                }
                int per = getWidth() / 4;
                int threePer = 3 * per;
                if (event.getX() >= 0 && event.getX() <= per) {
                    isLight = true;
                } else if (event.getX() >= threePer && event.getX() <= getWidth()) {
                    isVolume = true;
                }
                Log.d("AAA", event.getX() + "::::" + per + ":::::" + threePer + "::::::" + getWidth() + ":::::" + isVolume);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (videoControls == null) {
                    return true;
                }
                float distanceX = event.getX() - oX;
                float distanceY = event.getY() - oY;
                if (!isblockY && Math.abs(distanceX) > Math.abs(distanceY)) {
                    isblockX = true;
                } else if (!isblockX && Math.abs(distanceY) > Math.abs(distanceX)) {
                    isblockY = true;
                }
                Log.d("AAAAAAAAAA", distanceX + "--------" + distanceY + "-----" + mTouchSlop);
                if (distanceX > mTouchSlop && isblockX) {
                    oX = event.getX();
                    stepTime = stepTime + 1000;
                    updateUI(stepTime);
                } else if (distanceX < -mTouchSlop && isblockX) {
                    oX = event.getX();
                    stepTime = stepTime - 1000;
                    updateUI(stepTime);
                } else if (isblockY && distanceY > mTouchSlop) {
                    oY = event.getY();
                    if (isLight) {
                        stepLight -= 0.02;
                        updateLight(stepLight);
                    } else if (isVolume) {
                        stepVolume -= 0.02;
                        updateVolume(stepVolume);
                    }
                } else if (isblockY && distanceY < -mTouchSlop) {
                    oY = event.getY();
                    if (isLight) {
                        stepLight += 0.02;
                        updateLight(stepLight);
                    } else if (isVolume) {
                        stepVolume += 0.02;
                        updateVolume(stepVolume);
                    }
                }
            }
            gestureDetector.onTouchEvent(event);
            return true;
        }

        private float getScreenBrightness() {
            ContentResolver contentResolver = getContext().getContentResolver();
            int defVal = 125;

            return ((float) Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, defVal) / 255) * 1;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            return true;
        }

        private void updateVolume(@FloatRange(from = 0.0, to = 1.0) float volume) {
            Log.d("AAAAAAAAA", "::" + String.valueOf(volume));
            if (stepVolume > 1) {
                stepVolume = 1;
            } else if (stepVolume < 0) {
                stepVolume = 0;
            }
            setVolume(stepVolume);
            if (videoControls != null) {
                videoControls.showVolumeLightView(stepVolume, ExoVideoControls.VOLUME_MODEL);
            }
        }

        private void updateLight(@FloatRange(from = 0.0, to = 1.0) float lightLevel) {
            Log.d("AAAAAAAAA", "::" + String.valueOf(lightLevel));
            if (stepLight > 1) {
                stepLight = 1;
            } else if (stepLight < 0) {
                stepLight = 0;
            }
            Window window = ActivityUtils.getWindow(getContext());
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.screenBrightness = stepLight;
            window.setAttributes(lp);
            if (videoControls != null) {
                videoControls.showVolumeLightView(stepLight, ExoVideoControls.LIGHT_MODEL);
            }
        }

        private void updateUI(long stepTime) {
            if (videoControls != null) {
                if (pausedForSeek) {
                    videoControls.showFormatTime(stepTime);
                }
                if (isPlaying()) {
                    pausedForSeek = true;
                    audioFocusHelper.abandonFocus();
                    videoViewImpl.pause();
                    setKeepScreenOn(false);
                }
            }
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            // Toggles between hiding and showing the controls
            if (videoControls != null && videoControls.isVisible()) {
                videoControls.hide();
            } else {
                showControls();
            }

            return true;
        }
    }

    /**
     * A simple class that will retrieve the attributes and provide a simplified
     * interaction than passing around the {@link AttributeSet}
     */
    protected class AttributeContainer {
        /**
         * Specifies if the {@link ExoVideoControls} should be added to the view.  These
         * can be added through source code with {@link #setControls(ExoVideoControls)}
         */
        public boolean useDefaultControls = false;

        /**
         * Specifies if the {@link VideoViewApi} implementations should use the {@link TextureView}
         * implementations.  If this is false then the implementations will be based on
         * the {@link android.view.SurfaceView}
         */
        public boolean useTextureViewBacking = false;

        /**
         * The resource id that points to a custom implementation for the <code>ExoPlayer</code>
         * backed {@link VideoViewApi}
         */
        public int apiImplResourceId = R.layout.exomedia_default_exo_texture_video_view;

        /**
         * The resource id that points to a custom implementation for the Android {@link android.media.MediaPlayer}
         * backed {@link VideoViewApi}.  This will only be used on devices that do not support the
         * <code>ExoPlayer</code> (see {@link DeviceUtil#supportsExoPlayer(Context)} for details)
         */
        public int apiImplLegacyResourceId = R.layout.exomedia_default_native_texture_video_view;

        /**
         * Specifies the scale that the {@link ExoVideoView} should use. If this is <code>null</code>
         * then the default value from the {@link com.devbrackets.android.exomedia.core.video.scale.MatrixManager}
         * will be used.
         */
        @Nullable
        public ScaleType scaleType;

        /**
         * Specifies if the {@link ExoVideoView} should be measured based on the aspect ratio. Because
         * the default value is different between the {@link com.devbrackets.android.exomedia.core.video.ResizingSurfaceView}
         * and {@link com.devbrackets.android.exomedia.core.video.ResizingTextureView} this will be <code>null</code>
         * when not specified.
         */
        @Nullable
        public Boolean measureBasedOnAspectRatio;

        /**
         * Reads the attributes associated with this view, setting any values found
         *
         * @param context The context to retrieve the styled attributes with
         * @param attrs   The {@link AttributeSet} to retrieve the values from
         */
        public AttributeContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
            if (attrs == null) {
                return;
            }

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VideoView);
            if (typedArray == null) {
                return;
            }

            useDefaultControls = typedArray.getBoolean(R.styleable.VideoView_useDefaultControls, useDefaultControls);
            useTextureViewBacking = typedArray.getBoolean(R.styleable.VideoView_useTextureViewBacking, useTextureViewBacking);

            if (typedArray.hasValue(R.styleable.VideoView_videoScale)) {
                scaleType = ScaleType.fromOrdinal(typedArray.getInt(R.styleable.VideoView_videoScale, -1));
            }

            if (typedArray.hasValue(R.styleable.VideoView_measureBasedOnAspectRatio)) {
                measureBasedOnAspectRatio = typedArray.getBoolean(R.styleable.VideoView_measureBasedOnAspectRatio, false);
            }

            //Resets the default implementations based on useTextureViewBacking
            apiImplResourceId = useTextureViewBacking ? R.layout.exomedia_default_exo_texture_video_view : R.layout.exomedia_default_exo_surface_video_view;
            apiImplLegacyResourceId = useTextureViewBacking ? R.layout.exomedia_default_native_texture_video_view : R.layout.exomedia_default_native_surface_video_view;

            apiImplResourceId = typedArray.getResourceId(R.styleable.VideoView_videoViewApiImpl, apiImplResourceId);
            apiImplLegacyResourceId = typedArray.getResourceId(R.styleable.VideoView_videoViewApiImplLegacy, apiImplLegacyResourceId);

            typedArray.recycle();
        }
    }
}