ExpoConnect
==========

Two Android Apps using Wifi Direct, part of a bigger project developed for the Advanced User Interfaces course at polimi.

### The whole project:
An infrastructure that, using biometrical data of users, creates personalized recommendation of restaurants.
The interaction with the system takes place in the front of a big screen that is capable to track down the biometrical data of the users.
These data are sent to a backend that uses them to create a recommendation that will be showed to the user on the large screen.
A mobile application was also developed to let the users download the recommendation and continue their experience on a smartphone.

### The Mobile Application hosted in this repository:

The server application receives a JSON string from large-screen application. The string is received by a console command, translated in the right format and then sended by wifi direct.

The client application receives the JSON string, send the request to the server and then download a list of restaurants to visualize.
