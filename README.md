Original App Design Project - README Template
===

# APP_NAME_HERE

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
An app that allows Starcraft 2 players to follow the e-sports players and events and create their own tournaments.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category: Esports/Gaming/Social**
- **Mobile: Uses location**
- **Story: Allows users to watch the tournaments and communicate with people, who also like the game**
- **Market: About a few hundred thousands of people, who likes starcraft and want to discuss the matches.**
- **Habit: A few times a day, when there is a tournament update, or more often, if user want to see user posts. Users can create posts and tournaments.**
- **Scope: Reasonably challenging. It's acceptable to strip down some stories.**

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Create account / Login 
* Tabs of different feeds
    - Matches feed showing fixtures of games
    - Home feed, showing users/players/tournaments activity when followed.
* Filtering
    - Matches feed: based off of tournament ratings and teams
* Allow user to change account preferences
    - User can add profile image
    - User can edit preffered SC2 race(zerg/terran/protoss/random)
    - User can edit displayed name
    - User can add user info
* Player/Team page showing respective info.
    - Rating
    - Logo/photo
    - Recent/Upcoming matches
    - User comments
* Allow user to follow players, teams to get updates about their recent matches/activity
* User can follow other users so they can see posts added by this users
* Users can create posts
* User can search a professional player by name
* User can rate tournaments and leave comments
* User can sort tournamnets based on user rating

**Optional Nice-to-have Stories**

* User can get predictions about the match
* User can get latest player updates from twitter
* Preview of game can be showcased on match
* User can create tournaments
    - User can view and edit details of activity about user tournament
* User can get list of current players streams (possible to embbed?)

### 2. Screen Archetypes

* Login/Registration screen
    * User can login
    * User can create new account
* Steam
    * Matches feed
        - User can rate and leave comments
        - User can sort tournaments base on user rating
    * Home feed
    * User can filter feeds
    * (optional) user can get list of current streams
* Creation
    * User can create new post
    * (optional) User can create new tournament
* Detail
    * User can view player/team info
        - User can follow player/team and get info about recent activity
        - User can rate/comment player/team
    * User can see tournament info 
        - User can rate/comment tournament
    * User can see match info
    * (optional) User can get predictions about match
    * (optional) user can get players update from twitter
    * (optional) user can see game preview 
* Profile
    * User can view their profile and change the preferences (as descibed in part 1)
* Search
    * User can search a player by name

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home feed
* Match feed
* Profile

**Flow Navigation** (Screen to Screen)

* Login/Registration
    * Home
* Home
    * Creation - new post
    * Detail - player info
    * Search
* Match
    * Match info
    * Tournament info
* Tournament
    * Creation - new tournament
    * Detail - player info
    * Detail - tournament info
* Detail
    * Home
* Search
    * None

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype
<img src='https://github.com/APSSJL/sc2infoapp/blob/main/prototypegif_2.gif' />

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
