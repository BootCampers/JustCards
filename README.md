# *JustCards*

**JustCards** is an android app that allows a user to play card games on their android phone without needing physical cards. The game can be played by one user or a group of users. Any card game can be played in the app. Users can collaborate with other players, manage player positions, shuffle cards, show and hide cards.

## User Stories

The following **required** functionality is completed:

* [ ] User can be – player, operator or just watcher.
* [ ] Operator starts the session and creates a game id for other players to join.
* [ ] Everyone can create a profile with name and pic/avatar.
* [ ] As players join everyone’s screen will get a circular image view of the new user which he can drag around to place on the screen as he want to.
  * [] Long press on user image will allow to move it around and single click will allow to let see the user profile and current game score.
* [ ] Operator can choose how many deck of cards and which particular cards he wants to use for the game.
* [ ] Everyone will have access to score sheet as read only except operator will have write permission.
* [ ] Operator can deal the cards either manually by dragging cards onto user’s image or can distribute randomly. He can choose how many cards he want to distribute.
* [ ] Shuffle cards options should be available to operator before dealing cards.
* [ ] Operator can deal cards either facing up or facing down. However he chooses it should be delivered that way to the players.
* [ ] If cards are dealt facing down then anytime a player see the card, other players should be notified.
* [ ] Any move by the player should be visible to each player in the game by highlighting the user image as well as his last action and card.
* [ ] Operator should choose the order in which players will play. App will enforce that order. There can be no order as well. Players will play by that order or can choose to skip on his turn.
* [ ] Major sections\views in the  game are:
  * [ ] Player’s own hand – shows cards, either top 2-3 or all or in a way he can scroll and see all and rearrange them.
  * [ ] Table with
    * [ ] Cards pile which are placed on it by user while playing
    * [ ] Discard pile
    * [ ] Remaining pile
  * [ ] Player’s list
* [ ] Player can drag and drop from any pile on the table or onto other user’s hand.

The following **optional** features are implemented:

* [ ] Ability to login via fb or google or twitter accounts.
* [ ] Operator can drag and drop to create groups\teams and score based on that. That will be part of initial setup of game.
* [ ] Any user can leave anytime and score sheet, other views should be updated accordingly without affecting current game.

The following **bonus** features are implemented:

* [ ] If operator leaves he can choose another user as operator before leaving otherwise a random user will be marked as operator.
* [ ] Every activity will be logged in list which anyone can review anytime.
* [ ] When a card is moved from 1 place to another it should show nice animation of actual card moving out from one’s hand to other place.

**Future plans**:

* Compatible iOS app
* Compatible VR app - daydream/oculus. This could be first real phone and VR inter-operable app.
* Funding - either from VCs or Kickstarter\crowdsource
* Attach app to Google Play Games to show leaderboard, awards, levels, etc. 
* Do user trials and get feedback to improve usability
* Support ads

## Wireframes

* **Wireframes are in assets directory**

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='http://i.imgur.com/link/to/your/gif/file.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android

## License

    Copyright [2016] [Bootamp org]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.