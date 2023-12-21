## Fire TV Integration SDK Sample App

## Overview
This sample app demonstrates integrating with the Fire TV Integration SDK available on Amazon Fire TV. Further details regarding the Fire TV Integration SDK
on Amazon Fire TV can be found in the [Amazon Developer Portal](TODO). If you cannot access this link
please reach out to your Amazon contact for access

## Setup
- Ensure you have Java 11 installed to avoid build errors with Gradle
  - **NOTE**: We recommend building this project on a Mac or any environment with a Unix-like operating system
- Follow the steps [here](TODO) to include the Fire TV Integration SDK in the project within `app/libs`
  - You need to create the `libs` directory if it does not already exist
- Install the sample app on your Fire TV device
  - First, ensure you are able to connect to your Fire TV device using ADB (see instructions [here](https://developer.amazon.com/docs/fire-tv/connecting-adb-to-device.html))
  - Next run `./gradlew installDebug` to install the sample app on your device

## Features

### Watch Activity
**Documentation**: TODO

**Code**:
- [`VideoPlayerFragment.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/video/player/VideoPlayerFragment.kt)
  - ExoPlayer PlayerView is initialized
  - Listener is attached which handles reporting playback events on player state changes
  - Periodic reporter is started which handles reporting current playback state every 60s
  - **NOTE**: See comments [here](app/src/main/java/com/amazon/firetv/integrationsdk/video/player/VideoPlayerFragment.kt#L122-L130) for information on how to handle sending the in app profile ID. You must send a value even if your app does not have a profiles feature.
- [`PlaybackStateListener.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/video/player/PlaybackStateListener.kt)
  - Implementation of the Player.Listener interface from the ExoPlayer library
  - This handles detecting when the video starts (playing actual content or advertisement), pauses, exits, seeks forward or backward
  - **NOTE**: See comments [here](app/src/main/java/com/amazon/firetv/integrationsdk/video/player/PlaybackStateListener.kt#L54-L85) for more information on reporting the `INTERSTITIAL` playback state
- [`PeriodicPlaybackStateReporter.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/video/player/PeriodicPlaybackStateReporter.kt)
  - Abstraction over `java.util.Timer` to handle scheduling tasks at 60s interval to report the current state of the video player
- [`FireTvPlaybackReporter.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/sdk/FireTvPlaybackReporter.kt)
  - Helper class which wraps calls to `AmazonPlaybackReceiver.getInstance(context).addPlaybackEvent(...)`
  - **NOTE**: Ensure you are checking if the content personalization feature is available on the device. This is done using the `isFTVIntegrationSDKSupportedOnDevice()` function in this class
- [`FireTvIntegrationSDKUtils.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/sdk/FireTvIntegrationSDKUtils.kt)
  - Contains a utility higher order function for checking if the content personalization feature is available on the device
  - You should implement a similar check in your code to avoid receiving ClassNotFound errors when accessing any SDK class
  - Contains a common function for reliably reproducing a hashed representation of an internal profile ID
- [`ExoPlayerUtils.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/util/ExoPlayerUtils.kt)
  - Contains a helper extension function to centralize the logic for determining the current state of the video player

### Watchlist
**Documentation**: https://developer.integ.amazon.com/docs/fire-tv/watchlist.html

**Code**:
- [`AccountFragment.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/account/AccountFragment.kt#L93-94)
  - At sign-in, refreshes the state of each profile's watchlist for the account
- [`FireTvWatchlistReporter.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/sdk/FireTvWatchlistReporter.kt)
  - Helper class whichs wraps calls to the FireTvIntegrationSDK `AmazonCustomerListReceiver` to report watchlist changes as well as refreshing the state of the watchlist for all profiles

### Content Entitlements
**Documentation**: https://developer.integ.amazon.com/docs/fire-tv/individual-content-entitlements.html

**Code**:
- [`AccountFragment.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/account/AccountFragment.kt#L97-103)
  - At sign-in, refreshes the state of the user's content entitlements (purchases, rentals, and recordings)
- [`FireTvContentEntitlementReporter.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/sdk/FireTvContentEntitlementReporter.kt)
  - Helper class which wraps calls to the FireTvIntegrationSDK `AmazonEntitlementReceiver` to report content entitlement state

### Amazon Data Integration Service Implementation
**Documentation**: TODO

**Code**:
- [`MyAmznDataIntegrationService.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/sdk/MyAmznDataIntegrationService.kt)
  - Sample implementation of the Amazon Data Integration Service used for the Fire TV system to request customer data from the app
- [`MyCustomerDataApiClient.kt`](app/src/main/java/com/amazon/firetv/integrationsdk/client/MyCustomerDataApiClient.kt)
  - Code to simulate retrieving customer data from backend services in order to provide to the FireTvIntegrationSDK

**Usage**:
1. After launching the app you may be prompted to select a profile and sign in if this is the first time you are using the app
2. Then you can select any tile from the `Videos` row to start playing that video or add the video to the selected profile's watchlist
3. Select "Play" on the video then allow the video to play for a few minutes, you can seek forward to speed things up
4. Navigate out of the sample app and back to Home screen of your Fire TV device. You should see a tile within the `Continue Watching` row with the same image as the video tile that was selected from Step 2

**NOTE**: Deeplinking into the Sample App from the tile shown in the Continue Watching is currently not supported by this app but documentation can be found at: https://developer.amazon.com/docs/catalog/integrate-with-launcher.html

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This project is licensed under the Apache-2.0 License.

## Notice

Images/videos used in this sample are courtesy of the Blender Foundation, shared under the [Creative Commons Attribution 3.0 license](https://creativecommons.org/licenses/by/3.0/)
- Tears of Steel: (CC) Blender Foundation | mango.blender.org
- Big Buck Bunny: (c) copyright 2008, Blender Foundation | peach.blender.org
