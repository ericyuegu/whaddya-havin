# Whaddya-havin 
> An Android health app focused on photo-based food journaling
# Release Notes
## New features for this release
* Facebook login
* Upload meal photos
* Create meal title

## Bug fixes made since the last release
* Signing out of account correctly redirects to login screen

## Known bugs and defects
* There is no indication of what the main landing screen is inside the application when first logging in

* Swiping between screens on an actual phone is difficult as you have to swipe from the far right or left and not just from the middle of the screen

* Clicking the back button after logging out bypasses authentication and returns you into the application

* We haven’t completed the feature of providing users with a full diet plan to follow—we moved this user story to future work

## Install Guide for Users
### Pre-requisites:	
* Android device running Android 8.0 (Oreo) or newer — works best on Pixel and Pixel 2 devices

### Download Instructions:
* Download APK file from the repo: https://github.com/ericyuegu/whaddya-havin/

### Installation:
* Navigate to the APK file and open it — this will prompt you to proceed with the installation

### Run Instructions:
* Open the app drawer and find Whaddya-havin, tap the app icon to open

### Troubleshooting: 
* Allow installation of APK files by enabling “Unknown sources”. Use one of these steps to enable the option depending on your device: 
“Settings” > “Applications” > “Unknown sources”
“Settings” > “Security” > “Unknown sources”
Enable camera and file permission when prompted

## Install Guide for Developers
### Pre-requisites:
* Android Studio (Version 3.1 or newer)
* A device running Android 8.0 (Oreo) or newer
* A device with Google Play services 15.0.0 or higher

### Dependent Libraries:
* There are no dependent libraries that need to be installed

### Download Instructions:
* Clone or download from the repo: https://github.com/ericyuegu/whaddya-havin/

### Installation:
#### Run on a real device:
1. Connect your device to your development machine with a USB cable
2. Enable USB debugging in the Developer options
3. In Android Studio, click the app module in the Project window and then select Run > Run (or click Run  in the toolbar)
4. In the Select Deployment Target window, select your device, and click OK
5. Android Studio installs the app on your connected device and starts it
#### Run on an emulator:
1. In Android Studio, click the app module in the Project window and then select Run > Run
2. In the Select Deployment Target window, click Create New Virtual Device
3. In the Select Hardware screen, select a phone device, such as Pixel 2, and then click Next
4. In the System Image screen, select the version with the highest API level. If you don't have that version installed, a Download link is shown, so click that and complete the download
5. Click Next
6. On the Android Virtual Device (AVD) screen, leave all the settings alone and click Finish
7. Back in the Select Deployment Target dialog, select the device you just created and click OK
8. Android Studio installs the app on the emulator and starts it
* Additional information on building and running your Android application can be found here: [https://developer.android.com/training/basics/firstapp/running-app]

### Build and Run Instructions:
* After installing the application, select Run > Run in the menu (or click the green play button in the toolbar of Android Studio)
* Android Studio will prompt you to select a deployment target which will either be your own connected Android device or a downloaded emulator 
* Additional information on adding new emulators can be found at the link: [https://developer.android.com/studio/run/managing-avds]
Additional information on building and running your Android application can be found at the link: [https://developer.android.com/studio/run/]

### Database:
* Account management and file storage is currently handled through Firebase—for access, email project point of contact: eric.yue.gu@gmail.com
* Alternatively, to replace with your own Firebase project, use Firebase Assistant in Android Studio or follow the instructions here: [https://firebase.google.com/docs/android/setup]

### Troubleshooting:
#### To resolve Gradle build error:
1.  Click Build > Clean Project.
2. Click Build > Rebuild Project.

