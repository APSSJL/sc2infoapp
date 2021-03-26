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
We decided to use a digital platform to create the low-fidelity wireframe needed for this task.

### [BONUS] Digital Wireframes & Mockups

#### Overall wireframe

<img src='https://github.com/APSSJL/sc2infoapp/blob/main/Wireframe/Overall_Wireframe.PNG' /> 

### [BONUS] Interactive Prototype
#### Part 1

<img src='https://github.com/APSSJL/sc2infoapp/blob/main/Gifs/InteractivePrototype_2p1.gif' width='200' />

#### Part 2

<img src='https://github.com/APSSJL/sc2infoapp/blob/main/Gifs/InteractivePrototype_2p2.gif' width='200'  />

## Schema 
[This section will be completed in Unit 9]
### Models
Model: Post
| Property      | Type     | Description |
| ------------- | -------- | ------------|
| objectId      | String   | unique id for the user post (default field) |
| createdAt     | DateTime | date when post is created (default field) |
| updatedAt     | DateTime | date when post is last updated (default field) |
| author        | Pointer to User | creator of the post |
| content       | String | content of the post |
| title         | String | Post title |
| catergory     | String | Post category |
| tags          | Array| post Tags |

Model: User
| Property      | Type     | Description |
| ------------- | -------- | ------------|
| objectId      | String   | unique id for the user (default field) |
| createdAt     | DateTime | date when user is created (default field) |
| username      | String   | displayed name |
| password      | String   | password |
| in-game race  | String[need validation] | preffered race[zerg, protoss, terran, random, null] |
| profile picture | File | user's avatar |
| MMR           | Number | player level based on Starcraft internal ELO-like system. |
| userInfo      | String | bio information |
| follows       | Array | players/tournaments/users followed by user |
| player        | Pointer to Player | if user is a progamer, she/he can link the account |

Model: Tournament
| Property      | Type     | Description |
| ------------- | -------- | ------------|
| objectId      | String   | unique id for the Tournament post (default field) |
| createdAt     | DateTime | date when Tournament is created (default field) |
| updatedAt     | DateTime | date when Tournament is last updated (default field) |
| rating        | Number   | average tournament rating |
| name          | String   | tournament name |
| userCreated   | Pointer to UserTournament, [null, if tournament received from external API. If this is a user tournament, provides additional information.] |

Model: UserTournament
| Property      | Type     | Description |
| ------------- | -------- | ------------|
| objectId      | String   | unique id for the UserTournament post (default field) |
| createdAt     | DateTime | date when UserTournament is created (default field) | 
| updatedAt     | DateTime | date when UserTournament is last updated (default field) |
| organizer     | Pointer to User | tourmanter creator |
| matches       | Array    | matches in the tournament |
| logo          | file     | Tournament logo |
| description   | String   | tournament rules and description |

Model: Comment
| Property      | Type     | Description |
| ------------- | -------- | ------------|
| objectId      | String   | unique id for the comment (default field) |
| createdAt     | DateTime | date when comment is created (default field) |
| updatedAt     | DateTime | date when comment is last updated (default field) |
| author        | Pointer to user | comment creator |
| content       | String   | comment content |
| commentTo     | String   | id of object to which the comment refers[we have comments to players, posts and tournaments, and they all have the same properties, so I suggest to use objectId instead of pointer] |

Model: Player
| Property      | Type     | Description |
| ------------- | -------- | ------------|
| objectId      | String   | unique id for the player (default field) |
| createdAt     | DateTime | date when player is created (default field) |
| updatedAt     | DateTime | date when player is last updated (default field) |
| rating        | Number   | Player rating |
| name          | String   | players name |
|Picture        |File      | Player's photo|

Model: PlayerMatch [this entity for internal/user tournaments only, for other tournaments data should be fetched from the API] 
| Property      | Type     | Description |
| ------------- | -------- | ------------|
| objectId      | String   | unique id for the comment (default field)
| createdAt     | DateTime | date when comment is created (default field)
| updatedAt     | DateTime | date when comment is last updated (default field)
| Player1       | Pointer to User | id of first player
| Player2       | Pointer to User | id of second player
| details       | String   | match rules and desciption
| time          | DateTime | planned match time
| winner        | Pointer to user | who won this match

Model: TeamMatch [this entity for internal/user tournaments only, for other tournaments data should be fetched from the API] 
| Property      | Type     | Description |
| ------------- | -------- | ------------|
| objectId      | String   | unique id for the comment (default field) |
| createdAt     | DateTime | date when comment is created (default field) |
| updatedAt     | DateTime | date when comment is last updated (default field) |
| Team1         | String   | name of the first team |
| Team2         | String   | name of the second team |
| Lineup1       | Array    | Users playing for the first team |
| Lineup2       | Array    | Users playing for the second team |
| details       | String   | match rules and desciption |
| time          | DateTime | planned match time |
| winner        | String | who won this match

Model: Team
| Property      | Type     | Description |
| ------------- | -------- | ------------|
| objectId      | String   | unique id for the comment (default field) |
| teamName      |  String  | Name of team |
| lineup        |   Array  | List of players within the team |
| details       | String   | Information regarding the team, like wikipage info  |
| rating        | Number   | Team rating |
| updatedAt     | DateTime | date when team was last updated |
| createdAt     | DateTime | date when team was created |


### Networking
**Outline of Parse Network Requests**
* Login/Registration
    *   Login 
        - (Login/LOGIN) Login an user if Id/Password matches
    *   Registration
        - (Create/POST) Create a new User object
        - (Login/LOGIN) Login an user
* Home
    * Home feed screen
        - (Read/GET) Query a limited number of Tournaments
         ```java
         public ParseQuery<Tournament> getTournQuery(){
             ParseQuery<Tournament> tournQuery = ParseQuery.getQuery(Tournament.class);
             tournQuery.setLimit(10);
             tournQuery.orderBy("updatedAt");
             return tournQuery;
         }
         query = getTournQuery();
         query.findInBackground(new FindCallback<Tournament>(){
            public void done(List<Tournament> tournaments, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                for (Tournament tournament: tournaments){
                    printf("success");
                    ...
                }
            }
         }
         );
        ```
        - (Read/GET) Query a limited number of Posts
         ```java
         public ParseQuery<Post> getPostQuery(){
             ParseQuery<Post> postQuery = ParseQuery.getQuery(Post.class);
             postQuery.include("author");
             postQuery.setLimit(10);
             postQuery.orderBy("updatedAt");
             return postQuery;
         }
         query = getPostQuery();
         query.findInBackground(new FindCallback<Post>(){
            public void done(List<Post> posts, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                for (Tournament tournament: tournaments){
                    printf("success");
                    ...
                }
            }
         }
         ); 
        ```
    * Home filter screen 
        - (Read/GET) Query a limited number of Tournaments base on user selected filter
         ```java
         tournQuery = getTournQuery();
         tournQuery.WhereKey("objectId", equalTo: userInput_tournName);
         tournQuery.greaterThanOrEqualTo("rating", userInput_rating);
         tournQuery.findInBackground(new FindCallback<Tournament>(){
            public void done(List<Tournament> tournaments, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                for (Tournament tournament: tournaments){
                    printf("success");
                    ...
                }
            }
         }
         );
         ```
        - (Read/GET) Query a limited number of Posts base on user selected filter 
        ```java
         postQuery = getPostQuery();
         postQuery.WhereKey("author", equalTo: UserInput_author);
         postQuery.WhereKey("category", equalTo: UserInput_author);
         postQuery.WhereKey("tags", equalTo: UserInput_author);
         postQuery.lessThanOrEqualTo("updatedAt", userInput_time);
         postQuery.findInBackground(new FindCallback<Post>(){
            public void done(List<Post> posts, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                for (Post post: posts){
                    printf("success");
                    ...
                }
            }
         }
         );
         
         ``` 
    * Search screen
        - (Read/GET) Query a limited number of Tournaments base on user's search keywords
        ```java
        tournQuery = getTournQuery();
        tournQuery.setLimit(10);
        tournQuery.contains("name", equalTo: userInput);
        tournQuery.findInBackground(new FindCallback<Tournament>(){
            public void done(List<Tournament> tournaments, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                for (Tournament tournament: tournaments){
                    printf("success");
                    ...
                }
            }
         }
         );
        ```
        - (Read/GET) Query a limited number of Player base on user's search keywords
        ```java
        ParseQuery<Player> query = ParseQuery.getQuery(Player.class);
        query = getPlayerQuery();
        query.setLimit(10);
        query.contains("name", equalTo: userInput);
        query.findInBackground(new FindCallback<Player>(){
            public void done(List<Player> players, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                for (Player player: players){
                    printf("success");
                    ...
                }
            }
         }
         );
        ```
        - (Read/GET) Query a limited number of Users base on user's search keywords
        ```java
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.setLimit(10);
        query.contains("username", equalTo: userInput);
        query.findInBackground(new FindCallback<User>(){
            public void done(List<User> users, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                for (User user: users){
                    printf("success");
                    ...
                }
            }
         }
         );
        ```
* Match
    * Matches feed screen
        - (Read/GET) Query a limited number of matches
    * Match filter screen
        - (Read/GET) Query a limited number of matches base on user selected filter                                  
    * Match detail screen
        - (Read/GET) Query an informations of a selected match
        - (Read/GET) Query a previous meetings
        - (Create/POST) Create a new follow on a selected match
        - (Create/POST) Create a new rate on a selected match
        - (Create/POST) Create a new prediction on a selected match
    * Match comments screen
        - (Read/GET) Query a limited number of comments of a selected match
        - (Create/POST) Create a new Comment object
* Post
    * Post detail screen
        - (Read/GET) Query an informations of a selected post
        ```java
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo("objectId", equalTo: post.getObjectId());
        query.getFirstInBackground(new GetCallBack<Post>(){
        public void done(List<Post> posts, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                else{
                    printf("success");
                    ...
                }
            }
        }
        );
        ```
    * Post comments screen
        - (Read/GET) Query a limited number of comments of a selected post
        ```java
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereEqualTo("commentTo", equalTo: post.getObjectId());
        query.findInBackground(new FindCallBack<Comment>(){
        public void done(List<Comment> comments, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                for (Comment comment: comments){
                    printf("success");
                    ...
                }
            }
        }
        );
        ```
        - (Create/POST) Create a new Comment object
         ```java
         Comment postComment = new Comment();
         postComment.setAuthor(ParseUser.getCurrentUser());
         postComment.setcommentTo(Post.getObjectId());
         postComment.setContent(UserInput_comment);
         postComment.setCreatedAt(LocalDateTime.now());
         postComment.saveInBackground();
        ```
    * Post compose screen
        - (Create/POST) Create a new Post object
         ```java
         Post post = new Post();
         post.setAuthor(Parse.User.getCurrentUser());
         post.setContent(UserInput_content);
         post.setTitle(UserInput_PostTitle);
         post.setCategory(UserInput_category);
         post.setTags(UserInput_tags);
         post.saveInBackground();
        ```
* Player 
    * Player info screen
        - (Read/GET) Query an informations of a selected player
        - (Read/GET) Query matches that the player participated
        - (Create/POST) Create a new follow on a selected player
        - (Create/POST) Create a new rate on a selected player
        - (Create/POST) Create a new prediction on a selected player's match
    * Player comments screen
        - (Read/GET) Query a limited number of comments of a selected player
        - (Create/POST) Create a new Comment object
* Team
    * Team screen
        - (Read/GET) Query an informations of a selected team
        ```java
        ParseQuery<Team> query = ParseQuery.getQuery(Team.class)
        query.whereEqualTo("objectId", equalTo: team.getObjectId());
        query.getFirstInBackground(new GetCallBack<Team>(){
        public void done(List<Team> team, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                else{
                    printf("success");
                }
            }
        }
        );
        ```
* Tournament
    * Tournament info screen
        - (Read/GET) Query an informations of a selected tournament
    * Tournament comments screen
        - (Read/GET) Query a limited number of comments of a selected tournament
        - (Create/POST) Create a new Comment object
* User
    * User profile screen
        - (Read/GET) Query a logged in user object
        - (Update/PUT) Update user information

- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
- Liquipedia API:
  Base url: https://liquipedia.net/starcraft2/api.php
  
 | HTTP verb      | Description     | Endpoint |
 | ------------- | -------- | ------------|
 | ```GET```     | get content of specific page. Used to get players/tournaments info. | ?action=parse&page=name&formatversion=2&contentmodel=wikitext |
 | ```GET```     | get list of all matches | ?action=parse&page=Liquipedia:Upcoming_and_ongoing_matches&formatversion=2&contentmodel=wikitext |
