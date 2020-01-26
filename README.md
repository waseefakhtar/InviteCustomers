# Intercom Invite Customers App
 
The repo includes an APK to run on your Android device named `app-release.apk` and a result text file named `output.txt` (the solution of the problem) in the root directory.
 
## How to install APK on your device

1. Send the file to your Android device.
2. Install the APK.
3. Run the installed app. (Make sure you're connected to the internet)

## Features of the app

The app shows a list of customers with their respective distance from the Intercom Dublin office in kilometres (retrieved from the JSON data here: https://s3.amazonaws.com/intercom-take-home-test/customers.txt). 

The action bar shows a number of actions:
1. Show All Customers: Displays all customers from the list.
2. Show Customers to Invite: Displays all customers that are within 100 km from the Intercom Dublin office.
3. Save Invitation List: Stores an `output.txt` file in your device (in the directory Documents/Intercom Party Invitations) sorted by User IDs (ascending). (Make sure you give app the permission to access photos, media and files in order for it save the file in your directory)

## How to install and run the app from source code

1. Download and install Android Studio 3.5+
2. Download the `InviteCustomers` repository as zip.
3. Extract the zip file.
4. Click on open an existing Android Studio project.
5. Select the unzipped `InviteCustomers-master` folder and let the build to be completed.
6. Go to Run > Run 'app' to run on a connected device or on an emulator.

## Running tests with Android Studio

All the tests are in directory `app > java > com (androidTests)` and `app > java > com (test)`. If you want to run one of them:
1. Open the file, for instance `MainActivityPresenterTest.kt`.
2. Right click in the file opened and click `Run MainActivityPresenterTest`.

## Running tests with gradlew

1. Make sure you have `gradle` installed on your system: https://gradle.org/install/
2. Open the terminal either from your system or in Android Studio.
3. Go to `InviteCustomers-master` in the terminal.
4. Run the command `./gradlew test` to run all the tests in the project.



