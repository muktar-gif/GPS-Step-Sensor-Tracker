Setup Instructions:

-Open project in android studio
-Connect physical device, plug in device or enable wifi debugged (preferred)
-Run application
-Grant app permissions. Enable health permission and location permissions on prompt.
-Test application

Screen Shots:





Description of architecture:

The app consist of three screens the dashboard page, history page, and settings page. Each page
is displayed through fragments, with a bottom navigator in the main activity selecting a fragment
to display. The app consist of a foreground service responsible for registering step detector
sensors and location listeners.

The foreground service passes current day data to the dashboard and stores location data and
step/distance data as a string json using shared preferences.

In the dashboard page, the steps, distance actuated, and location data is displayed to the user.
In the history page, the steps/distance data and the location data is parsed and merged by date and
then displayed as a list. The setting page uses shared preferences to store enable location and
units preferred. If location is not enabled the location display is grayed out and all other
location related listener and IU are blocked from passing location data, else it is not grayed and
passes location information. Depending on which units preferred is selected, determines the if the
UI displays distance in miles or kilometers on the dashboard page and the history page.