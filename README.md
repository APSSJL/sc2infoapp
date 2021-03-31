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

- [x] User can create an account and login
- [ ] User can use bottom navigation view to switch between fragments
    - [ ] Home feed fragment
        - [ ] Homefeed showing recent activity based on user follows
    - [ ] Profile fragment
    - [ ] Match feed fragment
        - [ ] Shows all recent tournaments and matches  
- [x] User can change account preferences
    - [x] User can add user info
    - [x] User can edit username
    - [x] User can edit preffered SC2 race(zerg/terran/protoss/random)
    - [x] User can add profile image
    - [x] User can edit MMR
    - [ ] User can join and leave team
    - [ ] User can create team
- [x] User can view profile
    - [ ] User can view account info
    - [x] If user is in team, he/she can tap on team name to go to the team page
    - [ ] User can see recent posts and tournaments
- [x] User can create post
    - [ ] By tapping on the post, user can go to the detailed post view
    - [ ] User can rate post in detailed view
    - [ ] User can view comments in the detailed view
    - [ ] User can leave comments 
 - [ ] Users can view match detailed page
    - [ ] Users can leave predictions
    - [ ] Users can get predictions from aligulac
- [ ] Users can create teams
- [ ] User can view team detailed page
    - [ ] Players without team can ask to join
    - [ ] Team owners can manage team
        - [ ] Can change team settings
        - [ ] Can view, approve or decline requests
        - [ ] Can set ishiring chackbox to find new players
        - [ ] Can view user profile by tapping on the username in requests recycler view
- [ ] User can view player's detailed page
- [ ] User can rate and leave comments:
    - [ ] Players
    - [ ] Matches
    - [ ] Tournaments
    - [ ] Teams
    - [ ] Posts
- [ ] User can follow:
    - [ ] Players
    - [ ] Other users
    - [ ] Tournaments
    - [ ] Teams 
- [ ] User can view tournament detailed screen
    - [ ] User can create and edit new tournaments
- [ ] User can search and filter posts, players, teams and tournaments
    - [ ] by keywords
    - [ ] by categories
    - [ ] by tags
    - [ ] by rating
    - [ ] by ishiring (for teams)
    - [ ] by type

**Optional Nice-to-have Stories**
- [ ]  User can get latest player updates from twitter
- [ ]  User can get list of current players streams (possible to embbed?)

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
| ratingSum     | Number   | average tournament rating |
| ratingVotes   | Number   | votes for rating |
| rating        | Number   | rating[need for queries] |
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
| ratingSum     | Number   | average player rating |
| ratingVotes   | Number   | votes for rating |
| name          | String   | players name |
| rating        | Number   | rating[need for queries] |
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
| ratingSum     | Number   | average match rating |
| ratingVotes   | Number   | votes for rating |
| rating        | Number   | rating[need for queries] |
| p1PredictionVotes | Number | predictions for the player 1 |
| p2PredictionVotes | Number | predictions for the player 2 |

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
| winner        | String | who won this match |
| t1PredictionVotes | Number | predictions for the team 1 |
| t2PredictionVotes | Number | predictions for the team 2 |

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
| owner         | Pointer to user | user that manages team |
| lineup        | array    | users in team |
| ishiring      | boolean  | shows if team looking for new members | 


### Networking
**Outline of Parse Network Requests**
* Login/Registration
    *   Login 
        - (Login/LOGIN) Login an user if Id/Password matches
        ```java
            if(ParseUser.getCurrentUser() != null)
            {
                // to main activity
            }
            ParseUser.logInInBackground(username, password, new LogIncallback() 
            {
                // to main activity
            }
    *   Registration
        - (Create/POST) Create a new User object
        ```java
            ParseUser user = new ParseUser();
            user.setUSername(username);
            user.setPassword(password);
            user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            //exception handling 
                            }
                        // to login activity
                        finish();
                        }
                    });
                        
        ```
        - (Login/LOGIN) Login an user
            - Same as login
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
        ```java
        // Code for team match is similar
        public ParseQuery<PlayerMatch> getPlayerMatchQuery()
        {
            ParseQuery<PlayerMatch> query = ParseQuery.getQuery(PlayerMatch.class);
            query.include("Player1");
            query.include("Player2");
            query.setLimit(5);
            query.addDescendingOrder("created_at");
        }
        query.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> p, ParseException e)             {
                    // do something
                }}
                );
       ```
    * Match filter screen
        - (Read/GET) Query a limited number of matches base on user selected filter
        ```java
        query = getPlayerMatchQuery();
        query.whereLessThan("time", LocalDateTime.now());
        query.whereGreaterThan("rating", 6);
        query.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> p, ParseException e)             {
                    // do something
                }}
                );
        
       ```                                  
    * Match detail screen
        - (Read/GET) Query an informations of a selected match - passed as an extra from previous screen
        - (Read/GET) Query a previous meetings
        ```java
            // we should either add mirror conditions, or set an order for storing matches
            query = getPlayerMatchQuery();
            query.whereEqualTo("Player1", match.getPlayer1());
            query.whereEqualTo("Player2", match.getPlayer2());
            query.whereLessThan("time", LocalDateTime.now());
            query.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> p, ParseException e)             {
                    // do something
                }}
                );
        ```
        - (Create/POST) Create a new follow on a selected match
        ```java
            ParseUser user = getCurrentUser();
            user.add("follows", match);
            user.saveInBackground();
        ```
        - (Create/POST) Create a new rate on a selected match
        ```java
            match.increment("ratingSum", rate);
            match.increment("ratingVotes", 1);
            match.put("rating", match.getRatingSum() / match.getRatingVotes());
            match.saveInBackground();
        ```
        - (Create/POST) Create a new prediction on a selected match
        ```java
            if(vote == 1)
            {
                match.increment("p1PredictionVotes");
            }
            else
            {
                match.increment("p2PredictionVotes");
            }
            match.saveInBackground();
        ```
    * Match comments screen
        - (Read/GET) Query a limited number of comments of a selected match
        ```java
            ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
            query.whereEqualTo("commentTo", match.getObjectId());
            query.addDescendingOrder("created_at");
            query.setLimit(5);
        ```
        - (Create/POST) Create a new Comment object
        ```java
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setAuthor(ParseUser.getCurrentUser());
            comment.saveInBackground();
        ```
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
        ```java
        ParseQuery<Player> query = ParseQuery.getQuery(Player.class);
    
        query = getPlayerQuery();
        query.contains("objectId", equalTo: //value from intent);
        
        query.findInBackground(new FindCallback<Player>(){
            public void done(Player player, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                //Implement player adapter
            }
         });
        ```
        - (Read/GET) Query matches that the player participated
        ```java
        ParseQuery<PlayerMatch> query1 = ParseQuery.getQuery(Player.class);
        ParseQuery<PlayerMatch> query2 = ParseQuery.getQuery(Player.class);
    
        query1.include("Player1");
        query1.contains("Player1", equalTo: //value from intent);
        query1.addDescendingOrder("created_at");
        
        query2.include("Player2");
        query2.contains("Player2", equalTo: //value from intent);
        query2.addDescendingOrder("created_at");
        
        ParseQuery<PlayerMatch> mainQuery = Parse.Query.or(query1, query2)
        
        mainQuery.findInBackground(new FindCallback<Post>(){
            public void done(List<Post> posts, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                //Implement player adapter
            }
         });
        ```
        - (Create/POST) Create a new follow on a selected player
        ```java
        ParseUser user = getCurrentUser();
        user.add("follows", player);
        user.saveInBackground();
        ```
        - (Create/POST) Create a new rate on a selected player
        ```java
        player.increment("ratingSum", rate);
        player.increment("ratingVotes", 1);
        player.put("rating", player.getRatingSum() / player.getRatingVotes());
        player.saveInBackground();
        ```
        - (Create/POST) Create a new prediction on a selected player's match
        ```java
        if(vote == 1){
            match.increment("p1PredictionVotes");
        }
        else{
            match.increment("p2PredictionVotes");
        }
        match.saveInBackground();
        ```
    * Player comments screen
        - (Read/GET) Query a limited number of comments of a selected player
        ```java
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
    
        query.whereEqualTo("commentTo", player.getObjectId());
        query.addDescendingOrder("created_at");
        query.setLimit(5);
        
        query.findInBackground(new FindCallback<Comment>(){
            public void done(List<Comment> comments, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                //Implement comments adapter
            }
         });
        ```
        - (Create/POST) Create a new Comment object
        ```java
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCommentTo(player.getObjectId());
        comment.setAuthor(ParseUser.getCurrentUser());
        comment.saveInBackground();
        ```
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
        ```java
        ParseQuery<Tournament> query = ParseQuery.getQuery(Tournament.class);
    
        query = getTournamentrQuery();
        query.contains("objectId", equalTo: //value from intent);
        query.findInBackground(new FindCallback<Tournament>(){
            public void done(Tournament tournament, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                //Implement tournament adapter
            }
         });
        ```
    * Tournament comments screen
        - (Read/GET) Query a limited number of comments of a selected tournament
        ```java
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
    
        query.whereEqualTo("commentTo", tournament.getObjectId());
        query.addDescendingOrder("created_at");
        query.setLimit(5);
        
        query.findInBackground(new FindCallback<Comment>(){
            public void done(List<Comment> comments, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                //Implement comments adapter
            }
         });
        ```
        - (Create/POST) Create a new Comment object
        ```java
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCommentTo(tournament.getObjectId());
        comment.setAuthor(ParseUser.getCurrentUser());
        comment.saveInBackground();
        ```
* User
    * User profile screen
        - (Read/GET) Query a logged in user object
        ```java
        ParseUser user = getCurrentUser();
        ParseQuery<User> query = ParseQuery.getQuery(User.class)
        
        query.whereEqualsTo("objectId", user.getObjectId());
        
        query.findInBackground(new FindCallback<User>(){
            public void done(User user, ParseException e){
                if(e!=null){
                    throwException();
                    return;
                }
                //Implement user adapter
            }
         });
        ```
        - (Update/PUT) Update user information
        ```java
        if (userName != user.getUsername()) {
            user.put("username", userName);
        }
        if (userInfo != user.get("userinfo").toString()) {
            user.put("userInfo", userInfo);
        }
        if (selectedRace != user.get("inGameRace").toString()) {
            user.put("inGameRace", selectedRace);
        }
        getCurrentUser().saveInBackground(e -> {
            if (e != null) {
                //throwException
            }
            Log.i(TAG, "Updated user successfully");
            finish();
        });
        ``` 

- [OPTIONAL: List endpoints if using existing API such as Yelp]
- Liquipedia API:
  Base url: https://liquipedia.net/starcraft2/api.php
  
 | HTTP verb      | Description     | Endpoint |
 | ------------- | -------- | ------------|
 | ```GET```     | get content of specific page. Used to get players/tournaments info. | ?action=parse&page=name&formatversion=2&contentmodel=wikitext |
 | ```GET```     | get list of all matches | ?action=parse&page=Liquipedia:Upcoming_and_ongoing_matches&formatversion=2&contentmodel=wikitext |

Aligulac API
- base url: http://aligulac.com/api/v1/ 

| HTTP Verb      | Endpoint     | Description |
| ------------- | -------- | ------------|
| ```GET```      | /player   | gets player object |
| ```GET```      | /match   | gets match object |

